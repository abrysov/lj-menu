package com.sqiwy.menu.model;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class OrderList {
	private static final String TAG = "OrderList";
	
	protected ArrayList<Order> orderList = new ArrayList<Order> ();

    public OrderList() {
    }

	public ArrayList<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(ArrayList<Order> orderList) {
		this.orderList = orderList;
	}
    
    
}
