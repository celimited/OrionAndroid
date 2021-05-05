package com.orion.entities;

public class Slab {
	public int tprId;
	public int skuId = 0;
	public int slabId;
	public int minQty;
	public int slabType;
	public String description;
	public int forQty;
	public int itemQty;

	public String SlabType(int slabtype) {

		switch (slabtype) {
			case 1:
				description = "TK Off";
				break;
			case 2:
				description = "Product";
				break;
			case 3:
				description = "GiftItem";
				break;
			default:
				description = "";
				break;
		}
		return description;
	}
}