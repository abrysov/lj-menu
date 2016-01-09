/*
 * Copyright 2013 Niek Haarman
 *
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

import com.haarman.supertooltips.ToolTipView.Style;

import android.view.View;

public class ToolTip {

    public static final int ANIMATIONTYPE_FROMMASTERVIEW = 101;
    public static final int ANIMATIONTYPE_FROMTOP = 102;
    //
    private CharSequence text;
    private int textResId;
    private int colorTopPointer;
    private int colorBottomPointer;
    private int colorBackground;
    private View contentView;
    private int animationType;
    private boolean shadow;
    private Style mStyle;
    private boolean mIsMarginsSet = false;
    private int mMarginLeft = 0;
    private int mMarginTop = 0;
    private int mMarginRight = 0;
    private int mMarginBottom = 0;
    private boolean mIsRemoveByClick = false;

    /**
     * Creates a new ToolTip without any values.
     */
    public ToolTip() {
        text = null;
        textResId = 0;
        colorTopPointer = 0;
        colorBottomPointer = 0;
        colorBackground = 0;
        contentView = null;
        animationType = ANIMATIONTYPE_FROMMASTERVIEW;
        mStyle = Style.ROUNDED;
    }

    /**
     * Set the text to show. Has no effect when a content View is set using setContentView().
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withText(CharSequence text) {
        this.text = text;
        this.textResId = 0;
        return this;
    }

    /**
     * Set the text resource id to show. Has no effect when a content View is set using setContentView().
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withText(int resId) {
        this.textResId = resId;
        this.text = null;
        return this;
    }

    /**
     * Set the color of the ToolTop Top pointer.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withColorTopPointer(int color) {
        this.colorTopPointer = color;
        return this;
    }

    /**
     * Set the color of the ToolTop Bottom Pointer.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withColorBottomPointer(int color) {
        this.colorBottomPointer = color;
        return this;
    }

    /**
     * Set the color of the ToolTop Bottom Pointer.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withColorBackground(int color) {
        this.colorBackground = color;
        return this;
    }



    /**
     * Set a custom content View for the ToolTip. This will cause any text that has been set to be ignored.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withContentView(View view) {
        this.contentView = view;
        return this;
    }

    /**
     * Set the animation type for the ToolTip. One of ANIMATIONTYPE_FROMMASTERVIEW and ANIMATIONTYPE_FROMTOP. Default ANIMATIONTYPE_FROMMASTERVIEW.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withAnimationType(int animationType) {
        this.animationType = animationType;
        return this;
    }

    /**
     * Set whether to show a shadow below the ToolTip.
     *
     * @return this ToolTip to build upon.
     */
    public ToolTip withShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * 
     * @param style
     * @return
     */
    public ToolTip withStyle(Style style) {
        mStyle = style;
        return this;
    }
    
    public ToolTip withMargins(int left, int top, int right, int bottom) {
        mIsMarginsSet = true;
        mMarginLeft = left;
        mMarginTop = top;
        mMarginRight = right;
        mMarginBottom = bottom;
        return this;
    }
    
    public ToolTip removeByClick(boolean remove) {
    	mIsRemoveByClick = remove;
        return this;
    }
    
    public CharSequence getText() {
        return text;
    }

    public int getTextResId() {
        return textResId;
    }

    public int getColorTopPointer() {
        return colorTopPointer;
    }
    public int getColorBottomPointer() {
        return colorBottomPointer;
    }
    public int getColorBackground() {
        return colorBackground;
    }

    public View getContentView() {
        return contentView;
    }

    public int getAnimationType() {
        return animationType;
    }

    public boolean getShadow() {
        return shadow;
    }
    
    /**
     * 
     * @return
     */
    public Style getStyle() {
    	
    	return mStyle;
    }
    
    /**
     * 
     * @return
     */
    public boolean isMarginsSet() {
    	
        return mIsMarginsSet;
    }
    
    public int getMarginLeft() {
    	
    	return mMarginLeft;
    }
    
    public int getMarginTop() {
    	
    	return mMarginTop;
    }
    
    public int getMarginRight() {
    	
    	return mMarginRight;
    }
    
    public int getMarginBottom() {
    	
    	return mMarginBottom;
    }
    
    public boolean getIsRemoveByClick() {
    	
    	return mIsRemoveByClick;
    }
}