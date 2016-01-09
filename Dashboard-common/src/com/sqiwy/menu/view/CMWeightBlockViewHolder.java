package com.sqiwy.menu.view;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.cm.CMProductDetailDialog;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.model.WeightBlock;
import com.sqiwy.menu.model.WeightBlockCM;
import com.sqiwy.menu.model.WeightItem;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.util.CommonUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by abrysov
 */

public abstract class CMWeightBlockViewHolder {
	public abstract int getType();

	public abstract void populate(WeightBlock wb);

	public static View createView(int type, ViewGroup parent) {
        switch (WeightBlockCM.Type.values()[type]) {
            case TYPE_MEDIUM:
                return ViewHolderCMMedium.createView(parent);
            case TYPE_TWO_SMALL:
                return ViewHolderCMTwoSmall.createView(parent);
            case TYPE_SMALL_2TEXT:
                return ViewHolderCMSmall2Text.createView(parent);
            case TYPE_1TEXT_1TEXT:
                return ViewHolderCM1Text1Text.createView(parent);
        }
        return null;
    }
	
	private static void prepareDragView(ViewGroup group, Product product) {
		View v = new View(group.getContext());
		v.setOnClickListener(new TileOnClickListener());
		v.setOnLongClickListener(new TileOnLongClickListener());
		v.setTag(product);
		group.addView(v);
	}

	private static void loadImage(ImageView imageView, String url, boolean isBig) {
        if (!TextUtils.isEmpty(url)) {
            String name = CommonUtils.extractMenuProductImageName(url);
            File resourcePath = ResourcesManager.getResourcePath(name,
                    isBig ? Category.MENU_BIG : Category.MENU_SMALL);
            Picasso.with(MenuApplication.getContext()).load(resourcePath).into(imageView);
        }
	}

    private static class ViewHolderCMMedium extends CMWeightBlockViewHolder {
        FrameLayout fl;
        ImageView iv;
        TextView tv_name;
        TextView tv_price;
        TextView tv_weight;
//        TextView tv_desc;

        private ViewHolderCMMedium(View view) {
            fl = (FrameLayout) view.findViewById(R.id.fl_0);
            iv = (ImageView) fl.findViewById(R.id.iv);
            tv_name = (TextView) fl.findViewById(R.id.tv_name);
            tv_price = (TextView) fl.findViewById(R.id.tv_price);
            tv_weight = (TextView) fl.findViewById(R.id.tv_weight);
//            tv_desc = (TextView) fl.findViewById(R.id.tv_desc);
        }

        @Override
        public int getType() {
            return WeightBlockCM.Type.TYPE_MEDIUM.ordinal();
        }

        @Override
        public void populate(WeightBlock wb) {
            List<WeightItem> list = wb.getItems();
            // fm .setTag(list.get(i));
            Product product = (Product) list.get(0);
            prepareDragView(fl, product);
            loadImage(iv, product.getImgUrl(), true);
            tv_name.setText(product.getName());
            tv_price.setText(CommonUtils.formatPrice(product.getDefaultPrice()));
            tv_weight.setText(String.valueOf(CMProductDetailDialog.getDishWeightOfFirstModifierWeight(product)));
//            tv_desc.setText(product.getDesc());
        }

        static View createView(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View result = layoutInflater.inflate(
                    R.layout.list_item_product_block_cm_medium, parent, false);
            result.setTag(new ViewHolderCMMedium(result));
            return result;
        }
    }

    private static class ViewHolderCMTwoSmall extends CMWeightBlockViewHolder {
		FrameLayout[] fl = new FrameLayout[2];
		ImageView[] iv = new ImageView[2];
		TextView[] tv_name = new TextView[2];
		TextView[] tv_price = new TextView[2];
		TextView[] tv_weight = new TextView[2];
//		TextView[] tv_desc = new TextView[2];

		private ViewHolderCMTwoSmall(View view) {
			fl[0] = (FrameLayout) view.findViewById(R.id.fl_0);
			fl[1] = (FrameLayout) view.findViewById(R.id.fl_1);
			for (int i = 0; i < 2; i++) {
				iv[i] = (ImageView) fl[i].findViewById(R.id.iv);
				tv_name[i] = (TextView) fl[i].findViewById(R.id.tv_name);
				tv_price[i] = (TextView) fl[i].findViewById(R.id.tv_price);
				tv_weight[i] = (TextView) fl[i].findViewById(R.id.tv_weight);
//				tv_desc[i] = (TextView) fl[i].findViewById(R.id.tv_desc);
			}
		}

		@Override
		public int getType() {
			return WeightBlockCM.Type.TYPE_TWO_SMALL.ordinal();
		}

		@Override
		public void populate(WeightBlock wb) {
			List<WeightItem> list = wb.getItems();
			for (int i = 0; i < 2; i++) {
                if (i < list.size()) {
                    Product product = (Product) list.get(i);
                    prepareDragView(fl[i], product);
                    loadImage(iv[i], product.getImgUrl(), false);
                    tv_name[i].setText(product.getName());
                    tv_price[i].setText(CommonUtils.formatPrice(product.getDefaultPrice()));
                    tv_weight[i].setText(String.valueOf(CMProductDetailDialog.getDishWeightOfFirstModifierWeight(product)));
//                    tv_desc[i].setText(product.getDesc());
                    fl[i].setVisibility(View.VISIBLE);
                } else {
                    fl[i].setVisibility(View.INVISIBLE);
                }
            }
		}

		static View createView(ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			View result = layoutInflater.inflate(
					R.layout.list_item_product_block_cm_2small, parent, false);
			result.setTag(new ViewHolderCMTwoSmall(result));
			return result;
		}
	}

	private static class ViewHolderCMSmall2Text extends CMWeightBlockViewHolder {
		FrameLayout[] fl = new FrameLayout[3];
		ImageView iv;
		TextView[] tv_name = new TextView[3];
		TextView[] tv_price = new TextView[3];
		TextView[] tv_weight = new TextView[3];
//		TextView[] tv_desc = new TextView[3];

		private ViewHolderCMSmall2Text(View view) {
            fl[0] = (FrameLayout) view.findViewById(R.id.fl_10);
			fl[1] = (FrameLayout) view.findViewById(R.id.fl_00);
			fl[2] = (FrameLayout) view.findViewById(R.id.fl_01);
			for (int i = 0; i < 3; i++) {
				tv_name[i] = (TextView) fl[i].findViewById(R.id.tv_name);
				tv_price[i] = (TextView) fl[i].findViewById(R.id.tv_price);
				tv_weight[i] = (TextView) fl[i].findViewById(R.id.tv_weight);
//				tv_desc[i] = (TextView) fl[i].findViewById(R.id.tv_desc);
			}
			iv = (ImageView) fl[0].findViewById(R.id.iv);
		}

		@Override
		public int getType() {
			return WeightBlockCM.Type.TYPE_SMALL_2TEXT.ordinal();
		}

		@Override
		public void populate(WeightBlock wb) {
			List<WeightItem> list = wb.getItems();
			for (int i = 0; i < 3; i++) {
                if (i < list.size()) {
                    Product product = (Product) list.get(i);
                    prepareDragView(fl[i], product);

                    tv_name[i].setText(product.getName());
                    tv_price[i].setText(CommonUtils.formatPrice(product.getDefaultPrice()));
                    tv_weight[i].setText(String.valueOf(CMProductDetailDialog.getDishWeightOfFirstModifierWeight(product)));
//                    tv_desc[i].setText(product.getDesc());

                    if (0 == i) {
                        loadImage(iv, product.getImgUrl(), false);
                    }
                    fl[i].setVisibility(View.VISIBLE);
                } else {
                    fl[i].setVisibility(View.INVISIBLE);
                }
            }
		}

		static View createView(ViewGroup parent) {
			LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
			View result = layoutInflater.inflate(
					R.layout.list_item_product_block_cm_small_2text, parent, false);
			result.setTag(new ViewHolderCMSmall2Text(result));
			return result;
		}
	}

    private static class ViewHolderCM1Text1Text extends CMWeightBlockViewHolder {
        FrameLayout[] fl = new FrameLayout[2];
        TextView[] tv_name = new TextView[2];
        TextView[] tv_price = new TextView[2];
        TextView[] tv_weight = new TextView[2];
//        TextView[] tv_desc = new TextView[2];

        private ViewHolderCM1Text1Text(View view) {
            fl[0] = (FrameLayout) view.findViewById(R.id.fl_00);
            fl[1] = (FrameLayout) view.findViewById(R.id.fl_10);

            for (int i = 0; i < 2; i++) {
                tv_name[i] = (TextView) fl[i].findViewById(R.id.tv_name);
                tv_price[i] = (TextView) fl[i].findViewById(R.id.tv_price);
                tv_weight[i] = (TextView) fl[i].findViewById(R.id.tv_weight);
//                tv_desc[i] = (TextView) fl[i].findViewById(R.id.tv_desc);
            }
        }

        @Override
        public int getType() {
            return WeightBlockCM.Type.TYPE_1TEXT_1TEXT.ordinal();
        }

        @Override
        public void populate(WeightBlock wb) {
            List<WeightItem> list = wb.getItems();
            for (int i = 0; i < 2; i++) {
                if (i < list.size()) {
                    Product product = (Product) list.get(i);
                    prepareDragView(fl[i], product);

                    tv_name[i].setText(product.getName());
                    tv_price[i].setText(CommonUtils.formatPrice(product.getDefaultPrice()));
                    tv_weight[i].setText(String.valueOf(CMProductDetailDialog.getDishWeightOfFirstModifierWeight(product)));
//                    tv_desc[i].setText(product.getDesc());
                    fl[i].setVisibility(View.VISIBLE);
                } else {
                    fl[i].setVisibility(View.INVISIBLE);
                }
            }
        }

        static View createView(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View result = layoutInflater.inflate(
                    R.layout.list_item_product_block_cm_1text_1text, parent, false);
            result.setTag(new ViewHolderCM1Text1Text(result));
            return result;
        }
    }
}
