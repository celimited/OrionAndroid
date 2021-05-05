package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.FreeItem;
import com.orion.entities.OrderItem;

import java.util.ArrayList;

public class FreeProductItemListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<FreeItem> itemList;

	public FreeProductItemListAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<FreeItem>();
    }

    public int getCount() {
        return itemList.size();
    }

    public FreeItem getItem(int position) {
        return itemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public class ViewHolder {
        public TextView skuName;
        public TextView qty;
    }

    public void addItem(FreeItem item) {
        itemList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(OrderItem item) {
        itemList.remove(item);
        notifyDataSetChanged();
    }

	public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.free_item, null);
            holder = new ViewHolder();

            holder.skuName = (TextView) vi.findViewById(R.id.sku_name);
            holder.qty = (TextView) vi.findViewById(R.id.qty_value);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.skuName.setText((itemList.get(position)).skuName);
        holder.qty.setText(Integer.toString((itemList.get(position)).qty));
        return vi;
    }
 }
    