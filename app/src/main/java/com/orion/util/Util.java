package com.orion.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Display;

import com.orion.application.OutletVisitPageActivity;
import com.orion.database.DatabaseConstants;
import com.orion.entities.OrderItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Util extends Activity {
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	private static ProgressDialog waitingDialog;

	@SuppressWarnings("deprecation")
	public static void initialize(Context context) {
		SharedPreferences pref;
		//pref = context.getSharedPreferences(DatabaseConstants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
//		if (!pref.contains(DatabaseConstants.otherFields.MARKET_RETURN_ID)) {
//			SharedPreferences.Editor editor = pref.edit();
//			editor.putInt(DatabaseConstants.otherFields.MARKET_RETURN_ID, 1);
//			editor.commit();
//		}
		//DatabaseConstants.MARKET_RETURN_ID = pref.getInt(DatabaseConstants.otherFields.MARKET_RETURN_ID, 1);
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			SCREEN_WIDTH = size.x;
			SCREEN_HEIGHT = size.y;
		} else {
			SCREEN_WIDTH = display.getWidth();
			SCREEN_HEIGHT = display.getHeight();
		}
	}

	public static void showWaitingDialog(Context context) {
		waitingDialog = new ProgressDialog(context) {
			@Override
			public void onBackPressed() {
			}
		};
		waitingDialog.setCanceledOnTouchOutside(false);
		waitingDialog.setCancelable(false);
		waitingDialog.setMessage("Loading...");
		waitingDialog.show();
	}

	public static void cancelWaitingDialog() {
		if (waitingDialog != null) {
			waitingDialog.dismiss();
		}
	}

	public static void finalize(Context context) {
		SharedPreferences pref;
		pref = context.getSharedPreferences(DatabaseConstants.SHARED_PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(DatabaseConstants.otherFields.MARKET_RETURN_ID, DatabaseConstants.MARKET_RETURN_ID);
		editor.commit();
	}
/*

	public static void importDatabaseFromSdCard(Context context) {
		if (!Build.BRAND.equalsIgnoreCase("generic"))    // not in emulator
		{
			OutputStream output;
			try {

				String databasePath = DatabaseConstants.DATABASE_LOCAL_PATH;
				File databaseDirectory = new File(databasePath);
				if (!databaseDirectory.exists()) {
					databaseDirectory.mkdirs();
				}
				output = new FileOutputStream(DatabaseConstants.DATABASE_LOCAL_PATH + "/" + DatabaseConstants.DATABASE_NAME);
				File directory = new File(Environment.getExternalStorageDirectory() + "/Database");
				InputStream input = new FileInputStream(directory.getPath() + "/" + DatabaseConstants.DATABASE_NAME);
				byte[] buffer = new byte[102400];
				int length;
				while ((length = input.read(buffer)) > 0)
					output.write(buffer, 0, length);

				output.flush();
				output.close();
				input.close();
			} catch (FileNotFoundException e) {
				Toast.makeText(context, "Database Import Error", Toast.LENGTH_SHORT).show();
				DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 0;
				e.printStackTrace();
				return;
			} catch (IOException e) {
				Toast.makeText(context, "Database Import Error", Toast.LENGTH_SHORT).show();
				DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 0;
				e.printStackTrace();
				return;
			}
		}
		Toast.makeText(context, "Database Import Successful", Toast.LENGTH_SHORT).show();
		DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG = 1;
	}

	public static void exportDatabaseToSdCard(Context context) {
		if (DatabaseConstants.DATABASE_IMPORT_SUCCESS_FLAG == 0) {
			Toast.makeText(context, "Database Export Error", Toast.LENGTH_SHORT).show();
			return;
		}
		OutputStream output;
		try {
			output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Database/" + DatabaseConstants.DATABASE_NAME);

			File directory = new File(DatabaseConstants.DATABASE_LOCAL_PATH);
			InputStream input = new FileInputStream(directory.getPath() + "/" + DatabaseConstants.DATABASE_NAME);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) > 0)
				output.write(buffer, 0, length);

			output.flush();
			output.close();
			input.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(context, "Database Export Error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Toast.makeText(context, "Database Export Error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}

		showAlert(context, "Database export succesful");
	}
*/

	public static void showAlert(Context context, String msg) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("Alert Message");
		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

	public static OrderItem getOrderTotalBySkuId(Context context, String skuId, OutletVisitPageActivity outletVisitPageActivity) {
		OrderItem returnOrder = new OrderItem();
		returnOrder.Carton = 0;
		returnOrder.Total = 0;
		returnOrder.Piece = 0;

		ArrayList<OrderItem> orderList = outletVisitPageActivity.getOrderedItemList();
		for (int i = 0; i < orderList.size(); i++) {
			if (Objects.equals(orderList.get(i).SKUID, skuId)) {
				returnOrder.Carton += orderList.get(i).Carton;
				returnOrder.Piece += orderList.get(i).Piece;
				returnOrder.Total += orderList.get(i).Total;
			}
		}
		return returnOrder;
	}


	public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener mListener;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), mListener, year, month, day);
		}

        public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
            mListener = listener;
        }
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static boolean deleteStoredFile(String path) {
		File fdelete = new File(path);
		return fdelete.delete();
	}

}
