package com.sqiwy.menu.server;

import java.util.ArrayList;

import android.content.Context;

import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.model.WeightItem;

/**
 * Created by abrysov
 */

public class ServerQuery {
	private static final String TAG = "ServerQuery";
	
	/*public static ArrayList<TopProductCategory> getTopProductCategory(Context context){
		ArrayList<TopProductCategory> tlist = new ArrayList<TopProductCategory>();
		for(int i = 0; i < 10; i++){
			TopProductCategory tpc = new TopProductCategory();
			tpc.setName("TopCategory " + i);
			tpc.setId(i);
			if(i % 2 == 0) tpc.setList(getProductListCategory(context, i));
			tlist.add(tpc);
		}
		return tlist;
	}
	public static ArrayList<ProductCategory> getProductListCategory(Context context, int i){
		
		
		ArrayList<ProductCategory> list = new ArrayList<ProductCategory>();
		for(int j = 0; j < 6; j++){
			ProductCategory pc = new ProductCategory();
			pc.setName("SubCatgory " + i*100 + j);
			pc.setId(i*100 + j);
			list.add(pc);
		}
		return list;
	}
	
	
	public  static ArrayList<ProductCategory> getProductListSubCategory
			(Context context, int idCurProductCategory){
		ArrayList<ProductCategory> list = new ArrayList<ProductCategory>();
		for(int i = 0; i < 12; i++){
			ProductCategory pc = new ProductCategory();
			pc.setName("Catgory " + idCurProductCategory + "." + i);
			pc.setId(i);
			list.add(pc);
		}
		return list;
	}*/
	
	public static ArrayList<WeightItem> getProductList(Context context, int idProductCategory){
		return getCMProductList(context, idProductCategory);
	}
	
	public static ArrayList<WeightItem> getCMProductList(Context context, int idProductCategory){
		ArrayList<WeightItem> a = new ArrayList<WeightItem>();
		int i = 0;
		Product product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(1);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(1);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(1);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(2);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(2);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(1);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(2);i++;
		a.add(product);product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(2);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(0);i++;
		a.add(product);
		product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
		product.setItemWeight(1);i++;
		a.add(product);
		int j = 11;
		while(j > 0){
			product = new Product("Name " + i + " " + 0, "Desc " + i + " fvdvffvdfvfvdvfffv \neveeegeggeerergererg", i * 1000);
			product.setItemWeight(0);i++;
			a.add(product);
			j--;
		}
		return a;
	}
}
