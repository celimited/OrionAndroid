package com.orion.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.common.CommonFunction;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Order;
import com.orion.entities.Section;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;
import com.orion.webservice.Caller;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomePageActivity extends Fragment {

    private static final String TAG = "HomePageActivity";

	private User user;
	private double todaysOrder;
	private double LPCValue;
	private double remainingValue;
	private int outletRemainingValue;
	private String sWillUploadOrderToWeb;
	private String sWillTrackGPS;
	private String lastUpdateTime;
	private String visitDate;
	private int dateFlag = 0;
	private String sectionID = null;
	private String routeID = null;
	private Activity context;

	private TextView userNameView;
	private TextView sectionNameView;
	private TextView todaysOrderValueView;
	private TextView LPCValueView;
	private TextView todaysTargetValueView;
	private TextView remainingValueView;
	private TextView outletRemainingValueView;
	private TextView dateView;
	private TextView willuploadorderonline;
	private TextView willtrackgps;
	private TextView txtDepotName;
	private TextView txtTerritoryName;

	Button OrderButton;
//	Button OrderCompleteButton;
//	Button UploadOrder;

	private NumberFormat numberFormat;
	public static final int VISITED = 1;
	public static final int NOT_VISITED = 0;
	public static  boolean status;
	private Handler handler;
	private MenuItem menuItemUpload;

	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.home_page_layout, container, false);

        context = getActivity();
        user = DatabaseQueryUtil.getUser(context);

        Util.initialize(context);


//		if (DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG == 0) {
//			return;
//		}

        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
//		lastUpdateTime = getIntent().getStringExtra(DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME);
        visitDate =  getActivity().getIntent().getStringExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE);
        userNameView = (TextView) rootView.findViewById(R.id.user_name);
        sectionNameView = (TextView) rootView.findViewById(R.id.section_name);
        dateView = (TextView) rootView.findViewById(R.id.date_value);
        todaysOrderValueView = (TextView) rootView.findViewById(R.id.todays_order_value);
        LPCValueView = (TextView) rootView.findViewById(R.id.LPC_value);
		todaysTargetValueView = (TextView) rootView.findViewById(R.id.todays_target_value);
		remainingValueView = (TextView) rootView.findViewById(R.id.remaining_value);
        outletRemainingValueView = (TextView) rootView.findViewById(R.id.outlet_remaining_value);
        willuploadorderonline = (TextView) rootView.findViewById(R.id.will_upload_online_order);
        willtrackgps = (TextView) rootView.findViewById(R.id.will_track_gps);
        OrderButton = (Button) rootView.findViewById(R.id.order_button);
        //	OrderCompleteButton = (Button) findViewById(R.id.order_Complete_button);

        OrderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                orderButtonAction();
            }
        });

//
//		((Button) findViewById(R.id.order_Complete_button))
//		.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				*//*
//				 * Previous Code
//				 *//*
//				//orderCompleteButtonAction();
//
//				*//*
//				 * Added by Kayum Hossan on 15th Feb 2015
//				 *//*
//				if (DatabaseQueryUtil.isAllStoreVisited(HomePageActivity.this)) {
//					orderCompleteButtonAction();
//				} else {
//
//					allStoreNotVisitedDialog();
//				}
//			}
//		});
        ((MainNavigationActivity)context).setUser(user);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		txtDepotName = (TextView) context.findViewById(R.id.txtDepoName);
		txtTerritoryName = (TextView) context.findViewById(R.id.outlet_territory_name);

        initializeFields();
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.

		inflater.inflate(R.menu.main, menu);
		menuItemUpload = menu.findItem(R.id.upload_number);
		PendingOrdersChecking();

        super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.upload_number){
			orderUploadButtonAction();
		}

		return super.onOptionsItemSelected(item);
	}

	private void initializeFields() {
		DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 0;
		user = DatabaseQueryUtil.getUser(context);
		todaysOrder = DatabaseQueryUtil.getOrderTotalSumFromTblOrder(context, user.sectionId, user.visitDate);

		double totalOrder = DatabaseQueryUtil.getCountOfOrdersFromTblOrderItem(context, user.visitDate);

		/**
		 * Show Depot Name in Home
		 */
		String depotName = DatabaseQueryUtil.GetDepotName(context);
		//txtDepotName.setText(depotName);
		txtTerritoryName.setText(depotName); //Information Missmatch..... depotName is actually Territory name.

		/**
		 * RouteID isn't part of Orion's business (Sumi)
		 * Following commented method needs an overload.
		 */
		//double totalOutlet = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, routeID, VISITED);\
		double totalOutlet = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(context, VISITED, user.visitDate);
		LPCValue = totalOutlet == 0 ? 0 : totalOrder / totalOutlet;

		outletRemainingValue = DatabaseQueryUtil.getCountOfOutletFromTblOutlet(
				context, routeID, NOT_VISITED);
		userNameView.setText(user.name);
		sWillUploadOrderToWeb = "YES";
		sWillTrackGPS = "YES";
		remainingValue = user.dailyTarget - todaysOrder;
//		sectionNameView.setText(sectionName);
		todaysTargetValueView.setText(numberFormat.format(user.dailyTarget));
		remainingValueView.setText(numberFormat.format(remainingValue));
//		sectionName = DatabaseQueryUtil.getSection(context).defaultUserMarket.title;
//		section = DatabaseQueryUtil.getSection(context);
		willuploadorderonline.setText(sWillUploadOrderToWeb);
		willtrackgps.setText(sWillTrackGPS);

		dateFlag = context.getIntent().getIntExtra("visitDateFlag",0);
		System.out.println(user.visitDate);
		dateView.setText(visitDate);
		todaysOrderValueView.setText(numberFormat.format(todaysOrder));
		LPCValueView.setText(numberFormat.format(LPCValue));
		outletRemainingValueView.setText(Integer.toString(outletRemainingValue));
		OrderButton = (Button) context.findViewById(R.id.order_button);
		//OrderCompleteButton = (Button) findViewById(R.id.order_Complete_button);
		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());


		if (visitDate != null && !visitDate.equals(date) && dateFlag == 0)
		{
			AlertDialog.Builder visitDateErrorMessage = new AlertDialog.Builder(
					context);
			visitDateErrorMessage.setTitle("Warning!!");
			visitDateErrorMessage
					.setMessage("Your operation date doesn't match with your system date. Please click Ok to adjust the visit date.")
					.setIcon(android.R.drawable.ic_dialog_alert);

			visitDateErrorMessage.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
							Util.showWaitingDialog(context);
							Intent intent = new Intent(context,
									VisitDatePageActivity.class).putExtra(
									DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME, user.lastUpdateTime)
									.putExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE, user.visitDate);
							startActivity(intent);
							finish();
						}
					}).setNegativeButton(R.string.alert_dialog_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							//TODO Checking current date disabled
//							OrderButton.setEnabled(false);
//							UploadOrder.setEnabled(false);
//							menuItemUpload.setVisible(false);
//							OrderCompleteButton.setEnabled(false);
							dialog.cancel();
						}
					});
			visitDateErrorMessage.show();
		}

		GetTodaysSection(user.visitDate);
	//	downloadSalesPromotion();
//		else if(!visitDate.equals(date)&& dateFlag == 1 )
		OrderCompletionCheck();

		/**
		 * The following method needs to be called after on onCreateOptionsMenu
		 * as button isn't defined yet.
		 */
		//PendingOrdersChecking();
	}

	public void onResume() {
        Log.v(TAG, "onResume");

		user = DatabaseQueryUtil.getUser(context);
		dateView.setText(user.visitDate);

        if (DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG == 0) {
//			initializeFields();
			super.onResume();
			return;
		}

        if (DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG == 1){
			super.onResume();
			return;
	    }

        initializeFields();
		super.onResume();
	}

	public void downloadSalesPromotion() {
		boolean internet = new CommonFunction().isInternetOn(context);
		user = DatabaseQueryUtil.getUser(context);
		String date;
		date = user.visitDate;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
		Date dt = null;
		try {
			dt = sdf1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String visitDate = sdf2.format(dt);
		if(internet) {
			Caller caller = new Caller();
			caller.GetSalesPromotionsFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
			caller.GetSPChannelSKUFromWeb(context, null,  user.mobile_No, user.password, user.company_ID, visitDate);
			caller.GetSPSlabFromWeb(context, null,  user.mobile_No, user.password, user.company_ID, visitDate);
			caller.GetSPBonusesFromWeb(context, null,  user.mobile_No, user.password, user.company_ID, visitDate);
			caller.GetDSRDailyTargetInfoFromWeb(context,null,user.mobile_No, user.password, user.company_ID,user.dsrId);
			Toast.makeText(context,"Sales Promotion data updated", Toast.LENGTH_LONG).show();
		} else{
			Toast.makeText(context, "Sales Promotion data sync failed. No internet connection", Toast.LENGTH_LONG).show();
		}
	}

	private void orderCompleteButtonAction() {
		AlertDialog.Builder orderCompleteConfirmationMessage = new AlertDialog.Builder(
				context);
		orderCompleteConfirmationMessage.setTitle("Warning!!");
		orderCompleteConfirmationMessage
				.setMessage("Once you Complete Order, System will not allow you to take anymore Orders for this Section.");

		orderCompleteConfirmationMessage.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
//						Caller caller = new Caller();
//						caller.OrderCompleteConfirmationToWeb(context,
//								myHandler);
					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.cancel();
					}
				});
		orderCompleteConfirmationMessage.show();
	}
	
	private void allStoreNotVisitedDialog() {
		AlertDialog.Builder allStoreNotVisitedMessage = new AlertDialog.Builder(
				context);
		allStoreNotVisitedMessage.setTitle("Warning!!");
		allStoreNotVisitedMessage
                .setMessage("You have not visited all stores. Please visits all stores then complete order.")
                .setIcon(android.R.drawable.ic_dialog_alert);

		allStoreNotVisitedMessage.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();

					}
				});
		allStoreNotVisitedMessage.show();
	}

	private void listOfPromotionsButtonAction() {
		Util.showWaitingDialog(context);
		Intent intent = new Intent(context,
				PromotionsListActivity.class);
		startActivity(intent);
	}

	private void orderSummaryButtonAction() {
		Util.showWaitingDialog(context);
		Intent intent = new Intent(context,
				OrderSummaryBySkuActivity.class);
		startActivity(intent);
	}

    private void orderButtonAction() {
//        Util.showWaitingDialog(context);
        Intent intent = new Intent(context,
                OutletListPageActivity.class).putExtra(
                DatabaseConstants.tblDSRBasic.SECTION_ID, user.sectionId);
        context.startActivity(intent);
//        this.finish();
    }

    private void todaysStatusButtonAction() {
		Util.showWaitingDialog(context);
		Intent intent = new Intent(context,
				TodaysStatusPageActivity.class)
				.putExtra(DatabaseConstants.tblDSRBasic.TARGET, user.target)
				.putExtra(DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED,
						user.orderAchieved)
				.putExtra(DatabaseConstants.tblDSRBasic.TARGET_REM,
						user.target - user.orderAchieved)
				.putExtra(DatabaseConstants.tblDSRBasic.DAY_REMAIN,
						user.dayRemain)
				.putExtra(DatabaseConstants.tblDSRBasic.SECTION_ID,
						sectionID)
				.putExtra(DatabaseConstants.tblSection.ROUTE_ID,
						routeID);
		startActivity(intent);
	}

	private void orderUploadButtonAction() {
		final boolean internet = new CommonFunction().isInternetOn(context);
		AlertDialog.Builder orderCompleteConfirmationMessage = new AlertDialog.Builder(
				context);
		orderCompleteConfirmationMessage.setTitle("Upload Order");
		orderCompleteConfirmationMessage
				.setMessage("Are you sure you want to upload pending Orders?");

		orderCompleteConfirmationMessage.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						if(internet) {
							Caller caller = new Caller();
							caller.UploadDataToWeb(context, OrderUploadHandler,  user.mobile_No, user.password);
						}
						else {
							Toast.makeText(context,
									"No internet connection. Please Try again later.",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.cancel();
					}
				});
		orderCompleteConfirmationMessage.show();
	}

	public void OrderCompletionCheck() {
		user = DatabaseQueryUtil.getUser(context);
//		if (user.sentStatus == 1 && user.isOrderCompleted == 1) {
//			OrderButton.setEnabled(false);
//			//OrderCompleteButton.setEnabled(false);
//		}
//
//		if (user.sentStatus == 0 && user.isOrderCompleted == 0) {
//			OrderCompleteButton.setEnabled(false);
//		}
//
//		if (user.sentStatus == 1 && user.isOrderCompleted == 0) {
//			OrderCompleteButton.setEnabled(true);
//		}
	}

	private void GetTodaysSection(String visitDate) {
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
					sectionNameView.setText(sectionName);
					sectionID = marketList.list.get(i).sectionId;
					routeID = marketList.list.get(i).routeId;
					break;
				} else {
					String day = marketList.list.get(i).orderColDay;
					if (day.compareTo(dayOfTheWeek) == 0) {
						sectionName = marketList.list.get(i).title;
						sectionNameView.setText(sectionName);
						sectionID = marketList.list.get(i).sectionId;
						routeID = marketList.list.get(i).routeId;
					}
				}
			}
		}
	}
	public void PendingOrdersChecking() {
		user = DatabaseQueryUtil.getUser(context);
//		if (user.WillUploadOrderToWeb == 0) {
//			UploadOrder.setEnabled(false);
//			return;
//		} else {
			menuItemUpload.setEnabled(true);
			ArrayList<Order> NotSentOutletList = DatabaseQueryUtil
					.getOutletListWithNotSentStatus(context);
			int count = NotSentOutletList.size();
			if (count > 0) {
				menuItemUpload.setTitle("Upload(" + Integer.toString(count)
						+ ")");
			//	OrderCompleteButton.setEnabled(false);
			} else {
				menuItemUpload.setTitle("Upload(" + Integer.toString(count)
						+ ")");
				menuItemUpload.setEnabled(false);
//				if(user.sentStatus == 1 && user.isOrderCompleted == 0)
//					OrderCompleteButton.setEnabled(true);
			}
	}


//	@Override
//	public void onBackPressed() {
//		//OrderCompletionCheck();
//	}

//	@Override
	public void finish() {
		Util.cancelWaitingDialog();
        context.finish();
	}

//	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory( Intent.CATEGORY_HOME );
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			OrderCompletionCheck();
			DatabaseQueryUtil.updateTblDSRBasicWithOrderCompleteStatus(context, 1);

			Toast.makeText(context, R.string.order_complete_toast, Toast.LENGTH_SHORT).show();
		}
	};
	
	Handler OrderUploadHandler = new Handler() {
		public void handleMessage(Message msg) {
			PendingOrdersChecking();
			Toast.makeText(context,
					"Pending Orders Uploading Attempt Completed ",
					Toast.LENGTH_SHORT).show();
		}
	};
}
