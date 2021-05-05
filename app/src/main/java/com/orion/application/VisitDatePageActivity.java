package com.orion.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.common.CommonFunction;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Order;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;
import com.orion.webservice.Caller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by robiul on 10/25/2015.
 */
public class VisitDatePageActivity extends Activity {

    private TextView text_date;
    private TextView text_visit_date;
    private DatePicker date_picker;
    private Button button;
    private User user;
    private int flag = 0;
    private int year;
    private int month;
    private int day;
    private Context context;
    private String visitDate;
    private String _prevVisitDate;
    private String tempDate;
    private int dateFlag = 0;
    private String lastUpdateTime;
    static final int DATE_DIALOG_ID = 100;
    static final String MY_PREFS_NAME = "DayCount";


    private ArrayList<Order> NotSentOutletList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.visit_date_fix_layout);
        lastUpdateTime = getIntent().getStringExtra(DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME);
        visitDate = getIntent().getStringExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE);
        _prevVisitDate = visitDate;
        setCurrentDate();
        addButtonListener();

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

//        DatabaseQueryUtil.deleteTblTPRSKUChnl(context);
//        DatabaseQueryUtil.deleteTblTPR(context);
//        DatabaseQueryUtil.deleteTblTPRSlab(context);
//        DatabaseQueryUtil.deleteTblSlabItem(context);

        findViewById(R.id.button_done)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        doneButtonAction();
                    }
                });
    }

    private void doneButtonAction() {

        int dayCount=0;
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int restoredvalue = prefs.getInt("DayCount", 0);
        if (restoredvalue != 0) {
            dayCount = prefs.getInt("DayCount", 0); //0 is the default value.
        }

        if(dayCount > 2){
            dayCount = 0;
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt("DayCount", dayCount);
            editor.apply();
            DatabaseQueryUtil.deleteOrderSummary(context);
        }else {
            dayCount++;
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putInt("DayCount", dayCount);
            editor.apply();
        }

        dateFlag=0;
        NotSentOutletList = DatabaseQueryUtil
                .getOutletListWithNotSentStatus(context);
        visitDate = text_visit_date.getText().toString();
        tempDate = text_date.getText().toString();

        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String str1 = visitDate;
            Date date1 = formatter.parse(str1);

            String str2 = tempDate;
            Date date2 = formatter.parse(str2);

            String str3 = _prevVisitDate;
            Date date3 = formatter.parse(str3);

            if (date1.before(date3) || date1.after(date2) || date1.equals(date3))
            {
                dateFlag =1;
            }

        }catch (ParseException e1){
            e1.printStackTrace();
        }

        int count = NotSentOutletList.size();

        if(dateFlag !=1){
            if (!visitDate.equals(tempDate) && flag == 1) {

                AlertDialog.Builder visitDateErrorMessage = new AlertDialog.Builder(context);
                visitDateErrorMessage.setTitle("Warning!!");
                visitDateErrorMessage
                        .setMessage("Your operation date does not match with current system date.Do you still want to continue?")
                        .setIcon(android.R.drawable.ic_dialog_alert);

                visitDateErrorMessage.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                clearOutletVisitStatus(NotSentOutletList);
                                // goToHomePage(NotSentOutletList);
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.cancel();
                            }
                        });
                visitDateErrorMessage.show();
            } else if (visitDate.equals(tempDate) && flag == 1) {
                clearOutletVisitStatus(NotSentOutletList);
                //goToHomePage(NotSentOutletList);
            } else {
                Toast.makeText(context,
                        "Please Change the date",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            AlertDialog.Builder visitDateErrorMessage = new AlertDialog.Builder(context);
            visitDateErrorMessage.setTitle("Warning!!");
            visitDateErrorMessage
                    .setMessage("Invalid operation date")
                    .setIcon(android.R.drawable.ic_dialog_alert);
            visitDateErrorMessage.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
            visitDateErrorMessage.show();
        }
    }

    public void clearOutletVisitStatus(final ArrayList<Order> notSentOutletList) {
        int count = notSentOutletList.size();
        if (count > 0) {
            AlertDialog.Builder visitDateErrorMessage = new AlertDialog.Builder(context);
            visitDateErrorMessage.setTitle("Warning!!");
            visitDateErrorMessage
                    .setMessage("Do you want to send pending orders?")
                    .setIcon(android.R.drawable.ic_dialog_alert);

            visitDateErrorMessage.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            uploadData();
                            goToHomePage(notSentOutletList, false);
                        }
                    }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.cancel();
                            goToHomePage(NotSentOutletList, true);
                        }
                    });
            visitDateErrorMessage.show();
        }else{goToHomePage(NotSentOutletList, true);}
    }

    public void uploadData() {
        final boolean internet = new CommonFunction().isInternetOn(context);
        if (internet) {
            Caller caller = new Caller();
            caller.UploadDataToWeb(context, null, user.mobile_No, user.password);
        } else {
            Toast.makeText(context,
                    "No internet connection. Please Try again later.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void goToHomePage(ArrayList<Order> NotSentOutletList, Boolean updateSentStatus) {

        DatabaseQueryUtil.updateTblOutletVisitedField(context, 0);
        for(int i= 0; i< NotSentOutletList.size(); i++)
        {
            if(updateSentStatus)
                DatabaseQueryUtil.updateSentStatusOfTblOrder(context, NotSentOutletList.get(i).OutletID, 0);

        }
        DatabaseQueryUtil.updateTblDSRBasicWithUpdatedVisitDate(context, visitDate);
        DatabaseQueryUtil.updateTblDSRBasicSectionID(context, null);
        downloadSalesPromotion();
        this.finish();
        Intent intent = new Intent(VisitDatePageActivity.this,
                MainNavigationActivity.class).putExtra(DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME, lastUpdateTime)
                .putExtra("visitDateFlag", 1)
                .putExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE, visitDate);
        startActivity(intent);
        finish();
    }

    public String checkDigit(int number) {
        String date = String.valueOf(number);
        if (number <= 9) {
            date = String.format("%02d", number);
        }
        return date;
    }

    public void setCurrentDate() {
        text_date = (TextView) findViewById(R.id.text_date);
        text_visit_date = (TextView) findViewById(R.id.text_visit_date);
        final Calendar calendar = Calendar.getInstance();
        String months;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String days = checkDigit(day);
        if(month<=9)
        {
            months = checkDigit(month+1);
        }
        else {
            months =  String.format("%d", month+1);
        }

        //  set current date into textview
        text_date.setText(new StringBuilder()
                .append(days).append("-")
                .append(months).append("-")
                .append(year).append(""));

        if (visitDate.equals("")) {
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            text_visit_date.setText(date);
        }
        text_visit_date.setText(visitDate);
    }

    public void addButtonListener() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Long min_date = null;
        Long max_date = null;
        Date minDate = null, maxDate = null;
        Calendar c = null;
        String nextDate;
        try {
            String minimumDate = text_visit_date.getText().toString();
            String maximumDate = text_date.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            c = Calendar.getInstance();
            c.setTime(sdf.parse(minimumDate));
            c.add(Calendar.DATE, 1);
            nextDate = sdf.format(c.getTime());

            minDate = sdf.parse(nextDate);
            maxDate = sdf.parse(maximumDate);
            min_date = minDate.getTime();
            max_date = maxDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog dtpPicker = new DatePickerDialog(this, datePickerListener, year, month, day);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    c.setTime(minDate);
                    dtpPicker.getDatePicker().setMinDate(c.getTimeInMillis() - 1000);
                    c.setTime(maxDate);
                    dtpPicker.getDatePicker().setMinDate(c.getTimeInMillis());
                    dtpPicker.getDatePicker().setMinDate(min_date);
                    dtpPicker.getDatePicker().setMaxDate(max_date);
                }
                return dtpPicker;
        }
        return null;
    }

    @Override
    public void finish() {
        //downloadSalesPromotion();
        super.finish();
    }

    public void downloadSalesPromotion() {
        boolean internet = new CommonFunction().isInternetOn(context);
        user = DatabaseQueryUtil.getUser(context);
        String date = user.visitDate;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
        Date dt = null;
        try {
            dt = sdf1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String visitDate = sdf2.format(dt);
        if (internet) {
            Caller caller = new Caller();
            caller.GetSalesPromotionsFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPChannelSKUFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPSlabFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPBonusesFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
        } else {
            Toast.makeText(getBaseContext(), "Sales Promotion data sync failed. No ineternet connection", Toast.LENGTH_LONG).show();
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String months;
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            String days = checkDigit(day);
            if(month<=9) {
                months = checkDigit(month+1);
            }
            else {
                months = String.format("%d", month+1);
            }
            text_visit_date.setText(new StringBuilder().append(days)
                    .append("-").append(months).append("-").append(year).append(""));
            flag = 1;
        }
    };
}
