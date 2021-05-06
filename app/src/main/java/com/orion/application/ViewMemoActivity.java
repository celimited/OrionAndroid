package com.orion.application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.orion.adapter.FreeProductItemListAdapter;
import com.orion.adapter.OrderSummaryItemListAdapter;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.FreeOrDiscount;
import com.orion.entities.OrderItem;
import com.orion.print.PrintActivity;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ViewMemoActivity extends AppCompatActivity
{
   	private ListView orderListView;
   	private ListView freeListView;
	private OrderSummaryItemListAdapter orderListAdapter;
	private FreeProductItemListAdapter freeListAdapter;
	private double totalValue;
	private double totalDiscountValue;
	private double netValue;
	private NumberFormat numberFormat;
	private Context context;
	private ArrayList<OrderItem> orderItem;
	private FreeOrDiscount freeOrDiscount;
	private String outletId;
	private String channelId;
	private String sectionID;

	String msg="";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_memo_page_layout);
		context = this;

		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);

		orderListView = (ListView) findViewById(R.id.order_item_list_list_view);
		orderListAdapter = new OrderSummaryItemListAdapter(context);
		orderListView.setAdapter(orderListAdapter);

		//freeListView = (ListView) findViewById(R.id.free_item_list_list_view);
		//freeListAdapter = new FreeProductItemListAdapter(context);
		//freeListView.setAdapter(freeListAdapter);
		initializeFields();

		((TextView) findViewById(R.id.gross_value)).setText(numberFormat.format(totalValue));
		((TextView) findViewById(R.id.discount_value)).setText(numberFormat.format(totalDiscountValue));
		((TextView) findViewById(R.id.net_value_value)).setText(numberFormat.format(netValue));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_memo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_print){
			print();
		}
		return true;
	}



	private void updateFreeList() {
		freeOrDiscount = DatabaseQueryUtil.getFreeOrDiscount(context, outletId, channelId, OutletVisitPageActivity.outletVisitPageActivity);
		for (int i = 0; i < freeOrDiscount.freeItemList.size(); i++) {
			freeListAdapter.addItem(freeOrDiscount.freeItemList.get(i));
		}
	}

	private void initializeFields() {
		outletId = getIntent().getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID);
		channelId = getIntent().getStringExtra(DatabaseConstants.tblOutlet.CHANNEL_ID);
		sectionID = getIntent().getStringExtra(DatabaseConstants.tblSection.SECTION_ID);
		updateOrderList();
		//updateFreeList();
		totalDiscountValue = freeOrDiscount.discount;
		totalValue = orderListAdapter.getTotalValue();
		netValue = totalValue - totalDiscountValue;
	}

	//region old code
//	private void updateOrderList()
//	{
//		orderItem = OutletVisitPageActivity.outletVisitPageActivity.getOrderedItemList();
//		for(int i = 0; i < orderItem.size(); i ++)
//		{
//			orderListAdapter.addItem(orderItem.get(i));
//			String sku[]=orderListAdapter.getItem(i).sku.title.toString().split(" ");
//			String skuItem="\n";
//			int space=0,j;
//			for ( j=0;j<sku.length;j++)
//			{
//				skuItem+=sku[j]+" ";
//				if(j%2!=0 && (sku.length-1!=j)) {
//					skuItem += "\n";
//				}
//			}
//			msg=msg+skuItem+" "+
//					orderListAdapter.getItem(i).Carton+"      "
//			+orderListAdapter.getItem(i).Piece+"     "+
//					orderListAdapter.getItem(i).Total;
//
//		}
//		msg =msg+"\n -----------------------------";
//		msg =msg+"\n Total :"+numberFormat.format(totalValue).toString();
//		msg =msg+"\n Less Discount:";
//		msg =msg+"\n Net : "+numberFormat.format(netValue).toString();
//	}
//endregion



	private void updateOrderList() {
		freeOrDiscount = DatabaseQueryUtil.getFreeOrDiscount(context, outletId, channelId, OutletVisitPageActivity.outletVisitPageActivity);
		orderItem = OutletVisitPageActivity.outletVisitPageActivity.getOrderedItemList();
        Collections.sort(orderItem, new Comparator<OrderItem>() {
            public int compare(OrderItem v1, OrderItem v2) {
                return v1.sku.title.compareTo(v2.sku.title);
            }
        });


		for (int i = 0; i < orderItem.size(); i++) {
			orderListAdapter.addItem(orderItem.get(i));
			String sku[] = orderListAdapter.getItem(i).sku.title.toString().split(" ");
			String skuItem = "\n";
			int space = 0, j;
			for (j = 0; j < sku.length; j++) {
				skuItem += sku[j] + " ";
				if (j % 2 != 0 && (sku.length - 1 != j)) {
					skuItem += "\n";
				}
			}
			msg = msg + skuItem + " " +
					orderListAdapter.getItem(i).Carton + "-"
					+ orderListAdapter.getItem(i).Piece + "     " + orderListAdapter.getItem(i).sku.pcsRate + "    " +
					orderListAdapter.getItem(i).Total;

		}
		msg = msg + "\n------------------------------";
		msg = msg + "\n Total :" + orderListAdapter.getTotalValue();
		msg = msg + "\n Less Discount:" + freeOrDiscount.discount;
		msg = msg + "\n Net : " + (orderListAdapter.getTotalValue() - (freeOrDiscount.discount));
	}

	public void print() {
		Intent intent = new Intent(this.getApplicationContext(), PrintActivity.class);
		intent.putExtra("printContent", msg);
		intent.putExtra("outletID", outletId);
		intent.putExtra("sectionID", sectionID);//sectionID
		startActivity(intent);
	}
}