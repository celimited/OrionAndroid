package com.orion.entities;

/**
 * Created by atiqur on 1/23/2017.
 */

public class OrderItemsByOrderNo {

    private int orderNo;
    private double total;
    private int sentStatus;
    private String outletName;

    public OrderItemsByOrderNo(int _orderNo, double _total, int _sentStatus, String _outletName) {
        this.orderNo = _orderNo;
        this.total = _total;
        this.sentStatus = _sentStatus;
        this.outletName = _outletName;
    }

    public int getOrderNo(){
        return orderNo;
    }
    public double getTotal(){
        return total;
    }
    public int getSentStatus(){
        return sentStatus;
    }
    public  String getOutletName(){return outletName;}

}
