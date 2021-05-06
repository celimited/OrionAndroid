package com.orion.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.orion.database.DatabaseConstants;
import com.orion.entities.Outlet;
import com.orion.entities.User;
import com.orion.fragments.NewOutletListFragment;

import java.util.Locale;

public class MainNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NewOutletListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "MainNavigationActivity";
    private User user;
    private int dateFlag = 0;
    private int sectionID = 0;
    private int routeID = 0;
    private Activity context;

    public static boolean status;
    private Handler handler;
    private boolean isBangla;
    private Menu navigationMenu;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


//        menu.add(R.id.group1,Menu.NONE,Menu.NONE,itemName);
        Locale current;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            current = getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            current = getResources().getConfiguration().locale;
        }
        isBangla = current.toLanguageTag().equals("bn");
        Log.d(TAG, "Locale: " + current.toLanguageTag());


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationMenu = navigationView.getMenu();

        navigationView.setCheckedItem(R.id.nav_home);
        selectFragment(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navigationMenu.getItem(0).isChecked()) {
            navigationView.setCheckedItem(R.id.nav_home);
            selectFragment(R.id.nav_home);
            setTitle(navigationMenu.getItem(0).getTitle());
        } else {
            quitButtonAction();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectFragment(item.getItemId());
        item.setChecked(true);
        setTitle(item.getTitle());

        return true;
    }

    private void selectFragment(int id) {

        Fragment fragment = null;
        Class fragmentClass = TodaysStatusPageActivity.class;
        Bundle args = new Bundle();
        if (id == R.id.nav_home) {
            fragmentClass = HomePageActivity.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_todays_status) {
            fragmentClass = TodaysStatusPageActivity.class;
            args.putDouble(DatabaseConstants.tblDSRBasic.TARGET, user.target);
            args.putDouble(DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED, user.orderAchieved);
            args.putDouble(DatabaseConstants.tblDSRBasic.TARGET_REM, user.target - user.orderAchieved);
            args.putInt(DatabaseConstants.tblDSRBasic.DAY_REMAIN, user.dayRemain);
            args.putInt(DatabaseConstants.tblDSRBasic.SECTION_ID, sectionID);
            args.putInt(DatabaseConstants.tblSection.ROUTE_ID, routeID);
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_order_summary) {
            fragmentClass = OrderSummaryBySkuActivity.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.exit) {
            quitButtonAction();
            return;
        }


        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void quitButtonAction() {
        AlertDialog.Builder quitConfirmationMessage = new AlertDialog.Builder(MainNavigationActivity.this);
        quitConfirmationMessage.setTitle("Quit");
        quitConfirmationMessage.setMessage("Are you sure you want to quit?");

        quitConfirmationMessage.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 1;
                        finish();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        quitConfirmationMessage.show();
    }

    @Override
    public void onListFragmentInteraction(Outlet item) {
        Log.d(TAG, "onListFragmentInteraction");
    }
}
