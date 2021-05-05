package com.orion.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.orion.database.DatabaseQueryUtil;
import com.orion.database.CreateDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by robiul on 9/14/2015.
 */

public class MainActivity extends Activity {
    private Context context;
    private boolean flag = false ;
    private String TAG = "Trace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        Locale locale = new Locale("bn");
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
*/


        if( getIntent().getBooleanExtra("Exit", false)){
            flag = true;
            super.finish();
        }
        if(!flag) {
            context = this;
            boolean dbExist = new CreateDatabase(context).checkDataBase();

            if (!dbExist) {
                new CreateDatabase(context).createDataBase();
            }else {
                Log.v(TAG, "Database exist");
            }

            boolean status = DatabaseQueryUtil.CheckTblDSRBasic(context);
            this.finish();
            if (!status) {
                Log.v(TAG, "TblDSRBasic does not exist");
                Intent intent = new Intent(MainActivity.this,
                        RegisterPageActivity.class);
                startActivity(intent);
            } else {
                Log.v(TAG, "TblDSRBasic exists");
                Intent intent = new Intent(MainActivity.this,
                        LoginPageActivity.class);
                startActivity(intent);
            }
            //     readAddress();
            //     WebServiceConstants ws = new WebServiceConstants();
        }
        this.finish();
    }

    private String read_file(Context context, String filename) {
        File[] files = Environment.getExternalStorageDirectory().listFiles();
        File sdcard = Environment.getDownloadCacheDirectory();
        sdcard = Environment.getExternalStorageDirectory();
        File loc = sdcard;
        String path = sdcard.getAbsolutePath();
        File file = new File(sdcard,filename);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        }
        catch (IOException e) {
            return "";
        }
        return text.toString();
    }

    @Override
    public void finish() {
        super.finish();
    }

    public String readAddress() {
        Context context = this.context;
        String filename = "log.txt";
        return read_file(context, filename);
    }
}
