package com.orion.entities;

public class Order {
	public String OutletID;
	public String SectionID;
	public String OrderDate;
	public String StartTime;
	public String EndTime;
	public int OrderNo;
	public int SpotSale;
	public double OrderTotal;
	public int Visited;
	public double TotalTKOff;
	public int NotOrdCoz;
	public int SentStatus = 0;
	public String RefNo = "";
	public String OrderLatitude = "0.0";
	public String OrderLongitude = "0.0";
	public String OutletLatitude = "0.0";
	public String OutletLongitude = "0.0";
	public int PaymentMode = 0;
	public int DeliveryMode = 0;
	public String  DeliveryDate ;

	//	public int TPRID;
//	public int ItemID;
//	public double Norm;
//	public int InHand;
//	public int Suggested;
//	public int MustSKUNOReasonID;
//	public int IsMustSKU;
//	public Sku sku;
//	public String KPIType;
//	public String Status;
//
//	public String promoStatus ="";
//	public int Carton_Org;
//	public int Piece_Org;
//	public int ColorID;
//	public int MaxOrderQty;
	public Order() {
	}
}
