package com.orion.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.orion.adapter.SalesConfirmationRVAdapter;
import com.orion.application.OutletListPageActivity;
import com.orion.application.R;
import com.orion.application.SalesOrderConfirmationActivity;
import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Outlet;
import com.orion.entities.OutletItem;
import com.orion.entities.User;
import com.orion.util.Util;

import java.util.ArrayList;
import java.util.Calendar;

public class SalesConfirmationFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener,
        SalesConfirmationRVAdapter.OnListFragmentInteractionListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int RC_GOT_NEW_OUTLET = 99;
    private static final int SALES_CONFIRMATION_UPDATE = 91;


    private int mColumnCount = 1;
    private FragmentActivity context;
    private ArrayList<OutletItem> outletList = new ArrayList<>();
    private String TAG = "Trace";
    private SalesConfirmationRVAdapter outletListAdapter;
    private RecyclerView rvOutletList;

    private Button btnChooseDate;
    private User user;
    private String dateString = "";
    private TextView tvSelectedDate;

    public SalesConfirmationFragment() {}

    public static SalesConfirmationFragment newInstance(int columnCount) {
        SalesConfirmationFragment fragment = new SalesConfirmationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();

        user = DatabaseQueryUtil.getUser(context);

        /*if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sales_confirmation_fragment, container, false);


        tvSelectedDate = (TextView)view.findViewById(R.id.tvSelectedDate);
        rvOutletList = (RecyclerView)view.findViewById(R.id.rvOutletList);
        btnChooseDate = (Button)view.findViewById(R.id.btnChooseDate);
        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.DatePickerFragment newFragment = new Util.DatePickerFragment();
                newFragment.setOnDateSetListener(SalesConfirmationFragment.this);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        dateString = day + "-" + (month+1) + "-" + year;
        tvSelectedDate.setText(dateString);
        outletList = DatabaseQueryUtil.getVisitedOutletListOnDate( context, dateString );

        // Set the adapter
        if (rvOutletList instanceof RecyclerView) {
            Log.d(TAG, "SalesConfirmation: " + outletList.size() + ", column count: " + mColumnCount);

            Context context = view.getContext();
            rvOutletList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            rvOutletList.addItemDecoration(new VerticalSpaceItemDecoration(35));

            outletListAdapter = new SalesConfirmationRVAdapter(outletList, SalesConfirmationFragment.this);
            rvOutletList.setAdapter(outletListAdapter);
        }
        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_of_promotions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_order){
            Intent intent = new Intent(context,
                    OutletListPageActivity.class).putExtra(
                    DatabaseConstants.tblDSRBasic.SECTION_ID, user.sectionId);
            context.startActivity(intent);
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "List refresh 1 ");

        if (resultCode == Activity.RESULT_OK){
            Log.d(TAG, "List refresh 2 ");
            if (requestCode == RC_GOT_NEW_OUTLET){
                // refresh outlet list
                outletList.clear();
                outletList.addAll( DatabaseQueryUtil.getVisitedOutletListOnDate( context, dateString ) );
                outletListAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        outletList.clear();
        dateString = day + "-" + (month+1) + "-" + year;
        outletList.addAll( DatabaseQueryUtil.getVisitedOutletListOnDate( getActivity(), dateString) );
        outletListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListFragmentInteraction(Outlet item) {
        Intent salesOrderIntent = new Intent(context, SalesOrderConfirmationActivity.class);
        salesOrderIntent.putExtra(SalesOrderConfirmationActivity.KEY_OUTLET, item);
        salesOrderIntent.putExtra(SalesOrderConfirmationActivity.KEY_DATE, dateString);
        context.startActivityForResult(salesOrderIntent, SALES_CONFIRMATION_UPDATE);
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.top = mVerticalSpaceHeight;
        }
    }
}
