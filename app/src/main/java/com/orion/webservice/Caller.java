package com.orion.webservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.orion.application.RegisterPageActivity;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Brand;
import com.orion.entities.Channel;
import com.orion.entities.Image;
import com.orion.entities.MarketReturnItem;
import com.orion.entities.Order;
import com.orion.entities.OrderItem;
import com.orion.entities.Outlet;
import com.orion.entities.Reason;
import com.orion.entities.Section;
import com.orion.entities.Sku;
import com.orion.entities.Slab;
import com.orion.entities.Tpr;
import com.orion.entities.User;
import com.orion.database.DatabaseConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Caller implements IAsyncResponse {

    private ProgressDialog Dialog;
    public Context context;
    public User user;
    private Handler handler;
    private Sku sku;
    private Reason reason;
    private MarketReturnItem marketReturnItem;
    private Section section;
    private Outlet outlet;
    private Brand brand;
    private Channel channel;
    private Tpr tpr;
    private Slab slab;
    public static String Status = "";



    public void UploadNewOutletToWeb(Context context, Handler handler, String wsUserID, String wsPassword) {
        this.context = context;
        this.handler = handler;
        user = DatabaseQueryUtil.getUser(context);

        String NewOutletJsonString[] = NewOutletListToJsonString(context, user);
        String jsonOutletList = NewOutletJsonString[0];
        String jsonOutletImageList = "[{\"Image\":\" \",\"OutletID\":\"1\",\"ImageType\":\"0\",\"CaptureDate\":\"20\\/09\\/2016\"}]";
        // NewOutletJsonString[1];

        List<PropertyInfo> newOutletProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(user.mobile_No);
        newOutletProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(user.password);
        newOutletProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("outletData");
        pi.setValue(jsonOutletList);
        newOutletProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("outletImageData");
        pi.setValue("Image");
        pi.setValue(jsonOutletImageList);
        newOutletProperty.add(pi);


        System.out.println(WebServiceConstants.SOAP_ADDRESS + ": "
                + WebServiceConstants.Method_SaveNewOutletDetailsInfo + ": "
                + WebServiceConstants.PARAMETER_NAME);

        // Log.e("UserDetailString", (NewOutletJsonString[0] + NewOutletJsonString[1]));
        Log.e("UserDetailString", (NewOutletJsonString[0]));

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_SaveNewOutletDetailsInfo,
                WebServiceConstants.PARAMETER_NAME, jsonOutletList, jsonOutletImageList, newOutletProperty);//jsonOutletImageList
        objAsyncTask.execute();

    }

    public void UploadDataToWeb(Context context, Handler handler, String wsUserID, String wsPassword) {
        this.context = context;
        this.handler = handler;

        user = DatabaseQueryUtil.getUser(context);
        String OrderJsonString[] = OutletListToJsonString(context, user, null);
        String OrderListJsonString = OrderJsonString[0];
        String OrderItemtListJsonString = OrderJsonString[1];
        String MarketReturnJsonString = OrderJsonString[2];

        List<PropertyInfo> orderProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(user.mobile_No);//pi.setValue(user.dsrId);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(user.password);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("orderData");
        pi.setValue(OrderListJsonString);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("orderItemData");
        pi.setValue(OrderItemtListJsonString);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("marketReturnItem");
        pi.setValue(MarketReturnJsonString);
        orderProperty.add(pi);


        System.out.println(WebServiceConstants.SOAP_ADDRESS + ": "
                + WebServiceConstants.Method_SaveOrderDetailsInfo + ": "
                + WebServiceConstants.PARAMETER_NAME);

//		if (user.sentStatus == 0) {
//			String UserJsonString = UserToJsonString(user);
//			Log.e("UserString", UserJsonString);
//			System.out.println(UserJsonString);
//
//			//String OutletListJsonString = OutletListToJsonString(context, user);
//			//Log.e("UserDetailString", OutletListJsonString);
//
//			AsyncTaskHelper objAsyncTask = new AsyncTaskHelper(this,
//					WebServiceConstants.SOAP_ADDRESS,
//					WebServiceConstants.Method_SaveOrderMasterInfo,
//					WebServiceConstants.PARAMETER_NAME, UserJsonString);
//			objAsyncTask.execute();
//		} else {
//		String OrderJsonString[] = OutletListToJsonString(context, user);
//		String OrderListJsonString = OrderJsonString[0];
//		String OrderItemtListJsonString = OrderJsonString[1];
        Log.e("UserDetailString", (OrderJsonString[0] + OrderJsonString[1] + OrderJsonString[2]));


        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_SaveOrderDetailsInfo,
                WebServiceConstants.PARAMETER_NAME, OrderListJsonString, OrderItemtListJsonString, MarketReturnJsonString, orderProperty);
        /**
         * This is where we get the order string
         */
        objAsyncTask.execute();
   // }



    }

    public void
    UploadDataToWeb(Context context, Handler handler, String wsUserID, String password, String outletID) {
        this.context = context;
        this.handler = handler;

        user = DatabaseQueryUtil.getUser(context);
        String OrderJsonString[] = OutletListToJsonString(context, user, outletID);
        String OrderListJsonString = OrderJsonString[0];
        String OrderItemtListJsonString = OrderJsonString[1];
        String MarketReturnJsonString = OrderJsonString[2];

        List<PropertyInfo> orderProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(wsUserID);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("orderData");
        pi.setValue(OrderListJsonString);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("orderItemData");
        pi.setValue(OrderItemtListJsonString);
        orderProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("marketReturnItem");
        pi.setValue(MarketReturnJsonString);
        orderProperty.add(pi);


        System.out.println(WebServiceConstants.SOAP_ADDRESS + ": "
                + WebServiceConstants.Method_SaveOrderDetailsInfo + ": "
                + WebServiceConstants.PARAMETER_NAME);

//		if (user.sentStatus == 0) {
//			String UserJsonString = UserToJsonString(user);
//			Log.e("UserString", UserJsonString);
//			System.out.println(UserJsonString);
//
//			//String OutletListJsonString = OutletListToJsonString(context, user);
//			//Log.e("UserDetailString", OutletListJsonString);
//
//			AsyncTaskHelper objAsyncTask = new AsyncTaskHelper(this,
//					WebServiceConstants.SOAP_ADDRESS,
//					WebServiceConstants.Method_SaveOrderMasterInfo,
//					WebServiceConstants.PARAMETER_NAME, UserJsonString);
//			objAsyncTask.execute();
//		} else {
//		String OrderJsonString[] = OutletListToJsonString(context, user);
//		String OrderListJsonString = OrderJsonString[0];
//		String OrderItemtListJsonString = OrderJsonString[1];
        Log.e("UserDetailString", (OrderJsonString[0] + OrderJsonString[1] + OrderJsonString[2]));
        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_SaveOrderDetailsInfo,
                WebServiceConstants.PARAMETER_NAME, OrderListJsonString, OrderItemtListJsonString, MarketReturnJsonString, orderProperty);
        objAsyncTask.execute();
//		}
    }

    //region Get Functions
    public void GetBrandsFromWeb(Context context, Handler handler, String wsUserID, String password, int systemID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(wsUserID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(systemID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetBrands,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetChannelsFromWeb(Context context, Handler handler, String userID, String password, int systemID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(systemID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetChannels,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetHHTUsersFromWeb(Context context, Handler handler, String userID, String password, String mobile, String userPassword) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        pi.setType(String.class);

        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        pi.setType(String.class);

        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("mobile");
        pi.setValue(mobile);
        pi.setType(String.class);
        pi.setType(String.class);

        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("Userpassword");
        pi.setValue(userPassword);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetHHTUsers,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetDSRBasicInfosFromWeb(Context context, Handler handler, String userID, String password, User user) {
        this.context = context;
        this.handler = handler;
        this.user = user;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("srID");
        pi.setValue(user.dsrId);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(user.company_ID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetDSRBasicInfos,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetSKUsFromWeb(Context context, Handler handler, String userID, String password, int companyID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetSKUs,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetSectionFromWeb(Context context, Handler handler, String userID, String password, String SRID, int companyID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("srID");
        pi.setValue(SRID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetSection,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetNoOrderReasonInfoFromWeb(Context context, Handler handler, String userID, String password, int companyID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetNoOrderReasonInfo,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetMarketReturnReasonFromWeb(Context context, Handler handler, String userID, String password, int companyID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetMarketReturnReason,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetOutletInfoFromWeb(Context context, Handler handler, String userID, String password, String SRID, int companyID) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> brandProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("srID");
        pi.setValue(SRID);
        brandProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);

        pi.setType(String.class);
        brandProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetOutletInfos,
                WebServiceConstants.PARAMETER_NAME, "", brandProperty);
        objAsyncTask.execute();
    }

    public void GetSalesPromotionsFromWeb(Context context, Handler handler, String userID, String password, int companyID, String operationDate) {
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> spProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        spProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        spProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("companyID");
        pi.setValue(companyID);
        spProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("operationDate");
        pi.setValue(operationDate);
        pi.setType(String.class);
        spProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetTPR,
                WebServiceConstants.PARAMETER_NAME, "", spProperty);
        objAsyncTask.execute();
    }

    public void GetSPChannelSKUFromWeb(Context context, Handler handler, String userID, String password, int companyID, String operationDate) {

        List<PropertyInfo> spChnlSKUProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        spChnlSKUProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        spChnlSKUProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("companyID");
        pi.setValue(companyID);
        spChnlSKUProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("operationDate");
        pi.setValue(operationDate);
        pi.setType(String.class);
        spChnlSKUProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetTPRSKUChnl,
                WebServiceConstants.PARAMETER_NAME, "", spChnlSKUProperty);
        objAsyncTask.execute();
    }

    public void GetSPSlabFromWeb(Context context, Handler handler, String userID, String password, int companyID, String operationDate) {

        List<PropertyInfo> spSlabProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        spSlabProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        spSlabProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("companyID");
        pi.setValue(companyID);
        spSlabProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("operationDate");
        pi.setValue(operationDate);
        pi.setType(String.class);
        spSlabProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetTPRSlab,
                WebServiceConstants.PARAMETER_NAME, "", spSlabProperty);
        objAsyncTask.execute();
    }

    public void GetSPBonusesFromWeb(Context context, Handler handler, String userID, String password, int companyID, String operationDate) {

        List<PropertyInfo> spBonusesProperty = new ArrayList<PropertyInfo>();

        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        spBonusesProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        spBonusesProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("companyID");
        pi.setValue(companyID);
        spBonusesProperty.add(pi);

        pi = new PropertyInfo();
        pi.setName("operationDate");
        pi.setValue(operationDate);
        pi.setType(String.class);
        spBonusesProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetSlabItem,
                WebServiceConstants.PARAMETER_NAME, "", spBonusesProperty);
        objAsyncTask.execute();
    }
    //endregion

    public <T> void onTaskComplete(T result, T webMethod) {

        if (!result.toString().toUpperCase().contains("EXCEPTION")) {
            if (webMethod.toString().equals(
                    WebServiceConstants.Method_SaveOrderMasterInfo)) {

                user = DatabaseQueryUtil.getUser(context);
//				if (user.isOrderCompleted == 0) {
//
//				String OutletListJsonString = OutletListToJsonString(
//						context, user);
//
//				AsyncTaskHelper objAsyncTask = new AsyncTaskHelper(this,
//						WebServiceConstants.SOAP_ADDRESS,
//						WebServiceConstants.Method_SaveOrderDetailsInfo,
//						WebServiceConstants.PARAMETER_NAME,
//						OutletListJsonString);

//				objAsyncTask.execute();
            } else {

                if (null != Dialog && Dialog.isShowing()) {
                    Dialog.dismiss();
                }
                handler.sendEmptyMessage(0);
            }
        } else if (webMethod.toString().equals(
                WebServiceConstants.Method_SaveOrderDetailsInfo)) {
            if (result.toString() != null) {
                UpdateOutletWebDetailsKeyFromJson(result.toString(), user,
                        context);
            }
            if (null != Dialog && Dialog.isShowing()) {
                Dialog.dismiss();
            }
            handler.sendEmptyMessage(0);
            Toast.makeText(context, "Data successfully uploaded to web",
                    Toast.LENGTH_SHORT).show();
        } else if (webMethod.toString().equals(
                WebServiceConstants.Method_SaveNewOutletDetailsInfo)) {
            if (result.toString() != null) {
                // TODO :Delete New Outlet after upload
            }
            if (null != Dialog && Dialog.isShowing()) {
                Dialog.dismiss();
            }
            handler.sendEmptyMessage(0);
            Toast.makeText(context, "Data successfully uploaded to web",
                    Toast.LENGTH_SHORT).show();
        } else {
//			DatabaseQueryUtil.updateTblDSRBasicWithOrderCompleteStatus(context,
//					0);
            if (null != Dialog && Dialog.isShowing()) {
                Dialog.dismiss();
            }

            AlertDialog.Builder confirmmsg = new AlertDialog.Builder(context);
            // confirmmsg.setIcon((false) ? R.drawable.success :
            // R.drawable.fail);
            confirmmsg.setTitle("Connection Error");
            confirmmsg
                    .setMessage("Error occured while communicating to server\r\n"
                            + result.toString());
            confirmmsg.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            dialog.cancel();
                            handler.sendEmptyMessage(0);
                        }
                    });
            confirmmsg.show();
        }
    }

    public <T> void onTaskDataGetComplete(T result, T webMethod) {
        Log.v("Trace", "onTaskDataGetComplete");

        if (result != null) {
            if (!result.toString().toUpperCase().contains("EXCEPTION")) {
                if (webMethod.toString().equals(WebServiceConstants.Method_GetHHTUsers)) {

                    //False,Registered,Blocked
                    if (result.toString().equals("False")) {
                        Toast.makeText(context, "Not Authentic User !",
                                Toast.LENGTH_SHORT).show();
                        Status = "False";
                    } else if (result.toString().equals("Registered")) {
                        Toast.makeText(context, "You already a registered User !",
                                Toast.LENGTH_SHORT).show();
                        Status = "Registered";
                    } else if (result.toString().equals("Blocked")) {
                        Toast.makeText(context, "Your Account is blocked !",
                                Toast.LENGTH_SHORT).show();
                        Status = "Blocked";
                    } else if (result.toString().length() <= 0) {
                        Toast.makeText(context, "No such SR found! Please try again!",
                                Toast.LENGTH_SHORT).show();
                        Status = "False";
                    } else {
                        try {
                            if (result.toString() != null) {
                                JSONArray jsonMainNode = new JSONArray(result.toString());
                                if (!(jsonMainNode.length() <= 0)) {
                                    RegisterPageActivity newRegistry = new RegisterPageActivity();
                                    User newUser = new User();
                                    Toast.makeText(context, "Authentic User !",
                                            Toast.LENGTH_SHORT).show();
                                    Status = "Authentic";
                                    for (int i = 0; i < jsonMainNode.length(); i++) {
                                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                        /******* Fetch node values **********/
                                        newUser.company_ID = Integer.parseInt(jsonChildNode
                                                .optString("SystemID"));
                                        newUser.dsrId = jsonChildNode.optString("SRCode");
                                        newUser.mobile_No = jsonChildNode.optString("UserID");
                                        newUser.password = jsonChildNode.optString("Password");
                                    }
                                    GetDSRBasicInfosFromWeb(context, handler, newUser.mobile_No, newUser.password, newUser);
                                } else {
                                    Toast.makeText(context, "No such SR found! Please try again!",
                                            Toast.LENGTH_SHORT).show();
                                    Status = "False";
                                }

                            }
                        } catch (JSONException e) {
                            Status = "Errored";
                            Toast.makeText(context, "errors happen here" + e.toString(), Toast.LENGTH_LONG);
                            e.printStackTrace();
                        }
                    }
                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_SaveOrderDetailsInfo)) {

                    Toast.makeText(context, "Data successfully inserted into table",
                            Toast.LENGTH_SHORT).show();
                    if (result.toString() != null) {
                        if (result.equals("Exception")) {
                            handler.sendEmptyMessage(0);
                            Toast.makeText(context, "Data upload unsuccessful",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            UpdateOutletWebDetailsKeyFromJson(result.toString(), user,
                                    context);
                            handler.sendEmptyMessage(0);
                            Toast.makeText(context, "Data successfully uploaded to web",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetBrands)) {
                    if (result.toString() != null) {
                        insertTblBrands(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetChannels)) {
                    if (result.toString() != null) {
                        insertTblChannels(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }
                    //handler.sendEmptyMessage(0);

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetSKUs)) {
                    if (result.toString() != null) {
                        insertTblSKU(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetNoOrderReasonInfo)) {
                    if (result.toString() != null) {
                       // insertTblNoOrderReason(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetSection)) {
                    if (result.toString() != null) {
                        insertTblSection(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetOutletInfos)) {
                    if (result.toString() != null) {
                        insertTblOutlet(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetDSRBasicInfos)) {
                    if (result.toString() != null) {
                        insertTblDSRBasic(context, result.toString(), user);
                    }
                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetMarketReturnReason)) {
                    if (result.toString() != null) {
                        //insertTblMarketReason(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetTPR)) {
                    if (result.toString() != null) {
                        DatabaseQueryUtil.deleteTblTPR(context);
                        insertTblTPR(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetTPRSKUChnl)) {
                    if (result.toString() != null) {
                        DatabaseQueryUtil.deleteTblTPRSKUChnl(context);
                        insertTblTPRSKUChnl(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetTPRSlab)) {
                    if (result.toString() != null) {
                        DatabaseQueryUtil.deleteTblTPRSlab(context);
                        insertTblTPRSlab(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_GetSlabItem)) {
                    if (result.toString() != null) {
                        DatabaseQueryUtil.deleteTblSlabItem(context);
                        insertTblSlabItem(context, result.toString(), user);
                        Toast.makeText(context, "Data successfully inserted into table",
                                Toast.LENGTH_SHORT).show();
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }

                } else if (webMethod.toString().equals(
                        WebServiceConstants.Method_SaveNewOutletDetailsInfo)) {
                    if (result.toString() != null) {
                        if (result.toString().equals("OK")) {
                            DatabaseQueryUtil.deleteAllPendingNewOutlet(context, null);
                        } else if (result.toString().equals("Error Occured")) {
                            Toast.makeText(context, "Error Occured.Please Try Again.",
                                    Toast.LENGTH_SHORT).show();

                        } else if (result.toString().equals("NAU")) {
                            Toast.makeText(context, "You are not authorise to upload order via mobile.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }
                    handler.sendEmptyMessage(0);
                }
                else if (webMethod.toString().equals(WebServiceConstants.Method_GetDSRDailyTargetInfos))
                {
                    if(result.toString()!= null) {
                        updateTblDSRBasic(context, result.toString(), user);
                    }
                    if (null != Dialog && Dialog.isShowing()) {
                        Dialog.dismiss();
                    }
                    Toast.makeText(context, "Data successfully inserted into table",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!result.toString().toUpperCase().contains("EXCEPTION") || result.equals(null)) {


                    AlertDialog.Builder confirmmsg = new AlertDialog.Builder(context);
                    // confirmmsg.setIcon((false) ? R.drawable.success :
                    // R.drawable.fail);
                    confirmmsg.setTitle("Connection Error");
                    confirmmsg
                            .setMessage("Error occured while communicating to server\r\n"
                                    + result.toString());
                    confirmmsg.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.cancel();
                                    handler.sendEmptyMessage(0);
                                }
                            });
                    confirmmsg.show();
                }
            }
        } else {
            if (webMethod.toString().equals(
                    WebServiceConstants.Method_SaveNewOutletDetailsInfo)) {


                handler.sendEmptyMessage(0);

            }
        }
    }

    public void GetDSRDailyTargetInfoFromWeb(Context context, Handler handler, String userID, String password, int companyID,String dsrId){
        this.context = context;
        this.handler = handler;

        List<PropertyInfo> dsrInfoProperty=new ArrayList<PropertyInfo>();

        PropertyInfo pi=new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        dsrInfoProperty.add(pi);

        pi=new PropertyInfo();
        pi.setName("password");
        pi.setValue(password);
        dsrInfoProperty.add(pi);

        pi=new PropertyInfo();
        pi.setName("srID");
        pi.setValue(dsrId);
        dsrInfoProperty.add(pi);

        pi=new PropertyInfo();
        pi.setName("systemID");
        pi.setValue(companyID);
        dsrInfoProperty.add(pi);

        pi.setType(String.class);
        dsrInfoProperty.add(pi);

        AsyncTaskHelperForGetData objAsyncTask = new AsyncTaskHelperForGetData(this,
                WebServiceConstants.SOAP_ADDRESS,
                WebServiceConstants.Method_GetDSRDailyTargetInfos,
                WebServiceConstants.PARAMETER_NAME, "",dsrInfoProperty);
        objAsyncTask.execute();
    }

    //region update

    private void updateTblDSRBasic(Context context, String JsonString, User user) {
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            if (user == null)
                user = new User();

            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

            user.dsrId = jsonChildNode.optString(jsonChildNode.optString("DSRID"));
            user.target = Double.parseDouble(jsonChildNode.optString("target"));
            user.orderAchieved = Double.parseDouble(jsonChildNode.optString("OrderAchvd"));
            user.targetRemain = Double.parseDouble(jsonChildNode.optString("TargetRem"));
            user.dayRemain = Integer.parseInt(jsonChildNode.optString("DayRemain"));
            user.dailyTarget = Double.parseDouble(jsonChildNode.optString("DailyTarget"));

            DatabaseQueryUtil.updateTblDSRBasicWithTargetInformation(context, user);

            Toast.makeText(context, "Data successfully inserted into table",
                    Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void UpdateOutletWebDetailsKeyFromJson(String JsonString,
                                                   User user, Context context) {

        try {
            if (JsonString != "Data insert error!") {
                JSONArray jsonMainNode = new JSONArray(JsonString);
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    /******* Fetch node values **********/
                    String RefNo = jsonChildNode
                            .optString("RefNo");
                    String OutletID = (jsonChildNode.optString(
                            "OutletID"));
                    int OrderNo = Integer.parseInt(jsonChildNode.optString(
                            "OrderNo"));
                    double OrderTotal = DatabaseQueryUtil.getOrderTotalByOrderNo(context, OrderNo);
                    if ((RefNo != null) && (OrderNo != 0)) {
                        DatabaseQueryUtil.updateTblOutletWebOrderDetailKey(context,
                                OutletID, RefNo, OrderNo);
                        DatabaseQueryUtil.insertOrderToOrderSummary(context, OrderNo, OutletID, OrderTotal);

                    }
                }
                DatabaseQueryUtil.deleteAllOrder(context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //region Comment Code
//	public void OrderCompleteConfirmationToWeb(Context context, Handler handler) {
//		this.context = context;
//		this.handler = handler;
//		Dialog = new ProgressDialog(this.context);
//		Dialog.setCanceledOnTouchOutside(true);
//		Dialog.setCancelable(true);
//		Dialog.setMessage("Please wait..");
//		Dialog.show();
//
//		User user = DatabaseQueryUtil.getUser(context);
//
//		String UserJsonString = UserToJsonString(user);
//
//		AsyncTaskHelper objAsyncTask = new AsyncTaskHelper(this,
//				user.webServiceLink,
//				WebServiceConstants.Method_SaveOrderMasterInfo,
//				WebServiceConstants.PARAMETER_NAME, UserJsonString);
//
//		objAsyncTask.execute();
//	}
//	private static String UserToJsonString(User user) {
//		JSONObject eachJsonObject = new JSONObject();
//
//		try {
//			eachJsonObject.put("WebOrderMasterID",
//					String.valueOf(user.webOrderMasterId));
//			eachJsonObject.put("SectionID", String.valueOf(user.sectionId));
//			eachJsonObject.put("SectionName", String.valueOf(user.sectionName));
//			eachJsonObject.put("TownID", String.valueOf(user.townId));
//			eachJsonObject.put("DsrID", String.valueOf(user.dsrId));
//			eachJsonObject.put("DeliveryGroup",
//					String.valueOf(user.deliveryGroup));
//			eachJsonObject.put("ExclusiveChannel",
//					String.valueOf(user.exclusiveChannel));
//			eachJsonObject.put("Date", String.valueOf(user.visitDate));
//			eachJsonObject.put("DailyTarget", String.valueOf(user.dailyTarget));
//			eachJsonObject.put("RoamerLogID", String.valueOf(user.roamerLogId));
//			eachJsonObject.put("TotalOutlet", String.valueOf(user.TotalOutlet));
//
//			if (user.isOrderCompleted == 1) {
//				eachJsonObject.put("IsCompleted", "true");
//			}
//
//			else if (user.isOrderCompleted == 0) {
//				eachJsonObject.put("IsCompleted", "false");
//			}
//
//		} catch (JSONException e) {
//
//			e.printStackTrace();
//		}
//
//		return eachJsonObject.toString();
//	}

//	public static String OutletListToJsonString(Context context, User user) {
////		ArrayList<Outlet> NotSentOutletList = DatabaseQueryUtil
////				.getOutletListWithNotSentStatus(context);
//		JSONArray jsonOutletArr = new JSONArray();
//		JSONObject eachOutletJsonObject;
//
//		ArrayList<Order> oOrder;
//		JSONArray jsonOrderSkuArr = new JSONArray();
//		JSONObject eachOrderSkuJsonObject;
//
//		ArrayList<MarketReturnItem> oMarketReturnItem;
//		JSONArray jsonMarketReturnItemArr = new JSONArray();
//		JSONObject eachMarketReturnItemJsonObject;
//
//		try {
//
//			for (Outlet i : NotSentOutletList) {
//
//				eachOutletJsonObject = new JSONObject();
////				eachOutletJsonObject.put("OrderDetailsID",
////						String.valueOf(i.webOrderDetailKey));
//				eachOutletJsonObject.put("OrderDate",
//						String.valueOf(user.visitDate));
//				eachOutletJsonObject
//						.put("OutletID", String.valueOf(i.outletId));
//				eachOutletJsonObject.put("OutletLatitude",
//						String.valueOf(i.outletlatitude));
//				eachOutletJsonObject.put("OutletLongitude",
//						String.valueOf(i.outletlongitude));
//				eachOutletJsonObject.put("ChannelID",
//						String.valueOf(i.channelId));
////				eachOutletJsonObject.put("SectionID",
////						String.valueOf(i.sectionId));
//				eachOutletJsonObject.put("TownID", String.valueOf(user.townId));
//				eachOutletJsonObject.put("RouteID",
//						String.valueOf(user.routeId));
//				eachOutletJsonObject.put("DsrID", String.valueOf(user.dsrId));
////				eachOutletJsonObject.put("CheckInTime",
////						String.valueOf(i.checkInTime));
////				eachOutletJsonObject.put("CheckOutTime",
////						String.valueOf(i.checkOutTime));
////				eachOutletJsonObject.put("OrderLatitude",
////						String.valueOf(i.orderlatitude));
////				eachOutletJsonObject.put("OrderLongitude",
////						String.valueOf(i.orderlongitude));
////				eachOutletJsonObject.put("OrderAmount",
////						String.valueOf(i.orderTotal));
////				eachOutletJsonObject.put("TotalLineSold",
////						String.valueOf(i.totalLineSold));
//				eachOutletJsonObject.put("VisitStatus",
//						String.valueOf(i.visited));
//				eachOutletJsonObject.put("OrderMasterID",
//						String.valueOf(user.webOrderMasterId));
////				if (i.noOrderReason > 0) {
////					Reason reason = DatabaseQueryUtil.getReason(context,
////							i.noOrderReason, DatabaseConstants.NO_ORDER_REASON);
////					eachOutletJsonObject.put("Reason",
////							String.valueOf(reason.description));
////				}
//				jsonOutletArr.put(eachOutletJsonObject);
//
//				// Fetching SKU Order info;
//
//				oOrder = DatabaseQueryUtil.getAllOrderByOutlet(context,
//						i.outletId);
//				for (Order order : oOrder) {
//					eachOrderSkuJsonObject = new JSONObject();
//					eachOrderSkuJsonObject.put("TownID",
//							String.valueOf(user.townId));
//					eachOrderSkuJsonObject.put("OutletID",
//							String.valueOf(order.OutletID));
//					eachOrderSkuJsonObject.put("SkuID",
//							String.valueOf(order.SKUID));
//					eachOrderSkuJsonObject.put("Carton",
//							String.valueOf(order.Carton));
//					eachOrderSkuJsonObject.put("Piece",
//							String.valueOf(order.Piece));
//					eachOrderSkuJsonObject.put("Carton_Org",
//							String.valueOf(order.Carton_Org));
//					eachOrderSkuJsonObject.put("Piece_Org",
//							String.valueOf(order.Piece_Org));
//					eachOrderSkuJsonObject.put("Suggested",
//							String.valueOf(order.Suggested));
//					eachOrderSkuJsonObject.put("BrandID",
//							String.valueOf(order.BrandID));
//					eachOrderSkuJsonObject.put("TotalAmount",
//							String.valueOf(order.Total));
//					eachOrderSkuJsonObject.put("TkOff",
//							String.valueOf(order.TkOff));
//					jsonOrderSkuArr.put(eachOrderSkuJsonObject);
//				}
//
//				// Fetching Market Return info;
//
//				oMarketReturnItem = DatabaseQueryUtil
//						.getMarketReturnItemByOutlet(context, i.outletId);
//
//				for (MarketReturnItem marketReturnItem : oMarketReturnItem) {
//					eachMarketReturnItemJsonObject = new JSONObject();
//					eachMarketReturnItemJsonObject.put("TownID",
//							String.valueOf(user.townId));
//					eachMarketReturnItemJsonObject.put("OutletID",
//							String.valueOf(marketReturnItem.outletId));
//					eachMarketReturnItemJsonObject.put("SkuID",
//							String.valueOf(marketReturnItem.SKUID));
//					eachMarketReturnItemJsonObject.put("BrandID",
//							String.valueOf(marketReturnItem.brandId));
//					eachMarketReturnItemJsonObject.put("ExpDate",
//							String.valueOf(marketReturnItem.expDate));
//					eachMarketReturnItemJsonObject.put("MarketReasonID",
//							String.valueOf(marketReturnItem.marketReasonId));
//					eachMarketReturnItemJsonObject.put("Quantity",
//							String.valueOf(marketReturnItem.qty));
//					eachMarketReturnItemJsonObject.put("Batch",
//							String.valueOf(marketReturnItem.batch));
//					jsonMarketReturnItemArr.put(eachMarketReturnItemJsonObject);
//				}
//			}
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return jsonOutletArr.toString() + "~" + jsonOrderSkuArr.toString()
//				+ "~" + jsonMarketReturnItemArr.toString();
//	}
    //endregion

    //region Insert Functions
    private void insertTblBrands(Context context, String JsonString, User user) {
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            brand = new Brand();
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                /******* Fetch node values **********/
                brand.brandId = (jsonChildNode
                        .optString("BrandID"));
                brand.name = jsonChildNode.optString("Name");

                if ((brand.brandId != null) && (brand.name != null)) {
                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, brand.brandId, "tblBrand", DatabaseConstants.tblBrand.BRAND_ID);
                    if (IsExist) {
                        DatabaseQueryUtil.insertTblBrands(context, brand);
                    } else
                        DatabaseQueryUtil.updateTblBrands(context, brand);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void insertTblNoOrderReason(Context context, String JsonString, User user) {
//        try {
//            Reason reason = new Reason();
//            JSONArray jsonMainNode = new JSONArray(JsonString);
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//
//                /******* Fetch node values **********/
//                reason.reasonId = Integer.parseInt(jsonChildNode
//                        .optString("NoOrderReasonID"));
//                reason.description = jsonChildNode.optString("Name");
//
//                if ((reason.reasonId != 0) && (reason.description != null)) {
//                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, reason.reasonId, "tblNoOrderReason", DatabaseConstants.tblNoOrderReason.REASON_ID);
//                    if (IsExist) {
//                        DatabaseQueryUtil.insertTblNoOrderReason(context, reason);
//                    } else
//                        DatabaseQueryUtil.updatetTblNoOrderReason(context, reason);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void insertTblChannels(Context context, String JsonString, User user) {
        try {
            channel = new Channel();
            JSONArray jsonMainNode = new JSONArray(JsonString);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                /******* Fetch node values **********/
                channel.channelID = (jsonChildNode
                        .optString("ChannelID"));
                channel.name = jsonChildNode.optString("Name");

                if ((channel.channelID != null) && (channel.name != null)) {
                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, channel.channelID, "tblChannel", DatabaseConstants.tblChannel.CHANNEL_ID);
                    if (IsExist) {
                        DatabaseQueryUtil.insertTblChannels(context, channel);
                    } else
                        DatabaseQueryUtil.updatetTblChannels(context, channel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblDSRBasic(Context context, String JsonString, User user) {
        //user.visitDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                //user.sectionId = Integer.parseInt(jsonChildNode.optString("SectionID").toString());
                user.name = jsonChildNode.optString("NAME");

                user.visitDate = jsonChildNode.optString("VisitDate");

                //user.orderAchieved = Double.parseDouble(jsonChildNode.optString("OrderAchvd").toString());
                //user.dayRemain = Integer.parseInt(jsonChildNode.optString("DayRemain").toString());
                //user.targetRemain = Double.parseDouble(jsonChildNode.optString("TargetRem").toString());
                user.dailyTarget = Double.parseDouble(jsonChildNode.optString("DailyTarget"));
                //user.visitDate = jsonChildNode.optString("VisitDate").toString();
                //user.pdaUser =jsonChildNode.optString("PDAUser").toString();
                //user.dsrId = Integer.parseInt(jsonChildNode.optString("DSRID").toString());
                user.target = Double.parseDouble(jsonChildNode.optString("Target"));
                user.distributorName = jsonChildNode.optString("DistributorName");
                user.distributorAddress = jsonChildNode.optString("DistributorAddress");
                //user.roamerLogId = Integer.parseInt(jsonChildNode.optString("RoamerLogID").toString());
                //user.company_ID = Integer.parseInt(jsonChildNode.optString("Company_ID").toString());
                //user.mobile_No =  jsonChildNode.optString("Mobile_No").toString();
                //user.password = jsonChildNode.optString("Password").toString();
                //user.lastUpdateTime = jsonChildNode.optString("LastUpdateTime").toString();
                DatabaseQueryUtil.insertTblDSRBasic(context, user);
            }

            Toast.makeText(context, "Data successfully inserted into table",
                    Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void insertTblMarketReason(Context context, String JsonString, User user) {
//        try {
//            marketReturnItem = new MarketReturnItem();
//            JSONArray jsonMainNode = new JSONArray(JsonString);
//            for (int i = 0; i < jsonMainNode.length(); i++) {
//                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//
//                marketReturnItem.marketReasonId = Integer.parseInt(jsonChildNode
//                        .optString("ReasonID"));
//                marketReturnItem.reasonDescription = jsonChildNode
//                        .optString("Title");
//                String code = jsonChildNode
//                        .optString("Code");
//
//                if (marketReturnItem.marketReasonId != 0 && marketReturnItem.reasonDescription != null) {
//                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, marketReturnItem.marketReasonId, "tblMarketReason", DatabaseConstants.tblMarketReturn.MARKET_REASON_ID);
//                    if (IsExist) {
//                        DatabaseQueryUtil.insertTblMarketReason(context, marketReturnItem, code);
//                    } else
//                        DatabaseQueryUtil.updateTblMarketReason(context, marketReturnItem, code);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void insertTblSKU(Context context, String JsonString, User user) {
        try {
            sku = new Sku();
            JSONArray jsonMainNode = new JSONArray(JsonString);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                sku.skuId = (jsonChildNode
                        .optString("SKUCode"));
                sku.title = jsonChildNode.optString("Title");
                sku.brandId = (jsonChildNode
                        .optString("BrandID"));
                sku.pcsRate = Double.parseDouble(jsonChildNode
                        .optString("PcsRate"));
                sku.pcsPerCtn = Integer.parseInt(jsonChildNode
                        .optString("CartonPcsRatio"));
                sku.packSize = Double.parseDouble(jsonChildNode
                        .optString("PackSize"));
                sku.PositionValue = Integer.parseInt(jsonChildNode
                        .optString("PositionValue"));
                sku.ctnRate = Double.parseDouble(jsonChildNode
                        .optString("CtnRate"));
                if ((sku.skuId != null) && (sku.title != null)) {
                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, sku.skuId, "tblSKU", DatabaseConstants.tblSKU.SKUID);
                    if (IsExist) {
                        DatabaseQueryUtil.insertTblSku(context, sku);
                    } else
                        DatabaseQueryUtil.updateTblSku(context, sku);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblSection(Context context, String JsonString, User user) {
        try {
            section = new Section();
            JSONArray jsonMainNode = new JSONArray(JsonString);
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                section.title = jsonChildNode.optString("Title");
//				section.maxOrderNo = Integer.parseInt(jsonChildNode
//						.optString("MaxOrdNo").toString());
                section.dsrID = jsonChildNode
                        .optString("SRID");
                section.sectionID = jsonChildNode
                        .optString("SectionID");
                section.routeID = Integer.parseInt(jsonChildNode
                        .optString("RouteID"));
                section.orderColDay = jsonChildNode
                        .optString("OrderColDay");
                section.orderDlvDay = jsonChildNode
                        .optString("OrderDlvDay");

                if ((section.sectionID != null) && (section.title != null)) {
                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, section.sectionID, "tblSection", DatabaseConstants.tblSection.SECTION_ID);
                    if (IsExist) {
                        DatabaseQueryUtil.insertTblSection(context, section);
                    } else
                        DatabaseQueryUtil.updateTblSection(context, section);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblOutlet(Context context, String JsonString, User user) {
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            outlet = new Outlet();
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                outlet.outletId = (jsonChildNode
                        .optString("OutletCode"));
                outlet.description = jsonChildNode
                        .optString("Name");
                outlet.channelId = (jsonChildNode
                        .optString("ChannelID"));
                outlet.owner = jsonChildNode
                        .optString("OwnerName");
                outlet.address = jsonChildNode
                        .optString("Address");
                outlet.contactNo = jsonChildNode
                        .optString("ContactNo");
                outlet.visited = Integer.parseInt(jsonChildNode
                        .optString("Visited"));
                outlet.routeID = (jsonChildNode
                        .optString("RouteID"));
                outlet.outletlatitude = jsonChildNode
                        .optString("Latitude");
                outlet.outletlongitude = jsonChildNode
                        .optString("Longitude");

                if ((outlet.outletId != null) && (outlet.description != null)) {
                    boolean IsExist = DatabaseQueryUtil.isIDExist(context, outlet.outletId, "tblOutlet", DatabaseConstants.tblOutlet.OUTLET_ID);
                    if (IsExist) {
                        DatabaseQueryUtil.insertTblOutlet(context, outlet);
                    } else
                        DatabaseQueryUtil.updateTblOutlet(context, outlet);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblTPR(Context context, String JsonString, User user) {
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            tpr = new Tpr();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                tpr.tprId = Integer.parseInt(jsonChildNode
                        .optString("PromotionID"));
                tpr.description = jsonChildNode
                        .optString("ShortName");
                tpr.ceilingQty = (int) Double.parseDouble(jsonChildNode
                        .optString("CeilingAmount"));
                tpr.isFixedTPR = Integer.parseInt(jsonChildNode
                        .optString("CalcMethod"));
                tpr.programmType = Integer.parseInt(jsonChildNode
                        .optString("OfferBasis"));

                if (tpr.tprId != 0 && tpr.description != null) {
                    DatabaseQueryUtil.insertTblTPR(context, tpr);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblTPRSKUChnl(Context context, String JsonString, User user) {
        try {
            int tprID, channelID, skuID;
            JSONArray jsonMainNode = new JSONArray(JsonString);

            // tpr = new Tpr();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                tprID = Integer.parseInt(jsonChildNode
                        .optString("SPID"));
                channelID = Integer.parseInt(jsonChildNode
                        .optString("ChannelID"));
                skuID = Integer.parseInt(jsonChildNode
                        .optString("SKUID"));

                if (tprID != 0 && skuID != 0 && channelID != 0) {
                    DatabaseQueryUtil.insertTblTPRSKUChnl(context, tprID, channelID, skuID);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblTPRSlab(Context context, String JsonString, User user) {
        try {
            int tprID = 0, channelID = 0, skuID = 0;
            JSONArray jsonMainNode = new JSONArray(JsonString);

            slab = new Slab();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                slab.tprId = Integer.parseInt(jsonChildNode.optString("SPID"));
                slab.skuId = 0;
                slab.slabId = Integer.parseInt(jsonChildNode.optString("SlabID"));
                slab.minQty = (int) Double.parseDouble(jsonChildNode.optString("Threshold"));

                if (slab.tprId != 0 && slab.slabId != 0) {
                    DatabaseQueryUtil.insertTblTPRSlab(context, slab);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertTblSlabItem(Context context, String JsonString, User user) {
        try {
            JSONArray jsonMainNode = new JSONArray(JsonString);
            String skuID;
            slab = new Slab();

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                slab.tprId = Integer.parseInt(jsonChildNode.optString("SPID"));
                slab.slabId = Integer.parseInt(jsonChildNode.optString("SlabID"));
                slab.slabType = Integer.parseInt(jsonChildNode.optString("BonusType"));
                slab.forQty = (int) Double.parseDouble(jsonChildNode.optString("ForEach"));
                slab.itemQty = (int) Double.parseDouble(jsonChildNode.optString("FreeAmount"));
                slab.description = jsonChildNode.optString("ItemDesc");

                if (slab.tprId != 0 && slab.slabId != 0) {
                    DatabaseQueryUtil.insertTblSlabItem(context, slab);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region ObjectToJson Functions
    private static String UserToJsonString(User user) {
        JSONObject eachJsonObject = new JSONObject();

        try {
            eachJsonObject.put("SectionID", String.valueOf(user.sectionId));
            eachJsonObject.put("DsrID", String.valueOf(user.dsrId));
            eachJsonObject.put("Date", String.valueOf(user.visitDate));
            eachJsonObject.put("DailyTarget", String.valueOf(user.dailyTarget));
            eachJsonObject.put("RoamerLogID", String.valueOf(user.roamerLogId));

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return eachJsonObject.toString();
    }

    public static String[] OutletListToJsonString(Context context, User user, String outletID) {
//		ArrayList<Order> NotSentOutletList = DatabaseQueryUtil
//				.getOutletListWithNotSentStatus(context);
        ArrayList<Order> NotSentOrderList;
        if (outletID != null) {
            NotSentOrderList = DatabaseQueryUtil
                    .getOrderListToSend(context, outletID, user.visitDate);
        } else {
            NotSentOrderList = DatabaseQueryUtil
                    .getOrderListToSend(context, user.visitDate);
        }
        JSONArray jsonOrderArr = new JSONArray();
        JSONObject eachOrderJsonObject;

        ArrayList<OrderItem> oOrderItem;
        JSONArray jsonOrderItemSkuArr = new JSONArray();
        JSONObject eachOrderItemSkuJsonObject;

        ArrayList<MarketReturnItem> oMarketReturnItem;
        JSONArray jsonMarketReturnItemArr = new JSONArray();
        JSONObject eachMarketReturnItemJsonObject;

        try {

            for (Order i : NotSentOrderList) {
                eachOrderJsonObject = new JSONObject();
//				eachOutletJsonObject.put("OrderDetailsID",
//						String.valueOf(i.webOrderDetailKey));
                eachOrderJsonObject.put("RefNo",
                        String.valueOf(i.OrderNo));
                eachOrderJsonObject
                        .put("OrderDate", String.valueOf(i.OrderDate));
                eachOrderJsonObject
                        .put("SystemID", String.valueOf(user.company_ID));
                eachOrderJsonObject
                        .put("CustomerID", String.valueOf(i.OutletID));
                eachOrderJsonObject.put("OrderLatitude",
                        String.valueOf(i.OrderLatitude));
                eachOrderJsonObject.put("OrderLongitude",
                        String.valueOf(i.OrderLongitude));
//				eachOutletJsonObject.put("ChannelID",
//						String.valueOf(i.channelId));
                eachOrderJsonObject
                        .put("SectionID", String.valueOf(i.SectionID));
//				eachOutletJsonObject.put("RouteID",
//						String.valueOf(user.routeId));
                eachOrderJsonObject.
                        put("SRID", String.valueOf(user.dsrId));
                eachOrderJsonObject
                        .put("CheckInTime", String.valueOf(i.StartTime));
                eachOrderJsonObject
                        .put("CheckOutTime", String.valueOf(i.EndTime));
                String ordrLat = i.OrderLatitude;
                String ordrLongt = i.OrderLongitude;
                if (ordrLat == null || String.valueOf(ordrLat).isEmpty() || String.valueOf(ordrLat).equals("null"))
                    eachOrderJsonObject
                            .put("OrderLatitude", String.valueOf(0.0));
                if (ordrLongt == null || String.valueOf(ordrLongt).isEmpty() || String.valueOf(ordrLongt).equals("null"))
                    eachOrderJsonObject
                            .put("OrderLongitude", String.valueOf(0.0));
                else {
                    eachOrderJsonObject
                            .put("OrderLatitude", String.valueOf(i.OrderLatitude));
                    eachOrderJsonObject
                            .put("OrderLongitude", String.valueOf(i.OrderLongitude));
                }

                if (i.OutletLatitude == null || String.valueOf(i.OutletLatitude).isEmpty() || String.valueOf(i.OutletLatitude).equals("null"))
                    eachOrderJsonObject
                            .put("OutletLatitude", String.valueOf(i.OrderLatitude));
                if (i.OutletLongitude == null || String.valueOf(i.OutletLongitude).isEmpty() || String.valueOf(i.OutletLongitude).equals("null"))
                    eachOrderJsonObject
                            .put("OutletLongitude", String.valueOf(i.OrderLongitude));
                else {
                    eachOrderJsonObject
                            .put("OutletLongitude", String.valueOf(i.OutletLongitude));
                    eachOrderJsonObject
                            .put("OutletLatitude", String.valueOf(i.OutletLatitude));
                }
                eachOrderJsonObject
                        .put("NoOrderReasonID", String.valueOf(i.NotOrdCoz));
                eachOrderJsonObject
                        .put("OrderNo", String.valueOf(i.RefNo));
                eachOrderJsonObject
                        .put("PaymentMode", String.valueOf(i.PaymentMode));
                eachOrderJsonObject
                        .put("DeliveryMode", String.valueOf(i.DeliveryMode));
                eachOrderJsonObject
                        .put("DeliveryDate", String.valueOf(i.DeliveryDate));

                jsonOrderArr.put(eachOrderJsonObject);

                // Fetching SKU Order info;

                oOrderItem = DatabaseQueryUtil.getAllOrderByOutlet(context,
                        i.OutletID, i.OrderNo);

                for (OrderItem orderItem : oOrderItem) {
                    int pcsPerCtn = DatabaseQueryUtil.getQuantityBySku(context, orderItem.SKUID);
                    int quantity = pcsPerCtn * orderItem.Carton + orderItem.Piece;
                    eachOrderItemSkuJsonObject = new JSONObject();
                    eachOrderItemSkuJsonObject.put("RefNo", String.valueOf(orderItem.OrderNo));
                    eachOrderItemSkuJsonObject.put("SkuID",
                            String.valueOf(orderItem.SKUID));
                    eachOrderItemSkuJsonObject.put("TkOff",
                            String.valueOf(orderItem.TkOff));
                    eachOrderItemSkuJsonObject.put("Quantity",
                            String.valueOf(quantity));
                    jsonOrderItemSkuArr.put(eachOrderItemSkuJsonObject);
                }
                // Fetching Market Return info;

                oMarketReturnItem = DatabaseQueryUtil
                        .getMarketReturnItemByOutlet(context, i.OutletID);

                for (MarketReturnItem marketReturnItem : oMarketReturnItem) {
                    eachMarketReturnItemJsonObject = new JSONObject();

                    eachMarketReturnItemJsonObject.put("OrderNo",
                            String.valueOf(marketReturnItem.orderNo));
                    eachMarketReturnItemJsonObject.put("RefTranID",
                            String.valueOf(marketReturnItem.orderNo));
                    eachMarketReturnItemJsonObject.put("OutletID",
                            String.valueOf(marketReturnItem.outletId)); //AKCG
                    eachMarketReturnItemJsonObject.put("CustomerID",
                            String.valueOf(marketReturnItem.outletId)); //OnlineSales 11-Jul-16
                    eachMarketReturnItemJsonObject.put("SkuID",
                            String.valueOf(marketReturnItem.SKUID));
                    eachMarketReturnItemJsonObject.put("BrandID",
                            String.valueOf(marketReturnItem.brandId));
                    eachMarketReturnItemJsonObject.put("ExpiryFormattedDate",
                            String.valueOf(marketReturnItem.expDate));
                    eachMarketReturnItemJsonObject.put("MarketReasonID",
                            String.valueOf(marketReturnItem.marketReasonId));
                    eachMarketReturnItemJsonObject.put("Quantity",
                            String.valueOf(marketReturnItem.qty));
                    eachMarketReturnItemJsonObject.put("BatchNo",
                            String.valueOf(marketReturnItem.batch));
                    jsonMarketReturnItemArr.put(eachMarketReturnItemJsonObject);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//		return jsonOrderArr.toString() + "~" + jsonOrderItemSkuArr.toString()
//				+ "~" + jsonMarketReturnItemArr.toString();

        return new String[]{jsonOrderArr.toString(), jsonOrderItemSkuArr.toString(), jsonMarketReturnItemArr.toString()};
//		return jsonOrderArr.toString() + "~" + jsonOrderItemSkuArr.toString();
    }

    public static String[] NewOutletListToJsonString(Context context, User user) {

        JSONArray jsonOutletList = new JSONArray();
        JSONArray jsonOutletImageList = new JSONArray();
        JSONObject eachNewOutletJsonObject;
        JSONObject eachNewOutletImageJsonObject;
        ArrayList<Outlet> outletList;
        ArrayList<Image> outletImageList;

        try {

            outletList = DatabaseQueryUtil.getNewOutletList(context);

            for (Outlet newOutletObj : outletList) {
                eachNewOutletJsonObject = new JSONObject();

                eachNewOutletJsonObject.put("Name",
                        String.valueOf(newOutletObj.description));
                eachNewOutletJsonObject.put("OwnerName",
                        String.valueOf(newOutletObj.owner));
                eachNewOutletJsonObject.put("Address1",
                        String.valueOf(newOutletObj.address));
                eachNewOutletJsonObject.put("CustomerID",
                        String.valueOf(newOutletObj.outletId));
                eachNewOutletJsonObject.put("ContactNo",
                        String.valueOf(newOutletObj.contactNo));
                eachNewOutletJsonObject.put("Status",
                        String.valueOf(1));
                eachNewOutletJsonObject.put("SeqID",
                        String.valueOf(1));
                eachNewOutletJsonObject.put("ChannelID",
                        String.valueOf(newOutletObj.channelId));
                eachNewOutletJsonObject.put("CustomerGradeID",
                        String.valueOf(1));
                eachNewOutletJsonObject.put("RouteID",
                        String.valueOf(newOutletObj.routeID));
                eachNewOutletJsonObject.put("RefSalesPointID",
                        String.valueOf(0));
                eachNewOutletJsonObject.put("MHNodeID",
                        String.valueOf(newOutletObj.routeID));
                eachNewOutletJsonObject.put("Location",
                        String.valueOf(newOutletObj.address));
                eachNewOutletJsonObject.put("Latitude",
                        String.valueOf(newOutletObj.outletlatitude));
                eachNewOutletJsonObject.put("Longitude",
                        String.valueOf(newOutletObj.outletlongitude));
                eachNewOutletJsonObject.put("CreatedBy",
                        String.valueOf(user.dsrId));
                jsonOutletList.put(eachNewOutletJsonObject);

             /*   outletImageList = DatabaseQueryUtil.getOutletImageList(context, newOutletObj.outletId);
                for (Image imgObj : outletImageList) {

                    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    Date todayDate = new Date();
                    String thisDate = currentDate.format(todayDate);

                    eachNewOutletImageJsonObject = new JSONObject();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    Bitmap bm = BitmapFactory.decodeFile(imgObj.imageUrl,options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 60, baos); //bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);


                    eachNewOutletImageJsonObject.put("Image",
                            String.valueOf(encodedImage));
                    eachNewOutletImageJsonObject.put("OutletID",
                            String.valueOf(imgObj.outletId));
                    eachNewOutletImageJsonObject.put("ImageType",
                            String.valueOf(imgObj.imageType));
                    eachNewOutletImageJsonObject.put("CaptureDate",
                            String.valueOf(thisDate));
                    jsonOutletImageList.put(eachNewOutletImageJsonObject);
                }
*/

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new String[]{jsonOutletList.toString(), jsonOutletImageList.toString()};
    }
}
