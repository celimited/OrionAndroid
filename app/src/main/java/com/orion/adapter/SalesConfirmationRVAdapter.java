package com.orion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Outlet;
import com.orion.entities.OutletItem;

import java.util.List;

public class SalesConfirmationRVAdapter
        extends RecyclerView.Adapter<SalesConfirmationRVAdapter.ViewHolder> {

    private final List<OutletItem> mValues;
    private final OnListFragmentInteractionListener mListener;


    public SalesConfirmationRVAdapter(
            List<OutletItem> items,
            OnListFragmentInteractionListener listener) {

        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_outlet_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mOutletName.setText(mValues.get(position).description);
        holder.mOutleAddress.setText(mValues.get(position).address);
        holder.mOrderValue.setText( Double.toString( mValues.get(position).orderValue) );
        holder.mSaleValue.setText( Double.toString( mValues.get(position).saleValue) );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mOutletName;
        public final TextView mOutleAddress;
        public final TextView mOrderValue;
        public final TextView mSaleValue;
        public Outlet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOutletName = (TextView) view.findViewById(R.id.outlet_name);
            mOutleAddress = (TextView) view.findViewById(R.id.outlet_address);
            mOrderValue = (TextView) view.findViewById(R.id.tvOrderValue);
            mSaleValue = (TextView) view.findViewById(R.id.tvSalesValue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOutletName.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Outlet item);
    }


}
