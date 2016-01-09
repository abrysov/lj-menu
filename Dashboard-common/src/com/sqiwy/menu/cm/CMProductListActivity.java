package com.sqiwy.menu.cm;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sqiwy.dashboard.DBStartActivity;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Modifier;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class CMProductListActivity extends Activity implements CMMenuGrandFragment.Callbacks,
        CMCategoriesFragment.OnFragCategoryEventListener {
	
    public static final String EXTRA_CATEGORY = "com.sqiwy.dashboard.Category";

    private CMProductsFragment mProductsFragment;
    private CMBillFragment mBillFragment;
    private CMMenuGrandFragment mMenuGrandFragment;

    public int getInitialCategoryId() {
        int categoryId;
        try {
            categoryId = Integer.valueOf(getIntent().getStringExtra(EXTRA_CATEGORY));
        } catch (Exception e){
            categoryId = -1;
        }
        return categoryId;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.cm_activity_product_list);
        
		Bitmap back = ((MenuApplication) getApplication()).getDashboardBackground();
		ImageView iv = (ImageView) findViewById(R.id.iv_db_back);
		iv.setImageBitmap(back);
        
        
        FragmentManager fm = getFragmentManager();
        mProductsFragment = (CMProductsFragment) fm.findFragmentById(R.id.fragment_products);
        mBillFragment = (CMBillFragment) fm.findFragmentById(R.id.fragment_bill);
        mMenuGrandFragment=(CMMenuGrandFragment)fm.findFragmentById(R.id.fragment_menu_grand);
        new DBStartActivity.OpenTableSessionTask().execute(this);
 	}

	
	@Override
	public void onConfigurationChanged() {
		UIUtils.toggleOrientation(this);
	}

	@Override
	public void onCategoryChanged(int categoryId) {
        mProductsFragment.showProducts(categoryId);
	}

    public void addProduct(Product p, List<Modifier> checkedModifiers) {
        mBillFragment.addProduct(p, checkedModifiers);
    }

    public void updateProduct(long orderProductId, List<Modifier> checkedModifiers) {
        mBillFragment.updateProduct(orderProductId, checkedModifiers);
    }

    public void placeOrder() {
        mBillFragment.placeOrder();
    }
    
    private int mProductsActivityHeight=-1, mMenuGrandFragmentHeight=-1;
    
    /**
     * @return current height of activity main view. TODO: MAY CHANGE ON ACTIVITY LAYOUT CHANGES 
     */
    public int getProductListActivityHeight() {
    	if (mProductsActivityHeight<=0) {
    		View topView=getWindow().getDecorView();
    		
    		mProductsActivityHeight=topView.getHeight();
    	}
    	return mProductsActivityHeight;
    }
    
    /**
     * @return current height of top bar ('menu grand')
     */
    public int getMenuGrandFragmentHeight() {
    	if (mMenuGrandFragmentHeight<=0) {
    		View menuGrandFragmentView;
    		
    		menuGrandFragmentView=mMenuGrandFragment.getView(); mMenuGrandFragmentHeight=menuGrandFragmentView.getHeight();
    	}
    	return mMenuGrandFragmentHeight;
    }
}
