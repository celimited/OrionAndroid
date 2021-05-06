package com.orion.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Section;
import com.orion.entities.User;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodaysStatusPageActivity extends Fragment
{
	private static final int VISITED = 1;
	private static final int NOT_VISITED = 0;
	public static final int TOTAL = 2;
	private String date;
	private double targetThisMonthValue;
	private double achievedTillDateValue;
	private double remainingTargetValue;
	private int remainingVisitValue;
	private double avgTargetVisitValue;
	private int totalOutletValue;
	private int visitedOutletValue;
	private int outletRemainedValue;
	private double achievedTodayValue;
	private double sRValue;
	private double lpcValue;
	private NumberFormat numberFormat;
	private String sectionId, routeId;
	private User user;
	private Activity context;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View rootView = inflater.inflate(R.layout.todays_status_page_layout, container, false);
		Util.cancelWaitingDialog();

		context = getActivity();

		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);

		initializeFields();

		((TextView) rootView.findViewById(R.id.date_value)).setText(date);
		((TextView) rootView.findViewById(R.id.target_this_month_value)).setText(numberFormat.format(targetThisMonthValue));
		((TextView) rootView.findViewById(R.id.achieved_till_date_value)).setText(numberFormat.format(achievedTillDateValue));
		((TextView) rootView.findViewById(R.id.remaining_target_value)).setText(numberFormat.format(remainingTargetValue));
		((TextView) rootView.findViewById(R.id.remaining_visit_value)).setText(Integer.toString(remainingVisitValue));
		((TextView) rootView.findViewById(R.id.avg_target_visit_value)).setText(avgTargetVisitValue == -1 ? "-" : numberFormat.format(avgTargetVisitValue));
		((TextView) rootView.findViewById(R.id.total_outlet_value)).setText(Integer.toString(totalOutletValue));
		((TextView) rootView.findViewById(R.id.outlet_remained_value)).setText(Integer.toString(outletRemainedValue));
		((TextView) rootView.findViewById(R.id.achieved_today_value)).setText(numberFormat.format(achievedTodayValue));
		((TextView) rootView.findViewById(R.id.visited_outlet_value)).setText(Integer.toString(visitedOutletValue));
		((TextView) rootView.findViewById(R.id.s_r_value)).setText(numberFormat.format(sRValue) + "%");
		((TextView) rootView.findViewById(R.id.lpc_value)).setText(numberFormat.format(lpcValue));
		return rootView;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.todays_status, menu);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initializeFields();
	}

	private void GetTodaysSection() {
		Section marketList;
		String sectionName;
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		String finalDay = null;
		try {
			date = format2.parse(user.visitDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(format1.format(date));
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		//	Date d = new Date();
		String dayOfTheWeek = sdf.format(date);
		marketList = DatabaseQueryUtil.getSection(context);
		if (marketList != null) {
			for (int i = 0; i < marketList.list.size(); i++) {
				if (user.sectionId != null && marketList.list.get(i).sectionId == user.sectionId) {
					sectionName = marketList.list.get(i).title;
					//sectionNameView.setText(sectionName);
					sectionId = marketList.list.get(i).sectionId;
					routeId = marketList.list.get(i).routeId;
					break;
				} else {
					String day = marketList.list.get(i).orderColDay;
					if (day.compareTo(dayOfTheWeek) == 0) {
						sectionName = marketList.list.get(i).title;
						//sectionNameView.setText(sectionName);
						sectionId = marketList.list.get(i).sectionId;
						routeId = marketList.list.get(i).routeId;
					}
				}
			}
		}
	}

	private void initializeFields() {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		date = sdf.format(c.getTime());
		user = DatabaseQueryUtil.getUser(context);
		GetTodaysSection();
		targetThisMonthValue = context.getIntent().getDoubleExtra(DatabaseConstants.tblDSRBasic.TARGET, -1);
		achievedTillDateValue = context.getIntent().getDoubleExtra(DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED, -1);
		remainingTargetValue = context.getIntent().getDoubleExtra(DatabaseConstants.tblDSRBasic.TARGET_REM, -1);
		remainingVisitValue = context.getIntent().getIntExtra(DatabaseConstants.tblDSRBasic.DAY_REMAIN, -1);
		avgTargetVisitValue = remainingVisitValue == 0 ? -1 : (remainingTargetValue / (double) remainingVisitValue);

		routeId = context.getIntent().getStringExtra(DatabaseConstants.tblSection.ROUTE_ID);
		sectionId = context.getIntent().getStringExtra(DatabaseConstants.tblDSRBasic.SECTION_ID);
		visitedOutletValue = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, routeId, VISITED);
		outletRemainedValue = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, routeId, NOT_VISITED);

		totalOutletValue = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, routeId, TOTAL);
		achievedTodayValue = DatabaseQueryUtil.getOrderTotalSumFromTblOrder(context, sectionId, user.visitDate );

		sRValue = totalOutletValue == 0 ? 0 : (visitedOutletValue * 100) / (double) (totalOutletValue);

		double totalOrder = DatabaseQueryUtil.getCountOfOrdersFromTblOrderItem(context, user.visitDate);
		double totalOutlet = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, VISITED, user.visitDate);
		lpcValue = totalOutlet == 0 ? 0 : totalOrder / totalOutlet;
	}
}