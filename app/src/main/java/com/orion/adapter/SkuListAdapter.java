package com.orion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Sku;
import com.orion.util.Util;

import java.util.ArrayList;

public class SkuListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Sku> skuList;

	public SkuListAdapter(Context context) {
        this.context = context;
        skuList = new ArrayList<Sku>();
    }

    public int getCount() {
        return skuList.size();
    }

    public Sku getItem(int position) {
        return skuList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public class ViewHolder {
        public TextView skuName;
    }

    public void addItem(Sku item) {
        skuList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(Sku item) {
        skuList.remove(item);
        notifyDataSetChanged();
    }

	public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.sku_grid_item, null);
            vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.7), (int) (Util.SCREEN_WIDTH / 3.7)));
            holder = new ViewHolder();

            holder.skuName = (TextView) vi.findViewById(R.id.sku_name);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.skuName.setText(skuList.get(position).title);
        return vi;
    }
 }
    