package com.orion.entities;

public class ReturnItem {
	public String skuName;
	public String ctn;
	public String qty;
	public String value;

	public ReturnItem(String skuName, String ctn, String qty, String value) {
		this.skuName = skuName;
		this.ctn = ctn;
		this.qty = qty;
		this.value = value;
	}
}