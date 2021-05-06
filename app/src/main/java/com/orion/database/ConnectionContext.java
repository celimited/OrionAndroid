package com.orion.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConnectionContext extends SQLiteOpenHelper {

    private static final String TAG = "Trace";
    private Boolean _tranMode;
    private SQLiteDatabase _db;

    public ConnectionContext(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "ConnectionContext::onCreate");
        db.execSQL(DatabaseConstants.CREATE_TABLE_NEW_OUTLET);
        db.execSQL(DatabaseConstants.CREATE_TABLE_IMAGES);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "ConnectionContext::onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_NEW_OUTLET);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_IMAGES);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void Begin() {
        _tranMode = false;
        _db = this.getWritableDatabase();
    }

    public void Begin(Boolean transaction) {
        _tranMode = true;
        _db = this.getWritableDatabase();
        if (_db != null)
            _db.beginTransaction();
    }

    public long Insert(String tableName, String columnsName, String[] columnsValue) {
        long value = 0;
        try {
            if (tableName.trim().length() <= 0)
                throw new Exception("Table name cannot be empty.");

            String[] flds = columnsName.split(",");
            if (flds.length != columnsValue.length)
                throw new Exception("Number of columns and values are  not same.");

            ContentValues values = new ContentValues();
            for (int idx = 0; idx < columnsValue.length; idx++)
                values.put(flds[idx], columnsValue[idx]);

            // Inserting Row
            value = _db.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public long Update(String tableName, String columnsName, String[] columnsValue, String whereClause, String[] whereArgs) {
        long value = 0;
        try {
            if (tableName.trim().length() <= 0)
                throw new Exception("Table name cannot be empty.");

            String[] flds = columnsName.split(",");
            if (flds.length != columnsValue.length)
                throw new Exception("Number of columns and values are  not same.");

            ContentValues values = new ContentValues();
            for (int idx = 0; idx < columnsValue.length; idx++)
                values.put(flds[idx].trim(), columnsValue[idx]);

            // Update Row(s)
            value = _db.update(tableName, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public long Delete(String tableName, String whereClause, String[] whereArgs) {
        long value = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (tableName.trim().length() <= 0)
                throw new Exception("Table name cannot be empty.");

            // Delete Row(s)
            db.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            // how to do the rollback
            e.printStackTrace();
        }

        return value;
    }

    public Cursor Select(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            _db = this.getReadableDatabase();

            if (sql.trim().length() <= 0)
                throw new Exception("sql cannot be empty.");

            cursor = _db.rawQuery(sql, selectionArgs);

        } catch (Exception e) {
            // how to do the rollback
            e.printStackTrace();
        }

        return cursor;
    }

    public Cursor Select(String sql) {
        Cursor cursor = null;
        try {
            _db = this.getReadableDatabase();

            if (sql.trim().length() <= 0)
                throw new Exception("sql cannot be empty.");

            cursor = _db.rawQuery(sql, null);

        } catch (Exception e) {
            // how to do the rollback
            e.printStackTrace();
        }

        return cursor;
    }

    public void End() {
        if (_db != null) {
            if (_tranMode) {
                _db.setTransactionSuccessful();
                _db.endTransaction();
            }

            _db.close();
            _tranMode = false;
        }
        _db = null;
    }

    public String getSinlgeEntry(String userName) {

        String password = "";

        try {
            _db = this.getWritableDatabase();
            Cursor cursor = _db.query("tblDSRBasic", null, " MobileNo=?", new String[]{userName}, null, null, null);
            if (cursor.getCount() < 1) {
                // UserName Not Exist
                cursor.close();
                return "NOT EXIST";
            }
            cursor.moveToFirst();
            password = cursor.getString(cursor.getColumnIndex("Password"));
            cursor.close();
            _db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
}
