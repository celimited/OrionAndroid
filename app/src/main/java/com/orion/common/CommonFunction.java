package com.orion.common;

/**
 * Created by zakir on 9/30/2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class CommonFunction {

    public String CurrentDay() {
        // If current day is Sunday, day=1. Saturday, day=7.
        String today = "";

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 7) {
            today = "Saturday";
        } else if (day == 1) {
            today = "Sunday";
        } else if (day == 2) {
            today = "Monday";
        } else if (day == 3) {
            today = "Tuesday";
        } else if (day == 4) {
            today = "Wednesday";
        } else if (day == 5) {
            today = "Thursday";
        } else if (day == 6) {
            today = "Friday";
        }
        return today;
    }

    public static boolean isInternetOn(Context ctx) {
        Context context;
        context = ctx.getApplicationContext();
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public String readFromFile(Context ctx) {
        String ret = "";
        try {
            //InputStream inputStream = ctx.openFileInput("weblink.txt");

            //From Assets Folder
            InputStream inputStream = ctx.getResources().getAssets().open("weblink.txt");

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
