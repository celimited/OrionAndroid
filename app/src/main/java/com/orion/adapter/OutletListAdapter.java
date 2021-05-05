package com.orion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Outlet;
import com.orion.util.Util;

import java.util.ArrayList;

public class OutletListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Outlet> outletList;
    private String TAG = "Trace";

    public OutletListAdapter(Context context) {
        this.context = context;
        this.outletList = new ArrayList<>();
//        Log.v(TAG, "OutletListAdapter ");
    }

    public int getCount() {
//        Log.v(TAG, "OutletListAdapter getCount: " + outletList.size() );
        return outletList.size();
    }

    public Outlet getItem(int position) {
        return outletList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView outletName;
    }

    public void addItem(Outlet item) {
        Log.v(TAG, "addItem: " + item.description);
        outletList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(String item) {
//        Log.v(TAG, "removeItem");
        outletList.remove(item);
        notifyDataSetChanged();
    }

    public void removeAll() {
        outletList.clear();
        Log.v(TAG, "removeAll " + outletList.size());
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.outlet_list_item, parent, false);
            vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.7), (int) (Util.SCREEN_HEIGHT / 6.4)));
            holder = new ViewHolder();

//            Log.v(TAG, "Text is: " + outletList.get(position).description);
            holder.outletName = (TextView) vi.findViewById(R.id.outlet_name);
            vi.setTag(holder);
        } else {
//            Log.v(TAG, "Text nai ");
            holder = (ViewHolder) vi.getTag();
        }
//        Log.v(TAG, "Text is: " + getCount() + ":" + outletList.get(position).description + " (" + outletList.get(position).address + ")");
        holder.outletName.setText(outletList.get(position).description + " (" + outletList.get(position).address + ")");
        return vi;
    }
}