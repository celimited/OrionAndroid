package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.MarketReturnItem;

import java.util.ArrayList;

public class MarketReturnPageItemListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<MarketReturnItem> itemList;

	public MarketReturnPageItemListAdapter(Context context) {
		this.context = context;
		itemList = new ArrayList<MarketReturnItem>();
	}

	public int getCount() {
		return itemList.size();
	}

	public MarketReturnItem getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView skuName;
		public TextView quantity;
		public TextView reason;
		public TextView batch;
	}

	public void addItem(MarketReturnItem item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public void removeItem(MarketReturnItem item) {
		itemList.remove(item);
		notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = LayoutInflater.from(context).inflate(R.layout.market_return_page_item_list_item, null);
			holder = new ViewHolder();

			holder.skuName = (TextView) vi.findViewById(R.id.sku_name);
			holder.quantity = (TextView) vi.findViewById(R.id.qty_value);
			holder.reason = (TextView) vi.findViewById(R.id.reason_text);
			holder.batch = (TextView) vi.findViewById(R.id.batch_value);
			holder.skuName.setSelected(true);
			holder.reason.setSelected(true);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.skuName.setText((itemList.get(position)).skuName);
		holder.quantity.setText(Integer.toString((itemList.get(position)).qty));
		holder.reason.setText((itemList.get(position)).reasonDescription);
		holder.batch.setText((itemList.get(position)).batch);
		return vi;
	}
}
    