package com.orion.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.orion.adapter.OrderHistoryLastOrderNoGridAdapter;
import com.orion.adapter.OrderSummaryItemListAdapter;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.OrderItem;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrderHistoryActivity extends AppCompatActivity
{		
   	private ListView orderListView;
	private OrderSummaryItemListAdapter orderListAdapter;
	private String outletName;
	private String date;
	private GridView orderDate;
	private OrderHistoryLastOrderNoGridAdapter lastOrderNoAdapter;
	private ArrayList<OrderItem> orderList;
	private ArrayList<String>dateList;
	private int outletId;
	private Context context;
	private NumberFormat numberFormat;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_history_page_layout);
		context = this;

		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);

		initializeFields();

		((TextView) findViewById(R.id.outlet_name)).setText(outletName);

		orderListView = (ListView) findViewById(R.id.order_item_list_list_view);
		orderListAdapter = new OrderSummaryItemListAdapter(context);
		orderListView.setAdapter(orderListAdapter);

		((TextView) findViewById(R.id.total_value)).setText(numberFormat.format(orderListAdapter.getTotalValue()));

		orderDate = (GridView) findViewById(R.id.last_order_no_grid_view);
		lastOrderNoAdapter = new OrderHistoryLastOrderNoGridAdapter(context);
		orderDate.setAdapter(lastOrderNoAdapter);
		orderDate.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				updateOrderList(lastOrderNoAdapter.getItem(position));
				((TextView) findViewById(R.id.total_value)).setText(numberFormat.format(orderListAdapter.getTotalValue()));
			}
		});

		updateOrderNoList();
	}

	private void updateOrderNoList() {
		lastOrderNoAdapter.addItem("Today's Order");
		dateList = DatabaseQueryUtil.getOrderHistoryOrderDate(context, outletId);
		for (int i = 0; i < dateList.size(); i++)
			lastOrderNoAdapter.addItem(dateList.get(i));
//		lastOrderNoAdapter.addItem("1");
//		lastOrderNoAdapter.addItem("2");
//		lastOrderNoAdapter.addItem("3");
	}

	private void initializeFields() {
		outletName = getIntent().getStringExtra(DatabaseConstants.tblOutlet.DESCRIPTION);
		outletId = getIntent().getIntExtra(DatabaseConstants.tblOutlet.OUTLET_ID, 0);
	}

	private void updateOrderList(String orderDate) {
		orderListAdapter.removeAll();
		orderList = DatabaseQueryUtil.getOrderHistoryBySku(context, outletId, orderDate);
		for (int i = 0; i < orderList.size() && i < 3; i++)
			orderListAdapter.addItem(orderList.get(i));
		if (orderList.size() != 0) {
			date = DatabaseQueryUtil.getOrderHistoryOrderDate(context, outletId, orderDate);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.sss");
			Date realDate = Calendar.getInstance().getTime();
			try {
				realDate = sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sdf = new SimpleDateFormat("dd-MMM-yyyy");
			date = sdf.format(realDate);
			((TextView) findViewById(R.id.date_value)).setText(date);
		} else {
			((TextView) findViewById(R.id.date_value)).setText("-");
		}
	}
}