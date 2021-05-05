package com.orion.entities;

public class PopItem {
	public int popItemId;
	public String description;
	public String outletId;
	public int lastQty;
	public int currentQty;

	public PopItem(PopItem another) {
		this.popItemId = another.popItemId;
		this.description = another.description;
		this.outletId = another.outletId;
		this.lastQty = another.lastQty;
		this.currentQty = another.currentQty;
	}

	public PopItem() {
		currentQty = -1;
		lastQty = -1;
	}
}
