package com.sqiwy.medialoader.test;

import junit.framework.TestCase;
import android.os.Bundle;
import android.os.Parcel;

import com.sqiwy.medialoader.MediaItem;

/**
 * Created by abrysov
 */

public class MediaItemTest extends TestCase {

	public void testParcelableEmpty() {
		
		MediaItem mediaItemIn = new MediaItem();
		
		// Save to parcel
		Bundle bundleIn = new Bundle();
		bundleIn.putParcelable("test", mediaItemIn);
		Parcel parcel = Parcel.obtain();
	    bundleIn.writeToParcel(parcel, 0);
	    
	    // Read from parcel
	    parcel.setDataPosition(0);
	    Bundle bundleOut = parcel.readBundle();
	    bundleOut.setClassLoader(MediaItem.class.getClassLoader());
	    MediaItem mediaItemOut = bundleOut.getParcelable("test");
	    
	    assertFalse("Bundle is the same", bundleIn == bundleOut);
	    assertFalse("MediaItem is the same", mediaItemIn == mediaItemOut);
		assertNull(mediaItemOut.getServerUri());
		assertNull(mediaItemOut.getStorageUri());
		assertFalse(mediaItemOut.isArchive());
	}
	
	public void testParcelableNonEmpty() {
		
		MediaItem mediaItemIn = new MediaItem().setArchive(true).setServerUri("server/uri")
				.setStorageUri("storage/uri");
		
		// Save to parcel
		Bundle bundleIn = new Bundle();
		bundleIn.putParcelable("test", mediaItemIn);
		Parcel parcel = Parcel.obtain();
	    bundleIn.writeToParcel(parcel, 0);
	    
	    // Read from parcel
	    parcel.setDataPosition(0);
	    Bundle bundleOut = parcel.readBundle();
	    bundleOut.setClassLoader(MediaItem.class.getClassLoader());
	    MediaItem mediaItemOut = bundleOut.getParcelable("test");
	    
	    assertFalse("Bundle is the same", bundleIn == bundleOut);
	    assertFalse("MediaItem is the same", mediaItemIn == mediaItemOut);
		assertEquals(mediaItemIn.getServerUri(), mediaItemOut.getServerUri());
		assertEquals(mediaItemIn.getStorageUri(), mediaItemOut.getStorageUri());
		assertEquals(mediaItemIn.isArchive(), mediaItemOut.isArchive());
	}
}
