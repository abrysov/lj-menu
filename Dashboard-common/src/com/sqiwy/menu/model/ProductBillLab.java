package com.sqiwy.menu.model;

import java.util.ArrayList;
import java.util.Random;

import com.fosslabs.android.utils.JSLog;

import android.content.Context;

/**
 * Created by abrysov
 */

public class ProductBillLab {
	//Для сохранения счета после выхода на главную
	private static final String TAG = "ProductBillLab";
	
	private static ProductBillLab mProductBillLab;
	private ArrayList<ProductBill> mList = new ArrayList<ProductBill>();
	private ArrayList<Product> mProducts = new ArrayList<Product>();
	private Context mContext;
	private int pos = 0;

	private ProductBillLab(Context context) {
		mContext = context;
	}

	public static ProductBillLab get(Context c) {
		if (mProductBillLab == null) {
			mProductBillLab = new ProductBillLab(c.getApplicationContext());
		}
		return mProductBillLab;
	}

	public ProductBill getProductBill(int pos) {
		//if(pos >= mList.size()) return new ProductBill(mContext);
		return mList.get(pos);
	}

	public int size() {
		return mList.size();
	}

	public double[] getBillsTotalPrices() {
		double[] billPrices = new double[mList.size()];
		for (int i = 0; i < mList.size(); i++) {
			billPrices[i] = mList.get(i).getTotalPrice();
		}
		return billPrices;
	}

	public double getCommonBill() {
		double commonBill = 0;
		for (int i = 0; i < mList.size(); i++) {
			commonBill += mList.get(i).getTotalPrice();
		}
		return commonBill;
	}
	
	public void addProduct(Product product, int bill){
		if(bill >= mList.size()) return;
		mList.get(bill).getProducts().add(0,product);
	}
	
	public void addEmptyBill(){
		ProductBill bill = new ProductBill(mContext);
		//bill.setData(mList.size());
		//JSLog.d(TAG, "addEmptyBill " + mList.size() );
		bill.setData(pos);
		pos++;
		mList.add(bill);
	}
	
	public void show(){
		String res  = "";
		for (int i = 0; i < mList.size(); i++) {
			res += mList.get(i).show();
		}
		JSLog.d(TAG, "show " + res);  
	}
	
	public boolean isEmpty()
	{
		if(size() == 0)
			return true;
		else
			return false;
	}

}
