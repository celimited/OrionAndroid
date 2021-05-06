package com.orion.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.orion.adapter.OrderSummaryItemListByOrderAdapter;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.OrderItemsByOrderNo;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;

public class OrderSummaryBySkuActivity extends Fragment
{
    private ListView orderList;
    private OrderSummaryItemListByOrderAdapter orderListAdapter;
    private ArrayList<OrderItemsByOrderNo> orderSummary;
    private ArrayList<OrderItemsByOrderNo> orderSummaryFromTable;
    private Activity context;
    private NumberFormat numberFormat;
    private User user;
    private TextView totalValue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.order_summary_by_outlets, container, false);

        Util.cancelWaitingDialog();
        context = getActivity();

        user = DatabaseQueryUtil.getUser(context);

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        orderList = (ListView) rootView.findViewById(R.id.order_summary_by_outlets_list_view);
        orderListAdapter = new OrderSummaryItemListByOrderAdapter(context);

        orderList.setAdapter(orderListAdapter);

        totalValue = ((TextView) rootView.findViewById(R.id.order_summary_by_outlets_total_value));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateOrderList();
        totalValue.setText(numberFormat.format(orderListAdapter.getTotalValue()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.todays_status, menu);
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


    private void updateOrderList() {

        orderSummaryFromTable = DatabaseQueryUtil.getOrderSummaryFromTable(context);
        for (int i = 0; i < orderSummaryFromTable.size(); i++)
            orderListAdapter.addItem(orderSummaryFromTable.get(i));

        orderSummary = DatabaseQueryUtil.getOrderSummaryByOrderNo(context, user.visitDate);
        for (int i = 0; i < orderSummary.size(); i++)
            orderListAdapter.addItem(orderSummary.get(i));
    }
}