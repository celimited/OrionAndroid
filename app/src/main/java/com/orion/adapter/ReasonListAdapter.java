package com.orion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orion.entities.Reason;

import java.util.ArrayList;

public class ReasonListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Reason> list;

    public ReasonListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<Reason>();
    }

    public int getCount() {
        return list.size();
    }

    public Reason getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void addItem(Reason item) {
        list.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(String item) {
        list.remove(item);
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public TextView itemName;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView itemName = new TextView(context);
        itemName.setTextColor(Color.BLACK);
        itemName.setText(list.get(position).description);

        return itemName;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
            holder = new ViewHolder();

            holder.itemName = (TextView) vi.findViewById(android.R.id.text1);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.itemName.setText(list.get(position).description);
        return vi;
    }
}