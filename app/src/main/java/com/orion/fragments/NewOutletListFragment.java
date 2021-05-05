package com.orion.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orion.adapter.NewOutletListRecyclerViewAdapter;
import com.orion.adapter.RecyclerItemClickListenerAdapter;
import com.orion.application.NewOutletActivity;
import com.orion.application.R;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Outlet;
import com.orion.entities.User;
import com.orion.webservice.Caller;

import java.util.ArrayList;

public class NewOutletListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int RC_GOT_NEW_OUTLET = 99;
    private static final int RC_GOT_Edit_OUTLET = 100;


    private int mColumnCount = 3;
    private OnListFragmentInteractionListener mListener;
    private FragmentActivity context;
    private ArrayList<Outlet> outletList = new ArrayList<>();
    private String TAG = "Trace";
    private NewOutletListRecyclerViewAdapter newOutletListAdapter;
    private ProgressDialog progressDialog;

    public NewOutletListFragment() {
    }

    public static NewOutletListFragment newInstance(int columnCount) {
        NewOutletListFragment fragment = new NewOutletListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        context = getActivity();

        /*if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_outlet_list_fragment, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.d(TAG, "NewOutletListFragment: " + outletList.size() + ", column count: " + mColumnCount);

            outletList = DatabaseQueryUtil.getNewOutletList(context);

            final Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(35));

//            recyclerView.setHasFixedSize(true);
//            mLayoutManager = new LinearLayoutManager(getActivity());
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mRecyclerView.setItemAnimator(new DefaultItemAnimator());


            newOutletListAdapter = new NewOutletListRecyclerViewAdapter(outletList, mListener);
            recyclerView.setAdapter(newOutletListAdapter);

            //Edit Option Added By Zakir on 7.9.16
            recyclerView.setClickable(true);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListenerAdapter(context, recyclerView, new RecyclerItemClickListenerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Outlet editTableOutlet = outletList.get(position);
                            Intent actionInt = new Intent(context, NewOutletActivity.class);
                            actionInt.putExtra("editable_outlet", editTableOutlet);
                            startActivityForResult(actionInt, RC_GOT_Edit_OUTLET);
                        }

                        @Override
                        public void onLongItemClick(View view, final int position) {
                            final Outlet deleteableOutlet = outletList.get(position);

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                            // Setting Dialog Title
                            alertDialog.setTitle("Confirm Delete...");

                            // Setting Dialog Message
                            alertDialog.setMessage("Are you sure you want delete this?");

                            // Setting Icon to Dialog
                            // alertDialog.setIcon(R.drawable.delete);

                            // Setting Positive "Yes" Button
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseQueryUtil.deleteNewOutlet(context, deleteableOutlet.outletId);

                                    outletList.clear();
                                    outletList.addAll(DatabaseQueryUtil.getNewOutletList(context));
                                    newOutletListAdapter.notifyDataSetChanged();
                                }
                            });

                            // Setting Negative "NO" Button
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "You cancel Delete Action", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            });

                            // Showing Alert Message
                            alertDialog.show();
                        }
                    })
            );

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "NewOutletListFragment::onAttach");

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_outlets, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_outlet) {
            Intent intent = new Intent(context, NewOutletActivity.class);
            startActivityForResult(intent, RC_GOT_NEW_OUTLET);
        } else if (item.getItemId() == R.id.action_upload) {
            //TODO upload functionalities
            User user = DatabaseQueryUtil.getUser(context);
            Caller caller = new Caller();

            //Progress Bar
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Uploading New Outlet Data...");
            progressDialog.show();

            caller.UploadNewOutletToWeb(context, NewOutletUploadHandler, user.mobile_No, user.password);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "List refresh 1 ");

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "List refresh 2 ");
            if (requestCode == RC_GOT_NEW_OUTLET) {
                // refresh outlet list
                outletList.clear();
                outletList.addAll(DatabaseQueryUtil.getNewOutletList(context));
                newOutletListAdapter.notifyDataSetChanged();
            }
            if (requestCode == RC_GOT_Edit_OUTLET) {
                // refresh outlet list
                outletList.clear();
                outletList.addAll(DatabaseQueryUtil.getNewOutletList(context));
                newOutletListAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Outlet item);
    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.top = mVerticalSpaceHeight;
        }
    }

    Handler NewOutletUploadHandler = new Handler() {
        public void handleMessage(Message msg) {

            progressDialog.dismiss();
            Toast.makeText(context,
                    "New Outlets Uploading Attempt Completed ",
                    Toast.LENGTH_SHORT).show();
            outletList.clear();
            outletList.addAll(DatabaseQueryUtil.getNewOutletList(context));
            newOutletListAdapter.notifyDataSetChanged();
        }
    };
}
