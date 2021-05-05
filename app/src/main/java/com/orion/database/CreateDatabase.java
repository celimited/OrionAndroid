package com.orion.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by robiul on 9/8/2015.
 */
public class CreateDatabase extends SQLiteOpenHelper {

    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private String TAG = "Trace";

    public CreateDatabase(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() {
        Log.d(TAG, "createDataBase");
        this.getReadableDatabase();
        this.close();

        try {
            copyDataBase();
        } catch (IOException e) {
            throw new Error("Error copying database");
        }
    }

    public boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        String myPath = DatabaseConstants.DATABASE_LOCAL_PATH +"/"+ DatabaseConstants.DATABASE_NAME;

        File file = new File(myPath);
        if ( file.exists() ){
            try {
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG, "Database file does not exists");
        }


        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        OutputStream output;
        try {

            String databasePath = DatabaseConstants.DATABASE_LOCAL_PATH;
            File databaseDirectory = new File(databasePath);
            if (!databaseDirectory.exists()) {
                databaseDirectory.mkdirs();
            }
            InputStream input = myContext.getAssets().open(DatabaseConstants.DATABASE_NAME);
            String outFileName =  DatabaseConstants.DATABASE_LOCAL_PATH +"/"+ DatabaseConstants.DATABASE_NAME;
            output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[102400];
            int length;
            while ((length = input.read(buffer)) > 0)
                output.write(buffer, 0, length);

            output.flush();
            output.close();
            input.close();
        }
        catch (FileNotFoundException e) {
            Toast.makeText(myContext, "Database Import Error", Toast.LENGTH_SHORT).show();
            DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 0;
            e.printStackTrace();
            return;
        } catch (IOException e) {
            Toast.makeText(myContext, "Database Import Error", Toast.LENGTH_SHORT).show();
            DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 0;
            e.printStackTrace();
            return;
        }
        Toast.makeText(myContext,"Database Import Successful",Toast.LENGTH_SHORT).show();
        DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG=1;
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DatabaseConstants.DATABASE_LOCAL_PATH + DatabaseConstants.DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

