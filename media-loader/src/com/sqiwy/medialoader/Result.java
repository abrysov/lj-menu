package com.sqiwy.medialoader;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by abrysov
 */

/**
 * Result of loading media item.
 */
public class Result implements Parcelable {
	
	private static final String TAG = Result.class.getName();
	
	public static enum Status {
		SUCCESS,
		FAILURE
	}
	
	private MediaItem mediaItem;
	private Status status;

	public Result() {}
	
	public Result(MediaItem mediaItem, Status status) {
		this.mediaItem = mediaItem;
		this.status = status;
	}

	public MediaItem getMediaItem() {
		return mediaItem;
	}

	public Result setMediaItem(MediaItem mediaItem) {
		this.mediaItem = mediaItem;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public Result setStatus(Status status) {
		this.status = status;
		return this;
	}

	@Override
	public String toString() {
		return "Result [mediaItem=" + mediaItem + ", status=" + status + "]";
	}
	
	
	/* Parcelable implementation */

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		if (null != mediaItem) {
			dest.writeParcelable(mediaItem, 0);
		} else {
			dest.writeInt(-1);
		}
		if (null != status) {
			dest.writeInt(status.ordinal());
		} else {
			dest.writeInt(-1);
		}
	}

	public static final Creator<Result> CREATOR = new Creator<Result>() {
		@Override
		public Result createFromParcel(final Parcel source) {
			
			MediaItem mediaItem = null;
			try {
				mediaItem = (MediaItem) source.readParcelable(MediaItem.class
						.getClassLoader());
			} catch (BadParcelableException e) {
				Log.w(TAG, "Cannot parse media item.");
			}
			
			Status status = null;
			int ordinal = source.readInt();
			if (-1 != ordinal) {
				status = Status.values()[ordinal];
			}
			
			return new Result(mediaItem, status);
		}

		@Override
		public Result[] newArray(final int size) {
			return new Result[size];
		}
	};

	/* -- / -- */
}