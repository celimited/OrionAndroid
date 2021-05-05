package com.orion.application;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.orion.adapter.SalesOrderListConfirmationAdapter;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.OrderItem;
import com.orion.entities.Outlet;
import com.orion.entities.User;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SalesOrderConfirmationActivity extends AppCompatActivity
        implements SalesOrderListConfirmationAdapter.OnListInteractionListener{


    static final int DATE_DIALOG_ID = 100;

    public static final String KEY_OUTLET = "outlet kay";
    public static final String KEY_DATE = "selected date key";

    private RecyclerView orderList;
    private SalesOrderListConfirmationAdapter orderListAdapter;
    private ArrayList<OrderItem> orderSummary;
    private Activity context;
    private NumberFormat numberFormat;
    private User user;
    private TextView totalValue;
    private Outlet selectedOutlet;
    private String selectedDate;
    private String TAG = "Trace";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_confirmation);
        Util.cancelWaitingDialog();
        context = SalesOrderConfirmationActivity.this;

        user = DatabaseQueryUtil.getUser(context);

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        orderList = (RecyclerView) findViewById(R.id.order_item_list_view);
        totalValue = ((TextView) findViewById(R.id.total_value));

        orderList.setLayoutManager(new LinearLayoutManager(SalesOrderConfirmationActivity.this));
        orderList.setItemAnimator(null);

        // populate data
        if (getIntent().hasExtra(KEY_OUTLET) && getIntent().hasExtra(KEY_DATE)){
            selectedOutlet = (Outlet)getIntent().getSerializableExtra(KEY_OUTLET);
            selectedDate = getIntent().getStringExtra(KEY_DATE);
            orderSummary = DatabaseQueryUtil.getVisitedOutletDetailsOnDate(context, selectedOutlet.outletId, selectedDate);

            Log.d(TAG, "SalesOrderConfirmationActivity:" + orderSummary.size());

            orderListAdapter = new SalesOrderListConfirmationAdapter(orderSummary, SalesOrderConfirmationActivity.this);
            orderList.setAdapter(orderListAdapter);

        }

        totalValue.setText(Double.toString(orderListAdapter.totalCalculation()));
    }

    @Override
    public void onListItemInteraction(OrderItem item) {
        Log.v(TAG, "Confirmed....");
        DatabaseQueryUtil.setVisitedOutletConfirmation(SalesOrderConfirmationActivity.this, item);
    }
}
