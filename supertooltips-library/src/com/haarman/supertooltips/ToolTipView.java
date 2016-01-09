/*
 * Copyright 2013 Niek Haarman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haarman.supertooltips;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * A ViewGroup to visualize ToolTips. Use ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {

	public enum Style {
	
		ROUNDED(R.drawable.tooltip_top_frame, R.drawable.tooltip_bottom_frame),
		RECTANGULAR(R.drawable.tooltip_rectangular_top_frame, R.drawable.tooltip_rectangular_bottom_frame);
		
		private int mDrawableTopId = 0xFF000000;
		private int mDrawableBottomId = 0xFFCE3A4F;
		
		private Style(int drawableTopId, int drawableBottomId) {
			
			mDrawableTopId = drawableTopId;
			mDrawableBottomId = drawableBottomId;
		}
		
		int getTopFrameDrawable() {
			
			return mDrawableTopId;
		}
		
		int getBottomFrameDrawable() {
			
			return mDrawableBottomId;
		}		
	}
	
	/**
	 * variables
	 */
    private ImageView mTopPointerView;
    private View mTopFrame;
    private ViewGroup mContentHolder;
    private TextView mToolTipTV;
    private View mBottomFrame;
    private ImageView mBottomPointerView;
    private View mShadowView;
    private ToolTip mToolTip;
    private View mView = null;
    private Point mTargetPoint = new Point(0, 0);
    private boolean mDimensionsKnown;
    private int mRelativeMasterViewY;
    private int mRelativeMasterViewX;
    private int mWidth;
    private int mColorTopPointer = 0xFF000000;
    private int mColorBottomPointer = 0xFFCE3A4F;
    private int mColorBackground = 0xFF000000;
    private boolean mIsRemoveByClick = false;
    private OnToolTipViewClickedListener mListener;

    /**
     * 
     * @param context
     */
    public ToolTipView(Context context) {
        super(context);
        init();
    }

    /**
     * 
     */
    private void init() {
    	
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.tooltip, this, true);

        mTopPointerView = (ImageView) findViewById(R.id.tooltip_pointer_up);
        mTopFrame = findViewById(R.id.tooltip_topframe);
        mContentHolder = (ViewGroup) findViewById(R.id.tooltip_contentholder);
        mToolTipTV = (TextView) findViewById(R.id.tooltip_contenttv);
        mBottomFrame = findViewById(R.id.tooltip_bottomframe);
        mBottomPointerView = (ImageView) findViewById(R.id.tooltip_pointer_down);
        mShadowView = findViewById(R.id.tooltip_shadow);

        setOnClickListener(this);
        getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     * 
     */
    @Override
    public boolean onPreDraw() {
    	
        getViewTreeObserver().removeOnPreDrawListener(this);
        mDimensionsKnown = true;

        mWidth = mContentHolder.getWidth();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = mWidth;
        setLayoutParams(layoutParams);

        if (mToolTip != null) {
            applyToolTipPosition();
        }
        return true;
    }

    /**
     * 
     * @param toolTip
     * @param view
     */
    public void setToolTip(ToolTip toolTip, View view) {
    	
    	mView = view;
    	
    	setupTooltip(toolTip);

        if (mDimensionsKnown) {
        	
            applyToolTipPosition();
        }
    }
    
    /**
     * 
     * @param toolTip
     * @param x
     * @param y
     */
    public void setToolTip(ToolTip toolTip, int x, int y) {
    	
    	setupTooltip(toolTip);
    	
    	mTargetPoint.set(x, y);    	

        if (mDimensionsKnown) {
        	
            applyToolTipPosition();
        }
    }

    /**
     * 
     * @param toolTip
     */
	private void setupTooltip(ToolTip toolTip) {

		mToolTip = toolTip;

		if (mToolTip.getText() != null) {

			mToolTipTV.setText(mToolTip.getText());
		} 
		else 
		if (mToolTip.getTextResId() != 0) {
			
			mToolTipTV.setText(mToolTip.getTextResId());
		}

		if (mToolTip.getColorTopPointer() != 0 && mToolTip.getColorBottomPointer() != 0 && mToolTip.getColorBackground() != 0) {
			
			setColor(mToolTip.getColorTopPointer(), mToolTip.getColorBottomPointer(), mToolTip.getColorBackground());
		}

		if (mToolTip.getContentView() != null) {
			
			setContentView(mToolTip.getContentView());
		}

		if (!mToolTip.getShadow()) {
			
			mShadowView.setVisibility(View.GONE);
		}

		// apply style
		setStyle(toolTip.getStyle());

		// apply margins
		if (mToolTip.isMarginsSet()) {

			setContentMargins(mToolTip.getMarginLeft(),
					mToolTip.getMarginTop(), mToolTip.getMarginRight(),
					mToolTip.getMarginBottom());
		}
		
		// remove by click flag
		mIsRemoveByClick = mToolTip.getIsRemoveByClick();
	}

	/**
	 * 
	 */

    private void applyToolTipPosition() {
    	
    	final int[] masterViewScreenPosition = new int[2];
        final int[] parentViewScreenPosition = new int[2];
        final Rect viewDisplayFrame = new Rect(); // includes decorations (e.g. status bar)
        int masterViewWidth;
        int masterViewHeight;
        float toolTipViewAboveY;
        float toolTipViewBelowY;
        float toolTipViewY;
        float toolTipViewX;
        int viewWidth = 0;
        int viewHeight = 0;
        
        ((View)getParent()).getLocationOnScreen(parentViewScreenPosition);
        //getWindowVisibleDisplayFrame(viewDisplayFrame);
        viewDisplayFrame.set(0, 0, ((View)getParent()).getWidth(), ((View)getParent()).getHeight());
        
    	if(null != mView) {
    		
    		viewWidth = mView.getWidth();
    		viewHeight = mView.getHeight();
    		
            mView.getLocationOnScreen(masterViewScreenPosition);
            //mView.getWindowVisibleDisplayFrame(viewDisplayFrame);
            
            masterViewWidth = mView.getWidth();
            masterViewHeight = mView.getHeight();
            
            mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
            mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
            
            final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

            toolTipViewAboveY = mRelativeMasterViewY - getHeight();
            toolTipViewBelowY = mRelativeMasterViewY + masterViewHeight;
            
            toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
            
            if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            	
                toolTipViewX = viewDisplayFrame.right - mWidth;
            }

            setX(toolTipViewX);
            setPointerCenterX(relativeMasterViewCenterX);
    	}
    	else {
    		
    		viewWidth = 0;
    		viewHeight = 0;
    		
    		mRelativeMasterViewY = mTargetPoint.y;
    		mRelativeMasterViewX = mTargetPoint.x;
            toolTipViewAboveY = mTargetPoint.y - getHeight();
            toolTipViewBelowY = mTargetPoint.y;
            toolTipViewY = 0.0f;
            
            toolTipViewX = Math.max(0, mTargetPoint.x - mWidth / 2);
            
            if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            	
                toolTipViewX = viewDisplayFrame.right - mWidth;
            }
            
            setX(toolTipViewX);
            setPointerCenterX(mTargetPoint.x);
    	}

        final boolean showBelow = toolTipViewAboveY < 0;

        if (Build.VERSION.SDK_INT < 11) {
        	
            ViewHelper.setAlpha(mTopPointerView, showBelow ? 1 : 0);
            ViewHelper.setAlpha(mBottomPointerView, showBelow ? 0 : 1);
        } 
        else {
        	
            mTopPointerView.setVisibility(showBelow ? VISIBLE : GONE);
            mBottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
        }

        if (showBelow) {
        	
            toolTipViewY = toolTipViewBelowY;
        } 
        else {
        	
            toolTipViewY = toolTipViewAboveY;
        }

        List<Animator> animators = new ArrayList<Animator>();

        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMMASTERVIEW) {
        	
            animators.add(ObjectAnimator.ofFloat(this, "translationY", mRelativeMasterViewY + viewHeight / 2 - getHeight() / 2, toolTipViewY));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", mRelativeMasterViewX + viewWidth / 2 - mWidth / 2, toolTipViewX));
        } 
        else 
        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMTOP) {
        	
            animators.add(ObjectAnimator.ofFloat(this, "translationY", 0, toolTipViewY));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 0, 1));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 0, 1));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        if (Build.VERSION.SDK_INT < 11) {
            final float fToolTipViewX = toolTipViewX;
            final float fToolTipViewY = toolTipViewY;
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                    params.leftMargin = (int) fToolTipViewX;
                    params.topMargin = (int) fToolTipViewY;
                    setX(0);
                    setY(0);
                    setLayoutParams(params);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        animatorSet.start();
    }
/*
    private void applyToolTipPosition() {
    	
        final int[] masterViewScreenPosition = new int[2];
        final int[] parentViewScreenPosition = new int[2];

        final Rect viewDisplayFrame = new Rect(); // includes decorations (e.g. status bar)
        mView.getLocationOnScreen(masterViewScreenPosition);
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);
        ((View) getParent()).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        float toolTipViewAboveY = mRelativeMasterViewY - getHeight();
        float toolTipViewBelowY = mRelativeMasterViewY + masterViewHeight;
        float toolTipViewY;

        float toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - mWidth;
        }

        setX(toolTipViewX);
        setPointerCenterX(relativeMasterViewCenterX);

        final boolean showBelow = toolTipViewAboveY < 0;

        if (Build.VERSION.SDK_INT < 11) {
            ViewHelper.setAlpha(mTopPointerView, showBelow ? 1 : 0);
            ViewHelper.setAlpha(mBottomPointerView, showBelow ? 0 : 1);
        } else {
            mTopPointerView.setVisibility(showBelow ? VISIBLE : GONE);
            mBottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
        }

        if (showBelow) {
            toolTipViewY = toolTipViewBelowY;
        } else {
            toolTipViewY = toolTipViewAboveY;
        }

        List<Animator> animators = new ArrayList<Animator>();

        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMMASTERVIEW) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", mRelativeMasterViewY + mView.getHeight() / 2 - getHeight() / 2, toolTipViewY));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", mRelativeMasterViewX + mView.getWidth() / 2 - mWidth / 2, toolTipViewX));
        } else if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMTOP) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", 0, toolTipViewY));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 0, 1));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 0, 1));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        if (Build.VERSION.SDK_INT < 11) {
            final float fToolTipViewX = toolTipViewX;
            final float fToolTipViewY = toolTipViewY;
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                    params.leftMargin = (int) fToolTipViewX;
                    params.topMargin = (int) fToolTipViewY;
                    setX(0);
                    setY(0);
                    setLayoutParams(params);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        animatorSet.start();
    }
*/

    /**
     * 
     * @param pointerCenterX
     */
    public void setPointerCenterX(int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerView.getMeasuredWidth(), mBottomPointerView.getMeasuredWidth());

        ViewHelper.setX(mTopPointerView, pointerCenterX - pointerWidth / 2 - getX());
        ViewHelper.setX(mBottomPointerView, pointerCenterX - pointerWidth / 2 - getX());
    }

    /**
     * 
     * @param listener
     */
    public void setOnToolTipViewClickedListener(OnToolTipViewClickedListener listener) {
        mListener = listener;
    }

    /**
     * 
     * @param
     */
    public void setColor(int colorTop, int colorBottom, int colorBackground) {
    	mColorTopPointer = colorTop;
        mColorBottomPointer = colorBottom;
        mColorBackground = colorBackground;

        mTopPointerView.setColorFilter(mColorTopPointer, PorterDuff.Mode.MULTIPLY);
        mTopFrame.getBackground().setColorFilter(mColorTopPointer, PorterDuff.Mode.MULTIPLY);
        mBottomPointerView.setColorFilter(mColorBottomPointer, PorterDuff.Mode.MULTIPLY);
        mBottomFrame.getBackground().setColorFilter(mColorBottomPointer, PorterDuff.Mode.MULTIPLY);
        mContentHolder.setBackgroundColor(mColorBackground);
    }

    /**
     * 
     * @param view
     */
    private void setContentView(View view) {
    	
        mContentHolder.removeAllViews();
        mContentHolder.addView(view);
    }

    /**
     * 
     */
    public void remove() {
    	
        int viewWidth = 0;
        int viewHeight = 0;
        
        if(null != mView) {

        	viewWidth = mView.getWidth();
        	viewHeight = mView.getHeight();
        }

        if (Build.VERSION.SDK_INT < 11) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            setX(params.leftMargin);
            setY(params.topMargin);
            params.leftMargin = 0;
            params.topMargin = 0;
            setLayoutParams(params);
        }

        List<Animator> animators = new ArrayList<Animator>();
        if (mToolTip.getAnimationType() == ToolTip.ANIMATIONTYPE_FROMMASTERVIEW) {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", getY(), mRelativeMasterViewY + viewHeight / 2 - getHeight() / 2));
            animators.add(ObjectAnimator.ofFloat(this, "translationX", getX(), mRelativeMasterViewX + viewWidth / 2 - mWidth / 2));
        } else {
            animators.add(ObjectAnimator.ofFloat(this, "translationY", getY(), 0));
        }

        animators.add(ObjectAnimator.ofFloat(this, "scaleX", 1, 0));
        animators.add(ObjectAnimator.ofFloat(this, "scaleY", 1, 0));

        animators.add(ObjectAnimator.ofFloat(this, "alpha", 1, 0));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(ToolTipView.this);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animatorSet.start();
    }

    /**
     * 
     */
    @Override
    public void onClick(View view) {
    	
    	if(mIsRemoveByClick) {
        
    		remove();
    	}

        if (mListener != null) {
        	
            mListener.onToolTipViewClicked(this);
        }
    }

    /**
     * Convenience method for getting X.
     */
    @Override
    public float getX() {
    	
        if (Build.VERSION.SDK_INT >= 11) {
        	
            return super.getX();
        } 
        else {
        	
            return ViewHelper.getX(this);
        }
    }

    /**
     * Convenience method for setting X.
     */
    @Override
    public void setX(float x) {
    	
        if (Build.VERSION.SDK_INT >= 11) {
        	
            super.setX(x);
        } 
        else {
        	
            ViewHelper.setX(this, x);
        }
    }

    /**
     * Convenience method for getting Y.
     */
    @Override
    public float getY() {
        if (Build.VERSION.SDK_INT >= 11) {
            return super.getY();
        } else {
            return ViewHelper.getY(this);
        }
    }

    /**
     * Convenience method for setting Y.
     */
    @Override
    public void setY(float y) {
        if (Build.VERSION.SDK_INT >= 11) {
            super.setY(y);
        } else {
            ViewHelper.setY(this, y);
        }
    }

    public interface OnToolTipViewClickedListener {
        public void onToolTipViewClicked(ToolTipView toolTipView);
    }
    
    /**
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
	public void setContentMargins(int left, int top, int right, int bottom) {
    	
    	ViewGroup.LayoutParams lp;
    	
    	// top margin
    	lp = mTopFrame.getLayoutParams();
    	lp.height = top;
    	mTopFrame.setLayoutParams(lp);
    	
    	// bottom margin
    	lp = mBottomFrame.getLayoutParams();
    	lp.height = bottom;
    	mBottomFrame.setLayoutParams(lp);
    	
    	// left & right margin
    	mContentHolder.setPadding(left, getPaddingTop(), right, getPaddingBottom());
    }
    
    /**
     * 
     * @param style
     */
    public void setStyle(Style style) {
    	
    	mTopFrame.setBackgroundResource(style.getTopFrameDrawable());
    	mBottomFrame.setBackgroundResource(style.getBottomFrameDrawable());
    	setColor(mColorTopPointer, mColorBottomPointer, mColorBackground);
    }
}
