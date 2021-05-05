package com.orion.print;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Outlet;
import com.orion.entities.User;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by zakir on 9/30/2015.
 */

public class PrintActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    EditText message;
    Button printbtn;
    String msg = " ";
    private String outletId;
    private String sectiontID;
    static Outlet outletObj;
    static User userObj;
    public Context context;

    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("printContent");
        outletId = bundle.getString("outletID");
        sectiontID = bundle.getString("sectionID");

        outletObj = DatabaseQueryUtil.getOutlet(context, outletId);
        userObj = DatabaseQueryUtil.getUser(context);

        Date d = new Date();
        CharSequence datetime = DateFormat.format("d MMM yyyy, hh:mm a", d.getTime());

        msg = msg + "      " + userObj.distributorName;
        msg = msg + "\n " + userObj.distributorAddress;
        msg = msg + "\n       " + outletObj.description;
        msg = msg + "\n " + outletObj.address;
        msg = msg + "\n Date :" + datetime;
        msg = msg + "\n Delivery Day : " + DatabaseQueryUtil.getDeliveryDay(context, outletId, sectiontID);
        msg = msg + "\n SKU      Ctn-Pcs   Price  Value";

        msg = msg + message;
        msg = msg + "\n\n\n Collected By :\n " + userObj.name + "            Cust. Sign";

        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        connect();
    }

    protected void connect() {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
            this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
        } else {

            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;
            print_bt();

        }

    }

    private void print_bt() {
        try {
            Toast.makeText(getApplicationContext(), "Printing....", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            byte[] printformat = {0x1B, 0x21, FONT_TYPE};
            btoutputstream.write(printformat);
            btoutputstream.write(msg.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.write(0x0D);
            btoutputstream.write(0x0D);
            btoutputstream.flush();
            Toast.makeText(getApplicationContext(), "Print Completed", Toast.LENGTH_LONG).show();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (btsocket != null) {
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
            if (btsocket != null) {
                print_bt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}