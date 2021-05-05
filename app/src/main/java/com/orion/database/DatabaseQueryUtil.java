package com.orion.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.orion.application.HomePageActivity;
import com.orion.application.OutletVisitPageActivity;
import com.orion.application.TodaysStatusPageActivity;
import com.orion.entities.AllOrderList;
import com.orion.entities.Bonus;
import com.orion.entities.Brand;
import com.orion.entities.Channel;
import com.orion.entities.FreeItem;
import com.orion.entities.FreeOrDiscount;
import com.orion.entities.Image;
import com.orion.entities.Market;
import com.orion.entities.MarketReturnItem;
import com.orion.entities.Order;
import com.orion.entities.OrderItem;
import com.orion.entities.OrderItemsByOrderNo;
import com.orion.entities.Outlet;
import com.orion.entities.OutletItem;
import com.orion.entities.PopItem;
import com.orion.entities.Reason;
import com.orion.entities.Section;
import com.orion.entities.Sku;
import com.orion.entities.Slab;
import com.orion.entities.Tpr;
import com.orion.entities.User;
import com.orion.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DatabaseQueryUtil {

    private static String TAG = "Trace";
    private User user;
    private static String changeFormat(String visitDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(visitDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String _visitDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return _visitDate;
    }

    //region Insert Functions
    public static String createNewOutlet(Context context, Outlet outlet) {
        ConnectionContext ctx = new ConnectionContext(context);
        String id = null;
        try {
            ctx.Begin();

            id = String.valueOf(ctx.Insert(
                    DatabaseConstants.TABLE_NEW_OUTLET,
                    DatabaseConstants.KEY_DESCRIPTION + ", " +
                            DatabaseConstants.KEY_CHANNEL_ID + ", " +
                            DatabaseConstants.KEY_OWNER + " , " +
                            DatabaseConstants.KEY_ADDRESS + " , " +
                            DatabaseConstants.KEY_CONTACT_NO + " , " +
                            DatabaseConstants.KEY_VISITED + " , " +
                            DatabaseConstants.KEY_ROUTE_ID + " , " +
                            DatabaseConstants.KEY_IMAGE_URLS + " , " +
                            DatabaseConstants.KEY_OUTLET_LATITUDE + " , " +
                            DatabaseConstants.KEY_OUTLET_LONGITUDE,

                    new String[]{outlet.description,
                            (outlet.channelId),
                            outlet.owner,
                            outlet.address,
                            outlet.contactNo,
                            Integer.toString(outlet.visited),
                            (outlet.routeID),
                            Arrays.toString(outlet.imageUrls),
                            outlet.outletlatitude,
                            outlet.outletlongitude}));
            ctx.End();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }


    public static ArrayList<Outlet> getNewOutletList(Context context) {

        ArrayList<Outlet> returnOutletList = new ArrayList<Outlet>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            String query = DatabaseConstants.KEY_OUTLET_ID + " , " +
                    DatabaseConstants.KEY_DESCRIPTION + " , " +
                    DatabaseConstants.KEY_ROUTE_ID + " , " +
                    DatabaseConstants.KEY_CHANNEL_ID + ", " +
                    DatabaseConstants.KEY_ADDRESS + " , " +
                    DatabaseConstants.KEY_VISITED + " , " +
                    DatabaseConstants.KEY_OWNER + " , " +
                    DatabaseConstants.KEY_CONTACT_NO + " , " +
                    DatabaseConstants.KEY_IMAGE_URLS + " , " +
                    DatabaseConstants.KEY_OUTLET_LATITUDE + " , " +
                    DatabaseConstants.KEY_OUTLET_LONGITUDE;

            Cursor cursor = ctx.Select("SELECT " + query + " FROM " + DatabaseConstants.TABLE_NEW_OUTLET);

            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Outlet now = new Outlet();
                    now.outletId = cursor.getString(0);
                    now.description = cursor.getString(1);
                    now.routeID = cursor.getString(2);
                    now.channelId = cursor.getString(3);
                    now.address = cursor.getString(4);
                    now.visited = cursor.getInt(5);
                    now.owner = cursor.getString(6);
                    now.contactNo = cursor.getString(7);
                    now.imageUrls = cursor.getString(8).split(",");
                    now.outletlatitude = cursor.getString(9);
                    now.outletlongitude = cursor.getString(10);
                    returnOutletList.add(now);

                    cursor.moveToNext();
                }

                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }


    /**
     * Insert new Image
     *
     * @param context
     * @param images
     */
    public static void addNewImage(Context context, Image[] images) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            for (Image image : images) {
                ctx.Insert(
                        DatabaseConstants.TABLE_IMAGES,
                        DatabaseConstants.KEY_OUTLET_ID + ", " +
                                DatabaseConstants.KEY_IMAGE_TYPE + ", " +
                                DatabaseConstants.KEY_IMAGE_URL,

                        new String[]{
                                (image.outletId),
                                Integer.toString(image.imageType),
                                image.imageUrl
                        });
            }

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Image> getOutletImageList(Context context, String outletId) {

        ArrayList<Image> returnOutletList = new ArrayList<Image>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            String query = "SELECT " +
                    DatabaseConstants.KEY_IMAGE_ID + " , " +
                    DatabaseConstants.KEY_OUTLET_ID + " , " +
                    DatabaseConstants.KEY_IMAGE_TYPE + " , " +
                    DatabaseConstants.KEY_IMAGE_URL + " FROM " +
                    DatabaseConstants.TABLE_IMAGES + " WHERE " +
                    DatabaseConstants.KEY_OUTLET_ID + "=?";

            Cursor cursor = ctx.Select(query, new String[]{String.valueOf(outletId)});

            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Image now = new Image();
                    now.imageId = cursor.getInt(0);
                    now.outletId = cursor.getString(1);
                    now.imageType = cursor.getInt(2);
                    now.imageUrl = cursor.getString(3);
                    returnOutletList.add(now);
                    cursor.moveToNext();
                }

                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }


    //region Insert Functions
    public static void insertOrderToTblOrder(Context context, String outletID, String sectionID, String orderDate) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblOrder ("
                    + DatabaseConstants.tblOrder.OUTLET_ID + ", "
                    + DatabaseConstants.tblOrder.SECTION_ID + ", "
                    + DatabaseConstants.tblOrder.ORDER_DATE + ") " + " VALUES ("
                    + outletID + ", " + sectionID + ", "
                    + orderDate + " )");

            ctx.Insert(
                    "tblOrder",
                    DatabaseConstants.tblOrder.OUTLET_ID + ", "
                            + DatabaseConstants.tblOrder.SECTION_ID + ", "
                            + DatabaseConstants.tblOrder.SENT_STATUS + ", "
                            + DatabaseConstants.tblOrder.ORDER_DATE,
                    new String[]{(outletID),
                            (sectionID), Integer.toString(0), orderDate});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertOrderToOrderSummary(Context context, int orderNo, String outletID, double total) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblOrderSummary ("
                    + DatabaseConstants.tblOrder.OUTLET_ID + ", "
                    + DatabaseConstants.tblOrder.ORDER_TOTAL + ") " + " VALUES ("
                    + orderNo + ", " + outletID + ", " + total + ")");

            ctx.Insert(
                    "tblOrderSummary",
                    DatabaseConstants.tblOrder.ORDER_NO + ", "
                            + DatabaseConstants.tblOrder.OUTLET_ID + ", "
                            + DatabaseConstants.tblOrder.ORDER_TOTAL,
                    new String[]{Integer.toString(orderNo), (outletID),
                            Double.toString(total)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean IsOutletExists(String outletId, Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor = null;

        try {
            ctx.Begin();
            cursor = ctx.Select("SELECT OrderNo FROM tblOrder where OutletID = "+ outletId +" AND SentStatus = 0");
            cursor.moveToFirst();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(cursor.getCount() != 0)
            return  true;
        else
            return false;
    }

    public static int getExistingOrderNo(String outletID, Context context) {
        int _orderNo = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor;

        try {
            ctx.Begin();
            cursor = ctx.Select("SELECT OrderNo FROM tblOrder where OutletID = "+ outletID +" AND SentStatus = 0");
            cursor.moveToFirst();
            _orderNo = cursor.getInt(cursor.getColumnIndex("OrderNo"));
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  _orderNo;
    }

    private static void DeleteExistingOrderAndItemsByOutlet(String outletID, String skuid, Context context) {

        int _orderNo = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor;

        try {
            ctx.Begin();
            cursor = ctx.Select("SELECT OrderNo FROM tblOrder where OutletID = "+ outletID +" AND SentStatus = 0");
            cursor.moveToFirst();
            _orderNo = cursor.getInt(cursor.getColumnIndex("OrderNo"));
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ctx.Begin();
            ctx.Delete(
                    "tblOrderItem",
                    DatabaseConstants.tblOrderItem.ORDER_NO + " =? ", new String[]{(String.valueOf(_orderNo))});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertOrderItemToTblOrderItem(Context context, int orderNo, String outletID,
                                                     String skuId, int ctn, int pcs, String brandId, double total,
                                                     double tkOff, int allocationQty, int Carton_Org, int Pieces_Org) {
        //DeleteExistingOrderAndItemsByOutlet(outletID, skuId, context);

        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblOrderItem ("
                    + DatabaseConstants.tblOrderItem.ORDER_NO + ", "
                    + DatabaseConstants.tblOrderItem.OUTLET_ID + ", "
                    + DatabaseConstants.tblOrderItem.SKUID + ", "
                    + DatabaseConstants.tblOrderItem.CARTON + ", "
                    + DatabaseConstants.tblOrderItem.PIECE + ", "
                    + DatabaseConstants.tblOrderItem.BRAND_ID + ", "
                    + DatabaseConstants.tblOrderItem.TOTAL + ", "
                    + DatabaseConstants.tblOrderItem.TK_OFF + ", "
                    + DatabaseConstants.tblOrderItem.SUGGESTED + ") " + " VALUES ("
                    + orderNo + ", " + outletID + ", " + skuId + ", " + ctn + ", " + pcs + ", "
                    + brandId + ", " + total + ", " + tkOff + ", "
                    + allocationQty + ")");

            ctx.Insert(
                    "tblOrderItem",
                    DatabaseConstants.tblOrderItem.ORDER_NO + ", "
                            + DatabaseConstants.tblOrderItem.OUTLET_ID + ", "
                            + DatabaseConstants.tblOrderItem.SKUID + ", "
                            + DatabaseConstants.tblOrderItem.CARTON + ", "
                            + DatabaseConstants.tblOrderItem.PIECE + ", "
                            + DatabaseConstants.tblOrderItem.BRAND_ID + ", "
                            + DatabaseConstants.tblOrderItem.TOTAL + ", "
                            + DatabaseConstants.tblOrderItem.TK_OFF + ", "
                            + DatabaseConstants.tblOrderItem.SUGGESTED + ", "
                            + DatabaseConstants.tblOrderItem.CARTON_ORG + ", "
                            + DatabaseConstants.tblOrderItem.PIECE_ORG,
                    new String[]{Integer.toString(orderNo), (outletID),
                            (skuId), Integer.toString(ctn),
                            Integer.toString(pcs), (brandId),
                            Double.toString(total), Double.toString(tkOff),
                            Double.toString(allocationQty), Integer.toString(Carton_Org),
                            Integer.toString(Pieces_Org)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void insertMarketReturnItemToTblMarketReturn(Context context,
                                                               MarketReturnItem marketReturnItem, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblMarketReturn ("
                    + DatabaseConstants.tblMarketReturn.MARKET_RETURN_ID + ", "
                    + DatabaseConstants.tblMarketReturn.BRAND_ID + ", "
                    + DatabaseConstants.tblMarketReturn.QUANTITY + ", "
                    + DatabaseConstants.tblMarketReturn.BATCH + ", "
                    + DatabaseConstants.tblMarketReturn.MARKET_REASON_ID + ", "
                    + DatabaseConstants.tblMarketReturn.EXPIRY_DATE + ", "
                    + DatabaseConstants.tblMarketReturn.OUTLET_ID + ", "
                    + DatabaseConstants.tblMarketReturn.ORDER_NO + ", "
                    + DatabaseConstants.tblMarketReturn.SKUID + ") Values ("
                    + Integer.toString(marketReturnItem.marketReturnId) + ", "
                    + (marketReturnItem.brandId) + ", "
                    + Integer.toString(marketReturnItem.qty) + ", "
                    + (marketReturnItem.batch) + ", "
                    + Integer.toString(marketReturnItem.marketReasonId) + ", "
                    + (marketReturnItem.expDate) + ", "
                    + (marketReturnItem.outletId) + ", "
                    + Integer.toString(orderNo) + ", "
                    + (marketReturnItem.SKUID) + ")");

            ctx.Insert(
                    "tblMarketReturn",
                    DatabaseConstants.tblMarketReturn.MARKET_RETURN_ID
                            + ", "
                            + DatabaseConstants.tblMarketReturn.BRAND_ID
                            + ", "
                            + DatabaseConstants.tblMarketReturn.QUANTITY
                            + ", "
                            + DatabaseConstants.tblMarketReturn.BATCH
                            + ", "
                            + DatabaseConstants.tblMarketReturn.MARKET_REASON_ID
                            + ", "
                            + DatabaseConstants.tblMarketReturn.EXPIRY_DATE
                            + ", "
                            + DatabaseConstants.tblMarketReturn.OUTLET_ID
                            + ", "
                            + DatabaseConstants.tblMarketReturn.ORDER_NO
                            + ", "
                            + DatabaseConstants.tblMarketReturn.SKUID,
                    new String[]{
                            Integer.toString(marketReturnItem.marketReturnId),
                            (marketReturnItem.brandId),
                            Integer.toString(marketReturnItem.qty),
                            (marketReturnItem.batch),
                            Integer.toString(marketReturnItem.marketReasonId),
                            (marketReturnItem.expDate),
                            (marketReturnItem.outletId),
                            Integer.toString(orderNo),
                            (marketReturnItem.SKUID)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertPopItem(Context context, int popItemId,
                                     int outletId, int currentQty) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblPOPOutletItem ("
                    + DatabaseConstants.tblPOPOutletItem.POP_ITEM_ID + ", "
                    + DatabaseConstants.tblPOPOutletItem.OUTLET_ID + ", "
                    + DatabaseConstants.tblPOPOutletItem.CURRENT_QTY + ") "
                    + " VALUES (" + popItemId + ", " + outletId + ", "
                    + currentQty + ")");

            ctx.Insert(
                    "tblPOPOutletItem",
                    DatabaseConstants.tblPOPOutletItem.POP_ITEM_ID + ", "
                            + DatabaseConstants.tblPOPOutletItem.OUTLET_ID
                            + ", "
                            + DatabaseConstants.tblPOPOutletItem.CURRENT_QTY,
                    new String[]{Integer.toString(popItemId),
                            Integer.toString(outletId),
                            Integer.toString(currentQty)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void insertTblDSRBasic(Context context, User user) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblDSRBasic ("
                    + DatabaseConstants.tblDSRBasic.SECTION_ID + ", "
                    + DatabaseConstants.tblDSRBasic.NAME + ", "
                    + DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED + ", "
                    + DatabaseConstants.tblDSRBasic.DAY_REMAIN + ", "
                    + DatabaseConstants.tblDSRBasic.TARGET_REM + ", "
                    + DatabaseConstants.tblDSRBasic.DAILY_TAREGET + ", "
                    + DatabaseConstants.tblDSRBasic.VISIT_DATE + ", "
                    + DatabaseConstants.tblDSRBasic.PDA_USER + ", "
                    + DatabaseConstants.tblDSRBasic.DSR_ID + ", "
                    + DatabaseConstants.tblDSRBasic.TARGET + ", "
                    + DatabaseConstants.tblDSRBasic.ROAMER_LOG_ID + ", "
                    + DatabaseConstants.tblDSRBasic.COMPANY_ID + ", "
                    + DatabaseConstants.tblDSRBasic.DISTRIBUTOR_NAME + ", "
                    + DatabaseConstants.tblDSRBasic.DISTRIBUTOR_ADDRESS + ", "
                    + DatabaseConstants.tblDSRBasic.MOBILE_NO + ", "
                    + DatabaseConstants.tblDSRBasic.PASSWORD + ", "
                    + DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME + " )" + " VALUES ("
                    + user.sectionId + "," + user.name + "," + user.orderAchieved + ","
                    + user.dayRemain + "," + user.targetRemain + "," + user.dailyTarget + ","
                    + user.visitDate + "," + user.pdaUser + "," + user.dsrId + ","
                    + user.target + "," + user.roamerLogId + "," + user.company_ID + "," + user.distributorName + ","
                    + user.distributorAddress + "," + user.mobile_No + "," + user.password + "," + user.lastUpdateTime + ")");
            ctx.Insert(
                    "tblDSRBasic",
                    DatabaseConstants.tblDSRBasic.SECTION_ID + ", "
                            + DatabaseConstants.tblDSRBasic.NAME + ", "
                            + DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED + ", "
                            + DatabaseConstants.tblDSRBasic.DAY_REMAIN + ", "
                            + DatabaseConstants.tblDSRBasic.TARGET_REM + ", "
                            + DatabaseConstants.tblDSRBasic.DAILY_TAREGET + ", "
                            + DatabaseConstants.tblDSRBasic.VISIT_DATE + ", "
                            + DatabaseConstants.tblDSRBasic.PDA_USER + ", "
                            + DatabaseConstants.tblDSRBasic.DSR_ID + ", "
                            + DatabaseConstants.tblDSRBasic.TARGET + ", "
                            + DatabaseConstants.tblDSRBasic.ROAMER_LOG_ID + ", "
                            + DatabaseConstants.tblDSRBasic.COMPANY_ID + ", "
                            + DatabaseConstants.tblDSRBasic.DISTRIBUTOR_NAME + ", "
                            + DatabaseConstants.tblDSRBasic.DISTRIBUTOR_ADDRESS + ", "
                            + DatabaseConstants.tblDSRBasic.MOBILE_NO + ", "
                            + DatabaseConstants.tblDSRBasic.PASSWORD + ", "
                            + DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME,

                    new String[]{(user.sectionId), user.name,
                            Double.toString(user.orderAchieved),
                            Integer.toString(user.dayRemain), Double.toString(user.targetRemain),
                            Double.toString(user.dailyTarget), user.visitDate, user.pdaUser,
                            user.dsrId, Double.toString(user.target),
                            Integer.toString(user.roamerLogId), Integer.toString(user.company_ID),
                            user.distributorName, user.distributorAddress, user.mobile_No, user.password, user.lastUpdateTime});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        User us = getUser(context);
    }

    public static void insertTblSku(Context context, Sku sku) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblSKU ("
                    + DatabaseConstants.tblSKU.BRAND_ID + ", "
                    + DatabaseConstants.tblSKU.CRITICAL_STOCK + ", "
                    + DatabaseConstants.tblSKU.CTN_RATE + ", "
                    + DatabaseConstants.tblSKU.MESSAGE_FOR_HHT + ", "
                    + DatabaseConstants.tblSKU.PCS_RATE + ", "
                    + DatabaseConstants.tblSKU.PACK_SIZE + ", "
                    + DatabaseConstants.tblSKU.PCS_PER_CTN + ", "
                    + DatabaseConstants.tblSKU.POSITION_VALUE + ", "
                    + DatabaseConstants.tblSKU.SKUID + ", "
                    + DatabaseConstants.tblSKU.TARGET + ", "
                    + DatabaseConstants.tblSKU.TITLE + " )" + " VALUES ("
                    + sku.brandId + "," + sku.CriticalStock + "," + sku.ctnRate + ","
                    + sku.MessageForHHT + "," + sku.pcsRate + "," + sku.packSize + "," + sku.pcsPerCtn + ","
                    + sku.PositionValue + "," + sku.skuId + "," + sku.target + ","
                    + sku.title + ")");

            ctx.Insert(
                    "tblSKU",
                    DatabaseConstants.tblSKU.BRAND_ID + ", "
                            + DatabaseConstants.tblSKU.CRITICAL_STOCK + ", "
                            + DatabaseConstants.tblSKU.CTN_RATE + ", "
                            + DatabaseConstants.tblSKU.MESSAGE_FOR_HHT + ", "
                            + DatabaseConstants.tblSKU.PCS_RATE + ", "
                            + DatabaseConstants.tblSKU.PACK_SIZE + ", "
                            + DatabaseConstants.tblSKU.PCS_PER_CTN + ", "
                            + DatabaseConstants.tblSKU.POSITION_VALUE + ", "
                            + DatabaseConstants.tblSKU.SKUID + ", "
                            + DatabaseConstants.tblSKU.TARGET + ", "
                            + DatabaseConstants.tblSKU.TITLE,
                    new String[]{
                            sku.brandId,
                            Integer.toString(sku.CriticalStock),
                            Double.toString(sku.ctnRate), sku.MessageForHHT, Double.toString(sku.pcsRate),
                            Double.toString(sku.packSize), Integer.toString(sku.pcsPerCtn),
                            Integer.toString(sku.PositionValue), sku.skuId,
                            Integer.toString(sku.target), sku.title});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblBrands(Context context, Brand brand) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblBrand ("
                    + DatabaseConstants.tblBrand.BRAND_ID + ", "
                    + DatabaseConstants.tblBrand.NAME + " )" + " VALUES ("
                    + brand.brandId + ", " + brand.name + ")");

            ctx.Insert(
                    "tblBrand",
                    DatabaseConstants.tblBrand.BRAND_ID + ", "
                            + DatabaseConstants.tblBrand.NAME,
                    new String[]{(brand.brandId),
                            brand.name});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblChannels(Context context, Channel channel) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblChannel ("
                    + DatabaseConstants.tblChannel.CHANNEL_ID + ", "
                    + DatabaseConstants.tblChannel.NAME + ","
                    + DatabaseConstants.tblChannel.EDIT_ALLOWED + " )" + " VALUES ("
                    + channel.channelID + ", " + channel.name + ", 1 )");

            ctx.Insert(
                    "tblChannel",
                    DatabaseConstants.tblChannel.CHANNEL_ID + ", "
                            + DatabaseConstants.tblChannel.NAME + ","
                            + DatabaseConstants.tblChannel.EDIT_ALLOWED,
                    new String[]{(channel.channelID),
                            channel.name, Integer.toString(1)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblNoOrderReason(Context context, Reason reason) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblNoOrderReason ("
                    + DatabaseConstants.tblNoOrderReason.REASON_ID + ", "
                    + DatabaseConstants.tblNoOrderReason.DESCRIPTION + " )" + " VALUES ("
                    + reason.reasonId + ", " + reason.description + " )");

            ctx.Insert(
                    "tblNoOrderReason",
                    DatabaseConstants.tblNoOrderReason.REASON_ID + ", "
                            + DatabaseConstants.tblNoOrderReason.DESCRIPTION,
                    new String[]{Integer.toString(reason.reasonId),
                            reason.description});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblMarketReason(Context context, MarketReturnItem marketReturnItem, String code) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblMarketReason ("
                    + DatabaseConstants.tblMarketReason.MARKET_REASON_ID + ", "
                    + DatabaseConstants.tblMarketReason.TITLE + ", "
                    + DatabaseConstants.tblMarketReason.CODE + " )" + " VALUES ("
                    + marketReturnItem.marketReasonId + ", " + marketReturnItem.reasonDescription + ","
                    + code + " )");

            ctx.Insert(
                    "tblMarketReason",
                    DatabaseConstants.tblMarketReason.MARKET_REASON_ID + ", "
                            + DatabaseConstants.tblMarketReason.TITLE + ", "
                            + DatabaseConstants.tblMarketReason.CODE,
                    new String[]{Integer.toString(marketReturnItem.marketReasonId),
                            marketReturnItem.reasonDescription, code});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblSection(Context context, Section section) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblSection ("
                    + DatabaseConstants.tblSection.TITLE + ", "
                    + DatabaseConstants.tblSection.MAX_ORD_NO + ", "
                    + DatabaseConstants.tblSection.DSR_ID + ", "
                    + DatabaseConstants.tblSection.SECTION_ID + ", "
                    + DatabaseConstants.tblSection.ROUTE_ID + ", "
                    + DatabaseConstants.tblSection.ORDER_COL_DAY + ", "
                    + DatabaseConstants.tblSection.ORDER_DLV_DAY + ", "
                    + DatabaseConstants.tblSection.DELIVERYGROUP_ID + " )" + " VALUES ("
                    + section.title + "," + section.maxOrderNo + "," + section.dsrID + ","
                    + section.routeID + "," + section.orderColDay + "," + section.orderDlvDay + ","
                    + section.deliverygroupID + ")");

            ctx.Insert(
                    "tblSection",
                    DatabaseConstants.tblSection.TITLE + ", "
                            + DatabaseConstants.tblSection.MAX_ORD_NO + ", "
                            + DatabaseConstants.tblSection.DSR_ID + ", "
                            + DatabaseConstants.tblSection.SECTION_ID + ", "
                            + DatabaseConstants.tblSection.ROUTE_ID + ", "
                            + DatabaseConstants.tblSection.ORDER_COL_DAY + ", "
                            + DatabaseConstants.tblSection.ORDER_DLV_DAY + ", "
                            + DatabaseConstants.tblSection.DELIVERYGROUP_ID,
                    new String[]{section.title, Integer.toString(section.maxOrderNo),
                            section.dsrID, section.sectionID,
                            Integer.toString(section.routeID),
                            section.orderColDay, section.orderDlvDay,
                            Integer.toString(section.deliverygroupID)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblOutlet(Context context, Outlet outlet) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblOutlet ("
                    + DatabaseConstants.tblOutlet.OUTLET_ID + ", "
                    + DatabaseConstants.tblOutlet.DESCRIPTION + ", "
                    + DatabaseConstants.tblOutlet.CHANNEL_ID + ", "
                    + DatabaseConstants.tblOutlet.OWNER + ", "
                    + DatabaseConstants.tblOutlet.ADDRESS + ", "
                    + DatabaseConstants.tblOutlet.CONTACT_NO + ", "
                    + DatabaseConstants.tblOutlet.VISITED + ", "
                    + DatabaseConstants.tblOutlet.ROUTE_ID + ", "
                    + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", "
                    + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", "
                    + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE + " )" + " VALUES ("
                    + outlet.outletId + "," + outlet.description + ","
                    + outlet.channelId + "," + outlet.owner + "," + outlet.address + ","
                    + outlet.contactNo + "," + outlet.visited + ","
                    + outlet.routeID + "," + outlet.outletlatitude + "," + outlet.outletlongitude + " )");

            ctx.Insert(
                    "tblOutlet",
                    DatabaseConstants.tblOutlet.OUTLET_ID + ", "
                            + DatabaseConstants.tblOutlet.DESCRIPTION + ", "
                            + DatabaseConstants.tblOutlet.CHANNEL_ID + ", "
                            + DatabaseConstants.tblOutlet.OWNER + ", "
                            + DatabaseConstants.tblOutlet.ADDRESS + ", "
                            + DatabaseConstants.tblOutlet.CONTACT_NO + ", "
                            + DatabaseConstants.tblOutlet.VISITED + ", "
                            + DatabaseConstants.tblOutlet.ROUTE_ID + ", "
                            + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", "
                            + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE,
                    new String[]{(outlet.outletId), outlet.description,
                            (outlet.channelId),
                            outlet.owner, outlet.address, outlet.contactNo,
                            Integer.toString(outlet.visited), (outlet.routeID),
                            outlet.outletlatitude, outlet.outletlongitude});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblTPR(Context context, Tpr tpr) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblTPR("
                    + DatabaseConstants.tblTPR.TPR_ID + ", "
                    + DatabaseConstants.tblTPR.DESCRIPTION + ", "
                    + DatabaseConstants.tblTPR.CEILING_QTY + ", "
                    + DatabaseConstants.tblTPR.IS_FIXED_TPR + ", "
                    + DatabaseConstants.tblTPR.PROGRAM_TYPE + " )" + " VALUES ("
                    + tpr.tprId + "," + tpr.description + "," + tpr.ceilingQty + ","
                    + tpr.isFixedTPR + "," + tpr.programmType + ")");

            ctx.Insert(
                    "tblTPR",
                    DatabaseConstants.tblTPR.TPR_ID + ", "
                            + DatabaseConstants.tblTPR.DESCRIPTION + ", "
                            + DatabaseConstants.tblTPR.CEILING_QTY + ", "
                            + DatabaseConstants.tblTPR.IS_FIXED_TPR + ", "
                            + DatabaseConstants.tblTPR.PROGRAM_TYPE,
                    new String[]{Integer.toString(tpr.tprId), tpr.description,
                            Integer.toString(tpr.ceilingQty), Integer.toString(tpr.isFixedTPR),
                            Integer.toString(tpr.programmType)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblTPRSKUChnl(Context context, int tprID, int channelID, int skuID) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblTPRSKUChnl("
                    + DatabaseConstants.tblTPRSKUChnl.TPR_ID + ", "
                    + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + ", "
                    + DatabaseConstants.tblTPRSKUChnl.SKUID + " )" + " VALUES ("
                    + tprID + "," + channelID + "," + skuID + ")");

            ctx.Insert(
                    "tblTPRSKUChnl",
                    DatabaseConstants.tblTPRSKUChnl.TPR_ID + ", "
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + ", "
                            + DatabaseConstants.tblTPRSKUChnl.SKUID,
                    new String[]{Integer.toString(tprID), Integer.toString(channelID),
                            Integer.toString(skuID)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblTPRSlab(Context context, Slab slab) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblTPRSlab("
                    + DatabaseConstants.tblTPRSlab.TPR_ID + ", "
                    + DatabaseConstants.tblTPRSlab.SKUID + ", "
                    + DatabaseConstants.tblTPRSlab.SLAB_ID + ", "
                    + DatabaseConstants.tblTPRSlab.MIN_QTY + " )" + " VALUES ("
                    + slab.tprId + "," + slab.skuId + "," + slab.slabId + ","
                    + slab.minQty + ")");

            ctx.Insert(
                    "tblTPRSlab",
                    DatabaseConstants.tblTPRSlab.TPR_ID + ", "
                            + DatabaseConstants.tblTPRSlab.SKUID + ", "
                            + DatabaseConstants.tblTPRSlab.SLAB_ID + ", "
                            + DatabaseConstants.tblTPRSlab.MIN_QTY,
                    new String[]{Integer.toString(slab.tprId),
                            Integer.toString(slab.skuId), Integer.toString(slab.slabId),
                            Integer.toString(slab.minQty)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertTblSlabItem(Context context, Slab slab) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: INSERT INTO tblSlabItem("
                    + DatabaseConstants.tblSlabItem.TPR_ID + ", "
                    + DatabaseConstants.tblSlabItem.SLAB_ID + ", "
                    + DatabaseConstants.tblSlabItem.SLAB_TYPE + ", "
                    + DatabaseConstants.tblSlabItem.ITEM_DESC + ", "
                    + DatabaseConstants.tblSlabItem.FOR_QTY + ", "
                    + DatabaseConstants.tblSlabItem.ITEM_QTY + " )" + " VALUES ("
                    + slab.tprId + "," + slab.slabId + "," + slab.slabType + ","
                    + slab.description + "," + slab.forQty + "," + slab.itemQty + ")");

            ctx.Insert(
                    "tblSlabItem",
                    DatabaseConstants.tblSlabItem.TPR_ID + ", "
                            + DatabaseConstants.tblSlabItem.SLAB_ID + ", "
                            + DatabaseConstants.tblSlabItem.SLAB_TYPE + ", "
                            + DatabaseConstants.tblSlabItem.ITEM_DESC + ", "
                            + DatabaseConstants.tblSlabItem.FOR_QTY + ", "
                            + DatabaseConstants.tblSlabItem.ITEM_QTY,
                    new String[]{Integer.toString(slab.tprId),
                            Integer.toString(slab.slabId), Integer.toString(slab.slabType),
                            slab.description, Integer.toString(slab.forQty), Integer.toString(slab.itemQty)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Update Functions

    public static void updateTblDSRBasicWithTargetInformation(Context context, User user)
    {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: UPDATE tblDSRBasic SET " + " "
                    + DatabaseConstants.tblDSRBasic.TARGET + " = " + user.target + " "
                    + DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED + "=" + user.orderAchieved + " "
                    + DatabaseConstants.tblDSRBasic.TARGET_REM + "=" + user.targetRemain + " "
                    + DatabaseConstants.tblDSRBasic.DAY_REMAIN + "=" + user.dayRemain + " "
                    + DatabaseConstants.tblDSRBasic.DAILY_TAREGET + "=" + user.dailyTarget
                    + " WHERE DSR_ID = " + user.dsrId);

            ctx.Update("tblDSRBasic",
                    DatabaseConstants.tblDSRBasic.TARGET + ", "
                            + DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED + ", "
                            + DatabaseConstants.tblDSRBasic.TARGET_REM + ", "
                            + DatabaseConstants.tblDSRBasic.DAY_REMAIN + ", "
                            + DatabaseConstants.tblDSRBasic.DAILY_TAREGET + ",",
                    new String[]
                            {		Double.toString(user.target),
                                    Double.toString(user.orderAchieved),
                                    Double.toString(user.targetRemain),
                                    Integer.toString(user.dayRemain),
                                    Double.toString(user.dailyTarget)
                            },
                    DatabaseConstants.tblDSRBasic.DSR_ID + " =? ",
                    new String[]{user.dsrId});

            ctx.End();
        }

        catch (Exception e)
        {
            // TODO: Rollback
            e.printStackTrace();
        }
    }

    public static String updateNewOutlet(Context context, Outlet outlet) {

        int id = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            ctx.Update(
                    "new_outlet",
                    DatabaseConstants.KEY_DESCRIPTION + ", " +
                            DatabaseConstants.KEY_ROUTE_ID + ", " +
                            DatabaseConstants.KEY_CHANNEL_ID + ", " +
                            DatabaseConstants.KEY_ADDRESS + " , " +
                            DatabaseConstants.KEY_VISITED + " , " +
                            DatabaseConstants.KEY_OWNER + " , " +
                            DatabaseConstants.KEY_CONTACT_NO + " , " +
                            DatabaseConstants.KEY_IMAGE_URLS + " , " +
                            DatabaseConstants.KEY_OUTLET_LATITUDE + " , " +
                            DatabaseConstants.KEY_OUTLET_LONGITUDE,
                    new String[]{outlet.description,
                            String.valueOf(outlet.routeID), String.valueOf(outlet.channelId),
                            outlet.address,
                            String.valueOf(outlet.visited), outlet.owner,
                            outlet.contactNo, String.valueOf(outlet.imageUrls), outlet.outletlatitude, outlet.outletlongitude},
                    DatabaseConstants.tblOrderItem.OUTLET_ID + "=?",
                    new String[]{(outlet.outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outlet.outletId;
    }

    public static void updateNewImage(Context context, Image image) {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            ctx.Update(
                    "images",
                    DatabaseConstants.KEY_IMAGE_TYPE + ", " +
                            DatabaseConstants.KEY_IMAGE_URL,
                    new String[]{String.valueOf(image.imageType), image.imageUrl},
                    DatabaseConstants.tblOrderItem.OUTLET_ID + "=?",
                    new String[]{(image.outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOrderWithFirstEntryTime(Context context,
                                                        int outletId, String currentDateAndTime) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblOrder SET StartTime =\""
                            + currentDateAndTime + "\" WHERE OutletID='" + outletId+"'"));
            ctx.Update("tblOrder", DatabaseConstants.tblOrder.CHECK_IN_TIME,
                    new String[]{currentDateAndTime},
                    DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
                    new String[]{Integer.toString(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOrderWithFirstEntryExitTime(Context context,
                                                            String outletId, String startTime, String endTime) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println(("SQL COMMAND: UPDATE tblOrder SET StartTime = " + startTime + " EndTime =\""
                    + endTime + "\" WHERE OutletID='" + outletId+"'"));
            ctx.Update("tblOrder", DatabaseConstants.tblOrder.CHECK_IN_TIME + ","
                            + DatabaseConstants.tblOrder.CHECK_OUT_TIME,
                    new String[]{startTime, endTime},
                    DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
                    new String[]{(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOrderWithNotOrderCoz(Context context,
                                                     String outletId, String notOrderReasonId, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblOrder SET NotordCoz =\""
                            + notOrderReasonId + "\" WHERE OutletID='" + outletId+"'"));
            ctx.Update("tblOrder", DatabaseConstants.tblOrder.NO_ORDER_REASON,
                    new String[]{(notOrderReasonId)},
                    DatabaseConstants.tblOrder.OUTLET_ID + "=? AND "
                            + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                    new String[]{(outletId), Integer.toString(orderNo)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblDSRBasicSectionID(Context context, String sectionID) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblDSRBasic SET SectionID ='"
                            + sectionID));

            ctx.Update("tblDSRBasic", DatabaseConstants.tblDSRBasic.SECTION_ID,
                    new String[]{(sectionID)}, null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOrderOrderLocation(Context context,
                                                   String outletId, String OrderLatitude, String OrderLongitude, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblOrder SET OrderLatitude ='"
                            + OrderLatitude
                            + "', OrderLongitude= '"
                            + OrderLongitude + "'  WHERE OutletID='" + outletId+"'"));

            ctx.Update("tblOrder", DatabaseConstants.tblOrder.ORDER_LATITUDE
                            + "," + DatabaseConstants.tblOrder.ORDER_LONGITUDE,
                    new String[]{OrderLatitude, OrderLongitude},
                    DatabaseConstants.tblOrder.OUTLET_ID + "=? AND "
                            + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                    new String[]{(outletId), Integer.toString(orderNo)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSentStatusOfTblOrder(Context context, String outletId, int orderNo) {

        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println(("SQL COMMAND: UPDATE tblOrder SET SentStatus = 0 WHERE OutletID='" + outletId + "' AND OrderNo =" + orderNo));
            if (orderNo > 0) {
                ctx.Update("tblOrder", DatabaseConstants.tblOrder.SENT_STATUS,
                        new String[]{Integer.toString(0)},
                        DatabaseConstants.tblOrder.OUTLET_ID + " =? " + " AND "
                                + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                        new String[]{(outletId), Integer.toString(orderNo)});
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOrderOrderTotal(Context context, String outletID, double orderTotal, int paymentMode, int orderNo, int deliverymode, String deliverydate) {
        String  sdeliverydate=changeFormat(deliverydate);
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: UPDATE tblOrder set "
                    + DatabaseConstants.tblOrder.ORDER_TOTAL + " = "
                    + orderTotal + " WHERE OutletId = " + outletID);
            ctx.Update("tblOrder", DatabaseConstants.tblOrder.ORDER_TOTAL +", "
                            + DatabaseConstants.tblOrder.PAYMENT_MODE  +", "
                            + DatabaseConstants.tblOrder.DELIVERY_MODE +", "
                            +DatabaseConstants.tblOrder.DELIVERY_DATE,
                    new String[]{Double.toString(orderTotal), Integer.toString(paymentMode),
                            Integer.toString(deliverymode),sdeliverydate},
                    DatabaseConstants.tblOrder.OUTLET_ID + " =? AND "
                            + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                    new String[]{(outletID), Integer.toString(orderNo)});

            //           ctx.Update("tblOrder", DatabaseConstants.tblOrder.ORDER_TOTAL +","
            //                  + DatabaseConstants.tblOrder.PAYMENT_MODE,
            //                  new String[]{Double.toString(orderTotal), Integer.toString(paymentMode)},
            //                   DatabaseConstants.tblOrder.OUTLET_ID + " =? AND "
            //                           + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
            //                  new String[]{(outletID), Integer.toString(orderNo)});

//			ctx.Update("tblOrder", DatabaseConstants.tblOrder.ORDER_TOTAL,
//					new String[]{Double.toString(orderTotal)},
//					DatabaseConstants.tblOrder.OUTLET_ID + "=?",
//					new String[]{Integer.toString(outletID)});

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateOrderItemFromTblOrderItem(Context context, int orderNo, String outletID,
                                                       String skuId, int ctn, int pcs, double total, double tkOff,
                                                       int allocationQty, int Carton_Org, int Pieces_Org) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: UPDATE tblOrderItem SET " + " "
                    + DatabaseConstants.tblOrderItem.CARTON + " = " + ctn + ", "
                    + DatabaseConstants.tblOrderItem.PIECE + " = " + pcs + " ,"
                    + DatabaseConstants.tblOrderItem.TOTAL + " = " + total + ", "
                    + DatabaseConstants.tblOrderItem.TK_OFF + " = " + tkOff + ", "
                    + DatabaseConstants.tblOrderItem.SUGGESTED + " = " + allocationQty + ", "
                    + DatabaseConstants.tblOrderItem.CARTON_ORG + " = " + ctn +", "
                    + DatabaseConstants.tblOrderItem.PIECE_ORG + " = " + pcs +", "
                    + " WHERE OutletID = " + outletID
                    + " AND OrderNo = " + orderNo
                    + " AND SKUID = " + skuId);

            ctx.Update(
                    "tblOrderItem",
                    DatabaseConstants.tblOrderItem.CARTON + ", "
                            + DatabaseConstants.tblOrderItem.PIECE + ", "
                            + DatabaseConstants.tblOrderItem.TOTAL + ", "
                            + DatabaseConstants.tblOrderItem.TK_OFF + ", "
                            + DatabaseConstants.tblOrderItem.SUGGESTED + ","
                            + DatabaseConstants.tblOrderItem.CARTON_ORG + ", "
                            + DatabaseConstants.tblOrderItem.PIECE_ORG,
                    new String[]{Integer.toString(ctn),
                            Integer.toString(pcs),
                            Double.toString(total),
                            Double.toString(tkOff),
                            Integer.toString(allocationQty),
                            Integer.toString(ctn),
                            Integer.toString(pcs)},
                    DatabaseConstants.tblOrderItem.OUTLET_ID + " =? " + " AND "
                            + DatabaseConstants.tblOrderItem.ORDER_NO + " =? " + " AND "
                            + DatabaseConstants.tblOrderItem.SKUID + " =? ",
                    new String[]{(outletID), Integer.toString(orderNo),
                            (skuId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutletWithNoLocation(Context context,
                                                     String outletId, String OutletLatitude, String OutletLongitude) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblOutlet SET Latitude ='"
                            + OutletLatitude
                            + "', Longitude= '"
                            + OutletLongitude + "' WHERE OutletID='" + outletId+"'"));

            ctx.Update("tblOutlet", DatabaseConstants.tblOutlet.OUTLET_LATITUDE
                            + "," + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE,
                    new String[]{OutletLatitude, OutletLongitude},
                    DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
                    new String[]{(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutletVisitedField(Context context,
                                                   String outletId, int visitStatus) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println(("SQL COMMAND: UPDATE tblOutlet SET Visited =\""
                    + visitStatus + "\" WHERE OutletID='" + outletId+"'"));
            ctx.Update("tblOutlet", DatabaseConstants.tblOutlet.VISITED,
                    new String[]{Integer.toString(visitStatus)},
                    DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
                    new String[]{(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutletVisitedField(Context context, int visitStatus) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println(("SQL COMMAND: UPDATE tblOutlet SET Visited =\""
                    + visitStatus));
            ctx.Update("tblOutlet", DatabaseConstants.tblOutlet.VISITED,
                    new String[]{Integer.toString(visitStatus)},
                    DatabaseConstants.tblOutlet.VISITED + "=?",
                    new String[]{Integer.toString(1)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutletWebOrderDetailKey(Context context,
                                                        String outletId, String refNo, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblOrder SET RefNo=" + refNo
                            + " , SentStatus= 1  WHERE OutletID='"
                            + outletId + "' AND OrderNo=" + orderNo));
            ctx.Update(
                    "tblOrder",
                    DatabaseConstants.tblOrder.REF_NO + ","
                            + DatabaseConstants.tblOrder.SENT_STATUS,
                    new String[]{refNo,
                            Integer.toString(1)},
                    DatabaseConstants.tblOrder.OUTLET_ID + "=? and "
                            + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                    new String[]{(outletId),
                            Integer.toString(orderNo)});

            ctx.Update(
                    "tblOutlet",
                    DatabaseConstants.tblOutlet.VISITED ,
                    new String[]{Integer.toString(0)},
                    DatabaseConstants.tblOrder.OUTLET_ID + "=? ",
                    new String[]{(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutletTotalLineSoldandSentStatus(
            Context context, String outletID, int TotalLineSold, int sentStatus) {
//		ConnectionContext ctx = new ConnectionContext(context);
//		try {
//			ctx.Begin();
//			System.out.println("SQL COMMAND: UPDATE tblOutlet set "
//					+ DatabaseConstants.tblOrder.TOTAL_LINE_SOLD + " = "
//					+ TotalLineSold + ", "
//					+ DatabaseConstants.tblOrder.SENT_STATUS + "="
//					+ sentStatus + " WHERE OutletId = " + outletID);
//			ctx.Update(
//					"tblOutlet",
//					DatabaseConstants.tblOrder.TOTAL_LINE_SOLD + ","
//							+ DatabaseConstants.tblOrder.SENT_STATUS,
//					new String[] { Integer.toString(TotalLineSold),
//							Integer.toString(sentStatus) },
//					DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
//					new String[] { Integer.toString(outletID) });

			/*
             * ctx.Update("tblOutlet",
			 * DatabaseConstants.tblOutlet.TOTAL_LINE_SOLD, new
			 * String[]{Double.toString(TotalLineSold)},
			 * DatabaseConstants.tblOrder.OUTLET_ID+"=?", new
			 * String[]{Integer.toString(outletID)});
			 */
//
//			ctx.End();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    public static void updateTblSpMgtReturn(Context context, int outletId,
                                            int updated) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblSPMgtReturn SET IsUpdated = "
                            + updated + " WHERE OutletID= '" + outletId+"'");
            ctx.Update("tblSPMgtReturn",
                    DatabaseConstants.tblSPMgtReturn.IS_UPDATED,
                    new String[]{Integer.toString(updated)},
                    DatabaseConstants.tblSPMgtReturn.OUTLET_ID + " =? ",
                    new String[]{Integer.toString(outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePopItem(Context context, int popItemId,
                                     String outletId, int currentQty) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblPOPOutletItem SET currentQty = "
                            + currentQty
                            + " WHERE OutletID= '"
                            + outletId
                            + "' AND popItemId = " + popItemId));
            ctx.Update(
                    "tblPOPOutletItem",
                    DatabaseConstants.tblPOPOutletItem.CURRENT_QTY,
                    new String[]{Integer.toString(currentQty)},
                    DatabaseConstants.tblPOPOutletItem.OUTLET_ID + " =? AND "
                            + DatabaseConstants.tblPOPOutletItem.POP_ITEM_ID
                            + " =? ",
                    new String[]{(outletId),
                            Integer.toString(popItemId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblDSRBasicWithOrderCompleteStatus(
            Context context, int OrderCompleteStatus) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblDSRBasic set  isOrderCompleted=" + OrderCompleteStatus));

            ctx.Update("tblDSRBasic",
                    DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED,
                    new String[]{Integer.toString(OrderCompleteStatus)},
                    null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblDSRBasicWithUpdatedVisitDate(
            Context context, String visitDate) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(("SQL COMMAND: UPDATE tblDSRBasic set VisitDate=" + visitDate));

            ctx.Update("tblDSRBasic",
                    DatabaseConstants.tblDSRBasic.VISIT_DATE,
                    new String[]{visitDate},
                    null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//endregion

    //region Update table From Web
    public static void updateTblBrands(Context context, Brand brand) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblBrand SET Name = "
                            + brand.name + " WHERE BrandID= " + brand.brandId);
            ctx.Update("tblBrand",
                    DatabaseConstants.tblBrand.NAME,
                    new String[]{brand.name},
                    DatabaseConstants.tblBrand.BRAND_ID + " =? ",
                    new String[]{(brand.brandId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatetTblNoOrderReason(Context context, Reason reason) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblNoOrderReason SET Description = "
                            + reason.description + " WHERE ReasonID= " + reason.reasonId);
            ctx.Update("tblNoOrderReason",
                    DatabaseConstants.tblNoOrderReason.DESCRIPTION,
                    new String[]{reason.description},
                    DatabaseConstants.tblNoOrderReason.REASON_ID + " =? ",
                    new String[]{Integer.toString(reason.reasonId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatetTblChannels(Context context, Channel channel) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblChannel SET Name = "
                            + channel.name + " WHERE ChannelID= " + channel.channelID);
            ctx.Update("tblChannel",
                    DatabaseConstants.tblChannel.NAME,
                    new String[]{channel.name},
                    DatabaseConstants.tblChannel.CHANNEL_ID + " =? ",
                    new String[]{(channel.channelID)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblMarketReason(Context context, MarketReturnItem marketReturnItem, String code) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblMarketReason SET Title = "
                            + marketReturnItem.reasonDescription + ", Code = "
                            + code + " WHERE MarketReasonID= " + marketReturnItem.marketReasonId);
            ctx.Update("tblMarketReason",
                    DatabaseConstants.tblMarketReason.TITLE + ", "
                            + DatabaseConstants.tblMarketReason.CODE,
                    new String[]{marketReturnItem.reasonDescription, code},
                    DatabaseConstants.tblMarketReason.MARKET_REASON_ID + " =? ",
                    new String[]{Integer.toString(marketReturnItem.marketReasonId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblSku(Context context, Sku sku) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblSKU SET Title = "
                            + sku.title + ", BrandID = "
                            + sku.brandId + ", PcsRate = "
                            + sku.pcsRate + ", PcsPerCtn = "
                            + sku.pcsPerCtn + ", Packsize = "
                            + sku.packSize + ", CtnRate = "
                            + sku.ctnRate + ", Target = "
                            + sku.target + ", MessageForHHT = "
                            + sku.MessageForHHT + ", CriticalStock = "
                            + sku.CriticalStock + ", PositionValue = "
                            + sku.PositionValue + " WHERE SKUID= " + sku.skuId);
            ctx.Update("tblSKU",
                    DatabaseConstants.tblSKU.TITLE + ", "
                            + DatabaseConstants.tblSKU.BRAND_ID + ", "
                            + DatabaseConstants.tblSKU.PCS_RATE + ", "
                            + DatabaseConstants.tblSKU.PCS_PER_CTN + ", "
                            + DatabaseConstants.tblSKU.PACK_SIZE + ", "
                            + DatabaseConstants.tblSKU.CTN_RATE + ", "
                            + DatabaseConstants.tblSKU.TARGET + ", "
                            + DatabaseConstants.tblSKU.MESSAGE_FOR_HHT + ", "
                            + DatabaseConstants.tblSKU.CRITICAL_STOCK + ", "
                            + DatabaseConstants.tblSKU.POSITION_VALUE,
                    new String[]{sku.title, sku.brandId, Double.toString(sku.pcsRate),
                            Double.toString(sku.pcsPerCtn), Double.toString(sku.packSize), Double.toString(sku.ctnRate),
                            Integer.toString(sku.target), sku.MessageForHHT, Integer.toString(sku.CriticalStock), Integer.toString(sku.PositionValue)},
                    DatabaseConstants.tblSKU.SKUID + " =? ",
                    new String[]{sku.skuId});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblSection(Context context, Section section) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblSection SET Title = "
                            + section.title + ", DSRID = "
                            + section.dsrID + ", RouteID = "
                            + section.routeID + ", OrderColDay = "
                            + section.orderColDay + ", OrderDlvDay = "
                            + section.orderDlvDay + ", DeliveryGroupID = "
                            + section.deliverygroupID + " WHERE SectionID = " + section.sectionID);
            ctx.Update("tblSection",
                    DatabaseConstants.tblSection.TITLE + ", "
                            + DatabaseConstants.tblSection.DSR_ID + ", "
                            + DatabaseConstants.tblSection.ROUTE_ID + ", "
                            + DatabaseConstants.tblSection.ORDER_COL_DAY + ", "
                            + DatabaseConstants.tblSection.ORDER_DLV_DAY + ", "
                            + DatabaseConstants.tblSection.DELIVERYGROUP_ID,
                    new String[]{section.title, section.dsrID,
                            Integer.toString(section.routeID), section.orderColDay, section.orderDlvDay,
                            Integer.toString(section.deliverygroupID)},
                    DatabaseConstants.tblSection.SECTION_ID + " =? ",
                    new String[]{section.sectionID});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTblOutlet(Context context, Outlet outlet) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: UPDATE tblOutlet SET Desciption = "
                            + outlet.description + ", ChannelID = "
                            + outlet.channelId + ", Owner = "
                            + outlet.owner + ", Address = "
                            + outlet.address + ", Phone = "
                            + outlet.visited + ", RouteID = "
                            + outlet.routeID + ", Latitude = "
                            + outlet.outletlatitude + ", Longitude = "
                            + outlet.outletlongitude + " WHERE OutletID= '" + outlet.outletId +"'");
            ctx.Update("tblOutlet",
                    DatabaseConstants.tblOutlet.DESCRIPTION + ", "
                            + DatabaseConstants.tblOutlet.CHANNEL_ID + ", "
                            + DatabaseConstants.tblOutlet.OWNER + ", "
                            + DatabaseConstants.tblOutlet.ADDRESS + ", "
                            + DatabaseConstants.tblOutlet.CONTACT_NO + ", "
                            + DatabaseConstants.tblOutlet.ROUTE_ID + ", "
                            + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", "
                            + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE,
                    new String[]{outlet.description, (outlet.channelId), outlet.owner,
                            outlet.address, outlet.contactNo,
                            (outlet.routeID), outlet.outletlatitude, outlet.outletlongitude},
                    DatabaseConstants.tblOutlet.OUTLET_ID + " =? ",
                    new String[]{(outlet.outletId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Delete Functions

    public static void deleteOutlets(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            ctx.Delete("tblOutlet", null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteNewOutlet(Context context, String outletID) {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            ctx.Delete(
                    "new_outlet",
                    DatabaseConstants.KEY_OUTLET_ID + "=?",
                    new String[]{outletID});

            ctx.Delete(
                    "images",
                    DatabaseConstants.KEY_OUTLET_ID + "=?",
                    new String[]{(outletID)});

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllPendingNewOutlet(Context context, String outletID) {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            if (outletID != null) {
                deleteNewOutlet(context, outletID);
            } else {
                ctx.Begin();
                ctx.Delete(
                        "new_outlet",null,null);

                ctx.Delete(
                        "images",
                        null,
                        null);

                ctx.End();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteOrderFromTblOrderItem(Context context, String outletID,
                                                   String skuId) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblOrderItem WHERE OutletId = "
                            + outletID + " and SKUID = " + skuId);
            ctx.Delete(
                    "tblOrderItem",
                    DatabaseConstants.tblOrderItem.OUTLET_ID + " =? " + " AND "
                            + DatabaseConstants.tblOrderItem.SKUID + " =? ",
                    new String[]{(outletID),
                            (skuId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMarketReturnItemFromTblMarketReturn(
            Context context, MarketReturnItem marketReturnItem) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblMarketReturn WHERE MarketReturnId = "
                            + marketReturnItem.marketReturnId);
            ctx.Delete("tblMarketReturn",
                    DatabaseConstants.tblMarketReturn.MARKET_RETURN_ID + "=?",
                    new String[]{Integer
                            .toString(marketReturnItem.marketReturnId)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTblTPR(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblTPR");
            ctx.Delete("tblTPR", null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTblTPRSKUChnl(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblTPRSKUChnl");
            ctx.Delete("tblTPRSKUChnl", null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTblTPRSlab(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblTPRSlab");
            ctx.Delete("tblTPRSlab", null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTblSlabItem(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: DELETE FROM tblSlabItem");
            ctx.Delete("tblSlabItem", null, null);
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteOrder(Context context, String outletId, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            ctx.Delete(
                    "tblOrderItem",
                    DatabaseConstants.tblOrderItem.ORDER_NO + "=?",
                    new String[]{(String.valueOf(orderNo))});
            ctx.Delete(
                    "tblOrder",
                    DatabaseConstants.tblOrder.ORDER_NO + "=?",
                    new String[]{String.valueOf(orderNo)});
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static  void deleteAllOrder(Context context){
        ConnectionContext ctx = new ConnectionContext(context);
        try{
            ctx.Begin();
            //SQLiteDatabase db = ctx.getWritableDatabase();
            //delete all from tblOrder
            ctx.Delete("tblOrder", null, null);
            //delete all from tblOrderItem
            ctx.Delete("tblOrderItem", null, null);

            ctx.End();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  static  void deleteOrderSummary(Context context){
        ConnectionContext ctx = new ConnectionContext(context);


        try{
            ctx.Begin();
            //SQLiteDatabase db = ctx.getWritableDatabase();

            ctx.Delete("tblOrderSummary", null, null);
            //delete all from tblOrder
            //db.execSQL("Delete from tblOrderSummary");
            //db.close();
            ctx.End();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //endregion

    //region Get Functions
    public static ArrayList<Reason> getReasonListForNoOrder(Context context) {
        ArrayList<Reason> returnReasonList = new ArrayList<Reason>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblNoOrderReason");
            Cursor cursor = ctx.Select("SELECT * FROM tblNoOrderReason");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Reason now = new Reason(
                                cursor.getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblNoOrderReason.REASON_ID)),
                                cursor.getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblNoOrderReason.DESCRIPTION)));
                        returnReasonList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnReasonList;
    }

    public static ArrayList<OrderItem> getOrderSummaryBySku(Context context) {
        ArrayList<OrderItem> returnOrderList = new ArrayList<OrderItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT "
                    + DatabaseConstants.tblOrderItem.SKUID + ", SUM("
                    + DatabaseConstants.tblOrderItem.CARTON + "), SUM("
                    + DatabaseConstants.tblOrderItem.PIECE + "), SUM("
                    + DatabaseConstants.tblOrderItem.TOTAL
                    + ") FROM tblOrderItem GROUP BY "
                    + DatabaseConstants.tblOrderItem.SKUID);

            Cursor cursor = ctx.Select("SELECT "
                    + DatabaseConstants.tblOrderItem.SKUID + ", SUM("
                    + DatabaseConstants.tblOrderItem.CARTON + "), SUM("
                    + DatabaseConstants.tblOrderItem.PIECE + "), SUM("
                    + DatabaseConstants.tblOrderItem.TOTAL
                    + ") FROM tblOrderItem GROUP BY "
                    + DatabaseConstants.tblOrderItem.SKUID);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);
                        now.Carton = cursor.getInt(cursor.getColumnIndex("SUM("
                                + DatabaseConstants.tblOrderItem.CARTON + ")"));
                        now.Piece = cursor.getInt(cursor.getColumnIndex("SUM("
                                + DatabaseConstants.tblOrderItem.PIECE + ")"));
                        now.Carton += now.Piece / now.sku.pcsPerCtn;
                        now.Piece = now.Piece % now.sku.pcsPerCtn;
                        now.Total = cursor.getDouble(cursor
                                .getColumnIndex("SUM("
                                        + DatabaseConstants.tblOrderItem.TOTAL
                                        + ")"));
                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    public static ArrayList<OrderItemsByOrderNo> getOrderSummaryFromTable(Context context) {

        ArrayList<OrderItemsByOrderNo> returnOrderList = new ArrayList<OrderItemsByOrderNo>();
        ConnectionContext ctx = new ConnectionContext(context);

        //String[] splittedDate = date.split("-");
        //String newVisitedDateString = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];

        try {
            ctx.Begin();


            Cursor cursor = ctx.Select("select OrderNo, OrderTotal, 1 as SentStatus, OutletID from tblOrderSummary order by OrderNo asc");
            OrderItemsByOrderNo now;
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        int orderNo = cursor.getInt(cursor.getColumnIndex("OrderNo"));
                        double total = cursor.getDouble(cursor.getColumnIndex("OrderTotal"));
                        int sentStatus = cursor.getInt(cursor.getColumnIndex("SentStatus"));
                        String outletID = cursor.getString(cursor.getColumnIndex("OutletID"));

                        Cursor cursorGetsOutletName = ctx.Select("select Description from tblOutlet where OutletID = '"+outletID+"'");
                        cursorGetsOutletName.moveToFirst();
                        String outletName = cursorGetsOutletName.getString(cursorGetsOutletName.getColumnIndex("Description"));

                        now = new OrderItemsByOrderNo(orderNo, total, sentStatus, outletName);

                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    /**
     * Modification for getting orderNO, sum, sentStatus
     * @param context
     * @return
     */
    public static ArrayList<OrderItemsByOrderNo> getOrderSummaryByOrderNo(Context context, String date) {

        ArrayList<OrderItemsByOrderNo> returnOrderList = new ArrayList<OrderItemsByOrderNo>();
        ConnectionContext ctx = new ConnectionContext(context);

        String[] splittedDate = date.split("-");
        String newVisitedDateString = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];

        try {
            ctx.Begin();

            
            Cursor cursor = ctx.Select("select OrderNo, OrderTotal, SentStatus, OutletID from tblOrder where OrderDate='"+ newVisitedDateString +"' order by OrderNo asc");
            OrderItemsByOrderNo now;
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        int orderNo = cursor.getInt(cursor.getColumnIndex("OrderNo"));
                        double total = cursor.getDouble(cursor.getColumnIndex("OrderTotal"));
                        int sentStatus = cursor.getInt(cursor.getColumnIndex("SentStatus"));
                        String outletID = cursor.getString(cursor.getColumnIndex("OutletID"));

                        Cursor cursorGetsOutletName = ctx.Select("select Description from tblOutlet where OutletID = '"+outletID+"'");
                        cursorGetsOutletName.moveToFirst();
                        String outletName = cursorGetsOutletName.getString(cursorGetsOutletName.getColumnIndex("Description"));

                        now = new OrderItemsByOrderNo(orderNo, total, sentStatus, outletName);

                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    public static ArrayList<OrderItem> getOrderHistoryBySku(Context context, int outletId, String orderDate) {
        ArrayList<OrderItem> returnOrderList = new ArrayList<OrderItem>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime;
        currentDateandTime = sdf.format(new Date());
        String neworderDate;
        neworderDate = currentDateandTime;
        if (neworderDate.equals(orderDate)) {
            return returnOrderList;
        } else if (orderDate.equals("Today's Order")) {
            orderDate = neworderDate;
        }

        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println(" SELECT SKUID, SUM(Carton), SUM(Piece), SUM(Total), OrderDate FROM tblOrderItem " +
                            "JOIN tblOrder ON tblOrderItem.OrderNo = tblOrder.OrderNo"
                            + " Where tblOrder.OutletID =" + outletId + " Group By SKUID");
            Cursor cursor = ctx.Select(
                    "SELECT " + DatabaseConstants.tblOrderItem.SKUID
                            + ", SUM("
                            + DatabaseConstants.tblOrderItem.CARTON
                            + "), SUM("
                            + DatabaseConstants.tblOrderItem.PIECE
                            + "), SUM("
                            + DatabaseConstants.tblOrderItem.TOTAL
                            + "),"
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + " FROM tblOrderItem JOIN tblOrder ON tblOrderItem.OrderNo = tblOrder.OrderNo WHERE tblOrder."
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + " =? AND "
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + " =? GROUP BY "
                            + DatabaseConstants.tblOrderItem.SKUID,
                    new String[]{Integer.toString(outletId), orderDate});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);
                        now.Carton = cursor.getInt(cursor.getColumnIndex("SUM("
                                + DatabaseConstants.tblOrderItem.CARTON
                                + ")"));
                        now.Piece = cursor
                                .getInt(cursor
                                        .getColumnIndex("SUM("
                                                + DatabaseConstants.tblOrderItem.PIECE
                                                + ")"));
                        now.Carton += now.Piece / now.sku.pcsPerCtn;
                        now.Piece = now.Piece % now.sku.pcsPerCtn;
                        now.Total = cursor
                                .getDouble(cursor
                                        .getColumnIndex("SUM("
                                                + DatabaseConstants.tblOrderItem.TOTAL
                                                + ")"));
                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    public static ArrayList<Outlet> getOutletList(Context context, String routeId) {
        ArrayList<Outlet> returnOutletList = new ArrayList<Outlet>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOutlet WHERE RouteID="
                            + routeId);
            Cursor cursor = ctx.Select("SELECT * FROM tblOutlet WHERE "
                            + DatabaseConstants.tblOutlet.ROUTE_ID + "=?",
                    new String[]{(routeId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Outlet now = new Outlet();
                        now.outletId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_ID));
                        now.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.DESCRIPTION));
                        now.routeID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.ROUTE_ID));
                        now.channelId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.CHANNEL_ID));
                        now.address = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.ADDRESS));

                        now.visited = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.VISITED));

                        now.outletlatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LATITUDE));
                        now.outletlongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LONGITUDE));
                        returnOutletList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }

    public static ArrayList<Order> getOutletListWithNotSentStatus(Context context) {
        ArrayList<Order> returnOutletList = new ArrayList<Order>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOrder WHERE SentStatus = 0");
            Cursor cursor = ctx.Select("SELECT * FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.SENT_STATUS + "=?",
                    new String[]{Integer.toString(0)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Order now = new Order();
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.OUTLET_ID));
                        now.OrderTotal = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_TOTAL));
                        now.Visited = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.VISITED));
                        now.OrderLatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LATITUDE));
                        now.OrderLongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LONGITUDE));
                        now.StartTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_IN_TIME));
                        now.EndTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_OUT_TIME));
                        now.NotOrdCoz = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.NO_ORDER_REASON));
                        returnOutletList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }

    public static ArrayList<Order> getOrderListToSend(Context context, String visitDate) {
        visitDate = changeFormat(visitDate);
        ArrayList<Order> returnOutletList = new ArrayList<Order>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            Log.d(TAG, "SQL COMMAND: SELECT * FROM tblOrder WHERE SentStatus = 0");

            /*Cursor cursor = ctx.Select("SELECT * FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.SENT_STATUS + "=? AND "
                            + DatabaseConstants.tblOrder.ORDER_DATE + "=?",
                    new String[]{Integer.toString(0), visitDate});*/

            String mQuery = "SELECT * FROM tblOrder WHERE "
                    + DatabaseConstants.tblOrder.SENT_STATUS + "=" + new String[]{Integer.toString(0)};

            Cursor cursor = ctx.Select("SELECT ExptDlvDate, DeliveryShift, OrderNo, OutletID, SectionID, OrderDate||' '||StartTime as OrderDate, StartTime, EndTime, SpotSale, OrderTotal, Visited, TotalTKOff, NotordCoz, OrderLatitude, OrderLongitude, RefNo, SentStatus, SentStatusSale, PaymentMode FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.SENT_STATUS + "=?",
                    new String[]{Integer.toString(0)});


            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Order now = new Order();
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.OUTLET_ID));
                        now.OrderNo = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_NO));
                        now.OrderDate = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_DATE));
                        now.SectionID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.SECTION_ID));
                        now.OrderLongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LONGITUDE));
                        now.OrderLatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LATITUDE));
                        now.StartTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_IN_TIME));
                        now.EndTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_OUT_TIME));
                        now.NotOrdCoz = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.NO_ORDER_REASON));
                        now.RefNo = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.REF_NO));
                        now.PaymentMode = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.PAYMENT_MODE));
                        now.DeliveryMode = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.DELIVERY_MODE));
                        now.DeliveryDate = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.DELIVERY_DATE));
                        returnOutletList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }

    public static ArrayList<Order> getOrderListToSend(Context context, String outletID, String visitDate) {
        visitDate = changeFormat(visitDate);
        ArrayList<Order> returnOutletList = new ArrayList<Order>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT tblOrder.*, tblOutlet.Latitude, tblOutlet.Longitude FROM tblOrder INNER JOIN tblOutlet on  " +
                            "tblOrder.OutletID = tblOutlet.OutletID WHERE tblOrder.SentStatus=0 AND tblOrder.OutletID='"+ outletID+"'");
            Cursor cursor = ctx.Select("SELECT tblOrder.*, tblOutlet.Latitude, tblOutlet.Longitude FROM tblOrder INNER JOIN tblOutlet on tblOrder.OutletID = " +
                            "tblOutlet.OutletID WHERE tblOutlet."
                            + DatabaseConstants.tblOutlet.OUTLET_ID
                            + " =? AND "
                            + DatabaseConstants.tblOrder.SENT_STATUS
                            + " =? AND "
                            + DatabaseConstants.tblOrder.ORDER_DATE + " =? ",

                    new String[]{(outletID), Integer.toString(0), visitDate});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Order now = new Order();
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.OUTLET_ID));
                        now.OrderNo = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_NO));
                        now.OrderDate = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_DATE));
                        now.SectionID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.SECTION_ID));
                        now.OrderLongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LONGITUDE));
                        now.OrderLatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.ORDER_LATITUDE));
                        now.StartTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_IN_TIME));
                        now.EndTime = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.CHECK_OUT_TIME));
                        now.NotOrdCoz = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.NO_ORDER_REASON));
                        now.RefNo = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.REF_NO));
                        now.PaymentMode = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrder.PAYMENT_MODE));
                        now.OutletLongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LONGITUDE));
                        now.OutletLatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LATITUDE));
                        returnOutletList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }

    /**
     * @param context
     * @param outletId
     * @param orderDate
     * @return
     */
    public static ArrayList<OrderItem> getVisitedOutletDetailsOnDate(Context context, String outletId, String orderDate) {
        ArrayList<OrderItem> returnOrderList = new ArrayList<OrderItem>();
        orderDate = changeFormat(orderDate);
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            String query = "SELECT "
                    + DatabaseConstants.TABLE_ORDER_ITEM + "." + DatabaseConstants.tblOrderItem.ORDER_NO + ", "
                    + DatabaseConstants.TABLE_ORDER_ITEM + "." + DatabaseConstants.tblOrderItem.OUTLET_ID + ", "
                    + DatabaseConstants.TABLE_ORDER_ITEM + "." + DatabaseConstants.tblOrderItem.SALE_CARTON + ", "
                    + DatabaseConstants.TABLE_ORDER_ITEM + "." + DatabaseConstants.tblOrderItem.SALE_PIECE + ", "
                    + DatabaseConstants.TABLE_ORDER_ITEM + "." + DatabaseConstants.tblOrderItem.REPLACE_PIECE + ", "
                    + DatabaseConstants.tblOrderItem.SKUID + ", SUM("
                    + DatabaseConstants.tblOrderItem.CARTON + "), SUM("
                    + DatabaseConstants.tblOrderItem.PIECE + "), SUM("
                    + DatabaseConstants.tblOrderItem.TOTAL_SALE + "), SUM("
                    + DatabaseConstants.tblOrderItem.TOTAL + "),"
                    + DatabaseConstants.tblOrder.ORDER_DATE
                    + " FROM tblOrderItem JOIN tblOrder ON tblOrderItem.OrderNo = tblOrder.OrderNo WHERE tblOrder."
                    + DatabaseConstants.tblOrder.OUTLET_ID + " =? AND tblOrder."
                    + DatabaseConstants.tblOrder.ORDER_DATE + " =? GROUP BY "
                    + DatabaseConstants.tblOrderItem.SKUID;

            Log.d(TAG, "getVisitedOutletDetailsOnDate: " + query + ", " + orderDate);

            Cursor cursor = ctx.Select(query, new String[]{(outletId), orderDate});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();

                        now.OrderNo = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.ORDER_NO));
                        now.OutletID = cursor.getString(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.OUTLET_ID));
                        now.soldCartons = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.SALE_CARTON));
                        now.soldPieces = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.SALE_PIECE));
                        now.replacePieces = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.REPLACE_PIECE));
                        now.SKUID = cursor.getString(cursor.getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);
                        now.Carton = cursor.getInt(cursor.getColumnIndex("SUM(" + DatabaseConstants.tblOrderItem.CARTON + ")"));
                        now.Piece = cursor.getInt(cursor.getColumnIndex("SUM(" + DatabaseConstants.tblOrderItem.PIECE + ")"));
                        now.Carton += now.Piece / now.sku.pcsPerCtn;
                        now.Piece = now.Piece % now.sku.pcsPerCtn;
                        now.Total = cursor.getDouble(cursor.getColumnIndex("SUM(" + DatabaseConstants.tblOrderItem.TOTAL + ")"));
                        now.soldTotal = cursor.getDouble(cursor.getColumnIndex("SUM(" + DatabaseConstants.tblOrderItem.TOTAL_SALE + ")"));

                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                Log.d(TAG, "getVisitedOutletDetailsOnDate Cursor:" + DatabaseUtils.dumpCursorToString(cursor));
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    /**
     * Written by Md Shahriar Kabir
     *
     * @param context
     * @param visitDate
     * @return
     */
    public static ArrayList<OutletItem> getVisitedOutletListOnDate(Context context, String visitDate) {
        //edited by abrar
        visitDate = changeFormat(visitDate);
        ArrayList<OutletItem> returnOutletList = new ArrayList<OutletItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            String query = "SELECT " +
                    DatabaseConstants.TABLE_ORDER + "." + DatabaseConstants.tblOutlet.OUTLET_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.ADDRESS + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.CHANNEL_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.CONTACT_NO + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.DESCRIPTION + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OWNER + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.ROUTE_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.VISITED
                    + ", SUM(" + DatabaseConstants.tblOrderItem.TOTAL + ")"
                    + ", SUM(" + DatabaseConstants.tblOrderItem.TOTAL_SALE + "), "
                    + DatabaseConstants.tblOrder.ORDER_DATE
                    + " FROM tblOrderItem LEFT JOIN tblOrder ON tblOrder.OrderNo = tblOrderItem.OrderNo "
                    + " LEFT JOIN " + DatabaseConstants.TABLE_OUTLET + " ON " +
                    DatabaseConstants.TABLE_ORDER + "." + DatabaseConstants.tblOrder.OUTLET_ID + "=" +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OUTLET_ID
                    + " WHERE " + DatabaseConstants.TABLE_ORDER + "." + DatabaseConstants.tblOrder.ORDER_DATE + " =? AND "
                    + DatabaseConstants.TABLE_ORDER + "." + DatabaseConstants.tblOrder.SENT_STATUS + " =? "
                    + "GROUP BY "
                    + DatabaseConstants.TABLE_ORDER + "." + DatabaseConstants.tblOutlet.OUTLET_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.ADDRESS + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.CHANNEL_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.CONTACT_NO + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.DESCRIPTION + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OUTLET_LATITUDE + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OUTLET_LONGITUDE + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.OWNER + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.ROUTE_ID + ", " +
                    DatabaseConstants.TABLE_OUTLET + "." + DatabaseConstants.tblOutlet.VISITED;

//
            Log.d(TAG, "SQL COMMAND: " + query + ", " + visitDate);
            Cursor cursor = ctx.Select(query, new String[]{visitDate, Integer.toString(0)});

            Log.d(TAG, "GOT RESULT: " + DatabaseUtils.dumpCursorToString(cursor));
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast() && !cursor.isNull(0)) {
                    OutletItem now = new OutletItem();
                    now.outletId = cursor.getString(0);
                    now.address = cursor.getString(1);
                    now.channelId = cursor.getString(2);
                    now.contactNo = cursor.getString(3);
                    now.description = cursor.getString(4);
                    now.outletlatitude = cursor.getString(5);
                    now.outletlongitude = cursor.getString(6);
                    now.owner = cursor.getString(7);
                    now.routeID = cursor.getString(8);
                    now.visited = cursor.getInt(9);
                    now.orderValue = cursor.getDouble(10);
                    now.saleValue = cursor.getDouble(11);

                    returnOutletList.add(now);

                    cursor.moveToNext();
                }

                cursor.close();
            }

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutletList;
    }

    /**
     * Written by Md Shahriar Kabir
     *
     * @param context
     * @param orderItem
     * @return
     */
    public static long setVisitedOutletConfirmation(Context context, OrderItem orderItem) {
        long result = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            String query = DatabaseConstants.tblOrderItem.ORDER_NO + "=? AND " +
                    DatabaseConstants.tblOrderItem.OUTLET_ID + "=? AND " + DatabaseConstants.tblOrderItem.SKUID +"=? ";

            String fields = DatabaseConstants.tblOrderItem.SALE_CARTON + ", " +
                    DatabaseConstants.tblOrderItem.SALE_PIECE + ", " +
                    DatabaseConstants.tblOrderItem.TOTAL_SALE + ", " +
                    DatabaseConstants.tblOrderItem.REPLACE_PIECE;

            Log.d(TAG, "setVisitedOutletConfirmation::SQL COMMAND: " + query
                    + ", " + orderItem.OrderNo + ", " + orderItem.OutletID
                    + ", " + orderItem.soldCartons + ", " + orderItem.soldPieces + ", " + orderItem.replacePieces );

            result = ctx.Update(DatabaseConstants.TABLE_ORDER_ITEM, fields,
                    new String[]{Integer.toString(orderItem.soldCartons),
                            Integer.toString( orderItem.soldPieces ),
                            Double.toString( orderItem.soldTotal ),
                            Integer.toString(orderItem.replacePieces)},
                    query,
                    new String[]{Integer.toString(orderItem.OrderNo),(orderItem.OutletID),(orderItem.SKUID)});

            Log.d(TAG, "GOT RESULT: " + result);

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<OrderItem> getAllOrderByOutlet(Context context, String outletId, int orderNo) {
        ArrayList<OrderItem> returnAllOrder = new ArrayList<OrderItem>();

        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOrderItem WHERE OutletID='"
                            + outletId +"'");
            Cursor cursor = ctx.Select("SELECT * FROM tblOrderItem WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID + " =? AND "
                            + DatabaseConstants.tblOrder.ORDER_NO + " =? ",
                    new String[]{(outletId), Integer.toString(orderNo)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.OrderNo = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.ORDER_NO));
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.OUTLET_ID));
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.Carton = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON));
                        now.Piece = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE));
                        now.Carton_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON_ORG));
                        now.Piece_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE_ORG));
                        now.BrandID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.BRAND_ID));
                        now.Total = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TOTAL));
                        now.TkOff = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TK_OFF));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);
                        OrderItem outletSkuOrder = DatabaseQueryUtil
                                .getOrderSkuOrderItem(context, outletId,
                                        now.sku.skuId);
                        now.KPIType = outletSkuOrder != null ? outletSkuOrder.KPIType
                                : "";
                        now.Status = outletSkuOrder != null ? outletSkuOrder.Status
                                : "";
                        now.Suggested = outletSkuOrder != null ? outletSkuOrder.Suggested
                                : 0;
//						now.ColorID=outletSkuOrder != null ? outletSkuOrder.ColorID:0;
                        returnAllOrder.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnAllOrder;
    }

    public static ArrayList<Reason> getReasonListForMarketReturn(Context context) {
        ArrayList<Reason> returnReasonList = new ArrayList<Reason>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblMarketReason");
            Cursor cursor = ctx.Select("SELECT * FROM tblMarketReason");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Reason now = new Reason(
                                cursor.getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReason.MARKET_REASON_ID)),
                                cursor.getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReason.TITLE)));
                        returnReasonList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnReasonList;
    }

    // altered table MRSKU to SKU for test
    public static ArrayList<Sku> getSkuListForMarketReturnByBrand(Context context, String brandId) {
        ArrayList<Sku> returnSkuList = new ArrayList<Sku>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT SKUID, Title FROM tblSKU WHERE BrandID = "
                            + brandId);

            Cursor cursor = ctx.Select("SELECT "
                            + DatabaseConstants.tblSKU.SKUID + ", "
                            + DatabaseConstants.tblSKU.TITLE + ", "
                            + DatabaseConstants.tblSKU.PCS_RATE + " FROM tblSKU WHERE "
                            + DatabaseConstants.tblSKU.BRAND_ID + " =? ",
                    new String[]{(brandId)});

            //Cursor cursor=ctx.Select(sql);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Sku now = new Sku();
                        now.skuId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.SKUID));
                        now.title = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.TITLE)) + "(" + cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.PCS_RATE)) + ")";
                        now.pcsRate = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.PCS_RATE));

//						now.IsNSD=cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblSKU.IS_NSD));
                        returnSkuList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSkuList;
    }

    public static ArrayList<Sku> getSkuListFromTblSkuByBrand(Context context, int brandId) {
        ArrayList<Sku> returnSkuList = new ArrayList<Sku>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT SKUID, Title FROM tblSku");
            Cursor cursor = ctx.Select("SELECT "
                    + DatabaseConstants.tblSKU.SKUID + ", "
                    + DatabaseConstants.tblSKU.TITLE + ", "
                    + DatabaseConstants.tblSKU.PCS_RATE + " FROM tblSku", null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Sku now = new Sku();
                        now.skuId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.SKUID));
                        now.title = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.TITLE));
                        now.pcsRate = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblSKU.PCS_RATE));

                        returnSkuList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSkuList;
    }

    public static ArrayList<MarketReturnItem> getMarketReturnItemListFromTblMarketReturn(Context context, String outletId) {
        ArrayList<MarketReturnItem> returnMarketReturnList = new ArrayList<MarketReturnItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            String commandText = "SELECT A.OrderNo, A.MarketReturnId, A.SKUID,A.BrandID, A.Qty, A.MktReasonID, A.Batch, B.PcsRate Price, " +
                    "A.ExpDate FROM tblMarketReturn A, tblSKU B WHERE A.SkuID=B.SkuID AND A.outletId = '"
                    + outletId + "'  ORDER BY A.MarketReturnId";
            System.out.println(commandText);

            Cursor cursor = ctx.Select(commandText);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        MarketReturnItem now = new MarketReturnItem();
                        now.orderNo = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.ORDER_NO));
                        now.marketReturnId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.MARKET_RETURN_ID));
                        now.outletId = outletId;// cursor
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.SKUID));
                        now.skuName = DatabaseQueryUtil.getMRSku(context,
                                now.SKUID).title;
                        now.brandId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.BRAND_ID));
                        now.qty = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.QUANTITY));
                        now.marketReasonId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.MARKET_REASON_ID));
                        now.reasonDescription = DatabaseQueryUtil.getReason(
                                context, now.marketReasonId,
                                DatabaseConstants.MARKET_REASON).description;
                        now.batch = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.BATCH));
                        now.expDate = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.EXPIRY_DATE));
                        now.price = cursor.getDouble(cursor
                                .getColumnIndex("Price"));

                        returnMarketReturnList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMarketReturnList;
    }

    public static ArrayList<MarketReturnItem> getMarketReturnItemByOutlet(Context context, String outletId) {
        ArrayList<MarketReturnItem> returnMarketReturnList = new ArrayList<MarketReturnItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            Cursor cursor = ctx.Select("SELECT * FROM tblMarketReturn WHERE "
                            + DatabaseConstants.tblMarketReturn.OUTLET_ID + " =? ",
                    new String[]{(outletId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        MarketReturnItem now = new MarketReturnItem();
                        now.orderNo = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.ORDER_NO));
                        now.marketReturnId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.MARKET_RETURN_ID));
                        now.outletId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.OUTLET_ID));
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.SKUID));
                        now.skuName = DatabaseQueryUtil.getSku(context,
                                now.SKUID).title;
                        now.brandId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.BRAND_ID));
                        now.qty = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.QUANTITY));
                        now.marketReasonId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.MARKET_REASON_ID));
                        now.batch = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.BATCH));
                        now.expDate = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReturn.EXPIRY_DATE));

                        returnMarketReturnList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMarketReturnList;
    }

    public static ArrayList<Brand> getBrandList(Context context) {
        ArrayList<Brand> returnBrandList = new ArrayList<Brand>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblBrand");
            Cursor cursor = ctx.Select("SELECT * FROM tblBrand");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        returnBrandList
                                .add(new Brand(
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblBrand.BRAND_ID)),
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblBrand.NAME))));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBrandList;
    }

    public static ArrayList<Channel> getChannelList(Context context) {
        ArrayList<Channel> returnChannelList = new ArrayList<Channel>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblChannel");
            Cursor cursor = ctx.Select("SELECT * FROM tblChannel");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Channel now = new Channel();
                        now.channelID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblChannel.CHANNEL_ID));
                        now.name = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblChannel.NAME));
                        returnChannelList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnChannelList;
    }

    public static ArrayList<Tpr> getTprFromTblTpr(Context context, String channelID) {
        ArrayList<Tpr> returnTprlist = new ArrayList<Tpr>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {

            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT tblTPR.Description from tblTPR WHERE tblTPR.TPRID IN(SELECT tblTPRSKUChnl.TPRID FROM tblTPRSKUChnl WHERE tblTPRSKUChnl.ChannelID = "
                            + channelID + ")");
            Cursor cursor = ctx.Select("SELECT tblTPR."
                            + DatabaseConstants.tblTPR.DESCRIPTION
                            + " FROM tblTPR WHERE tblTPR."
                            + DatabaseConstants.tblTPR.TPR_ID
                            + " IN (SELECT tblTPRSKUChnl."
                            + DatabaseConstants.tblTPRSKUChnl.TPR_ID
                            + " FROM tblTPRSKUChnl WHERE tblTPRSKUChnl."
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + " =? )",
                    new String[]{(channelID)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Tpr now = new Tpr();
                        now.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblTPR.DESCRIPTION));
                        returnTprlist.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnTprlist;
    }

    public static ArrayList<PopItem> getPopItemList(Context context, String outletId) {
        ArrayList<PopItem> returnPopItemlist = new ArrayList<PopItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {

            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * from tblPOPItem");
            Cursor cursor = ctx.Select("SELECT * FROM tblPOPItem");

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        PopItem now = new PopItem();
                        now.popItemId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblPOPItem.POP_ITEM_ID));
                        now.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblPOPItem.DESCRIPTION));
                        now.outletId = outletId;
                        PopItem popOutletItem = DatabaseQueryUtil.getPopItem(
                                context, outletId, now.popItemId);
                        if (popOutletItem != null) {
                            now.lastQty = popOutletItem.lastQty;
                            now.currentQty = popOutletItem.currentQty;
                        }
                        returnPopItemlist.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnPopItemlist;
    }

    public static ArrayList<OrderItem> getOrderSummaryBySku(Context context, int outletId) {
        ArrayList<OrderItem> returnOrderList = new ArrayList<OrderItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT "
                    + DatabaseConstants.tblOrderItem.SKUID + ", SUM("
                    + DatabaseConstants.tblOrderItem.CARTON + "), SUM("
                    + DatabaseConstants.tblOrderItem.PIECE + "), SUM("
                    + DatabaseConstants.tblOrderItem.TOTAL
                    + ") FROM tblOrder Where OutletId = '" + outletId
                    + "' GROUP BY " + DatabaseConstants.tblOrderItem.SKUID);

            Cursor cursor = ctx.Select("SELECT "
                            + DatabaseConstants.tblOrderItem.SKUID + ", SUM("
                            + DatabaseConstants.tblOrderItem.CARTON + "), SUM("
                            + DatabaseConstants.tblOrderItem.PIECE + "), SUM("
                            + DatabaseConstants.tblOrderItem.TOTAL
                            + ") FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrderItem.OUTLET_ID + " =? GROUP BY "
                            + DatabaseConstants.tblOrderItem.SKUID,
                    new String[]{Integer.toString(outletId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);
                        now.Carton = cursor.getInt(cursor.getColumnIndex("SUM("
                                + DatabaseConstants.tblOrderItem.CARTON + ")"));
                        now.Piece = cursor.getInt(cursor.getColumnIndex("SUM("
                                + DatabaseConstants.tblOrderItem.PIECE + ")"));
                        now.Carton += now.Piece / now.sku.pcsPerCtn;
                        now.Piece = now.Piece % now.sku.pcsPerCtn;
                        now.Total = cursor.getDouble(cursor
                                .getColumnIndex("SUM("
                                        + DatabaseConstants.tblOrderItem.TOTAL
                                        + ")"));
                        returnOrderList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderList;
    }

    private static ArrayList<Slab> getSlabList(Context context, int tprId, double quantity) {
        ArrayList<Slab> returnSlabList = new ArrayList<Slab>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: Select SlabID from tblTprslab where tprid = "
                            + tprId + "  and minqty<=" + quantity);
            Cursor cursor = ctx.Select(
                    "Select " + DatabaseConstants.tblTPRSlab.SLAB_ID
                            + " FROM tblTPRSlab where "
                            + DatabaseConstants.tblTPRSlab.TPR_ID + " =? AND "
                            + DatabaseConstants.tblTPRSlab.MIN_QTY
                            + " <=? ORDER BY minqty DESC",
                    new String[]{Integer.toString(tprId),
                            Double.toString(quantity)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Slab now = new Slab();
                        now.slabId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblTPRSlab.SLAB_ID));
                        now.tprId = tprId;
                        now = DatabaseQueryUtil.getSlab(context, now.tprId,
                                now.slabId);
                        returnSlabList.add(now);
                        break;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSlabList;
    }

    private static ArrayList<Sku> getSkuListForTprCalculation(Context context, int tprId, String channelId) {
        ArrayList<Sku> skuList = new ArrayList<Sku>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT SKUID FROM tblTPRSKUChnl WHERE TPRID = "
                            + tprId + " and ChannelID = " + channelId);
            Cursor cursor = ctx.Select(
                    "SELECT SKUID FROM tblTPRSKUChnl WHERE "
                            + DatabaseConstants.tblTPRSKUChnl.TPR_ID
                            + " =? AND "
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID
                            + " =? ",
                    new String[]{Integer.toString(tprId),
                            (channelId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Sku now = new Sku();
                        now.skuId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblTPRSKUChnl.SKUID));
                        now = DatabaseQueryUtil.getSku(context, now.skuId);
                        skuList.add(now);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skuList;
    }

    public static ArrayList<Tpr> getTprList(Context context, String channelId) {
        ArrayList<Tpr> tprList = new ArrayList<Tpr>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: Select * from tblTPR WHERE TPRID IN (SELECT TPRID FROM tblTPRSKUChnl WHERE ChannelID = "
                            + channelId + ")");
            Cursor cursor = ctx.Select("SELECT * from tblTPR WHERE "
                            + DatabaseConstants.tblTPR.TPR_ID + " IN (SELECT "
                            + DatabaseConstants.tblTPR.TPR_ID
                            + " FROM tblTPRSKUChnl WHERE "
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + " =? )",
                    new String[]{(channelId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Tpr now = new Tpr();
                        now.tprId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblTPR.TPR_ID));
                        now.programmType = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblTPR.PROGRAM_TYPE));
                        tprList.add(now);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tprList;
    }

    public static double getOrderTotalSumFromTblOrder(Context context, String sectionId, String date) {
        date = changeFormat(date);
        double ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT SUM("
                    + DatabaseConstants.tblOrder.ORDER_TOTAL
                    + ") FROM tblOrder WHERE OrderDate =" + date);
//			Cursor cursor = ctx.Select(
//					"SELECT SUM(" + DatabaseConstants.tblOrder.ORDER_TOTAL
//							+ ") FROM tblOrder WHERE "
//							+ DatabaseConstants.tblOrder.SECTION_ID
//							+ "=? AND " + DatabaseConstants.tblOrder.VISITED
//							+ " =? ",

            Cursor cursor = ctx.Select(
                    "SELECT SUM(" + DatabaseConstants.tblOrder.ORDER_TOTAL
                            + ") FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + "=?",
                    new String[]{date});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    ret = cursor.getDouble(cursor.getColumnIndex("SUM("
                            + DatabaseConstants.tblOrder.ORDER_TOTAL + ")"));
                }
            }
            cursor.close();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getAllocationQty(Context context, int SKUID, int outletID) {
        int allocationQty = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT AllocationQty FROM tblOutletAllocation WHERE SKUID="
                            + SKUID + " AND OutletID='" + outletID+"'");
            Cursor cursor = ctx.Select(
                    "SELECT AllocationQty FROM tblOutletAllocation WHERE "
                            + DatabaseConstants.tblOutletAllocation.SKUID
                            + "=? AND "
                            + DatabaseConstants.tblOutletAllocation.Outlet_ID
                            + "=?", new String[]{Integer.toString(SKUID),
                            Integer.toString(outletID)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    allocationQty = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletAllocation.Allocation_Qty));

                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allocationQty;
    }

    public static double getOrderTotalFromTblOrder(Context context, String outletId, String visisdate) {
        visisdate = changeFormat(visisdate);
        double ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
//			System.out.println("SQL COMMAND: SELECT SUM("
//					+ DatabaseConstants.tblOrder.ORDER_TOTAL
//					+ ") FROM tblOrder WHERE SectionID=" + sectionId
//					+ " AND VISITED = 1");
            System.out.println("SQL COMMAND: SELECT ("
                    + DatabaseConstants.tblOrder.ORDER_TOTAL
                    + ") FROM tblOrder WHERE OutletID='" + outletId
                    + "' AND OrderDate=" + visisdate);
//			Cursor cursor = ctx.Select(
//					"SELECT SUM(" + DatabaseConstants.tblOrder.ORDER_TOTAL
//							+ ") FROM tblOrder WHERE "
//							+ DatabaseConstants.tblOrder.SECTION_ID
//							+ "=? AND " + DatabaseConstants.tblOrder.VISITED
//							+ " =? ",

            Cursor cursor = ctx.Select(
                    "SELECT (" + DatabaseConstants.tblOrder.ORDER_TOTAL
                            + ") FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + "=? AND "
                            + DatabaseConstants.tblOrder.SENT_STATUS
                            + "=? AND "
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + "=? ",
                    new String[]{(outletId), Integer.toString(0), visisdate});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    ret = cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.tblOrder.ORDER_TOTAL));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static double getOrderTotalByOrderNo(Context context, int orderNo) {

        double ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();

            System.out.println("SQL COMMAND: SELECT ("
                    + DatabaseConstants.tblOrder.ORDER_TOTAL
                    + ") FROM tblOrder WHERE OrderNo=" + orderNo);

            Cursor cursor = ctx.Select(
                    "SELECT (" + DatabaseConstants.tblOrder.ORDER_TOTAL
                            + ") FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.ORDER_NO
                            + "=? ",
                    new String[]{Integer.toString(orderNo)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    ret = cursor.getDouble(cursor.getColumnIndex(DatabaseConstants.tblOrder.ORDER_TOTAL));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getCountOfOrdersFromTblOrderItem(Context context, String date) {
        int ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {

            String[] splittedDate = date.split("-");
            String newVisitedDateString = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];

            ctx.Begin();
            System.out.println("SQL COMMAND: select * from tblOrder tbo join tblORderItem tboi on\n" +
                    "\n" + "tbo.OutletID = tboi.OutletID where tbo.orderdate = '\"+ newVisitedDateString +\"'");
            // Old select method, -------ZX
            //Cursor cursor = ctx.Select("SELECT COUNT(*) FROM tblOrderItem");
            Cursor cursor = ctx.Select("select * from tblOrder tbo join tblORderItem tboi on\n" +
                    " tbo.OutletID = tboi.OutletID where tbo.orderdate = '"+ newVisitedDateString +"'");



            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    //ret = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
                    ret = cursor.getCount();
                }
            }
            cursor.close();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getCountOfOutletFromTblOutlet(Context context, String routeId, int visitStatus) {
        int ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor;
        try {
            ctx.Begin();
            if (visitStatus == TodaysStatusPageActivity.TOTAL) {
                System.out
                        .println("SQL COMMAND: SELECT COUNT(*) FROM tblOutlet");
                cursor = ctx.Select("SELECT COUNT(*) FROM tblOutlet WHERE "
                                + DatabaseConstants.tblOutlet.ROUTE_ID + "=?",
                        new String[]{(routeId)});

            } else if (visitStatus == HomePageActivity.VISITED) {
                System.out
                        .println("SQL COMMAND: SELECT COUNT(*) FROM tblOutlet"
                                + " WHERE Visited = 1 and orderTotal > 0");
                cursor = ctx.Select(
                        "SELECT COUNT(*) FROM tblOutlet WHERE "
                                + DatabaseConstants.tblOutlet.VISITED
                                + "=?",
                        new String[]{Integer.toString(visitStatus)});
            } else {
                System.out
                        .println("SQL COMMAND: SELECT COUNT(*) FROM tblOutlet WHERE Visited = 1");
                cursor = ctx.Select(
                        "SELECT COUNT(*) FROM tblOutlet WHERE "
                                + DatabaseConstants.tblOutlet.VISITED + "=? AND "
                                + DatabaseConstants.tblOutlet.ROUTE_ID
                                + " =?",
                        new String[]{Integer.toString(visitStatus), (routeId)});
            }
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    ret = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
                }
                cursor.close();
            }

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getCountOfOutletFromTblOutlet(Context context, int visitStatus, String date) {
        int ret = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor;
        try {
            ctx.Begin();

            String[] splittedDate = date.split("-");
            String visitedDate = splittedDate[2]+"-"+splittedDate[1]+"-"+splittedDate[0];

            if (visitStatus == TodaysStatusPageActivity.TOTAL) {
                System.out.println("SQL COMMAND: select count(OrderNo) from tblOrder where orderdate = '"+visitedDate+"'");

                cursor = ctx.Select("select count(OrderNo) from tblOrder where orderdate = '"+visitedDate+"'");

            } else if (visitStatus == HomePageActivity.VISITED) {
                System.out.println("SQL COMMAND: select * from tblOrder where orderdate = '"+visitedDate+"'");
                cursor = ctx.Select("select * from tblOrder where orderdate = '"+visitedDate+"'");
            } else {
                System.out
                        .println("select * from tblOrder where orderdate = '"+visitedDate+"'");
                cursor = ctx.Select("select * from tblOrder where orderdate = '"+visitedDate+"'");
            }

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    //ret = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
                    ret = cursor.getCount();
                }
                cursor.close();
            }

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getOrderHistoryOrderDate(Context context, int outletId, String orderDate) {
        String returnDate = "-";
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT OrderDate FROM tblOrder Where OutletID = '"+outletId+"'");

            Cursor cursor = ctx.Select(
                    "SELECT " + DatabaseConstants.tblOrder.ORDER_DATE
                            + " FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + " =? ",
                    new String[]{Integer.toString(outletId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnDate = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOrder.ORDER_DATE));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDate;
    }

    public static ArrayList<String> getOrderHistoryOrderDate(Context context, int outletId) {
        ArrayList<String> returnOrderDateList = new ArrayList<String>();
        String returnDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime;
        currentDateTime = sdf.format(new Date());
        String currentDate = currentDateTime;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOrder Where OutletID = '" + outletId + "' ORDER BY OrderDate DESC LIMIT 3  ");


            Cursor cursor = ctx.Select(
                    "SELECT * FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + " =? AND"
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + "!=? 	ORDER BY OrderDate DESC LIMIT 3 ",
                    new String[]{Integer.toString(outletId), currentDate});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnDate = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOrder.ORDER_DATE));
                    returnOrderDateList.add(returnDate);
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrderDateList;
    }

    public static String PromotionalDescription(Context context, String skuId, String outletId) {
        ConnectionContext ctx = new ConnectionContext(context);
        String PromoDescription = "";
        try {
            ctx.Begin();
            Log.e("query",
                    ("Select description " + "from tblTPR where TPRID in" + "("
                            + "select TPRID " + " from tblTPRSKUChnl "
                            + " where " + DatabaseConstants.tblTPRSKUChnl.SKUID
                            + "= 105 AND "
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + "="
                            + "(Select ChannelID from tblOutlet Where "
                            + DatabaseConstants.tblOutlet.OUTLET_ID
                            + "= 761  )" + ") "));
            Log.e("skuidoutlet",
                    (skuId) + "--" + (outletId));
            Cursor cursor = ctx.Select(
                    "Select description " + "from tblTPR where TPRID in" + "("
                            + "select TPRID " + " from tblTPRSKUChnl "
                            + " where " + DatabaseConstants.tblTPRSKUChnl.SKUID
                            + "=? AND "
                            + DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID + "="
                            + "(Select ChannelID from tblOutlet Where "
                            + DatabaseConstants.tblOutlet.OUTLET_ID + "=? )"
                            + ") ",
                    new String[]{(skuId),
                            (outletId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    Log.e("check",
                            (skuId) + "--"
                                    + (outletId));
                    // cursor.getString(0)
                    Log.e("Index", cursor.getColumnIndex("Description") + "");
                    PromoDescription = cursor.getString(cursor
                            .getColumnIndex("Description"));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PromoDescription;
    }

    public String GetLastUpdateTime(Context context) {
        String lastUpdateTime = "";
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT LastUpdateTime FROM tblDSRBasic");

            Cursor cursor = ctx.Select("SELECT LastUpdateTime FROM tblDSRBasic");

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        lastUpdateTime = cursor.getString(cursor.getColumnIndex(DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME));

                    } while (cursor.moveToNext());
                }
            }

            cursor.close();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastUpdateTime;
    }

    public static int getOrderNo(Context context, String outletId, String currentOrderDate) {
        int orderNo = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOrder WHERE OutletID = '" + outletId + "' AND OrderDate = "
                            + currentOrderDate);
            Cursor cursor = ctx.Select("SELECT * FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + " =? AND "
                            + DatabaseConstants.tblOrder.ORDER_DATE
                            + " =?",

                    new String[]{(outletId), currentOrderDate});

//            Cursor cursor = ctx.Select("SELECT OrderNo FROM tblOrder WHERE OutletID="+outletId+" AND OrderDate=" +currentOrderDate);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        orderNo = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblOrder.ORDER_NO));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderNo;
    }

    public static String GetMobilefromTblDSRBasic(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        String Mobile = "";
        try {
            ctx.Begin();
            String sql;
            sql = "SELECT MobileNo FROM tblDSRBasic";
            //Cursor cursor = ctx.Select(sql, new String[]{Integer.toString(0)});
            SQLiteDatabase db = ctx.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                Mobile = cursor.getString(cursor.getColumnIndex("MobileNo"));
            }
            cursor.close();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mobile;
    }

    public static User getUser(Context context) {
        User returnUser = new User();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblDSRBasic");
            Cursor cursor = ctx.Select("SELECT * FROM tblDSRBasic");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnUser.sectionId = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.SECTION_ID));
                    returnUser.name = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.NAME));
                    returnUser.orderAchieved = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.ORDER_ACHIEVED));
                    returnUser.dayRemain = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.DAY_REMAIN));
                    returnUser.targetRemain = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.TARGET_REM));
                    returnUser.dailyTarget = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.DAILY_TAREGET));
                    returnUser.target = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.TARGET));
                    returnUser.visitDate = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.VISIT_DATE));
                    returnUser.pdaUser = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.PDA_USER));

                    returnUser.dsrId = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.DSR_ID));
                    returnUser.company_ID = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.COMPANY_ID));
                    returnUser.distributorName = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.DISTRIBUTOR_NAME));
                    returnUser.distributorAddress = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.DISTRIBUTOR_ADDRESS));
                    returnUser.roamerLogId = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.ROAMER_LOG_ID));
                    returnUser.mobile_No = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.MOBILE_NO));
                    returnUser.password = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.PASSWORD));
                    returnUser.lastUpdateTime = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblDSRBasic.LAST_UPDATE_TIME));

                }
                cursor.close();
            }

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(returnUser.WillTrackGPS);
        return returnUser;
    }

    public static Section getSection(Context context) {
        Section returnSection = new Section();
        returnSection.list = new ArrayList<Market>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblSection");
            Cursor cursor = ctx.Select("SELECT * FROM tblSection");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        returnSection.list
                                .add(new Market(
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblSection.SECTION_ID)),
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblSection.ROUTE_ID)),
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblSection.ORDER_COL_DAY)),
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblSection.ORDER_DLV_DAY)),
                                        cursor.getString(cursor
                                                .getColumnIndex(DatabaseConstants.tblSection.TITLE))));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSection;
    }

    public static AllOrderList getAllOrder(Context context, String outletId, String channelId, String visitDate) {
        visitDate = changeFormat(visitDate);
        AllOrderList returnAllOrder = new AllOrderList();
        returnAllOrder.orderedList = new ArrayList<OrderItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOrderItem WHERE OutletID='"
                            + outletId +"'");
			/*
			 * select tblOrder.*, ifnull(tbloutletsku.KPIType, "") KPIType,
			 * ifnull(tbloutletsku.Status, "")Status from tblOrder left outer
			 * join tbloutletsku on tblOrder.skuid = tbloutletsku.skuid and
			 * tbloutletsku.outletid = 210 where tblOrder.outletid = 210
			 */

            //region new Cursor

            Cursor cursor = ctx
                    .Select("Select A.*, ifnull(tblOutletSKU.KPIType, '') KPIType,ifnull(tblOutletSKU.Status, '')Status, ifnull(tblOutletSKU.SuggestedQty, 0)SuggestedQty,MaxOrderQty, ifnull(tblOutletSKU.ColorID, 0)ColorID"
                                    + " From tblOrderItem A Left Outer Join(Select OutletID, SKUID, max(KPIType) KPIType, max(Status) Status,SuggestedQty,ColorID,MaxOrderQty from tblOutletSKU where OutletID =? group by OutletID, SKUID"
                                    + " ) tblOutletSKU on A.SKUID = tblOutletSKU.SKUID and tblOutletSKU.OutletID"
                                    + " =?"
                                    + " INNER JOIN tblOrder B on A.OutletID= B.OutletID AND A.OrderNo = B.OrderNo WHERE B.OrderDate =? AND A.OutletID = ? AND B.SentStatus=0",
                            new String[]{(outletId), (outletId), visitDate,
                                    (outletId)});


            //endregion

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.OUTLET_ID));
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.Carton = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON));
                        now.Piece = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE));
                        now.Carton_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON_ORG));
                        now.Piece_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE_ORG));
                        now.BrandID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.BRAND_ID));
                        now.Total = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TOTAL));
                        now.TkOff = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TK_OFF));
                        now.sku = DatabaseQueryUtil.getSku(context, now.SKUID);

                        now.KPIType = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.KPI_TYPE));
                        now.Status = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.STATUS));

                        now.Suggested = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY));
//						now.ColorID=cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblOutletSKU.COLORID));
//						now.MaxOrderQty=cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblOutletSKU.MAXORDERQTY));
                        if (DatabaseQueryUtil.hasPromotion(context,
                                now.sku.skuId, channelId))
                            now.promoStatus = "*";
                        returnAllOrder.orderedList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        returnAllOrder.notOrderedList = new ArrayList<OrderItem>();
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT SKUID FROM tblSKU WHERE SKUID NOT IN(SELECT SKUID FROM tblOrderItem WHERE outletId='"
                            + outletId + "') ORDER BY PositionValue");
            Cursor _cursor = ctx
                    .Select("SELECT tblSKU.SKUID, ifnull(tblOutletSKU.KPIType, '') KPIType, ifnull(tblOutletSKU.Status, '') Status,ifnull(tblOutletSKU.SuggestedQty, 0) SuggestedQty,MaxOrderQty, ifnull(tbloutletsku.ColorID, 0) ColorID"
                                    + " FROM tblSKU left outer join "
                                    + " ( "
                                    + "    select OutletID, SKUID, max(KPIType) KPIType, max(Status) Status,SuggestedQty,max(ColorID) ColorID,MaxOrderQty from tblOutletSKU where OutletID =? group by OutletID, SKUID "
                                    + " ) tblOutletSKU "
                                    + " on tblSKU.SKUID = tblOutletSKU.SKUID "
                                    + " and tblOutletSKU.OutletID "
                                    + " =? "
                                    + " WHERE tblSKU.SKUID "
                                    + " NOT IN (SELECT tblOrderItem.SKUID "
                                    + " FROM tblOrderItem INNER JOIN tblOrder B on tblOrderItem.OutletID= B.OutletID AND tblOrderItem.OrderNo = B.OrderNo WHERE B.OrderDate =? AND B.SentStatus =? AND "
                                    + " tblOrderItem.OutletID"
                                    + " =? " + ") " + " ORDER BY tblSKU.PositionValue",
                            new String[]{(outletId), (outletId), visitDate, Integer.toString(0),
                                    (outletId)});

            if (_cursor != null && _cursor.getCount() > 0) {
                if (_cursor.moveToFirst()) {
                    do {
                        Sku nowSku = DatabaseQueryUtil
                                .getSku(context,
                                        _cursor.getString(_cursor
                                                .getColumnIndex(DatabaseConstants.tblSKU.SKUID)));
                        OrderItem nowOrder = new OrderItem(nowSku);
                        nowOrder.OutletID = outletId;
                        OrderItem outletSkuOrder = DatabaseQueryUtil
                                .getOrderSkuOrderItem(context, outletId,
                                        nowOrder.sku.skuId);
                        nowOrder.KPIType = outletSkuOrder != null ?
                                outletSkuOrder.KPIType
                                : "";
                        nowOrder.KPIStatus = outletSkuOrder != null ?
                                outletSkuOrder.KPIStatus
                                : "";
                        if (DatabaseQueryUtil.hasPromotion(context,
                                nowOrder.sku.skuId, channelId))
                            nowOrder.promoStatus = "*";
                        nowOrder.KPIType = _cursor
                                .getString(_cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.KPI_TYPE));
                        nowOrder.KPIStatus = _cursor
                                .getString(_cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.STATUS));
                        nowOrder.Suggested = _cursor
                                .getInt(_cursor
                                        .getColumnIndex(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY));
//						nowOrder.ColorID = cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblOutletSKU.COLORID));
//						nowOrder.MaxOrderQty=cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblOutletSKU.MAXORDERQTY));

                        returnAllOrder.notOrderedList.add(nowOrder);

                    } while (_cursor.moveToNext());
                }
                _cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnAllOrder;
    }

    public static OrderItem getOrderSkuOrderItem(Context context, String outletId, String skuId) {
        OrderItem returnOrder = null;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOutletSKU WHERE OutletID = '"
                            + outletId + "' AND SKUID=" + skuId);
            Cursor cursor = ctx.Select(
                    "SELECT * FROM tblOutletSKU WHERE "
                            + DatabaseConstants.tblOutletSKU.OUTLET_ID
                            + "=? AND " + DatabaseConstants.tblOutletSKU.SKUID
                            + "=?",
                    new String[]{(outletId),
                            (skuId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnOrder = new OrderItem();
                    returnOrder.SKUID = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletSKU.SKUID));
                    returnOrder.KPIType = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletSKU.KPI_TYPE));
                    returnOrder.Status = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletSKU.STATUS));
                    returnOrder.OutletID = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletSKU.OUTLET_ID));
                    returnOrder.Suggested = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY));
//					returnOrder.ColorID = cursor
//							.getInt(cursor
//									.getColumnIndex(DatabaseConstants.tblOutletSKU.COLORID));
//					returnOrder.MaxOrderQty = cursor
//							.getInt(cursor
//									.getColumnIndex(DatabaseConstants.tblOutletSKU.MAXORDERQTY));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOrder;

    }

    public static AllOrderList getAllOrder(Context context) {
        AllOrderList returnAllOrderList = new AllOrderList();
        returnAllOrderList.orderedList = new ArrayList<OrderItem>();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblOrderItem");
            Cursor cursor = ctx.Select("SELECT * FROM tblOrderItem");
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        OrderItem now = new OrderItem();
                        now.OutletID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.OUTLET_ID));
                        now.SKUID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.SKUID));
                        now.Carton = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON));
                        now.Piece = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE));

                        now.BrandID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.BRAND_ID));
                        now.Total = cursor
                                .getDouble(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TOTAL));
                        now.TkOff = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.TK_OFF));
                        now.Piece_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.PIECE_ORG));
                        now.Carton_Org = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOrderItem.CARTON_ORG));
                        returnAllOrderList.orderedList.add(now);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnAllOrderList;
    }

    public static Sku getSku(Context context, String skuId) {
        Sku returnSku = new Sku();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: SELECT * FROM tblSKU WHERE SKUID="
                    + skuId);
            Cursor cursor = ctx.Select("SELECT * FROM tblSKU WHERE "
                            + DatabaseConstants.tblSKU.SKUID + "=?",
                    new String[]{(skuId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnSku.skuId = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.SKUID));
                    returnSku.title = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.TITLE));
                    returnSku.brandId = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.BRAND_ID));
                    returnSku.pcsRate = cursor.getDouble(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.PCS_RATE));
                    returnSku.pcsPerCtn = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSKU.PCS_PER_CTN));
                    returnSku.packSize = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblSKU.PACK_SIZE));
                    returnSku.ctnRate = cursor.getDouble(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.CTN_RATE));
                    returnSku.target = cursor.getInt(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.TARGET));
                    returnSku.MessageForHHT = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblSKU.MESSAGE_FOR_HHT));
                    returnSku.CriticalStock = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSKU.CRITICAL_STOCK));
                    returnSku.PositionValue = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSKU.POSITION_VALUE));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSku;
    }

    public static Sku getMRSku(Context context, String skuId) {
        Sku returnSku = new Sku();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
//			System.out.println("SQL COMMAND: SELECT * FROM tblMRSKU WHERE SKUID="
//					+ skuId);
//			Cursor cursor = ctx.Select("SELECT * FROM tblMRSKU WHERE "
//					+ DatabaseConstants.tblSKU.SKUID + "=?",

            System.out.println("SQL COMMAND: SELECT * FROM tblSKU WHERE SKUID="
                    + skuId);
            Cursor cursor = ctx.Select("SELECT * FROM tblSKU WHERE "
                            + DatabaseConstants.tblSKU.SKUID + "=?",
                    new String[]{(skuId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnSku.skuId = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.SKUID));
                    returnSku.title = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.TITLE));
                    returnSku.brandId = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.BRAND_ID));
                    returnSku.pcsRate = cursor.getDouble(cursor
                            .getColumnIndex(DatabaseConstants.tblSKU.PCS_RATE));


                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSku;
    }

    public static Reason getReason(Context context, int reasonId, int reasonType) {
        Reason returnReason = new Reason();
        if (reasonType == DatabaseConstants.MARKET_REASON) {
            ConnectionContext ctx = new ConnectionContext(context);
            try {
                ctx.Begin();
                System.out
                        .println("SQL COMMAND: SELECT * FROM tblMarketReason WHERE MarketReasonID="
                                + reasonId);
                Cursor cursor = ctx
                        .Select("SELECT * FROM tblMarketReason WHERE "
                                        + DatabaseConstants.tblMarketReason.MARKET_REASON_ID
                                        + "=?",
                                new String[]{Integer.toString(reasonId)});
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        returnReason.reasonId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReason.MARKET_REASON_ID));
                        returnReason.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblMarketReason.TITLE));
                    }
                    cursor.close();
                }
                ctx.End();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (reasonType == DatabaseConstants.NO_ORDER_REASON) {
            ConnectionContext ctx = new ConnectionContext(context);
            try {
                ctx.Begin();
                System.out
                        .println("SQL COMMAND: SELECT * FROM tblNoOrderReason WHERE ReasonID="
                                + reasonId);
                Cursor cursor = ctx.Select(
                        "SELECT * FROM tblNoOrderReason WHERE "
                                + DatabaseConstants.tblNoOrderReason.REASON_ID
                                + "=?",
                        new String[]{Integer.toString(reasonId)});
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        returnReason.reasonId = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblNoOrderReason.REASON_ID));
                        returnReason.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblNoOrderReason.DESCRIPTION));
                    }
                    cursor.close();
                }
                ctx.End();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnReason;
    }

    public static Channel getChannel(Context context, String channelId) {
        Channel returnChannel = new Channel();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblChannel WHERE channelId = "
                            + channelId);
            Cursor cursor = ctx.Select("SELECT * FROM tblChannel WHERE "
                            + DatabaseConstants.tblChannel.CHANNEL_ID + " =? ",
                    new String[]{(channelId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnChannel.channelID = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblChannel.CHANNEL_ID));
                    returnChannel.name = cursor.getString(cursor
                            .getColumnIndex(DatabaseConstants.tblChannel.NAME));
                    returnChannel.editAllowed = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblChannel.EDIT_ALLOWED));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnChannel;
    }

    public static Outlet getOutlet(Context context, String outletId) {
        Outlet returnOutlet = new Outlet();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblOutlet WHERE OutletID='"
                            + outletId+"'");
            Cursor cursor = ctx.Select("SELECT * FROM tblOutlet WHERE "
                            + DatabaseConstants.tblOutlet.OUTLET_ID + "=?",
                    new String[]{(outletId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        returnOutlet.outletId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_ID));
                        returnOutlet.description = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.DESCRIPTION));
                        returnOutlet.routeID = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.ROUTE_ID));
                        returnOutlet.channelId = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.CHANNEL_ID));
                        returnOutlet.address = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.ADDRESS));
//						returnOutlet.orderTotal = cursor
//								.getDouble(cursor
//										.getColumnIndex(DatabaseConstants.tblOutlet.ORDER_TOTAL));
                        returnOutlet.visited = cursor
                                .getInt(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.VISITED));
//						returnOutlet.productivity = cursor
//								.getInt(cursor
//										.getColumnIndex(DatabaseConstants.tblOutlet.PRODUCTIVITY));
                        returnOutlet.outletlatitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LATITUDE));
                        returnOutlet.outletlongitude = cursor
                                .getString(cursor
                                        .getColumnIndex(DatabaseConstants.tblOutlet.OUTLET_LONGITUDE));
//						returnOutlet.orderlatitude = cursor
//								.getString(cursor
//										.getColumnIndex(DatabaseConstants.tblOutlet.ORDER_LATITUDE));
//						returnOutlet.orderlongitude = cursor
//								.getString(cursor
//										.getColumnIndex(DatabaseConstants.tblOutlet.ORDER_LONGITUDE));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnOutlet;
    }

    private static PopItem getPopItem(Context context, String outletId, int popItemId) {
        PopItem returnPopItem = null;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * from tblPOPOutletItem WHERE outletId = '"
                            + outletId + "' AND popItemId = " + popItemId);
            Cursor cursor = ctx.Select(
                    "SELECT * FROM tblPOPOutletItem WHERE "
                            + DatabaseConstants.tblPOPOutletItem.OUTLET_ID
                            + " =? AND "
                            + DatabaseConstants.tblPOPOutletItem.POP_ITEM_ID
                            + " =? ",
                    new String[]{outletId,
                            Integer.toString(popItemId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnPopItem = new PopItem();
                    returnPopItem.lastQty = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblPOPOutletItem.LAST_QTY));
                    returnPopItem.currentQty = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnPopItem;
    }

    public static Bonus getBonus(Context context, String outletId) {
        Bonus returnBonus = null;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT BonusQty, SPMonth from tblSPMgtReturn WHERE outletId = '"
                            + outletId +"'");
            Cursor cursor = ctx.Select("SELECT "
                            + DatabaseConstants.tblSPMgtReturn.BONUS_QTY + ", "
                            + DatabaseConstants.tblSPMgtReturn.SP_MONTH + ", "
                            + DatabaseConstants.tblSPMgtReturn.IS_UPDATED
                            + " FROM tblSPMgtReturn WHERE "
                            + DatabaseConstants.tblSPMgtReturn.OUTLET_ID + " =? ",
                    new String[]{(outletId)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnBonus = new Bonus();
                    returnBonus.bonusQty = cursor
                            .getDouble(cursor
                                    .getColumnIndex(DatabaseConstants.tblSPMgtReturn.BONUS_QTY));
                    returnBonus.spMonth = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblSPMgtReturn.SP_MONTH));
                    returnBonus.isUpdated = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSPMgtReturn.IS_UPDATED));

                    returnBonus.outletId = outletId;
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBonus;
    }

    private static Slab getSlab(Context context, int tprId, int slabId) {
        Slab returnSlab = new Slab();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT * FROM tblSlabItem WHERE SlabID="
                            + slabId + " and tprid = " + tprId);
            Cursor cursor = ctx.Select(
                    "SELECT * FROM tblSlabItem WHERE "
                            + DatabaseConstants.tblSlabItem.SLAB_ID
                            + " =?  AND "
                            + DatabaseConstants.tblSlabItem.TPR_ID + " =? ",
                    new String[]{Integer.toString(slabId),
                            Integer.toString(tprId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    returnSlab.slabId = slabId;
                    returnSlab.tprId = tprId;
                    returnSlab.slabType = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSlabItem.SLAB_TYPE));
                    returnSlab.description = cursor
                            .getString(cursor
                                    .getColumnIndex(DatabaseConstants.tblSlabItem.ITEM_DESC));
                    returnSlab.forQty = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSlabItem.FOR_QTY));
                    returnSlab.itemQty = cursor
                            .getInt(cursor
                                    .getColumnIndex(DatabaseConstants.tblSlabItem.ITEM_QTY));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnSlab;
    }

    public static FreeOrDiscount getFreeOrDiscount(Context context, String outletId, String channelId, OutletVisitPageActivity outletVisitPageActivity) {
        FreeOrDiscount freeOrDiscount = new FreeOrDiscount();
        ArrayList<Tpr> tprList = DatabaseQueryUtil.getTprList(context,
                channelId);
        for (int i = 0; i < tprList.size(); i++) {
            double orderQuantity = 0;
            Tpr nowTpr = tprList.get(i);

            ArrayList<Sku> skuList = DatabaseQueryUtil
                    .getSkuListForTprCalculation(context, tprList.get(i).tprId,
                            channelId);
            for (int j = 0; j < skuList.size(); j++) {
                Sku nowSku = skuList.get(j);
                OrderItem nowOrder = Util.getOrderTotalBySkuId(context,
                        nowSku.skuId, outletVisitPageActivity);
                double quantity = nowOrder.Carton * nowSku.pcsPerCtn
                        + nowOrder.Piece;
                double value = nowOrder.Total;
                double weight = quantity * nowSku.packSize;
                switch (nowTpr.programmType) {
                    case DatabaseConstants.TPR_PROGRAM_TYPE_QUANTITY_2:
                    case DatabaseConstants.TPR_PROGRAM_TYPE_QUANTITY_4:
                        orderQuantity += quantity;
                        break;
                    case DatabaseConstants.TPR_PROGRAM_TYPE_VALUE:
                        orderQuantity += value;
                        break;
                    case DatabaseConstants.TPR_PROGRAM_TYPE_WEIGHT:
                        orderQuantity += weight;
                        break;
                    default:
                        break;
                }
            }

            // Slab checking

            if (orderQuantity > 0) {
                ArrayList<Slab> slabList = DatabaseQueryUtil.getSlabList(context, nowTpr.tprId,
                        orderQuantity);
                for (int k = 0; k < slabList.size(); k++) {
                    Slab nowSlab = slabList.get(k);
                    double bonus = orderQuantity * nowSlab.itemQty
                            / (double) nowSlab.forQty;

                    if (nowSlab.slabType == DatabaseConstants.FREE_PRODUCT && nowSlab.minQty <= orderQuantity && nowSlab.forQty <= orderQuantity) {
                        freeOrDiscount.freeItemList.add(new FreeItem(
                                nowSlab.description, (int) bonus));

                    } else if (nowSlab.slabType == DatabaseConstants.DISCOUNT && nowSlab.minQty <= orderQuantity && nowSlab.forQty <= orderQuantity) {
                        // TODO show it or not?
                        // freeOrDiscount.freeItemList.add(new FreeItem(nowSlab.description, bonus));
                        freeOrDiscount.discount += (int) bonus;
                    }
                    orderQuantity = orderQuantity * nowSlab.itemQty
                            % (double) nowSlab.forQty;
                }
            }
        }

        return freeOrDiscount;
    }

    public static Intent totalOrderedQuantityForSkuInOtherVisitedOutlets(Context context, int outletId, int skuId) {
        int ctn = 0, pcs = 0;
        Intent returnIntent = new Intent();
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT SUM(Carton), SUM(Piece) FROM tblOrderItem WHERE SKUID="
                            + skuId + " AND OutletID !=" + outletId);
            Cursor cursor = ctx.Select(
                    "SELECT SUM(" + DatabaseConstants.tblOrderItem.CARTON
                            + "), SUM(" + DatabaseConstants.tblOrderItem.PIECE
                            + ") FROM tblOrderItem WHERE "
                            + DatabaseConstants.tblOrderItem.SKUID + "=? AND "
                            + DatabaseConstants.tblOrderItem.OUTLET_ID + " !=?",
                    new String[]{Integer.toString(skuId),
                            Integer.toString(outletId)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    ctn = cursor.getInt(cursor.getColumnIndex("SUM("
                            + DatabaseConstants.tblOrderItem.CARTON + ")"));
                    pcs = cursor.getInt(cursor.getColumnIndex("SUM("
                            + DatabaseConstants.tblOrderItem.PIECE + ")"));
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        returnIntent.putExtra(DatabaseConstants.tblOrderItem.CARTON, ctn);
        returnIntent.putExtra(DatabaseConstants.tblOrderItem.PIECE, pcs);
        return returnIntent;
    }

    public static int getQuantityBySku(Context context, String skuID) {

        int pcsPerCarton = 0;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out
                    .println("SQL COMMAND: SELECT PcsPerCtn FROM tblSKU WHERE SKUID = " + skuID);
            Cursor cursor = ctx.Select("SELECT "
                            + DatabaseConstants.tblSKU.PCS_PER_CTN + " FROM tblSKU WHERE "
                            + DatabaseConstants.tblSKU.SKUID
                            + " =?",
                    new String[]{(skuID)});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        pcsPerCarton = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.tblSKU.PCS_PER_CTN));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pcsPerCarton;
    }

    public static String getDeliveryDay(Context context, String outletID, String sectionID) {

        String pcsPerCarton = "";
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            System.out.println("SQL COMMAND: select tls.OrderDlvDay FROM tblSection tls LEFT JOIN tblOutlet tlo ON tls.RouteID=tlo.RouteID WHERE tls.SectionID=" + sectionID + " and tlo.OutletID='" + outletID+"'");
            Cursor cursor = ctx.Select("SELECT tbs."
                    + DatabaseConstants.tblSection.ORDER_DLV_DAY + " FROM tblSection tbs LEFT JOIN tblOutlet tbo ON tbs."
                    + DatabaseConstants.tblSection.ROUTE_ID + " = tbo." + DatabaseConstants.tblOutlet.ROUTE_ID + " WHERE tbs." + DatabaseConstants.tblSection.SECTION_ID + "=?"
                    + " AND tbo." + DatabaseConstants.tblOutlet.OUTLET_ID + " =?", new String[]{(sectionID), (outletID)});

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        pcsPerCarton = cursor.getString(cursor.getColumnIndex(DatabaseConstants.tblSection.ORDER_DLV_DAY));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pcsPerCarton;
    }
    //endregion

    //region Booleans
    public static boolean isAllStoreVisited(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            String sql = String.format("SELECT COUNT(%s) UnvisitedStore FROM tblOutlet WHERE %s = ?", DatabaseConstants.tblOutlet.VISITED, DatabaseConstants.tblOutlet.VISITED);
            Cursor cursor = ctx.Select(sql, new String[]{Integer.toString(0)});

            if (cursor.moveToFirst()) {
                if (cursor.getInt(cursor.getColumnIndex("UnvisitedStore")) > 0)
                    return false;
                else
                    return true;
            }
            cursor.close();

            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean CheckTblDSRBasic(Context context) {
        ConnectionContext ctx = new ConnectionContext(context);
        boolean status = false;
        try {
            ctx.Begin();
            String sql = "SELECT * FROM tblDSRBasic";
            //Cursor cursor = ctx.Select(sql, new String[]{Integer.toString(0)});
            SQLiteDatabase db = ctx.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                int cnt = cursor.getCount();
                status = cnt > 0;
            }
            cursor.close();
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    private static boolean hasPromotion(Context context, String skuId,
                                        String channelId) {
//		ConnectionContext ctx = new ConnectionContext(context);
        int count = 0;
//		try {
//			ctx.Begin();
//			System.out
//					.println("SQL COMMAND: SELECT COUNT(*) FROM tbltprskuchnl WHERE SKUID="
//							+ skuId + " and channelId = " + channelId);
//			Cursor cursor = ctx.Select(
//					"SELECT COUNT(*) FROM tblTPRSKUChnl WHERE "
//							+ DatabaseConstants.tblTPRSKUChnl.SKUID + "=? AND "
//							+ DatabaseConstants.tblTPRSKUChnl.CHANNEL_ID
//							+ "=? ", new String[]{Integer.toString(skuId),
//							Integer.toString(channelId)});
//			if (cursor != null && cursor.getCount() > 0) {
//				if (cursor.moveToFirst()) {
//					count = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
//				}
//				cursor.close();
//			}
//			ctx.End();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        return false;
    }

    public static boolean CheckOrderGPS(Context context, int orderNo) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            String sql;
            sql = String.format("SELECT OrderLatitude, OrderLongitude FROM tblOrder WHERE OrderNo = ?", DatabaseConstants.tblOrder.ORDER_NO);
            Cursor cursor = ctx.Select(sql, new String[]{Integer.toString(orderNo)});

            if (cursor.moveToFirst()) {
                if (cursor.getDouble(cursor.getColumnIndex("OrderLatitude")) == 0 || cursor.getDouble(cursor.getColumnIndex("OrderLongitude")) == 0) {
                    cursor.close();
                    ctx.End();
                    return false;
                } else {
                    cursor.close();
                    ctx.End();
                    return true;
                }
            }

            cursor.close();
            ctx.End();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;

    }

    public static boolean CheckOutletGPS(Context context, String outletID) {
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            String sql;
            sql = String.format("SELECT Latitude, Longitude FROM tblOutlet WHERE OutletID = ?", DatabaseConstants.tblOutlet.OUTLET_ID);
            Cursor cursor = ctx.Select(sql, new String[]{(outletID)});

            if (cursor.moveToFirst()) {
                if (cursor.getDouble(cursor.getColumnIndex("Latitude")) == 0 || cursor.getDouble(cursor.getColumnIndex("Longitude")) == 0) {
                    cursor.close();
                    ctx.End();
                    return false;
                } else {
                    cursor.close();
                    ctx.End();
                    return true;
                }
            }
            cursor.close();
            ctx.End();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;

    }

    public static boolean CheckNotOrderCause(Context context, int outeltID) {
        boolean status = false;
        ConnectionContext ctx = new ConnectionContext(context);
        try {
            ctx.Begin();
            Cursor cursor = ctx.Select(
                    "SELECT NotordCoz FROM tblOrder WHERE "
                            + DatabaseConstants.tblOrder.OUTLET_ID
                            + "=? ",
                    new String[]{Integer.toString(outeltID)});
            if (cursor != null && cursor.getCount() > 0) {
                status = true;
            }
            if (cursor != null) {
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static boolean isIDExist(Context context, String ID, String tblName, String columnName) {

        boolean status = true;
        ConnectionContext ctx = new ConnectionContext(context);

        try {
            ctx.Begin();
            Cursor cursor = ctx.Select(
                    "SELECT * FROM " + tblName + " WHERE "
                            + columnName
                            + "=? ",
                    new String[]{ID});
            if (cursor != null && cursor.getCount() > 0) {
                status = false;
            }
            if (cursor != null) {
                cursor.close();
            }
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String GetDepotName(Context context) {

        String _distributorName = null;
        ConnectionContext ctx = new ConnectionContext(context);
        Cursor cursor;

        try {
            ctx.Begin();
            cursor = ctx.Select("SELECT DistributorName FROM tblDSRBasic");
            cursor.moveToFirst();
            _distributorName = cursor.getString(cursor.getColumnIndex("DistributorName"));
            ctx.End();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  _distributorName;
    }




    //endregion


}
