package com.orion.entities;

import java.util.ArrayList;

public class FreeOrDiscount {
	public ArrayList<FreeItem> freeItemList;
	public double discount;

	public FreeOrDiscount() {
		freeItemList = new ArrayList<FreeItem>();
		discount = 0;
	}
}
