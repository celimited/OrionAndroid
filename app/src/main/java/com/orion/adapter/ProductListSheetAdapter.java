package com.orion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.OrderItem;

import java.util.List;

/**
 * Created by Günhan on 28.02.2016.
 */
public class ProductListSheetAdapter extends RecyclerView.Adapter<ProductListSheetAdapter.ItemHolder> {
    private List<OrderItem> list;
    private OnItemClickListener onItemClickListener;

    public ProductListSheetAdapter(List<OrderItem> list) {
        this.list = list;
    }

    @Override
    public ProductListSheetAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordered_item_layout, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ProductListSheetAdapter.ItemHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ItemHolder item, int position);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProductListSheetAdapter adapter;
        TextView textView;
        TextView itemAmount;

        public ItemHolder(View itemView, ProductListSheetAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.adapter = parent;

            itemAmount = (TextView) itemView.findViewById(R.id.item_amount);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }

        public void bind(OrderItem item) {
            textView.setText( item.promoStatus + item.sku.title );
            itemAmount.setText(item.Carton + " C   " + item.Piece + " P");
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = adapter.getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getAdapterPosition());
            }
        }
    }
}
