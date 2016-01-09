package com.sqiwy.menu.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.restaurant.api.data.ChatUser.Smile;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.view.TypefaceTextView;

/**
 * Created by abrysov
 */

public class FragUserData extends Fragment {

	private EditText mNickNameView;
	private EditText mStatusView;
	private TypefaceTextView mHeader;
	private TypefaceTextView mStartChat;
	private OnUserDataChangeListener mOnUserDataChangeListener;
	private LinearLayout mStatusInputRow;
	private GridView mColorGrid;
	private GridView mSmileGrid;
    private int[] mColors = null;
    private TypedArray mSmiles = null;
    private int mSelectedColorPosition = 27; // 27 is default color position 
    private int mSelectedSmilePosition = 1; // 1 is default color position 
    private ColorAdapter mColorAdapter;
    private SmileAdapter mSmileAdapter;
    private ImageButton mButtonColor;
    private ImageButton mButtonSmile;
    
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnUserDataChangeListener = (OnUserDataChangeListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("Activity must implement OnStartChatRequestListener");
		}
	}
	
	public class SmileAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mSmiles.length();
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
			ImageView imageView;
	        if (convertView == null) {
	            imageView = new ImageView(getActivity().getApplicationContext());
	            imageView.setLayoutParams(new GridView.LayoutParams(54, 54));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        imageView.setImageDrawable(getActivity().getApplicationContext()
	        		.getResources().getDrawable(mSmiles.getResourceId(position, -1)));
	        if (position != mSelectedSmilePosition) {
	        	imageView.setBackground(getResources().getDrawable(R.drawable.bg_edittext));
	        } else {
	        	imageView.setBackground(getResources().getDrawable(R.drawable.bg_edittext_active));
	        }
	        return imageView;
		}
	}
	
	public class ColorAdapter extends BaseAdapter {
	    public int getCount() {
	        return mColors.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {
	            imageView = new ImageView(getActivity().getApplicationContext());
	            imageView.setLayoutParams(new GridView.LayoutParams(54, 54));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        imageView.setImageDrawable(createCircleDrawable(mColors[position]));
	        //TODO draw a border for the circle
	        if (position != mSelectedColorPosition) {
	        	imageView.setBackground(getResources().getDrawable(R.drawable.bg_edittext));
	        } else {
	        	imageView.setBackground(getResources().getDrawable(R.drawable.bg_edittext_active));
	        }
	        return imageView;
	    }  
	}
	
	private ShapeDrawable createCircleDrawable(int color) {
		ShapeDrawable circle = new ShapeDrawable(new OvalShape());
	    circle.setIntrinsicHeight(25);
	    circle.setIntrinsicWidth(25);
	    circle.setBounds(new Rect(0, 0, 25, 25));
	    circle.getPaint().setColor(color);
	    return circle;
	}
	
	@Override
	public void onDestroyView() {
	        super.onDestroyView();
	        mSmiles.recycle();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.frag_user_data, null);
		mNickNameView = (EditText) fragView.findViewById(R.id.edName);
		mStatusView = (EditText) fragView.findViewById(R.id.edStatus);
		mHeader = (TypefaceTextView) fragView.findViewById(R.id.header);
		mStatusInputRow = (LinearLayout) fragView.findViewById(R.id.status_input);
		mButtonColor = (ImageButton) fragView.findViewById(R.id.button_set_color);
		mButtonSmile = (ImageButton) fragView.findViewById(R.id.button_set_smile);
		mColorGrid = (GridView) fragView.findViewById(R.id.color_grid);
		mSmileGrid = (GridView) fragView.findViewById(R.id.smile_grid);
		mSmileAdapter = new SmileAdapter();
		mColorAdapter = new ColorAdapter();
		mColors = getActivity().getApplicationContext().getResources().getIntArray(R.array.table_colors);
		mSmiles = getResources().obtainTypedArray(R.array.smiles);
		mColorGrid.setAdapter(mColorAdapter);
		mColorGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				mSelectedColorPosition = arg2;
				mButtonColor.setImageDrawable(createCircleDrawable(mColors[arg2]));
				setColor();
			}
	    });
		mSmileGrid.setAdapter(mSmileAdapter);
		mSmileGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				mSelectedSmilePosition = arg2;
				mButtonSmile.setImageDrawable(getActivity().getApplicationContext()
						.getResources().getDrawable(mSmiles.getResourceId(arg2, -1)));
				setSmile();
			}
	    });
		mStartChat = (TypefaceTextView) fragView.findViewById(R.id.btn_enter);
		mStartChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String nickValue = mNickNameView.getText().toString().trim();
				String statusValue = mStatusView.getText().toString().trim();
				String colorValue = String.format("#%06X", (0xFFFFFF & mColors[mSelectedColorPosition]));
				Smile smileValue = ChatManager.getInstance().getSmileFromDrawableId(
						mSmiles.getResourceId(mSelectedSmilePosition, -1));
				if (!nickValue.isEmpty()) {
					mOnUserDataChangeListener.onUserDataReady(nickValue, statusValue, colorValue, smileValue);
					getActivity().getFragmentManager().beginTransaction().remove(FragUserData.this).commit();
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mStartChat.getWindowToken(), 0);
				}
			}
		});
		if (ChatManager.getInstance().getMe() != null) {
			mHeader.setText(getResources().getString(R.string.editing));
			mNickNameView.setText(ChatManager.getInstance().getMe().getNickname());
			mStatusView.setText(ChatManager.getInstance().getMe().getStatus());
			mStartChat.setText(R.string.btn_save_and_continue);
			mButtonSmile.setImageDrawable(ChatManager.getInstance().getSmileDrawable(ChatManager.getInstance().getMe().getSmile(), 20));
			mButtonColor.setImageDrawable(createCircleDrawable(Color.parseColor(ChatManager.getInstance().getMe().getColor())));
			mSelectedColorPosition = getColorPosition(Color.parseColor(ChatManager.getInstance().getMe().getColor()));
			mSelectedSmilePosition = getSmilePosition(ChatManager.getInstance().getMe().getSmile());
		} else {
			// default values
			mButtonSmile.setImageDrawable(ChatManager.getInstance().getSmileDrawable(Smile.SMILE, 20));
			mSelectedSmilePosition = getSmilePosition(Smile.SMILE);
			mButtonColor.setImageDrawable(createCircleDrawable(mColors[mSelectedColorPosition]));
			mHeader.setText(getResources().getString(R.string.chat_sign_in));
		}
		return fragView;
	}
	
	private int getSmilePosition(Smile smile) {
		for (int i = 0; i < mSmiles.length(); i++) {
			int tempId = mSmiles.getResourceId(i, -1);
			if (smile.equals(ChatManager.getInstance().getSmileFromDrawableId(tempId))) {
				return i;
			}
		}
		return 0;
	}

	private int getColorPosition(int color) {
		for (int i = 0; i < mColors.length; i++) {
			int tempColor = mColors[i];
			if (tempColor == color) {
				return i;
			}
		}
		return 0;
	}

	public void setColor() {
		mSmileGrid.setVisibility(View.GONE);
		if (mStatusInputRow.getVisibility() == View.VISIBLE) {
			mStatusInputRow.setVisibility(View.GONE);
			mColorGrid.setVisibility(View.VISIBLE);
		} else {
			mStatusInputRow.setVisibility(View.VISIBLE);
			mColorGrid.setVisibility(View.GONE);
		}
	}
	
	public void setSmile() {
		mStatusInputRow.setVisibility(View.VISIBLE);
		mColorGrid.setVisibility(View.GONE);
		if (mSmileGrid.getVisibility() == View.VISIBLE) {
			mSmileGrid.setVisibility(View.GONE);
		} else {
			mSmileGrid.setVisibility(View.VISIBLE);
		}
	}
	
	public interface OnUserDataChangeListener {
		public void onUserDataReady(String name, String status, String color, ChatUser.Smile smile);
	}
}
