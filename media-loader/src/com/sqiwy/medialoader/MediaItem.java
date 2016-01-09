package com.sqiwy.medialoader;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abrysov
 */

public class MediaItem implements Parcelable {

	private String serverUri;
	private String storageUri;
	private boolean isArchive;
	private int version;
	
	public MediaItem() {}
	
	public String getServerUri() {
		return serverUri;
	}

	public MediaItem setServerUri(String serverUri) {
		this.serverUri = serverUri;
		return this;
	}

	public String getStorageUri() {
		return storageUri;
	}

	public MediaItem setStorageUri(String storageUri) {
		this.storageUri = storageUri;
		return this;
	}

	public boolean isArchive() {
		return isArchive;
	}

	public MediaItem setArchive(boolean isArchive) {
		this.isArchive = isArchive;
		return this;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		String result = "URL: " + serverUri + ", STORAGE: "
				+ storageUri;
		
		if (isArchive) {
			result += " (archive)";
		}
		
		return result;
	}
	
	public JSONObject toJSON() throws JSONException {
		
		JSONObject result = new JSONObject();
		result.put("serverUri", serverUri);
		result.put("storageUri", storageUri);
		result.put("isArchive", isArchive);
		result.put("version", version);
		
		return result;
	}
	
	public static MediaItem fromJSON(JSONObject json) throws JSONException {
		
		MediaItem item = new MediaItem();
		item.setServerUri(json.getString("serverUri"));
		item.setStorageUri(json.getString("storageUri"));
		item.setArchive(json.getBoolean("isArchive"));
		item.setVersion(json.getInt("version"));
		
		return item;
	}
	
	/* Parcelable implementation */
	
	public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(serverUri);
        out.writeString(storageUri);
        out.writeInt(isArchive ? 1 : 0);
        out.writeInt(version);
    }

    public static final Parcelable.Creator<MediaItem> CREATOR
            = new Parcelable.Creator<MediaItem>() {
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };
    
    private MediaItem(Parcel in) {
        serverUri = in.readString();
        storageUri = in.readString();
        isArchive = 1 == in.readInt();
        version = in.readInt();
    }
	
	/* -- / -- */
}
