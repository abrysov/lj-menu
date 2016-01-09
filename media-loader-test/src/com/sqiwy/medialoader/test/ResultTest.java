package com.sqiwy.medialoader.test;

import junit.framework.TestCase;
import android.os.Bundle;
import android.os.Parcel;

import com.sqiwy.medialoader.MediaItem;
import com.sqiwy.medialoader.Result;
import com.sqiwy.medialoader.Result.Status;

/**
 * Created by abrysov
 */

public class ResultTest extends TestCase {

	public void testParcelableEmpty() {
		
		Result resultIn = new Result();
		
		// Save to parcel
		Bundle bundleIn = new Bundle();
		bundleIn.putParcelable("test", resultIn);
		Parcel parcel = Parcel.obtain();
	    bundleIn.writeToParcel(parcel, 0);
	    
	    // Read from parcel
	    parcel.setDataPosition(0);
	    Bundle bundleOut = parcel.readBundle();
	    bundleOut.setClassLoader(Result.class.getClassLoader());
	    Result resultOut = bundleOut.getParcelable("test");
	    
	    assertFalse("Bundle is the same", bundleIn == bundleOut);
	    assertFalse("Result is the same", resultIn == resultOut);
		assertNull(resultOut.getMediaItem());
		assertNull(resultOut.getStatus());
	}
	
	public void testParcelableNonEmpty() {
		
		Result resultIn = new Result(new MediaItem().setArchive(true).setServerUri("server").setStorageUri("storage"),
				Status.SUCCESS);
		
		// Save to parcel
		Bundle bundleIn = new Bundle();
		bundleIn.putParcelable("test", resultIn);
		Parcel parcel = Parcel.obtain();
	    bundleIn.writeToParcel(parcel, 0);
	    
	    // Read from parcel
	    parcel.setDataPosition(0);
	    Bundle bundleOut = parcel.readBundle();
	    bundleOut.setClassLoader(Result.class.getClassLoader());
	    Result resultOut = bundleOut.getParcelable("test");
	    
	    assertFalse("Bundle is the same", bundleIn == bundleOut);
	    assertFalse("Result is the same", resultIn == resultOut);
		assertEquals(resultIn.getMediaItem().getServerUri(), resultOut.getMediaItem().getServerUri());
		assertEquals(resultIn.getMediaItem().getStorageUri(), resultOut.getMediaItem().getStorageUri());
		assertEquals(resultIn.getMediaItem().isArchive(), resultOut.getMediaItem().isArchive());
		assertEquals(resultIn.getStatus(), resultOut.getStatus());
	}
	
}
