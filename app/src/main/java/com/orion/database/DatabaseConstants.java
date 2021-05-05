package com.orion.database;

public class DatabaseConstants {
    public static final int DATABASE_VERSION = 1;


	public static final int TPR_PROGRAM_TYPE_QUANTITY_2 = 2;
	public static final int TPR_PROGRAM_TYPE_QUANTITY_4 = 4;
	public static final int TPR_PROGRAM_TYPE_WEIGHT = 5;
	public static final int TPR_PROGRAM_TYPE_VALUE = 7;

	public static final int MARKET_REASON = 1;
	public static final int NO_ORDER_REASON = 2;
	public static int DATABASE_IMPORT_SUCCESS_FLAG = 0;
	public static int MARKET_RETURN_ID;
	public static final String SHARED_PREF_FILE = "Nestle";

	public static final String DATABASE_LOCAL_PATH = "/data/data/com.orion.application/databases";
	public static final String DATABASE_NAME = "Orion.sql";
	public static final int FREE_PRODUCT = 2;  //previous 0
	public static final int DISCOUNT = 1;  // previous 1
	public static final int OUTLET_VISIT_PAGE = 1;


	public static final String TABLE_NEW_OUTLET = "new_outlet";
	public static final String TABLE_IMAGES = "images";
	public static final String TABLE_ORDER = "tblOrder";
	public static final String TABLE_ORDER_ITEM = "tblOrderItem";
	public static final String TABLE_OUTLET = "tblOutlet";

	// key names
	public static final String KEY_OUTLET_ID = "OutletID";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_CHANNEL_ID = "ChannelID";
	public static final String KEY_OWNER = "Owner";
	public static final String KEY_ADDRESS = "Address";
	public static final String KEY_CONTACT_NO = "Phone";
	public static final String KEY_VISITED = "Visited";
	public static final String KEY_ROUTE_ID = "RouteID";
	public static final String KEY_IMAGE_URLS = "ImageURLs";
	public static final String KEY_SENT_STATUS = "SentStatus";
	public static final String KEY_OUTLET_LATITUDE = "Latitude";
	public static final String KEY_OUTLET_LONGITUDE = "Longitude";

	public static final String KEY_IMAGE_ID = "image_id";
	public static final String KEY_IMAGE_TYPE = "image_type";
	public static final String KEY_IMAGE_DATE = "image_time";
    public static final String KEY_IMAGE_URL = "image_url";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";

	public static final String CREATE_TABLE_NEW_OUTLET = "CREATE TABLE " + TABLE_NEW_OUTLET +" (" +
			KEY_OUTLET_ID + "  INTEGER PRIMARY KEY, " +
			KEY_DESCRIPTION + " Text(400), " +
			KEY_CHANNEL_ID + " int, " +
			KEY_OWNER + " Text(100), " +
			KEY_ADDRESS + " Text(200), " +
			KEY_CONTACT_NO + " Text(200), " +
			KEY_VISITED + " smallint, " +
			KEY_ROUTE_ID + " INTEGER, " +
			KEY_IMAGE_URLS + " TEXT, " +
			KEY_OUTLET_LATITUDE + " TEXT, " +
			KEY_OUTLET_LONGITUDE + " TEXT " +
			")";

	public static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + TABLE_IMAGES +" (" +
			KEY_IMAGE_ID + "  INTEGER PRIMARY KEY, " +
			KEY_OUTLET_ID + "  INTEGER, " +
			KEY_IMAGE_TYPE + " smallint, " +
			KEY_IMAGE_DATE + " REAL, " +
			KEY_IMAGE_URL + " TEXT " +
			")";


	public class tblOutletAllocation {
		public static final String Allocation_Qty = "AllocationQty";
		public static final String Allocation = "Allocation";
		public static final String SKUID = "SKUID";
		public static final String Outlet_ID = "OutletID";
	}

	public class tblSKU {
		public static final String SKUID = "SKUID";
		public static final String TITLE = "Title";
		public static final String BRAND_ID = "BrandID";
		public static final String PCS_RATE = "PcsRate";
		public static final String PCS_PER_CTN = "PcsPerCtn";
		public static final String PACK_SIZE = "Packsize";
		public static final String CTN_RATE = "CtnRate";
		public static final String TARGET = "Target";
		public static final String MESSAGE_FOR_HHT = "MessageForHHT";
		public static final String CRITICAL_STOCK = "CriticalStock";
		public static final String POSITION_VALUE = "PositionValue";
		public static final String IS_NSD = "IsNSD";
	}

	public class tblOutletSKU {
		public static final String OUTLET_ID = "OutletID";
		public static final String SKUID = "SKUID";
		public static final String OUTLET_SKU_ID = "OutletSKUID";
		public static final String KPI_TYPE = "KPIType";
		public static final String STATUS = "Status";
		public static final String SUGGESTEDQTY = "SuggestedQty";
		public static final String MAXORDERQTY = "MaxOrderQty";
		public static final String ALLOCATEDQTY = "AllocatedQty";
		public static final String COLORID = "ColorID";
	}

	public class tblBrand {
		public static final String BRAND_ID = "BrandID";
		public static final String NAME = "Name";
	}

	public class tblChannel {
		public static final String CHANNEL_ID = "ChannelID";
		public static final String NAME = "Name";
		public static final String EDIT_ALLOWED = "EditAllowed";
	}

	public class tblDSRBasic {
		public static final String SECTION_ID = "SectionID";
		public static final String NAME = "Name";
		public static final String ORDER_ACHIEVED = "OrderAchvd";
		public static final String DAY_REMAIN = "DayRemain";
		public static final String TARGET_REM = "TargetRem";
		public static final String DAILY_TAREGET = "DailyTarget";
		public static final String VISIT_DATE = "VisitDate";
		public static final String PDA_USER = "PDAUser";
		public static final String DSR_ID = "DSRID";
		public static final String TARGET = "target";
		public static final String ROAMER_LOG_ID = "RoamerLogID";
		public static final String COMPANY_ID = "CompanyID";
		public static final String DISTRIBUTOR_NAME = "DistributorName";
		public static final String DISTRIBUTOR_ADDRESS = "DistributorAddress";
		public static final String MOBILE_NO= "MobileNo";
		public static final String PASSWORD = "Password";
		public static final String LAST_UPDATE_TIME = "LastUpdateTime";
	}

	public class tblMarketReason {
		public static final String MARKET_REASON_ID = "MarketReasonID";
		public static final String TITLE = "Title";
		public static final String CODE = "Code";
	}

	public class tblMarketReturn {
		public static final String MARKET_RETURN_ID = "MarketReturnID";
		public static final String BRAND_ID = "BrandID";
		public static final String QUANTITY = "Qty";
		public static final String BATCH = "Batch";
		public static final String MARKET_REASON_ID = "MktReasonID";
		public static final String EXPIRY_DATE = "ExpDate";
		public static final String OUTLET_ID = "OutletID";
		public static final String SKUID = "SKUID";
		public static final String ORDER_NO = "OrderNo";
	}

	public class tblMerchMaterial {
		public static final String UNIQUE_ID = "UniqueID";
		public static final String TITLE = "Title";
		public static final String BRAND_OPTION = "BrandOption";
	}

	public class tblOrderItem {
		public static final String OUTLET_ID = "OutletID";
		public static final String ORDER_NO = "OrderNo";
		public static final String SKUID = "SKUID";
		public static final String CARTON = "Carton";
		public static final String PIECE = "Piece";
		public static final String BRAND_ID = "BrandID";
		public static final String TOTAL = "Total";
		public static final String TK_OFF = "TkOff";
		public static final String TPR_ID = "TPRID";
		public static final String ITEM_ID = "ItemID";
		public static final String NORM = "Norm";
		public static final String IN_HAND = "InHand";
		public static final String SUGGESTED = "Suggested";
		public static final String MUST_SKU_NO_REASON_ID = "MustSKUNOReasonID";
		public static final String IS_MUST_SKU = "IsMustSKU";
		public static final String CARTON_ORG = "OriginalCarton";
		public static final String PIECE_ORG = "OriginalPiece";
		public static final String SALE_CARTON = "SaleCarton"; //--
		public static final String SALE_PIECE = "SalePiece";
		public static final String REPLACE_PIECE = "ReplacePiece";
		public static final String TOTAL_SALE = "TotalSale";
	}

	public  class  tblOrder {
		public static final String OUTLET_ID = "OutletID";
		public static final String SECTION_ID = "SectionID";
		public static final String ORDER_DATE = "OrderDate";
		public static final String CHECK_IN_TIME = "StartTime";
		public static final String CHECK_OUT_TIME = "EndTime";
		public static final String ORDER_NO = "OrderNo";
		public static final String SPOT_SALE = "SpotSale";
		public static final String ORDER_TOTAL = "OrderTotal";
		public static final String VISITED = "Visited";
		public static final String NO_ORDER_REASON = "NotordCoz";
		public static final String REF_NO = "RefNo";
		public static final String TOTAL_TK_OFF = "TotalTKOff";
		public static final String ORDER_LATITUDE = "OrderLatitude";
		public static final String ORDER_LONGITUDE = "OrderLongitude";
		public static final String SENT_STATUS = "SentStatus";
		public static final String PAYMENT_MODE = "PaymentMode";
		public static final String DELIVERY_MODE ="DeliveryShift" ;
		public static final String DELIVERY_DATE ="ExptDlvDate" ;
	}

	public class tblOrderHistory {
		public static final String OUTLET_ID = "OutletID";
		public static final String SKUID = "SKUID";
		public static final String CARTON = "Carton";
		public static final String PIECE = "Piece";
		public static final String TOTAL = "Total";
		public static final String TK_OFF = "TkOff";
		public static final String ORDER_DATE = "OrderDate";
		public static final String NO_OF_ORDER = "NoOfOrder";
		public static final String TPR_ID = "TPRID";
		public static final String ITEM_ID = "ItemID";
		public static final String DSR_ID = "DSRID";
		public static final String BRAND_ID = "BrandID";
	}

	public class tblOutlet {
		public static final String OUTLET_ID = "OutletID";
		public static final String DESCRIPTION = "Description";
		public static final String CHANNEL_ID = "ChannelID";
		public static final String OWNER = "Owner";
		public static final String ADDRESS = "Address";
		public static final String CONTACT_NO = "Phone";
		public static final String VISITED = "Visited";
		public static final String ROUTE_ID = "RouteID";
		public static final String SENT_STATUS = "SentStatus";
		public static final String OUTLET_LATITUDE = "Latitude";
		public static final String OUTLET_LONGITUDE = "Longitude";
	}

	public class tblNoOrderReason {
		public static final String REASON_ID = "ReasonID";
		public static final String DESCRIPTION = "Description";
	}

	public class tblSection {
		public static final String TITLE = "Title";
		public static final String MAX_ORD_NO = "MaxOrdNo";
		public static final String DSR_ID = "DSRID";
		public static final String SECTION_ID = "SectionID";
		public static final String ROUTE_ID = "RouteID";
		public static final String DELIVERYGROUP_ID = "DeliveryGroupID";
		public static final String ORDER_COL_DAY = "OrderColDay";
		public static final String ORDER_DLV_DAY = "OrderDlvDay";
	}

	public class tblSPMgtReturn {
		public static final String SP_MGT_RETURN_ID = "SPMgtReturnID";
		public static final String OUTLET_ID = "OutletID";
		public static final String BRAND_SKU_ID = "BrandSKUID";
		public static final String BONUS_QTY = "BonusQty";
		public static final String SLAB_IS = "SlabID";
		public static final String SP_MONTH = "SPMonth";
		public static final String IS_UPDATED = "IsUpdate";
	}

	public class tblSPMgtScheme {
		public static final String UNIQUE_ID = "UniqueID";
		public static final String CODE = "Code";
		public static final String FACINGS = "Facings";
		public static final String BRAND_SKU_ID = "BrandSKUID";
		public static final String QTY = "Qty";
	}

	public class tblTPR {
		public static final String DESCRIPTION = "Description";
		public static final String CEILING_QTY = "CeilingQty";
		public static final String IS_FIXED_TPR = "IsFixedTPR";
		public static final String TPR_ID = "TPRID";
		public static final String PROGRAM_TYPE = "ProgramType";
	}

	public class tblTPRSKUChnl {
		public static final String TPR_SKU_CHANNEL_ID = "TPRSKUChnlID";
		public static final String TPR_ID = "TPRID";
		public static final String CHANNEL_ID = "ChannelID";
		public static final String SKUID = "SKUID";
	}

	public class tblTPRSlab {
		public static final String TPR_ID = "TPRID";
		public static final String SKUID = "SKUID";
		public static final String SLAB_ID = "SlabID";
		public static final String MIN_QTY = "MinQty";
		public static final String TPR_SLAB_ID = "TPRSlabID";
	}

	public class tblSlabItem {
		public static final String TPR_ID = "TPRID";
		public static final String SLAB_ID = "SlabID";
		public static final String SKUID = "SKUID";
		public static final String SLAB_TYPE = "SlabType";
		public static final String ITEM_DESC = "ItemDesc";
		public static final String FOR_QTY = "ForQty";
		public static final String ITEM_QTY = "ItemQty";
		public static final String SLAB_ITEM_ID = "SlabItemID";
	}

	public class tblOutletMerch {
		public static final String OUTLET_MERCH_ID = "OutletMerchID";
		public static final String OUTLET_ID = "OutletID";
		public static final String MATERIAL_ID = "MaterialID";
		public static final String INSTALL_DATE = "InstallDate";
		public static final String QTY = "Qty";
		public static final String BRANDS = "Brands";
		public static final String MODIFIED = "Modified";
	}

	public class tblPOPOutletItem {
		public static final String POP_ITEM_ID = "POPItemID";
		public static final String OUTLET_ID = "OutletID";
		public static final String LAST_QTY = "LastQty";
		public static final String CURRENT_QTY = "CurrentQty";
	}

	public class tblPOPItem {
		public static final String POP_ITEM_ID = "POPItemID";
		public static final String DESCRIPTION = "Description";
	}

	public class otherFields {
		public static final String REMOVE_STATUS = "removeStatus";
		public static final String RETURN_ITEM_COUNT = "returnItemCount";
		public static final String ORDER_STATUS = "orderedStatus";
		public static final String MARKET_RETURN_ID = "marketReturnId";
		public static final String RETURN_ITEM_VALUE = "returnItemValue";
	}
}
