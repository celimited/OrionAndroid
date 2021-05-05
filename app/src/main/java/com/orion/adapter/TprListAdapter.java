package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Tpr;

import java.util.ArrayList;

public class TprListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Tpr> list;

	public TprListAdapter(Context context) {
		this.context = context;
		list = new ArrayList<Tpr>();
	}

	public int getCount() {
		return list.size();
	}

	public Tpr getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView itemName;
	}

	public void addItem(Tpr item) {
		list.add(item);
		notifyDataSetChanged();
	}

	public void removeItem(String item) {
		list.remove(item);
		notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = LayoutInflater.from(context).inflate(R.layout.list_view_item, null);
			holder = new ViewHolder();

			holder.itemName = (TextView) vi.findViewById(R.id.item_name);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		holder.itemName.setText(list.get(position).description);
		return vi;
	}

	public void removeAll() {
		list.clear();
		notifyDataSetChanged();
	}
}
    