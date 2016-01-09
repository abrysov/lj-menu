package com.sqiwy.menu.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class Order {
	private static final String TAG = "Order";
	
	protected int mId;
    protected Date mTime;
    protected Integer mStaffId;

    protected ArrayList<OrderProduct> mProductList = new ArrayList<OrderProduct>();

    public Order() {
    	for(int i = 0; i < 10; i++){
    		OrderProduct orderProduct = new OrderProduct();
    		Product product = new Product();
    		product.setName("Product " + i);
    		orderProduct.setProduct(product);
    		orderProduct.setPrice(new BigDecimal(100));
    		orderProduct.setCount(1);
    		mProductList.add(orderProduct);
    	}
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public Date getTime() {
		return mTime;
	}

	public void setTime(Date time) {
		mTime = time;
	}

	public Integer getStaffId() {
		return mStaffId;
	}

	public void setStaffId(Integer staffId) {
		mStaffId = staffId;
	}

	public ArrayList<OrderProduct> getProductList() {
		return mProductList;
	}

	public void setProductList(ArrayList<OrderProduct> productList) {
		mProductList = productList;
	}
    
	public BigDecimal getOrderPrice(){
		BigDecimal orderPrice = new BigDecimal(0);
		for (OrderProduct orderProduct : mProductList) {
			orderPrice.add(orderProduct.getPrice());
		}
		return orderPrice;
	}
    
}
