package com.sqiwy.menu.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.fosslabs.android.utils.JSLog;
import com.sqiwy.menu.model.WeightBlock;
import com.sqiwy.menu.model.WeightBlockCH.WeightBlockType;

/**
 * Created by abrysov
 */

public abstract class CHWeightBlockViewHolder {
	private static final String TAG = "CHWeightBlockViewHolder";

	CHWeightBlockViewHolder(Context context, View view) {
		// If you have some common view initialize them here
	}

	abstract int getType();

	abstract void populate(WeightBlock wb);

	static View createView(Context context, int position, int type,
			ViewGroup parent) {
		
		JSLog.d(TAG, "createView" + type + " " + position);
		View result = null;
		WeightBlockType wtype = WeightBlockType.values()[type];
		switch (wtype) {

		case TYPE_SMALL_MEDIUM:
			//result = ViewHolderCHSmallMedium.createView(context, parent);
			break;
		case TYPE_BIG_TWO_SMALL:
			//result = ViewHolderCHBigTwoSmall.createView(context, parent);
			break;
		//JSTODO
		case TYPE_TWO_TEXT:;
			break;
			
		default:
			result = null;
			break;
		}

		return result;
	}
/*
	private static class ViewHolderCHSmallMedium extends
			CHWeightBlockViewHolder {
		FrameLayout[] fl = new FrameLayout[2];
		ImageView[] iv = new ImageView[2];
		TextView[] tv_name = new TextView[2];
		TextView[] tv_price = new TextView[2];
		// TextView[] tv_weight = new TextView[2];
		TextView[] tv_desc = new TextView[2];
		Context mContext;

		private ViewHolderCHSmallMedium(Context context, View view) {
			super(context, view);
			JSLog.d(TAG, "ViewHolderCMTwoSmall");
			mContext = context;
			fl[0] = (FrameLayout) view.findViewById(R.id.fl_0);
			fl[0].setBackgroundColor(Color.RED);
			fl[1] = (FrameLayout) view.findViewById(R.id.fl_1);
			fl[1].setBackgroundColor(Color.BLUE);

			for (int i = 0; i < 2; i++) {
				iv[i] = (ImageView) fl[i].findViewById(R.id.iv);
				tv_name[i] = (TextView) fl[i].findViewById(R.id.tv_name);
				tv_name[i].setBackgroundColor(Color.RED);
				tv_price[i] = (TextView) fl[i].findViewById(R.id.tv_price);
				// tv_weight[i] = (TextView) fl[i].findViewById(R.id.tv_weight);
				tv_desc[i] = (TextView) fl[i].findViewById(R.id.tv_desc);
			}

		}

		@Override
		int getType() {
			return WeightBlockType.TYPE_SMALL_MEDIUM.ordinal();
		}

		@Override
		void populate(WeightBlock wb) {
			JSLog.d(TAG, "populate " + fl.length);
			ArrayList<WeightItem> list = wb.getItems();
			for (int i = 0; i < 2; i++) {
				// fm [i].setTag(list.get(i));
				fl[i].setOnClickListener(new TileOnClickListener(mContext));

				iv[i].setBackgroundResource(R.drawable.ic_launcher);
				tv_name[i].setText("name");
				tv_price[i].setText("price");
				// tv_weight[i].setText("weight");
				tv_desc[i].setText("desc");
			}
		}

		static View createView(Context context, ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			JSLog.d(TAG, "createView");
			View result = layoutInflater.inflate(
					R.layout.list_item_product_block_ch_0, parent, false);
			result.setTag(new ViewHolderCHSmallMedium(context, result));
			return result;
		}

	}

	private static class ViewHolderCHBigTwoSmall extends
			CHWeightBlockViewHolder {
		FrameLayout[] fl = new FrameLayout[3];
		ImageView[] iv = new ImageView[3];
		TextView[] tv_name = new TextView[3];
		TextView[] tv_price = new TextView[3];
		// TextView[] tv_weight = new TextView[3];
		TextView[] tv_desc = new TextView[3];
		Context mContext;

		private ViewHolderCHBigTwoSmall(Context context, View view) {
			super(context, view);
			JSLog.d(TAG, "ViewHolderCHBigTwoSmall");
			mContext = context;
			fl[0] = (FrameLayout) view.findViewById(R.id.fl_0);
			fl[0].setBackgroundColor(Color.RED);
			fl[1] = (FrameLayout) view.findViewById(R.id.fl_1);
			fl[1].setBackgroundColor(Color.BLUE);

			fl[2] = (FrameLayout) view.findViewById(R.id.fl_2);
			fl[2].setBackgroundColor(Color.BLUE);
			for (int i = 0; i < 3; i++) {
				iv[i] = (ImageView) fl[i].findViewById(R.id.iv);
				tv_name[i] = (TextView) fl[i].findViewById(R.id.tv_name);
				tv_name[i].setBackgroundColor(Color.RED);
				tv_price[i] = (TextView) fl[i].findViewById(R.id.tv_price);
				// tv_weight[i] = (TextView) fl[i].findViewById(R.id.tv_weight);
				tv_desc[i] = (TextView) fl[i].findViewById(R.id.tv_desc);
			}

		}

		@Override
		int getType() {
			return WeightBlockType.TYPE_BIG_TWO_SMALL.ordinal();
		}

		@Override
		void populate(WeightBlock wb) {
			JSLog.d(TAG, "populate " + fl.length);
			ArrayList<WeightItem> list = wb.getItems();
			for (int i = 0; i < 2; i++) {
				// fm [i].setTag(list.get(i));
				fl[i].setOnClickListener(new TileOnClickListener(mContext));

				iv[i].setBackgroundResource(R.drawable.ic_launcher);
				tv_name[i].setText("name");
				tv_price[i].setText("price");
				// tv_weight[i].setText("weight");
				tv_desc[i].setText("desc");
			}
		}

		static View createView(Context context, ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			JSLog.d(TAG, "createView");
			View result = layoutInflater.inflate(
					R.layout.list_item_product_block_ch_1, parent, false);
			result.setTag(new ViewHolderCHBigTwoSmall(context, result));
			return result;
		}

	}*/
}
