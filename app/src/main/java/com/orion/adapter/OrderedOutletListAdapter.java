package com.orion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Outlet;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;

public class OrderedOutletListAdapter extends ArrayAdapter<Outlet> {
    private static final String TAG = "Trace";
    private Context context;
    private ArrayList<Outlet> outletList;

    public OrderedOutletListAdapter(Context context, int layout, ArrayList outletList) {
        super(context, layout, outletList);
        Log.v(TAG, "OrderedOutletListAdapter()");
        this.context = context;
        this.outletList = outletList;
    }

    public int getCount() {
        return (outletList == null )? 0 : outletList.size();
    }

    public Outlet getItem(int position) {
        return outletList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView outletName;
        public TextView orderValue;
    }

    public void addItem(Outlet item) {
        outletList.add(item);
    }

    public void removeItem(String item) {
        outletList.remove(item);
        this.notifyDataSetChanged();
    }

    public void removeAll() {
        Log.v(TAG, "removeAll()" );
        outletList.clear();
        this.notifyDataSetChanged();
    }

    public void updateList(ArrayList newList){
        outletList.clear();
        outletList.addAll(newList);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.outlet_list_item, null);
            //vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH /2), (int) (Util.SCREEN_WIDTH / 3.1)));
            // Width - Height
            //vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.1), ViewGroup.LayoutParams.WRAP_CONTENT));
            vi.setLayoutParams(new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.1), (int) (Util.SCREEN_WIDTH / 3.1)));
            holder = new ViewHolder();

            holder.outletName = (TextView) vi.findViewById(R.id.outlet_name);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

//        Log.v(TAG, "view text: " + getItem(position).description);

        holder.outletName.setText(outletList.get(position).description +"\n"+ "[" +outletList.get(position).address +"]");
        holder.outletName.setTextSize(13);

        //holder.outletName.setText(outletList.get(position).description +"\n"+ "[" +outletList.get(position).outletId +"]"
                //+"\n"+ "[" +outletList.get(position).address +"]");
        //	holder.orderValue.setText( numberFormat.format((double)(outletList.get(position).orderTotal)));
        //vi.height
        return vi;
    }
}
    