package com.sqiwy.menu.model;

import android.content.Context;
import android.util.Log;

import com.sqiwy.menu.util.CommonUtils;

import java.util.ArrayList;

/**
 * Created by abrysov
 */

public class ProductBill {
	
	private static ProductBill mProductBill;
	private ArrayList<Product> mProducts = new ArrayList<Product>();
	private Context mContext;
	
	private final String[] mNames = { "Pasta", "Borsch", "Ice Cream", "Tea",
			"Coffee", "Mazarella" };
	private final String[] mDescs = { "Second", "Soup", "Desert", "Drink",
			"Drink", "Pizza" };
	private final float[] mPrices = { 120, 140, 50, 25, 40, 150 };
	private final int[] mIdsGroup = { 2, 1, 3, 5, 5, 4 };
	private int mPos = -100;
	
	
	public ProductBill(Context context) {
		mContext = context;
		setData(0);
	}
	
	public static ProductBill get(Context c) {
		if (mProductBill == null) {
			mProductBill = new ProductBill(c.getApplicationContext());
		}
		return mProductBill;
	}

	public void setData(int pos){
		for (int i = 0; i < mNames.length; i++) {
			//mProducts.add(new Product(mNames[i] + " " + pos, mDescs[i], mPrices[i],
			//		mIdsGroup[i]));
			Product product = new Product(mNames[i], "desc", mPrices[i]);
			product.setId(i);
			mProducts.add(product);
		}
		mPos = pos;
		Log.d("ProductBill", mProducts.toString());
	}
	public ArrayList<Product> getProducts() {
		return mProducts;
	}
	
	public float getTotalPrice()
	{
		float price = 0;
		for (Product p : mProducts) {
			price += p.getPrice() * p.getCount();
		}
		return price;
	}
	
	public String getFormatPrice(){
        return CommonUtils.formatPrice(getTotalPrice());
	}
	
	public String show(){
		return "" + mPos + " ";
	}
	
	public int findProduct(Product product){
		int index = mProducts.size();
		for (int i = 0; i < mProducts.size(); i++) {
			if(mProducts.get(i).equals(product))
				index = i;
		}
		return index;
	}

}
