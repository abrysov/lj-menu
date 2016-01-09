package com.sqiwy.menu.advertisement;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;

import android.text.TextUtils;

import com.sqiwy.dashboard.model.action.Action;

/**
 * Created by abrysov
 * TODO: rewrite to Parcelable
 */
@SuppressWarnings("serial")
public class Advertisement implements Serializable {

	public static enum Type {
		VIDEO,
		IMAGE
	}
	
	public enum Places {
		DASHBOARD("dashboard"), VIDEO_SCREEN("video_screen"), VENUE_MAP("venue_map");
		
		private String mPlace;
		
		private Places(String str) {
			mPlace=str;
		}

		@Override
		public String toString() {
			return mPlace;
		}
		
		public static Places fromString(String arg) {
			if (arg==null || TextUtils.isEmpty(arg)) {
				throw new IllegalArgumentException("Cannot generate proper Places for argument '"+arg+"'");
			}
			if (arg.equals("video_screen")) {
				return VIDEO_SCREEN;
			} else if (arg.equals("dashboard")) {
				return DASHBOARD;
			} else if (arg.equals("venue_map")) {
				return VENUE_MAP;
			}
			throw new IllegalArgumentException("Cannot generate proper Places for argument '"+arg+"'");
		}
	}

	int id;
	String name;
	Places mPlaces[];
	int timeShow;
	Type type;
	Action action;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTimeShow() {
		return timeShow;
	}

	public void setTimeShow(int timeShow) {
		this.timeShow = timeShow;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public void setPlaces(JSONArray jArr) {
		if (jArr==null || jArr.length()==0) {
			mPlaces=null;
		} else {
			int j=jArr.length();
			String rawPlace;
			
			mPlaces=new Places[j];
			for (int i=0; i<j; i++) {
				try {
					rawPlace=jArr.getString(i);
					mPlaces[i]=Places.fromString(rawPlace);
				} catch (JSONException e) {
					mPlaces[i]=null;
				}
			}
		}
	}
	
	public Places[] getPlaces() {
		return mPlaces;
	}
	
	public boolean hasPlace(Places place) {
		if (place==null) {
			throw new IllegalArgumentException();
		}
		if (mPlaces!=null) {
			for (Places nextPlace:mPlaces) {
				if (nextPlace!=null && nextPlace.equals(place)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + timeShow;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		if (mPlaces!=null) {
			for (Places place: mPlaces) {
				result=prime*result+place.toString().hashCode();
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Advertisement other = (Advertisement) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (timeShow != other.timeShow)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (mPlaces==null) {
			if (other.mPlaces!=null)
				return false;
		} else {
			if (other.mPlaces==null) {
				return false;
			}
			if (mPlaces.length!=other.mPlaces.length) {
				return false;
			}
			for (int i=0; i<mPlaces.length; i++) {
				if ((other.mPlaces[i]!=null && !other.mPlaces[i].equals(mPlaces[i])) || (other.mPlaces[i]==null && mPlaces[i]!=null))
					return false;
			}
		}
		return true;
	}

}