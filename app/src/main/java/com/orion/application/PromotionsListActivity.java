package com.orion.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Channel;
import com.orion.entities.Tpr;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.util.ArrayList;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class PromotionsListActivity extends DialogFragment implements MaterialTabListener {

	protected static final int VISIT_OUTLET = 200;
    private static final String TAG = "Trace";
	private Channel selectedChannel;
	private ArrayList<Channel> channelList;
	private ArrayList<Tpr> tprList;
	private FragmentActivity context;
	private int channelId;
	private int sectionId;


	private MaterialTabHost tabHost;
	private ViewPager viewPager;
	private ViewPagerAdapter androidAdapter;
	private User user;
	private Button btnClose;

	static PromotionsListActivity newInstance() {
        PromotionsListActivity frag = new PromotionsListActivity();
        Bundle args = new Bundle();
        args.putBoolean("isDialog", true);
        frag.setArguments(args);
        return frag;
    }

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.list_of_promotions_page_layout, container, false);

		Util.cancelWaitingDialog();
		context = getActivity();

		user = DatabaseQueryUtil.getUser(context);


		channelList = DatabaseQueryUtil.getChannelList(context);
		channelId = context.getIntent().getIntExtra(DatabaseConstants.tblChannel.CHANNEL_ID, -1);

		//tab host
		tabHost = (MaterialTabHost) rootView.findViewById( R.id.tabHostPromotions );
		viewPager = (ViewPager) rootView.findViewById( R.id.viewPager );
		btnClose = (Button)rootView.findViewById( R.id.btnClose );

		//adapter view
        boolean isDialog = getArguments().getBoolean("isDialog");
		if (isDialog){
			androidAdapter = new ViewPagerAdapter(getChildFragmentManager());
			btnClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					PromotionsListActivity.this.dismiss();
				}
			});
		}
        else{
			androidAdapter = new ViewPagerAdapter(context.getSupportFragmentManager());
			btnClose.setVisibility(View.GONE);
		}

        Log.v(TAG, "reached this position...." + (viewPager == null));

        viewPager.setAdapter(androidAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int tabposition) {
                tabHost.setSelectedNavigationItem(tabposition);
            }
        });


        //for tab position
        for (int i = 0; i < channelList.size(); i++) {

            tabHost.addTab(
                    tabHost.newTab()
                            .setText(channelList.get(i).name)
                            .setTabListener(this)
            );
        }
		return rootView;
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.list_of_promotions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_order){
			Intent intent = new Intent(context,
					OutletListPageActivity.class).putExtra(
					DatabaseConstants.tblDSRBasic.SECTION_ID, user.sectionId);
			context.startActivity(intent);
		}
		return true;
	}

	protected void updateTpListByChannel(Channel selectedChannel) {
		tprList = DatabaseQueryUtil.getTprFromTblTpr(context, selectedChannel.channelID);
	}

	/*
	@Override
	public void onBackPressed() {
		if (channelId == -1) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == VISIT_OUTLET) {
			//	Util.exportDatabaseToSdCard(context);
			finish();
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		//	Util.finalize(context);
		super.finish();
	}
	*/


    @Override
    public void onTabSelected(MaterialTab tab) {
        int num = tab.getPosition();
        viewPager.setCurrentItem(num);
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }


    PromotionsListFragment currentTabFragment;
	// view pager adapter
	private class ViewPagerAdapter extends FragmentStatePagerAdapter {


		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		public Fragment getItem(int num) {
			Log.v(TAG, "current market position: " + num);
//            selectedChannel = channelListAdapter.getItem(num);
            updateTpListByChannel(channelList.get(num));

			currentTabFragment = new PromotionsListFragment();
			currentTabFragment.setTprList(tprList);
			return currentTabFragment;
		}

		@Override
		public int getCount() {
			return channelList.size();
		}

		@Override
		public CharSequence getPageTitle(int tabposition) {
			return "Tab " + tabposition;
		}
	}




}