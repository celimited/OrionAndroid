package com.orion.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.orion.adapter.OrderedOutletListAdapter;
import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Channel;
import com.orion.entities.Market;
import com.orion.entities.Outlet;
import com.orion.entities.User;
import com.orion.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


/**
 * Created by shahriar on 6/12/16.
 */

public class OutletListFragment extends Fragment {

    public static final int YET_TO_VISIT = 0;
    public static final int VISITED = 1;
    public static final int NOT_ORDERED = 2;
    public static final int EDIT_NOT_ALLOWED = 1;

    private OrderedOutletListAdapter orderedOutletListAdapter;
    private GridView outletListGridView;

    private Market selectedMarket;
    private Outlet selectedOutlet;
    private int visitStatus = YET_TO_VISIT;
    private ArrayList<Outlet> outletList;
    public ArrayList<Outlet> outletListFiltered = new ArrayList<>();

    private RadioButton yetToVisitRadioButton;

    private FragmentActivity context;

    protected static final int VISIT_OUTLET = 200;
    private User user;
    private String TAG = "Trace";
    private String outletName;
    private TextView countOfOutlet;
    private int routeID = 0;
    private SearchView searchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_outlet_list_layout, container, false);

        outletListGridView = (GridView) rootView.findViewById(R.id.outlet_list_grid_view);
        outletListGridView.setEmptyView(rootView.findViewById(R.id.empty_grid_view));
        outletList = new ArrayList<>();

        orderedOutletListAdapter = new OrderedOutletListAdapter(getActivity(), R.layout.outlet_list_item, outletListFiltered);

        outletListGridView.setAdapter(orderedOutletListAdapter);
        outletListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                selectedOutlet = orderedOutletListAdapter.getItem(position);

                if (selectedOutlet.outletId == null) {
                    Intent newOutlet = new Intent(context, NewOutletActivity.class);
                    newOutlet.putExtra(NewOutletActivity.KEY_EDITABLE_OUTLET, selectedOutlet);
                    startActivity(newOutlet);
                } else {
                    outletSelectedAction(selectedMarket, selectedOutlet);
                }
            }

        });

        registerForContextMenu(outletListGridView);

        countOfOutlet = (TextView) rootView.findViewById(R.id.count_of_outlet);

        searchView = (SearchView) rootView.findViewById(R.id.outlet_search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                populateOutletList();
                updateOutletList(selectedMarket, visitStatus, s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateOutletList();
                updateOutletList(selectedMarket, visitStatus, s);
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OutletListFragment::onActivityResult " + requestCode);
        if (requestCode == Activity.RESULT_OK) {
            if (requestCode == VISIT_OUTLET) {
                populateOutletList();
                updateOutletList(selectedMarket, visitStatus, outletName);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated");
        if (selectedMarket != null) {
            populateOutletList();
            updateOutletList(selectedMarket, visitStatus, outletName);
        }

    }

    private void populateOutletList() {
        outletList = DatabaseQueryUtil.getOutletList(context, selectedMarket.routeId);
        Log.v(TAG, "populateOutletList:" + outletList.size());
    }

    private void updateOutletList(Market market, int visitedStatus, String outletName) {
        int sz = outletList.size();
        Log.v(TAG, "updateOutletList size: " + sz);
        Log.v(TAG, "visitedStatus: " + visitedStatus + ", user.visitDate: " + user.visitDate);

        orderedOutletListAdapter.clear();
        outletListFiltered.clear();

       // Outlet newOutlet = new Outlet();
       // newOutlet.description = getString(R.string.new_outlet);
        //newOutlet.routeID = selectedMarket.routeId;
       // newOutlet.outletId = null;
       // outletListFiltered.add(newOutlet);

        Outlet now;
        for (int i = 0; i < sz; i++) {
            now = outletList.get(i);

            if (outletName == null || outletName.isEmpty())
                outletListFiltered.add(now);

            else if (now.description.toLowerCase().startsWith(outletName.toLowerCase())) {
                outletListFiltered.add(now);
            }
        }

        Log.v(TAG, "outletListFiltered size: " + outletListFiltered.size());
        orderedOutletListAdapter.notifyDataSetChanged();
        outletListGridView.invalidate();
        countOfOutlet.setText(" (" + (orderedOutletListAdapter.getCount() - 1) + ")");

    }

    private void showCanProceedDialogBox(String message) {
        AlertDialog.Builder confirmmsg = new AlertDialog.Builder(context);
        // confirmCheckoutDialog.setTitle("Outlet Location");
        confirmmsg.setMessage(message);
        confirmmsg.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        outletSelectedAction(selectedMarket, selectedOutlet);
                    }
                }).setNegativeButton("Wait", new
                DialogInterface.OnClickListener() {
                    public void
                    onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        confirmmsg.show();
    }

    private void showMessageDialogBox(String message) {
        AlertDialog.Builder confirmmsg = new AlertDialog.Builder(context);
        // confirmCheckoutDialog.setTitle("Outlet Location");
        confirmmsg.setMessage(message);
        confirmmsg.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void
                    onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        confirmmsg.show();
    }

    private void outletSelectedAction(Market market, Outlet selectedOutlet) {
        Channel channel = DatabaseQueryUtil.getChannel(context,
                selectedOutlet.channelId);
        if (selectedOutlet.visited == VISITED
                && channel.editAllowed == EDIT_NOT_ALLOWED) {
            Util.showAlert(context, "You have already visited this outlet");
        } else {

            Intent outletVisitPageActivityIntent = new Intent(context, OutletVisitPageActivity.class)
                    .putExtra(DatabaseConstants.tblOutlet.OUTLET_ID, selectedOutlet.outletId)
                    .putExtra(DatabaseConstants.tblSection.SECTION_ID, selectedMarket.sectionId);

            startActivityForResult(outletVisitPageActivityIntent, VISIT_OUTLET);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSelectedMarket(Market selectedMarket) {
        this.selectedMarket = selectedMarket;
    }

    public void setOutletName(String name) {
        this.outletName = name;
    }

    public void setVisitStatus(int status) {
        this.visitStatus = status;
        updateOutletList(selectedMarket, this.visitStatus, outletName);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId()==R.id.outlet_list_grid_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            String currentDate = changeFormat(user.visitDate);
            int visitStatus = outletListFiltered.get(info.position).visited;
            int orderNo = DatabaseQueryUtil.getOrderNo(context, outletListFiltered.get(info.position).outletId, currentDate);
            menu.setHeaderTitle(String.valueOf(outletListFiltered.get(info.position).description));
            String[] menuItems = null;
            if(visitStatus > 0)
                menuItems = getResources().getStringArray(R.array.OutletOptionEdit);
            else
                menuItems = getResources().getStringArray(R.array.OutletOption);

            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean isVisited = false;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String currentDate = changeFormat(user.visitDate);
        int orderNo = DatabaseQueryUtil.getOrderNo(context, outletListFiltered.get(info.position).outletId, currentDate);
        String[] menuItems = null;
        if(orderNo > 0) {
            menuItems = getResources().getStringArray(R.array.OutletOptionEdit);
            isVisited = true;
        }
        else
            menuItems = getResources().getStringArray(R.array.OutletOption);

        String menuItemName = menuItems[menuItemIndex];
        String listItemName = String.valueOf(outletListFiltered.get(info.position).description);
        if(isVisited && Objects.equals(menuItems[menuItemIndex], "Delete")) {
            DatabaseQueryUtil.deleteOrder(context, outletListFiltered.get(info.position).outletId, orderNo);
            DatabaseQueryUtil.updateTblOutletVisitedField(context, outletListFiltered.get(info.position).outletId, 0);
            Util.showAlert(context, "Order deleted!");
            //super.onResume();
        }
        else{
            selectedOutlet = outletListFiltered.get(info.position);
            outletSelectedAction(selectedMarket, selectedOutlet);
        }

        return true;
    }
    private static String changeFormat(String visitDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(visitDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String _visitDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return _visitDate;
    }

}
