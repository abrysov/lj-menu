package com.sqiwy.menu.view;

import android.app.Activity;
import android.view.View;

import com.sqiwy.menu.cm.CMProductDetailDialog;
import com.sqiwy.menu.model.Product;

/**
 * Created by abrysov
 */

public class TileOnClickListener implements View.OnClickListener {
	@Override
    @SuppressWarnings("ConstantConditions")
	public void onClick(View v) {
		Product p = (Product) v.getTag();
        if (p != null) {
            CMProductDetailDialog.show(((Activity) v.getContext()).getFragmentManager(), p.getId());
        }
    }
}
