package com.orion.entities;

public class Sku {
	public String skuId;
	public String title;
	public String brandId;
	public double pcsRate;
	public int pcsPerCtn;
	public double packSize;
	public double ctnRate;
	public int target = 0;
	public String MessageForHHT = "";
	public int CriticalStock = 200;
	public int PositionValue = 0;
	public int IsNSD;

	public Sku() {
	}

	public Sku(String skuId, String title) {
		this.skuId = skuId;
		this.title = title;
	}
}