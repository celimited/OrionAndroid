package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Item;
import com.orion.util.Util;

import java.util.ArrayList;

public class OrderHistoryLastOrderNoGridAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> itemList;

	public OrderHistoryLastOrderNoGridAdapter(Context context) {
		this.context = context;
		itemList = new ArrayList<String>();
	}

	public int getCount() {
		return itemList.size();
	}

	public String getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView itemName;
	}

	public void addItem(String item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public void removeItem(Item item) {
		itemList.remove(item);
		notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = LayoutInflater.from(context).inflate(R.layout.order_history_last_order_no_item, null);
			vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.7), (int) (Util.SCREEN_HEIGHT / 6.4)));
			holder = new ViewHolder();

			holder.itemName = (TextView) vi.findViewById(R.id.item_name);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		holder.itemName.setText(itemList.get(position));
		return vi;
	}

	public void removeAll() {
		itemList.clear();
		notifyDataSetChanged();
	}
}
    