package com.orion.entities;

public class OrderItem {
	/* previous */
//	public String skuName;
//	public String ctn;
//	public String pcs;
//	public String value;

	/*new*/
	public int OrderNo;
	public String OutletID;
	public String SKUID;
	public int Carton;
	public int Piece;
	public String BrandID;
	public double Total;
	public double TkOff;
	public int TPRID;
	public int ItemID;
	public double Norm;
	public int InHand;
	public int Suggested;
	public int MustSKUNOReasonID;
	public int IsMustSKU;
	public String KPIType;
	public Sku sku;
	public String KPIStatus;
	public String promoStatus = "";
	public String Status = "";
	public int Carton_Org = 0;
	public int Piece_Org = 0;
	public int soldCartons = 0;
	public int soldPieces = 0;
	public double soldTotal = 0f;
	public int replacePieces = 0;
	public String skuName="";


	public OrderItem() {
	}

	public OrderItem(Sku sku) {
		this.sku = sku;
		this.SKUID = sku.skuId;
		this.BrandID = sku.brandId;
	}
}