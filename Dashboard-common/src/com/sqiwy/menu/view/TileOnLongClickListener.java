package com.sqiwy.menu.view;

import android.content.ClipData;
import android.view.View;
import android.view.View.DragShadowBuilder;

/**
 * Created by abrysov
 */

public class TileOnLongClickListener implements View.OnLongClickListener{
	@Override
	public boolean onLongClick(View v) {
		ClipData data = ClipData.newPlainText("", "");
		DragShadowBuilder shadowBuilder = new DragShadowBuilder((View) v.getParent());
        v.startDrag(data, shadowBuilder, v.getTag(), 0);
		return false;
	}
}
