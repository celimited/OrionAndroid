package com.orion.entities;

public class Market {
	public String sectionId;
	public String routeId;
	public String title;
	public String orderColDay;
	public String orderDelDay;

	public Market(String sectionId, String routeId, String orderColDay, String orderDelDay, String title) {
		this.sectionId = sectionId;
		this.routeId = routeId;
		this.title = title;
		this.orderColDay = orderColDay;
		this.orderDelDay = orderDelDay;
	}
}
