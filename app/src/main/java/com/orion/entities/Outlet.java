package com.orion.entities;

import java.io.Serializable;

public class Outlet implements Serializable {
	public String outletId;
	public String description;
	public String channelId;
	public String owner;
	public String address;
	public String contactNo;
	public int visited;
	public String routeID = null;
	public String outletlatitude = "0.0";
	public String outletlongitude = "0.0";
	public String [] imageUrls;
	public int sentStatus = 0;

//		public String orderlatitude;
//		public String orderlongitude;
//		public int totalLineSold;
//		public int webOrderDetailKey ;

// 		public int type;
//		public String checkInTime;
//		public String checkOutTime;
//		public String orderNo;
//		public int spotSale;
//		public double orderTotal;
//		public double discount;
//		public int noOrderReason;
//		public String outletFlag;
//		public String valueClass;
//		public int changed;
//		public String distCorItems;
//		public int productivity;
//		public int sectionId;

	public Outlet() {}
}