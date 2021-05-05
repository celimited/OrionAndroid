package com.orion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.OrderItem;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SalesOrderConfirmationListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<OrderItem> itemList;
	private String TAG = "Trace";

	public SalesOrderConfirmationListAdapter(Context context) {
		this.context = context;
		itemList = new ArrayList<OrderItem>();
	}

	public int getCount() {
		return itemList.size();
	}

	public OrderItem getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView skuName;
		public TextView ctn;
		public TextView pcs;
		public TextView value;
	}

	public void addItem(OrderItem item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public void removeItem(OrderItem item) {
		itemList.remove(item);
		notifyDataSetChanged();
	}

	public double getTotalValue() {
		int i;
		double total = 0;
		for (i = 0; i < getCount(); i++) {
			double now = 0;
			try {
				now = itemList.get(i).Total;
			} catch (Exception e) {
				e.printStackTrace();
			}
			total += now;
		}

		Log.v(TAG, "Order summery count;" + getCount());
		Log.v(TAG, "Order summery total;" + total);
		return total;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = LayoutInflater.from(context).inflate(R.layout.sales_confirmation_list_item, null);
			holder = new ViewHolder();

			holder.skuName = (TextView) vi.findViewById(R.id.sku_name);
			holder.ctn = (TextView) vi.findViewById(R.id.ctn_value);
			holder.pcs = (TextView) vi.findViewById(R.id.pcs_value);
			holder.value = (TextView) vi.findViewById(R.id.value_value);
			holder.skuName.setSelected(true);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);

		holder.skuName.setText((itemList.get(position)).sku.title);
		holder.ctn.setText(Integer.toString((itemList.get(position)).Carton));
		holder.pcs.setText(Integer.toString((itemList.get(position)).Piece));
		holder.value.setText(numberFormat.format((itemList.get(position)).Total));
		return vi;
	}

	public void removeAll() {
		itemList.clear();
		notifyDataSetChanged();
	}
}