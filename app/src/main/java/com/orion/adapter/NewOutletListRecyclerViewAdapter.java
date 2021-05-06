package com.orion.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.orion.application.R;
import com.orion.entities.Outlet;
import com.orion.fragments.NewOutletListFragment;
import com.orion.util.Util;

import java.util.List;

public class NewOutletListRecyclerViewAdapter
        extends RecyclerView.Adapter<NewOutletListRecyclerViewAdapter.ViewHolder> {

    private final List<Outlet> mValues;
    private final NewOutletListFragment.OnListFragmentInteractionListener mListener;

    private GridView.LayoutParams layoutParams;

    public NewOutletListRecyclerViewAdapter(
            List<Outlet> items,
            NewOutletListFragment.OnListFragmentInteractionListener listener) {

        mValues = items;
        mListener = listener;

        layoutParams = new GridView.LayoutParams((int) (Util.SCREEN_WIDTH / 3.7),
                (int) (Util.SCREEN_WIDTH / 3.7));


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.outlet_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {



        holder.mItem = mValues.get(position);
        holder.mOutletName.setText(mValues.get(position).description);
        holder.mView.setLayoutParams( layoutParams );

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
        public Outlet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOutletName = (TextView) view.findViewById(R.id.outlet_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOutletName.getText() + "'";
        }
    }


}
