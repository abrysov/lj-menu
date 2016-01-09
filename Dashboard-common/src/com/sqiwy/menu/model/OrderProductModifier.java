package com.sqiwy.menu.model;

import java.math.BigDecimal;

/**
 * Created by abrysov
 */

public class OrderProductModifier {
	private static final String TAG = "OrderProductModifier";
	
	protected int mId;
    protected int mOrderProductId;
    protected int mModifierId;
    protected BigDecimal mPrice;
    protected int mCount;

    public OrderProductModifier() {
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getOrderProductId() {
		return mOrderProductId;
	}

	public void setOrderProductId(int orderProductId) {
		mOrderProductId = orderProductId;
	}

	public int getModifierId() {
		return mModifierId;
	}

	public void setModifierId(int modifierId) {
		mModifierId = modifierId;
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
    
    
}
