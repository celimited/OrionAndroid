package com.orion.application;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.orion.common.CommonFunction;
import com.orion.database.ConnectionContext;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;
import com.orion.webservice.Caller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginPageActivity extends Activity {

    private static final String TAG = "LoginPageActivity";
    private static final int REQUEST_SIGNUP = 0;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private User user;
    ConnectionContext connectionContext;
    public Context context;
    private Handler handler;
    public static Boolean logInStatus = false;
    public String LastUpdateTime = "";
    public String visitDate = "";

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_layout);

        connectionContext = new ConnectionContext(this);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterPageActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        checkPermissions();
    }

    public void login() {
        context = this;
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginPageActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        /**
         * We need to update Outlets on login. As Outlet info is stored locally,
         * we are going to remove them first and load on each login
         */
        DatabaseQueryUtil.deleteOutlets(context);

        String email = new DatabaseQueryUtil().GetMobilefromTblDSRBasic(context);
        String password = _passwordText.getText().toString();
        LastUpdateTime = new DatabaseQueryUtil().GetLastUpdateTime(context);
        user = DatabaseQueryUtil.getUser(context);
        User us = DatabaseQueryUtil.getUser(context);
        visitDate = user.visitDate;

        String storedPassword = connectionContext.getSinlgeEntry(email);

        // check if the Stored password matches with  Password entered by user
        if (password.equals(us.password)) {
            Toast.makeText(LoginPageActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
            logInStatus = true;
        } else {
            Toast.makeText(LoginPageActivity.this, "Password does not match", Toast.LENGTH_LONG).show();
            logInStatus = false;
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (logInStatus) {
                            onLoginSuccess();
                        }
                        if (!logInStatus) {
                            onLoginFailed();
                        }

                    }
                }, 300);
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginSuccess() {
        boolean internet = new CommonFunction().isInternetOn(this.context);
        _loginButton.setEnabled(true);

        if (internet) {
            Caller aCaller = new Caller();

//            aCaller.GetBrandsFromWeb(context, handler, "01730164824", "1234", user.company_ID);
//            aCaller.GetChannelsFromWeb(context, handler, "01730164824", "1234", user.company_ID);
//            aCaller.GetSectionFromWeb(context,handler, "01730164824","1234",user.dsrId,user.company_ID);
//            aCaller.GetSKUsFromWeb(context, handler, "01730164824", "1234", user.company_ID);
//            aCaller.GetNoOrderReasonInfoFromWeb(context, handler, "01730164824", "1234", user.company_ID);
//            aCaller.GetOutletInfoFromWeb(context,handler, "01730164824","1234",user.dsrId,user.company_ID);
            //aCaller.GetMarketReturnReasonFromWeb(context, handler, "01730164824", "1234", user.company_ID);

            aCaller.GetBrandsFromWeb(context, handler, user.mobile_No, user.password, user.company_ID);
            aCaller.GetChannelsFromWeb(context, handler, user.mobile_No, user.password, user.company_ID);
            aCaller.GetSectionFromWeb(context, handler, user.mobile_No, user.password, user.dsrId, user.company_ID);
            aCaller.GetSKUsFromWeb(context, handler, user.mobile_No, user.password, user.company_ID);
            aCaller.GetNoOrderReasonInfoFromWeb(context, handler, user.mobile_No, user.password, user.company_ID);
            aCaller.GetOutletInfoFromWeb(context, handler, user.mobile_No, user.password, user.dsrId, user.company_ID);
            // downloadSalesPromotion();
        } else {
            Toast.makeText(getBaseContext(), "Data connection not available!! Please connect to the internet and re-login  to update the data", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(LoginPageActivity.this,
                MainNavigationActivity.class).putExtra(DatabaseConstants.tblDSRBasic.VISIT_DATE, visitDate);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
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
        if (internet) {
            Caller caller = new Caller();
            caller.GetSalesPromotionsFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPChannelSKUFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPSlabFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetSPBonusesFromWeb(context, null, user.mobile_No, user.password, user.company_ID, visitDate);
            caller.GetDSRDailyTargetInfoFromWeb(context, null, user.mobile_No, user.password, user.company_ID, user.dsrId);
            Toast.makeText(getBaseContext(), "Sales Promotion data updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Sales Promotion data sync failed. No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validate() {
        boolean valid = true;
        EditText usernameEditText = (EditText) findViewById(R.id.input_password);
        String pass = usernameEditText.getText().toString();
        if (pass.matches("")) {
            Toast.makeText(this, "You did not enter a Password", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission is granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}