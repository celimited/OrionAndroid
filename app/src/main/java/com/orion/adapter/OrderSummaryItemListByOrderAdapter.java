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
import com.orion.entities.OrderItemsByOrderNo;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by atiqur on 1/23/2017.
 */

public class OrderSummaryItemListByOrderAdapter extends BaseAdapter {



    private Context context;
    private ArrayList<OrderItemsByOrderNo> itemList;
    private String TAG = "Trace";

    public OrderSummaryItemListByOrderAdapter(Context context) {
        this.context = context;
        itemList = new ArrayList<OrderItemsByOrderNo>();
    }

    public int getCount() {
        return itemList.size();
    }

    public OrderItemsByOrderNo getItem(int position) {
        return itemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView txtOrderNo;
        public TextView txtOutletName;
        public TextView txtTotal;
        public TextView txtSentStatus;
    }

    public void addItem(OrderItemsByOrderNo item) {
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
                now = itemList.get(i).getTotal();
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
            vi = LayoutInflater.from(context).inflate(R.layout.order_summary_by_outlets_single_item, null);
            holder = new ViewHolder();

            holder.txtOrderNo = (TextView) vi.findViewById(R.id.order_summary_by_outlets_single_item_txtOrderNo);
            holder.txtOutletName = (TextView) vi.findViewById(R.id.order_summary_by_outlets_single_item_txtOutletName);
            holder.txtTotal = (TextView) vi.findViewById(R.id.order_summary_by_outlets_single_item_txtTotal);
            holder.txtSentStatus = (TextView) vi.findViewById(R.id.order_summary_by_outlets_single_item_txtSentStatus);
            holder.txtOrderNo.setSelected(true);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        String sentStatus;
        if(itemList.get(position).getSentStatus() == 0) sentStatus = "No"; else sentStatus = "Yes";

        holder.txtOrderNo.setText(String.valueOf((itemList.get(position)).getOrderNo()));
        holder.txtOutletName.setText(itemList.get(position).getOutletName());
        holder.txtTotal.setText(String.valueOf(numberFormat.format(itemList.get(position).getTotal())));
        holder.txtSentStatus.setText(sentStatus);


        return vi;
    }

    public void removeAll() {
        itemList.clear();
        notifyDataSetChanged();
    }
}
