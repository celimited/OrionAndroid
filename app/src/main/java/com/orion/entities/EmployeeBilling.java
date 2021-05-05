package com.orion.entities;

import java.io.Serializable;

public class EmployeeBilling implements Serializable {
	public String billingDate;
	public int employeeID;
	public String startPoint;
	public String endPoint;
	public String distance;
	public double billingAmount =0f;
	//Transportmdoe ??
	//Location type ExHQ/ HQ
	public EmployeeBilling() {}
}