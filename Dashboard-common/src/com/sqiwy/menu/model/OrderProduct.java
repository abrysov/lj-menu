package com.sqiwy.menu.model;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class OrderProduct {
	private static final String TAG = "OrderProduct";

	protected int mId;
	protected int mOrderId;
	//protected int mProductId;
	protected Product mProduct;
	protected BigDecimal mPrice = new BigDecimal(0);
	protected int mCount;

	protected ArrayList<OrderProductModifier> mModifierList = new ArrayList<OrderProductModifier>();

	public OrderProduct() {
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getOrderId() {
		return mOrderId;
	}

	public void setOrderId(int orderId) {
		mOrderId = orderId;
	}

//	public int getProductId() {
//		return mProductId;
//	}
//
//	public void setProductId(int productId) {
//		mProductId = productId;
//	}
	
	public Product getProduct(){
		return mProduct;
	}
	
	public void setProduct(Product product){
		mProduct = product;
	}

	public BigDecimal getPrice() {
		return mPrice;
	}

	public void setPrice(BigDecimal price) {
		mPrice = price;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		mCount = count;
	}

	public ArrayList<OrderProductModifier> getModifierList() {
		return mModifierList;
	}

	public void setModifierList(ArrayList<OrderProductModifier> modifierList) {
		mModifierList = modifierList;
	}
	
	public void countPrice(){
		mPrice.add(new BigDecimal(mProduct.getPrice()));
		
		for (OrderProductModifier modifier : mModifierList) {
			mPrice.add(modifier.getPrice());
		}
		
		mPrice.multiply(new BigDecimal(mCount));
	}
}
