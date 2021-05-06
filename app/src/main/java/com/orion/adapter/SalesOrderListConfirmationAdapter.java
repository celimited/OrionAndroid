package com.orion.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.OrderItem;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SalesOrderListConfirmationAdapter extends
        SelectableAdapter<SalesOrderListConfirmationAdapter.ViewHolder> {

    private final ArrayList<OrderItem> mValues;
    private final OnListInteractionListener mListener;
    private String TAG = "Trace";

    public SalesOrderListConfirmationAdapter(ArrayList<OrderItem> items, OnListInteractionListener listener) {
        Log.d(TAG, "SalesOrderListConfirmationAdapter::SalesOrderListConfirmationAdapter " );
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_confirmation_list_item, parent, false);
        Log.d(TAG, "SalesOrderListConfirmationAdapter::onCreateViewHolder " );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        Log.d(TAG, "item title: " + (mValues.get(position)).sku.title);

        holder.skuName.setText((mValues.get(position)).sku.title);
        holder.ctn.setText(Integer.toString((mValues.get(position)).Carton));
        holder.pcs.setText(Integer.toString((mValues.get(position)).Piece));

        //edited by abrar
        /*if ((mValues.get(position)).soldCartons == 0){
            holder.ctnSold.setText(Integer.toString((mValues.get(position)).Carton));
            mValues.get(position).soldCartons = mValues.get(position).Carton;
        }
        else{
            holder.ctnSold.setText(Integer.toString((mValues.get(position)).soldCartons));
        }
        if ((mValues.get(position)).soldPieces == 0){
            holder.pcsSold.setText(Integer.toString((mValues.get(position)).Piece));
            mValues.get(position).soldPieces = mValues.get(position).Piece;
        }
        else{
            holder.pcsSold.setText(Integer.toString((mValues.get(position)).soldPieces));
        }*/

        holder.ctnSold.setText(Integer.toString((mValues.get(position)).soldCartons));
        holder.pcsSold.setText(Integer.toString((mValues.get(position)).soldPieces));

        holder.etCtn.setText(Integer.toString((mValues.get(position)).Carton));
        holder.etPcs.setText(Integer.toString((mValues.get(position)).Piece));
        holder.etReplace.setText("0");
        holder.value.setText(numberFormat.format((mValues.get(position)).Total));

/*        if (isSelected(position)){
            holder.mView.setBackgroundResource(R.color.theme_color1);
            holder.mIdView.setTextColor( 0xFFFFFFFF );
            holder.mContentView.setTextColor( 0xFFFFFFFF );
            holder.mContentView1.setTextColor( 0xFFFFFFFF );
            holder.mContentView2.setTextColor( 0xFFFFFFFF );
        }else {
            holder.mView.setBackgroundResource(R.color.white);
            holder.mIdView.setTextColor( 0xFF000000 );
            holder.mContentView.setTextColor( 0xFF000000 );
            holder.mContentView1.setTextColor( 0xFF000000 );
            holder.mContentView2.setTextColor( 0xFF000000 );
        }*/

        holder.mMainLayout.setOnClickListener(new SalesOrderItemListener(holder, position));
        holder.btnCancel.setOnClickListener(new SalesOrderItemListener(holder, position));
        holder.btnConfirm.setOnClickListener(new SalesOrderItemListener(holder, position));

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "SalesOrderListConfirmationAdapter::getItemCount() " + mValues.size());
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final View mExtendedView;
        public final View mMainLayout;
        public final TextView skuName;
        public final TextView ctn;
        public final TextView pcs;
        public final TextView ctnSold;
        public final TextView pcsSold;
        public final TextView value;
        public final EditText etCtn;
        public final EditText etPcs;
        public final EditText etReplace;
        public final Button btnConfirm;
        public final Button btnCancel;
        public OrderItem mItem;

        public ViewHolder(View view) {
            super(view);
            Log.d(TAG, "SalesOrderListConfirmationAdapter::ViewHolder ");
            mView = view;
            mExtendedView = view.findViewById(R.id.extendedLayout);
            mMainLayout = view.findViewById(R.id.mainLayout);
            skuName = (TextView) view.findViewById(R.id.sku_name);
            ctn = (TextView) view.findViewById(R.id.ctn_value);
            pcs = (TextView) view.findViewById(R.id.pcs_value);
            ctnSold = (TextView) view.findViewById(R.id.ctn_sold_value);
            pcsSold = (TextView) view.findViewById(R.id.pcs_sold_value);
            value = (TextView) view.findViewById(R.id.value_value);
            etCtn = (EditText)view.findViewById(R.id.etCtn);
            etPcs = (EditText)view.findViewById(R.id.etPcs);
            etReplace = (EditText) view.findViewById(R.id.etReplace);

            btnCancel = (Button)view.findViewById(R.id.btnCancel);
            btnConfirm = (Button)view.findViewById(R.id.btnConfirm);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + skuName.getText() + "'";
        }
    }

    private class SalesOrderItemListener implements View.OnClickListener{

        private final ViewHolder holder;
        private final int position;

        public SalesOrderItemListener(ViewHolder holder, int position){
            this.holder = holder;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            Log.v("Trace", "Clicked on item " + getSelectedItemCount());

            if (id == R.id.btnCancel){
                holder.mExtendedView.setVisibility(View.GONE);
            }else if (id == R.id.btnConfirm){
                holder.mItem.replacePieces = Integer.parseInt(holder.etReplace.getText().toString());
                holder.mItem.soldPieces = Integer.parseInt(holder.etPcs.getText().toString());
                holder.mItem.soldCartons = Integer.parseInt(holder.etCtn.getText().toString());
                //valueNumber = ctnNumber * ctnRate + pcsNumber * pcsRate;
                holder.mItem.soldTotal = Integer.parseInt(holder.etCtn.getText().toString()) * holder.mItem.sku.ctnRate +  Integer.parseInt(holder.etPcs.getText().toString()) * holder.mItem.sku.pcsRate;

                mListener.onListItemInteraction(holder.mItem);
                holder.mExtendedView.setVisibility(View.GONE);

                holder.ctnSold.setText(Integer.toString((mValues.get(position)).soldCartons));
                holder.pcsSold.setText(Integer.toString((mValues.get(position)).soldPieces));


            }else if (id == R.id.mainLayout){
                clearSelection();
                toggleSelection(position);

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                }
                holder.mExtendedView.setVisibility(View.VISIBLE);
            }
        }
    }

    public double totalCalculation(){
        double sum = 0;
        for (OrderItem anItem  : this.mValues) {
            sum += anItem.Total;
        }
        return sum;
    }


    public interface OnListInteractionListener {
        void onListItemInteraction(OrderItem item);
    }
}
