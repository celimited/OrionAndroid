package com.orion.application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.webservice.Caller;

public class RegisterPageActivity extends Activity {
    private static final String TAG = "RegisterPageActivity";
    private EditText _emailText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;
    public String status = "Registered";
    private Context context;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_layout);
        context = this;
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(RegisterPageActivity.this,
                        LoginPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "RegisterPage");

//        if (!validate()) {
//            onSignupFailed();
//            return;
  //      }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterPageActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        Caller aCaller = new Caller();

        aCaller.GetHHTUsersFromWeb(context, handler, email, password, email, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        Caller statusCaller = new Caller();
                        if (Caller.Status.equals("Registered") || Caller.Status.equals("False") || Caller.Status.equals("Blocked") || Caller.Status.equals("Errored")) {
                            onSignupFailed();
                        }
                        if (Caller.Status.equals("Authentic")) {
                            onSignupSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent intent = new Intent(RegisterPageActivity.this,
                LoginPageActivity.class);
        startActivity(intent);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
        //Intent intent = new Intent(RegisterPageActivity.this,
        // MainActivity.class);
        // startActivity(intent);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || email.length() != 11) {
            _emailText.setError(Html.fromHtml("<font color='red'>Enter a valid Mobile No</font>"));
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 && password.length() > 10) {

            _passwordText.setError(Html.fromHtml("<font color='red'>Enter Valid Password</font>"));
            valid = false;
        }

        return valid;
    }
}