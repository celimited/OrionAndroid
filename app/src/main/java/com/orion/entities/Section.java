package com.orion.entities;

import java.util.ArrayList;

public class Section {
	public ArrayList<Market> list;
	public Market defaultUserMarket;
	public String title;
	public int maxOrderNo = 0;
	public String dsrID;
	public String sectionID;
	public int routeID;
	public int deliverygroupID;
	public String orderColDay;
	public String orderDlvDay;

	public Section() {
		list = new ArrayList<Market>();
	}
}