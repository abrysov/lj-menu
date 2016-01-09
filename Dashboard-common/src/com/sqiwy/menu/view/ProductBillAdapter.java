package com.sqiwy.menu.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fosslabs.android.view.JSAdapterView;
import com.fosslabs.android.view.JSGallery;
import com.fosslabs.android.view.JSNoFlingGallery;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.provider.OrderManager;
import com.sqiwy.menu.util.CommonUtils;

import java.util.List;

/**
 * Created by abrysov
 */

public class ProductBillAdapter extends BaseAdapter {
	private final Context mContext;
	private final OnBillChangeListener mListener;
    private final LayoutInflater mLayoutInflater;
    private OrderManager.Order mOrder = new OrderManager.Order();
    private boolean mClearOrder;
    private int mTextColor, mSelectedTextColor;

	public ProductBillAdapter(Context context, OnBillChangeListener listener) {
		mContext = context;
		mTextColor=context.getResources().getColor(R.color.cm_fragment_bill_item_count_textcolor);
		mSelectedTextColor=context.getResources().getColor(R.color.cm_fragment_bill_item_count_selectedtextcolor);
		mListener = listener;
        mLayoutInflater = LayoutInflater.from(mContext);
	}

    public void setOrder(OrderManager.Order order) {
        mOrder = order;
        notifyDataSetChanged();
    }

    public void clearOrder() {
        mClearOrder = true;
        notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return mOrder.getProductList().size();
	}

	@Override
	public Object getItem(int position) {
		return mOrder.getProductList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
    @SuppressWarnings("ConstantConditions")
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int price;
        String priceString;
        
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_product_bill, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderManager.OrderProduct op = mOrder.getProductList().get(position);
		holder.nameTextView.setText(op.getProductName());
		holder.priceTextView.setText(op.getFullPrice());
		holder.countGallery.setSelection(op.getCount());
        holder.countGallery.setTag(position);
        holder.countGallery.setOnItemSelectedListener(new JSGallery.OnItemSelectedListener() {
			@Override
			public void onItemSelected(JSAdapterView<?> parent, View view,
					int position, long id) {
				parent.postDelayed(new SetupJSGalleryRunnable(parent,position), 100);
			}

			@Override
			public void onNothingSelected(JSAdapterView<?> parent) {}
        });
        
        if (op.getCount()>0) {
        	holder.enablePriceText();
        } else {
        	holder.enableRemoveButton();
        }
        
        holder.weight.setText(op.getWeight());
        
        setModifiers(holder.modifiersTextView, op.getModifierList());
        holder.removeProductView.setOnClickListener(new OnRemoveProductClickListener(position));

        if (mClearOrder) {
            final View view = convertView;
            Animator animator = ObjectAnimator.ofFloat(view, "translationY", parent.getHeight());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setTranslationY(0);
                    if (mClearOrder) {
                        mListener.onOrderCleared();
                        mClearOrder = false;
                    }
                }
            });
            animator.start();
        }

		return convertView;
	}
	
	private class SetupJSGalleryRunnable implements Runnable {
		
		private JSAdapterView<?> mParent;
		private int mCurrentPos;
		
		public SetupJSGalleryRunnable(JSAdapterView<?> parent, int currentPos) {
			mParent=parent; mCurrentPos=currentPos;
		}
		
		public void run() {
			int fp=mParent.getFirstVisiblePosition(), lp=mParent.getLastVisiblePosition(), i;
			View cv;
			TypefaceTextView tcv;
			
			for (i=fp; i<=lp; i++) {
				cv=mParent.getChildAt(i-fp);
				if (cv instanceof TextView) {
					tcv=(TypefaceTextView)cv;
					if (i==mCurrentPos) {
						tcv.setTextColor(mSelectedTextColor);
					} else {
						tcv.setTextColor(mTextColor);
					}
				}
			}
		}
	};
	
    private static void setModifiers(TypefaceTextView modifiersTextView,
                                     List<OrderManager.OrderProductModifier> modifiers) {
        if (!modifiers.isEmpty()) {
            StringBuilder modifiersText = new StringBuilder();
            for (OrderManager.OrderProductModifier opm : modifiers) {
                if (modifiersText.length() != 0) {
                    modifiersText.append("; ");
                }
                modifiersText.append(opm.getName());
            }
            modifiersTextView.setText(modifiersText);
            modifiersTextView.setVisibility(View.VISIBLE);
        } else {
            modifiersTextView.setVisibility(View.GONE);
        }
    }

    public interface OnBillChangeListener {
        public void setCount(int pos, int count);
        public void onOrderCleared();
        public void onClickRemoveProductButton(int pos);
    }
    
    private class OnRemoveProductClickListener implements View.OnClickListener {
    	private int mPos;
    	
    	public OnRemoveProductClickListener(int pos) {
    		mPos=pos;
    	}
    	
		public void onClick(View v) {
			mListener.onClickRemoveProductButton(mPos);
		}
    }

    private class ViewHolder implements IOnRemoveButtonStateChangeListener {
        public final TypefaceTextView nameTextView;
        public final TypefaceTextView priceTextView;
        public final TypefaceTextView removeProductView;
        public final TypefaceTextView modifiersTextView;
        public final TypefaceTextView weight;
        public final JSNoFlingGallery countGallery;

        public ViewHolder(View view) {
            nameTextView = (TypefaceTextView) view.findViewById(R.id.tv_product_name);
            priceTextView = (TypefaceTextView) view.findViewById(R.id.tv_product_price);
            modifiersTextView = (TypefaceTextView) view.findViewById(R.id.tv_product_modifiers);
            countGallery = (JSNoFlingGallery) view.findViewById(R.id.gallery_product_count);
            removeProductView=(TypefaceTextView)view.findViewById(R.id.remove_product_btn);
            weight = (TypefaceTextView) view.findViewById(R.id.tv_weight);
            countGallery.setAdapter(new CountAdapter(mContext));
            countGallery.setOnTouchListener(new CountOnTouchListener(this));
        }

		@Override
		public void enableRemoveButton() {
			priceTextView.setVisibility(View.INVISIBLE); 
			priceTextView.setClickable(false);
			removeProductView.setVisibility(View.VISIBLE); 
			removeProductView.setClickable(true);
		}

		@Override
		public void enablePriceText() {
			priceTextView.setVisibility(View.VISIBLE); 
			priceTextView.setClickable(true);
			removeProductView.setVisibility(View.INVISIBLE); 
			removeProductView.setOnClickListener(null);
			removeProductView.setClickable(false);
		}
    }

	public interface IOnRemoveButtonStateChangeListener {
		public void enableRemoveButton();
		public void enablePriceText();
	}

	private class CountOnTouchListener implements View.OnTouchListener {
		private IOnRemoveButtonStateChangeListener mOnRemoveButtonStateChangeListener;
		
    	public CountOnTouchListener(IOnRemoveButtonStateChangeListener callback) {
    		mOnRemoveButtonStateChangeListener=callback;
    	}
    	
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            // Change the product count after a gesture is finished.
            // Note that we use some delay to properly handle clicks and fling.
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	JSGallery jsGallery=(JSGallery)v;
                    	int curPos=jsGallery.getSelectedItemPosition();

                    	mListener.setCount((Integer)jsGallery.getTag(), curPos);
                        if (curPos==0) {
                        	mOnRemoveButtonStateChangeListener.enableRemoveButton();
                        } else {
                        	mOnRemoveButtonStateChangeListener.enablePriceText();
                        }
                        jsGallery.post(new SetupJSGalleryRunnable(jsGallery,curPos));
                    }
                }, 100);
            }
            return false;
        }
    }

	private class CountAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		private int mItemWidth, mItemHeight;

		public CountAdapter(Context context) {
			mContext = context; mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			Resources res = mContext.getResources();
			
			mItemWidth=res.getDimensionPixelSize(R.dimen.cm_fragment_bill_item_gallery_width)/3;
			mItemHeight=res.getDimensionPixelSize(R.dimen.cm_fragment_bill_item_gallery_height);
		}

        @Override
        public int getCount() {
            return OrderManager.MAX_PRODUCT_UNIT_COUNT + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
        	TypefaceTextView countTextView = (TypefaceTextView) convertView;
            if (countTextView == null) {
                countTextView=(TypefaceTextView)mInflater.inflate(R.layout.list_item_product_list_count_gallery_2, parent, false);
                countTextView.setLayoutParams(new JSGallery.LayoutParams(mItemWidth,mItemHeight));
                // countTextView.setGravity(Gravity.CENTER);
                // countTextView.setTextSize(res.getDimensionPixelSize(
                //        R.dimen.cm_fragment_bill_item_tv_size_gallery_selected));
                // countTextView.setTextColor(mTextColor);
            }
            countTextView.setText(/* position == 0 ? "X" : */ Integer.toString(position));
			return countTextView;
		}
	}
}
