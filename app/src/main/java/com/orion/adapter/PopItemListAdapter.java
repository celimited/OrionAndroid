package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Item;
import com.orion.entities.PopItem;

import java.util.ArrayList;

public class PopItemListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PopItem> itemList;

	public PopItemListAdapter(Context context) {
		this.context = context;
		itemList = new ArrayList<PopItem>();
	}

	public int getCount() {
		return itemList.size();
	}

	public PopItem getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView name;
		public TextView last;
		public TextView now;
	}

	public void addItem(PopItem item) {
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
			vi = LayoutInflater.from(context).inflate(R.layout.pop_item, null);
			holder = new ViewHolder();

			holder.name = (TextView) vi.findViewById(R.id.pop_name);
			holder.last = (TextView) vi.findViewById(R.id.last_value);
			holder.now = (TextView) vi.findViewById(R.id.now_value);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		holder.name.setText((itemList.get(position)).description);
		holder.last.setText((itemList.get(position)).lastQty < 0 ? "" : Integer.toString((itemList.get(position)).lastQty));
		holder.now.setText((itemList.get(position)).currentQty < 0 ? "" : Integer.toString((itemList.get(position)).currentQty));
		return vi;
	}
}
    