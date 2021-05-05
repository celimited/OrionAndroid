package com.orion.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.orion.database.DatabaseConstants;
import com.orion.entities.Outlet;
import com.orion.entities.User;
import com.orion.fragments.NewOutletListFragment;

import java.util.Locale;


public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewOutletListFragment.OnListFragmentInteractionListener
        {

    private static final String TAG = "MainNavigationActivity";
    private User user;
    private int dateFlag = 0;
    private int sectionID = 0;
    private int routeID = 0;
    private Activity context;

    public static  boolean status;
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            current = getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            current = getResources().getConfiguration().locale;
        }
        isBangla = current.toLanguageTag().equals("bn");
        Log.d(TAG, "Locale: " + current.toLanguageTag());


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationMenu = navigationView.getMenu();

        navigationView.setCheckedItem( R.id.nav_home );
        selectFragment( R.id.nav_home );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navigationMenu.getItem(0).isChecked()){
            navigationView.setCheckedItem( R.id.nav_home );
            selectFragment( R.id.nav_home );
            setTitle(navigationMenu.getItem(0).getTitle());
        } else {
            quitButtonAction();
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        selectFragment(item.getItemId());
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        return true;
    }

    private void selectFragment(int id){
//        int id = item.getItemId();

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
        }

//        else if (id == R.id.nav_sales_confirmation) {
//            fragment = SalesConfirmationFragment.newInstance(1);
//        } else if (id == R.id.nav_promotions) {
//            fragment = PromotionsListActivity.newInstance();
//        } else if (id == R.id.nav_new_outlets) {
//            fragment = NewOutletListFragment.newInstance(3);
//        }else if(id == R.id.nav_employee_billing){
//
//        }

        else if (id == R.id.exit) {
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


    private void quitButtonAction(){
        AlertDialog.Builder quitConfirmationMessage = new AlertDialog.Builder(MainNavigationActivity.this);
        quitConfirmationMessage.setTitle("Quit");
        quitConfirmationMessage.setMessage("Are you sure you want to quit?");

        quitConfirmationMessage.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
//						DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 1;
//						Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						intent.putExtra("Exit", true);
                        DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 1;
                        //HomePageActivity.this.finish();
                        finish();
                        //startActivity(intent);
                        //Util.exportDatabaseToSdCard(context);
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

    /*
    private void orderButtonAction() {
//        Util.showWaitingDialog(context);
        Intent intent = new Intent(context,
                OutletListPageActivity.class).putExtra(
                DatabaseConstants.tblDSRBasic.SECTION_ID, user.sectionId);
        context.startActivity(intent);
//        this.finish();
    }
*/


}
