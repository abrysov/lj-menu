package com.sqiwy.menu.resource;

import java.util.List;

import com.sqiwy.medialoader.MediaItem;

/**
 * Created by abrysov
 */

public class ResourceOperation {

	public enum Operation {
		LOAD,
		REMOVE
	}
	
	public Operation operation;
	public List<MediaItem> mediaItems;

	public ResourceOperation(Operation operation) {
		this.operation = operation;
	}
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public List<MediaItem> getMediaItems() {
		return mediaItems;
	}
	public void setMediaItems(List<MediaItem> mediaItems) {
		this.mediaItems = mediaItems;
	}

}
