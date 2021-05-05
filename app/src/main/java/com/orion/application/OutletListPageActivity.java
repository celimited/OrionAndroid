package com.orion.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Market;
import com.orion.entities.Section;
import com.orion.entities.User;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class OutletListPageActivity extends AppCompatActivity implements MaterialTabListener{

	private String TAG = "Trace";

	protected static final int VISIT_OUTLET = 200;
	private User user;
	private Section marketList;
	private double orderValue;
	private double lpcValue;
	public String sectionId;
	private String routeId;
	private static final int EDIT_NOT_ALLOWED = 0;

	private Context context;
	private int positionOfDefaultMarket;
    private Market selectedMarket;
	private TextView orderValueTextView;
	private TextView LPCValueView;
	private NumberFormat numberFormat;

	private SharedPreferences sharedpreferences;
	private String previousLatitudeKey = "PreviousLatitudeKey";
	private String previousLongitudeKey = "PreviousLongitudeKey";

	public static boolean isOrderLocationRecorded = false;

    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private ViewPagerAdapter androidAdapter;
    private TextView countOfOutlet;
	private String outlet;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outlet_list_page_layout);
		context = this;
		user = DatabaseQueryUtil.getUser(context);
		user.WillTrackGPS = 1;
		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		sharedpreferences = getSharedPreferences("PreviousStoredLocation",
				Context.MODE_PRIVATE);

		orderValueTextView = (TextView) findViewById(R.id.order_value);
		LPCValueView = (TextView) findViewById(R.id.lpc_value);

		initializeFields(0);

		//tab host
		tabHost = (MaterialTabHost) findViewById(R.id.tabHostMaterial);
		viewPager = (ViewPager) findViewById(R.id.outletListViewPager);

		//adapter view
		androidAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(androidAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int tabposition) {
				tabHost.setSelectedNavigationItem(tabposition);
			}
		});


		//for tab position
		for (int i = 0; i < marketList.list.size(); i++) {
			tabHost.addTab(
					tabHost.newTab()
							.setText(marketList.list.get(i).title)
							.setTabListener(this));

		}
	}

	@Override
	protected void onResume() {
		initializeFields(1);
		super.onResume();
	}


	private void initializeFields(int onResume) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
			Date d = new Date();
			String dayOfTheWeek = sdf.format(d);
			int flag = 0;
			updateMarketList();
			if (onResume != 1) {
				for (int i = 0; i < marketList.list.size(); i++) {
					if (user.sectionId != null && Objects.equals(marketList.list.get(i).sectionId, user.sectionId)) {
						positionOfDefaultMarket = i;
						selectedMarket = marketList.list.get(i);
						flag++;
						break;
					} else {
						String day = marketList.list.get(i).orderColDay;
						if (day.compareTo(dayOfTheWeek) == 0) {
							positionOfDefaultMarket = i;
							selectedMarket = marketList.list.get(i);
							flag++;
						}
					}
				}
				if(flag == 0) {
					positionOfDefaultMarket = 0;
					selectedMarket = marketList.list.get(0);
				}
			}
            this.sectionId = selectedMarket.sectionId;
            this.routeId = selectedMarket.routeId;
            orderValue = DatabaseQueryUtil.getOrderTotalSumFromTblOrder(
                    context, sectionId, user.visitDate);

            double totalOrder = DatabaseQueryUtil
                    .getCountOfOrdersFromTblOrderItem(context, user.visitDate);
            double totalOutlet = DatabaseQueryUtil
                    .getCountOfOutletFromTblOutlet(context, OutletListFragment.YET_TO_VISIT, user.visitDate);
            lpcValue = totalOutlet == 0 ? 0 : totalOrder / totalOutlet;
			orderValueTextView.setText(numberFormat.format(orderValue));
            LPCValueView.setText(numberFormat.format(lpcValue));

		}catch (Exception e) {
			e.printStackTrace();
		}
//		updateOutletList(selectedMarket, visitStatus);
	}

	private void updateMarketList() {
//		marketListAdapter.removeAll();
		marketList = DatabaseQueryUtil.getSection(context);
//		for (int i = 0; i < marketList.list.size(); i++) {
//			marketListAdapter.addItem(marketList.list.get(i));
//		}
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
//		Util.showWaitingDialog(context);
//		Intent intent = new Intent(OutletListPageActivity.this, HomePageActivity.class).putExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE, user.visitDate);
//		Util.cancelWaitingDialog();
//		startActivity(intent);
//		this.finish();
	}

	@Override
	public void finish() {
		Util.cancelWaitingDialog();
        Util.finalize(context);
		super.finish();
	}

	private void searchOutlet(String name)
	{
		androidAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(androidAdapter);
	}


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
		viewPager.clearOnPageChangeListeners();
	}


    // view pager adapter
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
		private String outletName;


		public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int num) {

			Log.v(TAG, "current market position: " + num);

			OutletListFragment currentTabFragment = new OutletListFragment();
			currentTabFragment.setUser(user);
			currentTabFragment.setSelectedMarket(marketList.list.get(num));
			positionOfDefaultMarket = num;

			return currentTabFragment;
        }

        @Override
        public int getCount() {
            return marketList.list.size();
        }

        @Override
        public CharSequence getPageTitle(int tabposition) {
            return "Tab " + tabposition;
        }
    }


}
