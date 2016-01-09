package com.sqiwy.menu.chat;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.GridPickerDialog.Builder.OnBoundViewPositionListener;

/**
 * Created by abrysov
 */

/**
 * GridPickerDialog is the dialog that 
 * shows items in the GridView and allows to user 
 * to pick one item at the time.
 *
 */
public class GridPickerDialog extends DialogFragment implements OnItemClickListener{
				
	
	
	/**
	 * GridView which displays the 
	 */
	private GridView gridView;
	
	/**
	 * Dialog root view
	 */
	private RelativeLayout root;
	
	
	private FrameLayout wrapper;
	

	private Builder builder;

	/**
	 * Pass builder
	 * @param builder
	 */
	private void set(Builder builder){
		this.builder = builder;
	}
	
	
	
	
	/*
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		this.root = new RelativeLayout(getActivity());
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		lp.bottomMargin = 0;
		lp.topMargin = 0;
		
		this.root.setLayoutParams(lp);
		this.root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GridPickerDialog.this.dismiss();
			}
		});
		
//		this.root.setBackgroundColor(Color.parseColor("#44000000"));
		
		this.wrapper = new FrameLayout(getActivity());
		this.wrapper.setBackgroundColor(Color.parseColor("#e5e5e5"));
		int[] xy = this.builder.getLocationInWindow();
		this.wrapper.setTranslationX(xy[0]);
		this.wrapper.setTranslationY(xy[1]);
		
		
		LayoutParams fllp = new LayoutParams(450, LayoutParams.WRAP_CONTENT);
		
		this.wrapper.setLayoutParams(fllp);
		
		this.gridView = new GridView(getActivity());
		this.gridView.setNumColumns(8);
		this.gridView.setOnItemClickListener(this);
		this.gridView.setVerticalSpacing(2);
		this.gridView.setHorizontalSpacing(2);
		this.gridView.setSelector(android.R.color.transparent);
		
		this.gridView.setOnGenericMotionListener(new OnGenericMotionListener() {
			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
					
					break;
					
				case MotionEvent.ACTION_MOVE:
					Log.e("GRID DRAG", String.valueOf(event.getX()) + "" + String.valueOf(event.getY()));
					break;
				
				case MotionEvent.ACTION_UP:
					
					break;
				case MotionEvent.ACTION_HOVER_MOVE:
					Log.e("GRID DRAG", String.valueOf(event.getX()) + "" + String.valueOf(event.getY()));
					break;
				default:
					Log.e("GRID DRAG", String.valueOf(event.getX()) + "" + String.valueOf(event.getY()));
					break;
				}
				return false;
			}
		});
		
		this.wrapper.addView(gridView);
		
		this.root.addView(this.wrapper);
		
		// TODO: set adapter
		if(this.builder.getAdapter() != null){
			this.gridView.setAdapter(this.builder.getAdapter());
		}
		
		// TODO: position the grid view
		
		this.builder.setOnBoundViewPositionListener(positionListener);
		
		
		return root; 
	}
	
	
	
	/*
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if(this.listener != null){
			this.listener.onItemSelected(arg2);  
		}
		
		this.dismiss();
		
	}
	
	
	
	/*
	 * Listener handling
	 */
	
	private OnGridPickerDialogEvent listener;

	public interface OnGridPickerDialogEvent{
		void onItemSelected(int index);
	}
		
	public void addOnGridPickerDialogEventListener(OnGridPickerDialogEvent listener){
		this.listener = listener;
	}
	
	
	public void removeOnGridPickerDialogEventListener(){
		this.listener = null;
	}

	
	
	/*
	 * Bound view position listening
	 */
	private  OnBoundViewPositionListener positionListener = new OnBoundViewPositionListener() {
		@Override
		public void updatePosition() {
			int[] xy = GridPickerDialog.this.builder.getLocationInWindow();
			GridPickerDialog.this.wrapper.setTranslationX(xy[0]);
			GridPickerDialog.this.wrapper.setTranslationY(xy[1]);
		}
	}; 
	
	

	/*
	 * Dialog Builder
	 */
	public static class Builder implements OnLayoutChangeListener{
		
		
		private BaseAdapter adapter;
		private int width;
		private int height;
		private PrevSelection prevSelection;
		private View button;
		
		/**
		 * Path the adapter to GridPicherDialog.
		 * This adapter will be used to inflate 
		 * internal GridView.
		 * @param adapter
		 * @return
		 */
		public Builder setAdapter(BaseAdapter adapter){
			this.adapter = adapter;
			return this;
		}
		
		
		public BaseAdapter getAdapter(){
			return this.adapter;
		}
		
		
		/**
		 * Pass the ImageButton that should be bounded to this
		 * GridPickerDialog. While changes is happens in GridPickerDialog
		 * those changes can be reflected on ImageButton
		 * @param view
		 * @return
		 */
		public Builder bindView(View view){
			// TODO: obtain view width, height and 
			// X and Y window coordinate
			
			Object raw = view.getTag();
			
			if(raw != null && raw instanceof PrevSelection){
				this.prevSelection = (PrevSelection) raw;
			} else {
				this.prevSelection = new PrevSelection();
				view.setTag(prevSelection);
			}
			
			this.button = view;
			
			view.addOnLayoutChangeListener(this);
			
			return this;
		}
		
		
		public Builder setWidth(int width){
			this.width = width;
			return this;
		}
		
		public int getWidth(){
			return this.width;
		}
		
		
		public Builder setHeight(int height){
			this.height = height;
			return this;
		}
		
		
		public int getHeight(){
			return this.height;
		}
		
		
		/**
		 * Show GridPickerDialog.
		 * @param fm
		 * @param tag
		 */
		public void show(FragmentManager fm, String tag) {
			GridPickerDialog dialog = new GridPickerDialog();
			dialog.set(this);
			dialog.setStyle(STYLE_NO_TITLE, R.style.grid_picker_dialog);
			dialog.show(fm, tag);
		}
		
		
		/**
		 * Returns the array of two integers
		 * which is corresponds to absolute x and y coordinates
		 * on the screen in pixels.
		 * @return
		 */
		public int[] getLocationInWindow(){
			int[] location = new int[2];
			this.button.getLocationInWindow(location);
			
			location[1] += this.button.getHeight(); 
			
			return location;
		}


		@Override
		public void onLayoutChange(View v, int left, int top, int right,
				int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			
			if(this.listener != null){
				this.listener.updatePosition();
			}
			
		}
		
		private OnBoundViewPositionListener listener;
		
		public interface OnBoundViewPositionListener{
			void updatePosition();
		}
		
		public void setOnBoundViewPositionListener(OnBoundViewPositionListener listener){
			this.listener = listener;
		}
		
	}
	
	/**
	 *  Use this class to store 
	 */
	public static class PrevSelection{
		private int index;
		public void setSelection(int index){
			this.index = index;
		}
		public int getSelection(){
			return this.index;
		}
	}

}
