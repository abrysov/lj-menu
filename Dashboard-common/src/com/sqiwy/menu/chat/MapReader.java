package com.sqiwy.menu.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sqiwy.menu.chat.MapReader.MapData.MapObjectData;
import com.sqiwy.menu.chat.MapReader.MapData.MapObjectType;

import android.util.JsonReader;

/**
 * Created by abrysov
 */

public class MapReader {

	
	public MapData read(InputStream in){
		InputStreamReader isr = new InputStreamReader(in);
		JsonReader reader = new JsonReader(isr);
		
		MapData mapData = new MapData();
		
		try {
			reader.beginObject();
			while(reader.hasNext()){
				String token = reader.nextName();
				if(token.equals(MapData.RATIO)){
					readMapRatio(mapData, reader);
				} else if(token.equals(MapData.OBJECT)){
					readMapObjectType(mapData, reader);
				} else if(token.equals(MapData.MAP)){
					readMapObject(mapData, reader);
				}
			}
			reader.endObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Cleanup
		try {
			isr.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return mapData;
	}
	
	
	
	
	private void readMapRatio(MapData mapData, JsonReader reader) throws IOException{
		reader.beginObject();
		while(reader.hasNext()){
			String token = reader.nextName();
			if(token.equals(MapData.RATIO_X)){
				mapData.setRatioX(reader.nextInt());
			} else if(token.equals(MapData.RATIO_Y)){
				mapData.setRatioY(reader.nextInt());
			}
		}
		reader.endObject();
	}
	
	
	
	private void readMapObjectType(MapData mapData, JsonReader reader) throws IOException{
		double width = 0, height = 0;
		String shape = null;
		
		reader.beginArray();
	    while (reader.hasNext()) {
	    	
	    	reader.beginObject();
	    	
	    	while(reader.hasNext()){
	    		String token = reader.nextName();
	    		
	    		if(token.equals(MapData.WIDTH)){
	    			width = reader.nextDouble();
	    		}else if(token.equals(MapData.HEIGHT)){
	    			height = reader.nextDouble();
	    		}else if(token.equals(MapData.SHAPE)){
	    			shape = reader.nextString();
	    		}
	    		
	    	}
	    	
	    	reader.endObject();
	    	
	    	MapObjectType mapObjectType = 
	    			new MapObjectType(width, height, shape);
	    	
	    	mapData.addMapObjectType(mapObjectType);
	    	
	    	
	    }
	    reader.endArray();
		
	}
	
	
	
	private void readMapObject(MapData mapData, JsonReader reader) throws IOException{
		double x=0, y=0;
		int objectType=0, rotate=0, person=0; 
		String color = null, objectId=null;
		
		reader.beginArray();
		while(reader.hasNext()){
			reader.beginObject();
			
			while(reader.hasNext()){
				String token = reader.nextName();
				
				if(token.equals(MapData.ID)){
					objectId = reader.nextString();
				}else if(token.equals(MapData.OBJECT)){
					objectType = reader.nextInt();
				}else if(token.equals(MapData.X)){
					x = reader.nextDouble();
				}else if(token.equals(MapData.Y)){
					y = reader.nextDouble();
				}else if(token.equals(MapData.ROTATE)){
					rotate = reader.nextInt();
				}else if(token.equals(MapData.COLOR)){
					color = reader.nextString();
				}else if(token.equals(MapData.PERSON)){
					person = reader.nextInt();
				}
			}
			
			reader.endObject();
			
			MapObjectData mapObject = 
					new MapObjectData(
							objectId, 
							objectType, 
							x, 
							y, 
							rotate, 
							color,
							person);
			
			mapData.addMapObject(mapObject);
			
		}
		
		reader.endArray();
		
	}
	
	
	public static class MapData{
		
		public static final String RATIO = "ratio";
		public static final String RATIO_X = "ratio_x";
		public static final String RATIO_Y = "ratio_y";
		
		public static final String OBJECT = "object";
		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";
		public static final String SHAPE = "shape";
		
		public static final String MAP = "map";
		public static final String X = "x";
		public static final String Y = "y";
		public static final String ID = "id";
		public static final String ROTATE = "rotate";
		public static final String COLOR = "color";
		public static final String PERSON = "person";
		
		
		private int ratioX;
		private int ratioY;
		
		private List<MapObjectType> mapObjectTypes =
				new ArrayList<MapReader.MapData.MapObjectType>();
		
		private List<MapObjectData> mapObjects = 
				new ArrayList<MapReader.MapData.MapObjectData>();
		
		
		public int getRatioX() {
			return ratioX;
		}

		protected void setRatioX(int ratioX) {
			this.ratioX = ratioX;
		}

		public int getRatioY() {
			return ratioY;
		}

		protected void setRatioY(int ratioY) {
			this.ratioY = ratioY;
		}
		
		protected void addMapObjectType(MapObjectType mapObjectType){
			this.mapObjectTypes.add(mapObjectType);
		}
		
		protected void addMapObject(MapObjectData mapObject){
			this.mapObjects.add(mapObject);
		}
		
		public List<MapObjectData> getMapObjects(){
			return this.mapObjects;
		}
		
		public List<MapObjectType> getMapObjectTypes(){
			return this.mapObjectTypes;
		}
		
		public static class MapObjectType{
			private double width;
			private double height;
			private Shape shape;

			public MapObjectType(double width, double height, String shape){
				this.width = width;
				this.height = height;
				
				this.shape = Shape.valueOf(shape);
			}
			public double getWidth() {
				return width;
			}
			public double getHeight() {
				return height;
			}
			public Shape getShape() {
				return shape;
			}
			public enum Shape{
				RECTANGLE,
				OVAL
			}
		}
		
		public static class MapObjectData {
			
			private String objectId;
			private int objectType;
			private double x;
			private double y;
			private int rotate;
			private String color;
			private int places;
			
			public MapObjectData(String objectId, 
					int objectType, 
					double x, 
					double y, 
					int rotate, 
					String color, 
					int places) {
				
				this.objectId = objectId;
				this.objectType = objectType;
				this.x = x;
				this.y = y;
				this.rotate = rotate;
				this.color = color;
				this.places = places;
			}
			
			public String getObjectId() {
				return objectId;
			}
			public int getObjectType() {
				return objectType;
			}
			public double getX() {
				return x;
			}
			public double getY() {
				return y;
			}
			public int getRotate() {
				return rotate;
			}
			public String getColor() {
				return color;
			}
			public int getPlaces() {
				return places;
			}
			
		}
	}
	
}
