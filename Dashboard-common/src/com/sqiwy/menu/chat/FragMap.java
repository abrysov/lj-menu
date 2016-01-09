package com.sqiwy.menu.chat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sqiwy.restaurant.api.data.ChatUser;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.chat.ChatManager.OnChatManagerEventListener;
import com.sqiwy.menu.chat.MapReader.MapData;
import com.sqiwy.menu.chat.MapReader.MapData.MapObjectData;
import com.sqiwy.menu.chat.MapReader.MapData.MapObjectType;
import com.sqiwy.menu.chat.MapReader.MapData.MapObjectType.Shape;

/**
 * Created by abrysov
 */

public class FragMap extends Fragment implements OnGlobalLayoutListener,
												 OnChatManagerEventListener{

	private MapData mapData;
	private OnMapEventListener listener;
	private RelativeLayout root;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.listener = (OnMapEventListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("Activity must implement OnMpEventListener.");
		}
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		InputStream is = getResources().openRawResource(R.raw.map_round);

		MapReader reader = new MapReader();
		this.mapData = reader.read(is);
		
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.frag_chat_map, null);
		
		this.root = (RelativeLayout) view.findViewById(R.id.root);
		
		ViewTreeObserver observer = this.root.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(this);
		
		return view;
	}
	
	
	
	public interface OnMapEventListener{
		void onMapObjectClick(String tableId);
	}
	
	
	
	public static class MapView extends FrameLayout implements OnClickListener {
		
		private MapObjectType type;
		private MapObjectData data;
		private OnMapEventListener listener;
		
		/**
		 * Create view that represents table on the map.
		 */
		public MapView(Context context, MapObjectType type, 
				MapObjectData data, OnMapEventListener listener) {
			super(context);
			this.type = type;
			this.data = data;
			this.setOnClickListener(this);
			this.listener = listener;
		}


//		public void setOnClickListener(){
//			this.setOnClickListener(this);
//		}
//		
//		public void removeOnClickListener(){
//			this.setOnClickListener(null);
//		}
		
		
		public void setContentView(View view){
			view.setRotation(-data.getRotate());
			this.removeAllViews();
			this.addView(view);
		}
		
		/**
		 * Do fancy shake animation to get user attention
		 */
		public void shake(){
			
		}
		
		private int chatUserId;

		
		public void setActive(ChatUser user) {
			this.chatUserId = user.getId();
			int color = Color.parseColor(user.getColor());
			this.setBackground(createShapeDrawable(type.getShape(), color));
		}
		
		
		public void setInactive(int chatUserId){
			this.chatUserId = 0;
		}
		
		
		public void placeOnMap(ViewGroup root) {
			
			float unit = (float) Math.min(root.getWidth(), root.getHeight()) / 100;
			
			this.setTranslationX((float) (unit * data.getX()));
			this.setTranslationY((float) (unit * data.getY()));
			this.setRotation(data.getRotate());
			
			this.setBackground(createShapeDrawable(type.getShape(), Color.WHITE));
//			this.setBackground(getResources().getDrawable(R.drawable.rect_lght_gray));
			
			this.setTag(data.getObjectId());
			
			int width = (int) (unit * type.getWidth());
			int height = (int) (unit * type.getHeight());
			
			LayoutParams lp = new LayoutParams(width, height);
			this.setLayoutParams(lp);
			
			root.addView(this);
			
		}

		
		
		private Drawable createShapeDrawable(Shape shape, int color){
			ShapeDrawable sd = null;
			
			switch (shape) {
			case OVAL:
				sd = new ShapeDrawable(new OvalShape());
				break;
				
			case RECTANGLE:
				sd = new ShapeDrawable(new RectShape());
				break;
			}
			
			sd.getPaint().setColor(color);
			sd.getPaint().setStrokeWidth(1);
			
			return sd;
			
		}


		
		@Override
		public void onClick(View v) {
            String tableId = null;
            try {
                tableId = String.valueOf(getObjectId());
                Log.d("FragMap", "tableId: " + tableId);
            } catch (NumberFormatException e) {
                Log.e(getClass().getName(), e.getMessage(), e.getCause());
            }
            if (tableId != null) {
	            int userId = getChatUserId(tableId);
	            if (userId != 0) {
	            	ChatManager.getInstance().setVisavisId(userId);
	            }
            }
            //listener.onMapObjectClick(getObjectId());
        }

        private int getChatUserId(String tableId) {
        	ArrayList<ChatUser> chatUsers = ChatManager.getInstance().getChatUserList();
            for (ChatUser user : chatUsers) {
            	Log.d("FragMap", "getChatUserId clientId: " + user.getClientId() + " userName: " + user.getNickname());
                if (user.getClientId().equals(tableId)) {
                    return user.getId();
                }
            }
            return 0;
        }

		public String getObjectId() {
			return this.data.getObjectId();
		}

	}

	private void setSizeByRatio() {
		int ratioX = mapData.getRatioX();
		int ratioY = mapData.getRatioY();
		
		float ratio = (float)ratioX / (float)ratioY;
		
		int fragWidth = this.root.getWidth();
		int fragHeight = this.root.getHeight();
		
		float actualRatio = (float)fragWidth / (float)fragHeight;
		
		if (ratio > actualRatio) {
			
			fragHeight = (ratioY * fragWidth) / ratioX;
			
		} else if(ratio < actualRatio) {
			
			fragWidth = (ratioX * fragHeight) / ratioY;
			
		}
		
		LayoutParams lp = (LayoutParams) this.root.getLayoutParams();
		lp.width = fragWidth;
		lp.height = fragHeight;
		this.root.requestLayout();
	}

	private List<MapView> mapViews;
	
	private void placeMapObjects() {
		List<MapObjectType> mapObjectTypes = mapData.getMapObjectTypes();
		mapViews = new ArrayList<FragMap.MapView>();
		List<MapObjectData> mapObjects = mapData.getMapObjects();
		
		for(MapObjectData mapObject : mapObjects){
			
			MapView mapView = new MapView(
					getActivity(), 
					mapObjectTypes.get(mapObject.getObjectType()), 
					mapObject,
					this.listener);
		
			TextView tv = (TextView) LayoutInflater.from(getActivity())
					.inflate(R.layout.map_object_content, null);
			
			tv.setText(String.valueOf(mapObject.getObjectId()));
			
			mapView.setContentView(tv);
			
			mapViews.add(mapView);
			
			mapView.placeOnMap(root);
			
		}
	}

	@Override
	public void onGlobalLayout() {
		ViewTreeObserver observer = this.root.getViewTreeObserver();
		observer.removeOnGlobalLayoutListener(this);
		setSizeByRatio();
		placeMapObjects();
	}
	
	private void actualizeMap(ArrayList<ChatUser> chatUsers) {
		if (mapViews != null && chatUsers != null) {
			for (int i=0; i < chatUsers.size();i++) {
				for (MapView mapView : mapViews) {
					if (mapView.getObjectId().equals(chatUsers.get(i).getClientId())) {
						mapView.setActive(chatUsers.get(i));
						break;
					}
				} 
			}
		}
	}
	
	private void shakeMapView(String objectId){
		for (MapView mapView : mapViews) {
			if (mapView.getObjectId() == objectId) {
				mapView.shake();
				return;
			}
		}
	}
	
	/*
	 * Chat manager callbacks
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void onChatManagerEvent(ChatManagerEvent event, Object... args) {
		switch (event) {
			case CHAT_STARTED:
				break;
			case MESSAGE_RECEIVED:
				break;
			case USER_LIST_CHANGED:
				ArrayList<ChatUser> chatUsers = null;
				if (args.length > 0) {
					chatUsers = (ArrayList<ChatUser>) args[0];
					actualizeMap(chatUsers);
				}
				break;
			case VISAVIS_CHANGED:
				
				break;
		}
	}

}
