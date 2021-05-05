package com.orion.application;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orion.adapter.GPSLocationAdapter;
import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Bonus;
import com.orion.entities.FreeOrDiscount;
import com.orion.entities.MarketReturnItem;
import com.orion.entities.Outlet;
import com.orion.entities.Reason;
import com.orion.entities.User;
import com.orion.gps.GPSLocation;
import com.orion.gps.GPSTracker;
import com.orion.gps.LocationConstant;
import com.orion.util.Util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CheckOutActivity extends AppCompatActivity {
    private static final int MARKET_RETURN = 200;

//	private ReasonListAdapter reasonListAdapter;
//  private Spinner reasonListSpinner;

    private Reason selectedReason;
    private User user;
    private Outlet outlet;
    private String outletName;
    private Context context;
    private ArrayList<Reason> reasonList;
    private CharSequence[] reasonListArray;
    private double orderTotal;
    private double orderDiscount;
    private double spcMgtValue;
    private double returnItemValue;
    private double netValue;
    private NumberFormat numberFormat;
    private int orderNo;
    private int flag = 0;
    private int gpsTryCount = 0;
    ToggleButton togButton;
    ToggleButton toggleButtonDeliveryMode;
    private Location outletLocation;
    private Location orderLocation;
    private String TAG = "Trace";
    private Button button;
    private TextView text_date;
    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 100;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.cancelWaitingDialog();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out_page_layout);
        context = this;

        Log.v(TAG, "This is CheckoutActivity");
        user = DatabaseQueryUtil.getUser(context);
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        togButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButtonDeliveryMode = (ToggleButton) findViewById(R.id.toggleButtonDeliveryMode);
        button = (Button) findViewById(R.id.btndatepicker);

        user.WillTrackGPS = 1;
        initializeFields();

        if (netValue <= 0) {
            updateReasonList();
            togButton.setVisibility(View.INVISIBLE);
            final Button selectReason = (Button) findViewById(R.id.selectReason);
            findViewById(R.id.selectReason).setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            reasonListArray = new CharSequence[reasonList.size()];
                            for (int i = 0; i < reasonList.size(); i++) {
                                reasonListArray[i] = reasonList.get(i).description;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);
                            builder.setTitle(R.string.select_reason);
                            builder.setItems(reasonListArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
//								Toast.makeText(getApplicationContext(), reasonListArray[item], Toast.LENGTH_SHORT).show();
                                    selectedReason = reasonList.get(item);
                                    selectReason.setText(selectedReason.description);
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();


                        }
                    });


            selectReason.setVisibility(View.VISIBLE);
        } else {
            togButton.setVisibility(View.VISIBLE);

        }

        setCurrentDate();
        //addButtonListener();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        findViewById(R.id.final_check_out_button).setOnClickListener(new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View arg0) {
//				if(gpsTryCount <3 && flag == 0)
//					GPSFix();
//				else if(flag == 1){
                                                                             showConfirmFinalCheckOutDialogBox();
//				}
                                                                         }
                                                                     }

        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkout, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_market_return) {
            /**
             * Causing App crash, commented out for now needs to be fixed.
             */
            //mrButtonClickedAction();
        } else if (item.getItemId() == R.id.action_pop_item) {
            popButtonClickedAction();
        } else if (item.getItemId() == R.id.action_order_history) {
            orderHistoryButtonClickedAction();
        }
        return true;
    }

    private void orderHistoryButtonClickedAction() {
        Util.showWaitingDialog(context);
        Intent intent = new Intent(CheckOutActivity.this, OrderHistoryActivity.class)
                .putExtra(DatabaseConstants.tblOutlet.OUTLET_ID, OutletVisitPageActivity.outletVisitPageActivity.outlet.outletId)
                .putExtra(DatabaseConstants.tblOutlet.DESCRIPTION, OutletVisitPageActivity.outletVisitPageActivity.outlet.description);
        startActivity(intent);
    }

    private void popButtonClickedAction() {
        Util.showWaitingDialog(context);
        Intent intent = new Intent(CheckOutActivity.this, PopItemDetailsActivity.class)
                .putExtra(DatabaseConstants.tblOutlet.OUTLET_ID, OutletVisitPageActivity.outletVisitPageActivity.outlet.outletId);
        startActivity(intent);
    }

    private void mrButtonClickedAction() {
        Util.showWaitingDialog(context);
        Intent intent = new Intent(CheckOutActivity.this, MarketReturnPageActivity.class)
                .putExtra(DatabaseConstants.tblOutlet.OUTLET_ID, OutletVisitPageActivity.outletVisitPageActivity.outlet.outletId);

        startActivityForResult(intent, MARKET_RETURN);
    }

    private void updateReasonList() {
//		reasonListAdapter.addItem(new Reason(0, "--Select Reason--"));
        reasonList = DatabaseQueryUtil.getReasonListForNoOrder(context);
        for (int i = 0; i < reasonList.size(); i++) {
//			reasonListAdapter.addItem(reasonList.get(i));
        }
    }


    public void setCurrentDate() {
        //text_date = (TextView) findViewById(R.id.text_date);
        final Calendar calendar = Calendar.getInstance();
        String months;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String days = checkDigit(day);
        if (month <= 9) {
            months = checkDigit(month + 1);
        } else {
            months = String.format("%d", month + 1);
        }

        //  set current date into textview
        button.setText(new StringBuilder()
                .append(days).append("-")
                .append(months).append("-")
                .append(year).append(""));

    }

    public void addButtonListener() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    public String checkDigit(int number) {
        String date = String.valueOf(number);
        if (number <= 9) {
            date = String.format("%02d", number);
        }
        return date;
    }

    private void initializeFields() {
        outletName = getIntent().getStringExtra(DatabaseConstants.tblOutlet.DESCRIPTION);
        orderTotal = getIntent().getDoubleExtra(DatabaseConstants.tblOrder.ORDER_TOTAL, 0);
        FreeOrDiscount freeOrDiscount = DatabaseQueryUtil.getFreeOrDiscount(context, OutletVisitPageActivity.outletVisitPageActivity.outlet.outletId, OutletVisitPageActivity.outletVisitPageActivity.outlet.channelId, OutletVisitPageActivity.outletVisitPageActivity);
        orderDiscount = (freeOrDiscount == null) ? 0 : freeOrDiscount.discount;
        Bonus bonus = DatabaseQueryUtil.getBonus(context, OutletVisitPageActivity.outletVisitPageActivity.outlet.outletId);
        spcMgtValue = (bonus != null && bonus.isUpdated != 0) ? bonus.bonusQty : 0;
        returnItemValue = getIntent().getDoubleExtra(DatabaseConstants.otherFields.RETURN_ITEM_VALUE, 0);
        netValue = orderTotal - orderDiscount - returnItemValue - spcMgtValue;
        outlet = DatabaseQueryUtil.getOutlet(context, getIntent()
                .getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID));
        orderNo = getIntent().getIntExtra(DatabaseConstants.tblOrder.ORDER_NO, -1);
        ((TextView) findViewById(R.id.outlet_name)).setText(outletName);
        ((TextView) findViewById(R.id.order_items_value)).setText(numberFormat.format(orderTotal));
       // ((TextView) findViewById(R.id.order_mr_adj_value)).setText(numberFormat.format(returnItemValue));
        ((TextView) findViewById(R.id.order_tp_disc_value)).setText(numberFormat.format(orderDiscount));
        //((TextView) findViewById(R.id.order_spc_mgt_value)).setText(numberFormat.format(spcMgtValue));
        ((TextView) findViewById(R.id.order_net_value)).setText(numberFormat.format(netValue));

    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // Enable vibrate
        } else {
            // Disable vibrate
        }
    }

    public void onToggleDeliveryModeClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // Enable vibrate
        } else {
            // Disable vibrate
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Util.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }


    private void showSelectReasonIdDialogBox() {
        AlertDialog.Builder selectReasonMessage = new AlertDialog.Builder(context);
        selectReasonMessage.setTitle("Select Reason");
        selectReasonMessage.setMessage("Please select a reason.");

        selectReasonMessage.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        selectReasonMessage.show();
    }

    private void showConfirmFinalCheckOutDialogBox() {
        AlertDialog.Builder confirmCheckoutDialog = new AlertDialog.Builder(context);
        confirmCheckoutDialog.setTitle("Check Out");
        confirmCheckoutDialog.setMessage("Do you want to check out from " + outletName + "?");

        confirmCheckoutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                if (netValue <= 0) {
                    Log.d(TAG, "net value is zero");

                    if (selectedReason != null && selectedReason.reasonId > 0) {
                        Log.d(TAG, "Reason selected");

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(DatabaseConstants.tblMarketReason.MARKET_REASON_ID, selectedReason.reasonId);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        showSelectReasonIdDialogBox();
                    }
//					showUploadToWebDialogBox();
                } else {
                    Log.d(TAG, "net value not zero");
                    int paymentMode = togButton.isChecked() ? 0 : 1;
                    int deliverymode =toggleButtonDeliveryMode.isChecked() ? 0 : 1;
                    String deliverydate= (String) button.getText();
                    // show visit outlet
                    Intent returnIntent = new Intent();
                    returnIntent
                            .putExtra(DatabaseConstants.tblOrder.PAYMENT_MODE, paymentMode);
                    returnIntent
                            .putExtra(DatabaseConstants.tblOrder.DELIVERY_MODE,deliverymode );
                    returnIntent
                            .putExtra(DatabaseConstants.tblOrder.DELIVERY_DATE, deliverydate);
                    setResult(RESULT_OK, returnIntent);
                    finish();

                    //GPSFix();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        confirmCheckoutDialog.show();
    }


    /**
     *
     */
    private void GPSFix() {
        boolean ordergps = false;
        boolean outletgps = DatabaseQueryUtil.CheckOutletGPS(context, outlet.outletId);
        if (orderNo > 0)
            ordergps = DatabaseQueryUtil.CheckOrderGPS(context, orderNo);

        if ((user.WillTrackGPS == 1)) {
            if (!outletgps) {
                GPSLocationAdapter gps = new GPSLocationAdapter(CheckOutActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    outletLocation = new Location("");
                    outletLocation.setLatitude(Double
                            .valueOf(latitude));
                    outletLocation.setLongitude(Double
                            .valueOf(longitude));
                    DatabaseQueryUtil
                            .updateTblOutletWithNoLocation(context,
                                    outlet.outletId,
                                    String.valueOf(latitude),
                                    String.valueOf(longitude));
                    Log.e("GPSdone", "GPS done");
                }
            } else if (outletgps == true
                    && ordergps == true) {
                Toast.makeText(context, "Loaction already recorded",
                        Toast.LENGTH_LONG).show();
            }
            if (!ordergps) {

                GPSLocationAdapter gps = new GPSLocationAdapter(CheckOutActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    orderLocation = new Location("");
                    orderLocation.setLatitude(Double
                            .valueOf(latitude));
                    orderLocation.setLongitude(Double
                            .valueOf(longitude));

                    showOutletLocationCheckDialogBox(outletgps);
                }

                Log.e("GPSdone", "GPS done");
            }
        }
    }

    private void showOutletLocationCheckDialogBox(
            final boolean IsOutletLocationExists) {

        GPSLocation.GetLocation(context);// Initiate GPS

        AlertDialog.Builder gpsAlert = new AlertDialog.Builder(
                context);
        gpsAlert
                .setMessage("Check Out: System is going to store/update your order location. Please make sure you are in the correct position and your GPS of the device is Enabled and Fixed.");

        gpsAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String string = "GPS_FIXED,";
                        if (orderLocation.getLatitude() == 0.0 && orderLocation.getLongitude() == 0.0) {
                            GPSTracker gps = new GPSTracker(context);
                            // check if GPS enabled
                            if (gps.canGetLocation()) {

                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                string += latitude + "," + longitude;
                                flag = 1;
                                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                            } else {
                                gpsTryCount = gpsTryCount + 1;
                                Toast.makeText(
                                        context,
                                        "Try Count: " + gpsTryCount,
                                        Toast.LENGTH_LONG).show();

                                if (gpsTryCount <= 3 && flag < 1) {
                                    if (gps.IsGPSEnabled()) {
                                        showStatusCheckDialogBox("GPS is still searching ,Try again when GPS is fixed or Ignore and proceed" + "Try Count: " + gpsTryCount);
                                    } else {
                                        gps.showSettingsAlert();
                                    }
                                    return;
                                }
                            }
                            if (gpsTryCount > 3 && flag > 0) {
                                string += 0 + "," + 0;
                            }
                            //gpsTryCount=0;

                            String[] parts = string.split(",");
                            String gpsstatus = parts[0];

                            if (gpsstatus.equals(LocationConstant.GPS_FIXED)) {

                                if (!IsOutletLocationExists) {
                                    DatabaseQueryUtil
                                            .updateTblOutletWithNoLocation(context,
                                                    outlet.outletId,
                                                    String.valueOf(parts[1]),
                                                    String.valueOf(parts[2]));

                                    orderLocation = new Location("");
                                    orderLocation.setLatitude(Double.valueOf(parts[1]));
                                    orderLocation.setLongitude(Double.valueOf(parts[2]));

                                    Toast.makeText(
                                            context,
                                            "Outlet and Order location Updated Successfully ",
                                            Toast.LENGTH_LONG).show();

                                } else {

                                    orderLocation = new Location("");
                                    orderLocation.setLatitude(Double
                                            .valueOf(parts[1]));
                                    orderLocation.setLongitude(Double
                                            .valueOf(parts[2]));
                                    // else {
//								DatabaseQueryUtil.updateTblOrderOrderLocation(
//										context, outlet.outletId,
//										String.valueOf(parts[1]),
//										String.valueOf(parts[2]));
                                    Toast.makeText(
                                            context,
                                            "Only Order Location Updated Successfully",
                                            Toast.LENGTH_LONG).show();
                                    Log.e("LocationAfter", "Order Latitude:"
                                            + String.valueOf(parts[1])
                                            + " Order Longitude"
                                            + String.valueOf(parts[1]));
                                }

                            } else {
                                Toast.makeText(
                                        context,
                                        "GPS has started. Wait for it to fix or restart it.",
                                        Toast.LENGTH_SHORT).show();

                            }
                            dialog.cancel();
                        } else {
                            DatabaseQueryUtil
                                    .updateTblOutletWithNoLocation(context,
                                            outlet.outletId,
                                            String.valueOf(orderLocation.getLatitude()),
                                            String.valueOf(orderLocation.getLongitude()));
                            flag = 1;
                        }
                    }
                });

        gpsAlert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == MARKET_RETURN) {
            double riv = 0;
            for (MarketReturnItem item : OutletVisitPageActivity.outletVisitPageActivity.previousMarketReturnItemList)
                riv += item.price * item.qty;

            for (MarketReturnItem item : OutletVisitPageActivity.outletVisitPageActivity.removedMarketReturnItemList)
                riv -= item.price * item.qty;

            for (MarketReturnItem item : OutletVisitPageActivity.outletVisitPageActivity.addedMarketReturnItemList)
                riv += item.price * item.qty;

            returnItemValue = riv;
           // ((TextView) findViewById(R.id.order_mr_adj_value)).setText(numberFormat.format(returnItemValue));
            netValue = orderTotal - orderDiscount - returnItemValue - spcMgtValue;
            ((TextView) findViewById(R.id.order_net_value)).setText(numberFormat.format(netValue));
        }
    }

    private void showStatusCheckDialogBox(String message) {
        AlertDialog.Builder confirmmsg = new AlertDialog.Builder(context);
        // confirmCheckoutDialog.setTitle("Outlet Location");
        confirmmsg.setMessage(message);
        confirmmsg.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.cancel();
                    }
                });/*
					 * .setNegativeButton("Cancel", new
					 * DialogInterface.OnClickListener() { public void
					 * onClick(DialogInterface dialog, int whichButton) {
					 * dialog.cancel(); } });
					 */
        confirmmsg.show();
    }

    Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            //setResult(RESULT_OK);
            //finish();
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CheckOut Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.orion.application/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CheckOut Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.orion.application/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 100) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                     /*arg1 = year;
                     arg2 = month;
                     arg3 = day;*/
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        button.setText(new StringBuilder().append(day).append("-")
                .append(month).append("-").append(year).append(""));
        String mm= (String) button.getText();
    }



}