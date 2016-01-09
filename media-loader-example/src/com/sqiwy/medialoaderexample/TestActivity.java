package com.sqiwy.medialoaderexample;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sqiwy.medialoader.MediaItem;
import com.sqiwy.medialoader.MediaLoader;
import com.sqiwy.medialoader.Result;
import com.sqiwy.medialoader.util.FileUtil;
import com.sqiwy.medialoader.util.FileUtil.StorageException;

public class TestActivity extends Activity {

	private static final String TAG = TestActivity.class.getName();
	
	public static final int REQUEST_CODE_LOAD_ARCHIVE = 1;
	public static final int REQUEST_CODE_LOAD_VIDEO = 2;
	public static final int REQUEST_CODE_LOAD_MULTIPLE_ITEMS = 3;

	private TestBroadcastReceiver mReceiver;

	private TextView mMessages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);

		mMessages = (TextView) findViewById(R.id.test_activity_messages);
		
		// Stub data
		String testArchive = "https://docs.google.com/uc?export=download&id=0B0lKX51T_sQFazlYbkhWRjhTSzA";
		String testVideo = "https://docs.google.com/uc?export=download&id=0B0lKX51T_sQFNXRJSlJlZVVJS2s";
		
		File storage = null;;
		
		// Get storage to load data to
		try {
			storage = FileUtil.getStorage(false);
		} catch (StorageException e) {
			Log.e(TAG, "Stroage is not available", e);
			Toast.makeText(this, "Storage is not available", Toast.LENGTH_LONG).show();
			return;
		}
		
		// Create root dir of our app
		File root = new File(storage, "TEST_DIR");
		if (!root.exists() && !root.mkdir()) {
			Toast.makeText(this, "Cannot create: " + root.getAbsolutePath(), Toast.LENGTH_LONG).show();
			return;
		}
		
		// Create receiver for load completion events/intents
		mReceiver = new TestBroadcastReceiver();
		
		// Create media items
		MediaItem archive = new MediaItem().setArchive(true).setServerUri(testArchive);
		MediaItem video = new MediaItem().setArchive(false).setServerUri(testVideo);

		// Set storage uri/path where we load particular data to
		archive.setStorageUri(new File(root, "archive").getAbsolutePath());
		video.setStorageUri(new File(root, "video.mp4").getAbsolutePath());
		
		IntentFilter filter;

		// Load single media item
		filter= MediaLoader.load(getApplicationContext(), REQUEST_CODE_LOAD_ARCHIVE, archive);
		// Register receiver for load completion event of media item we loading
		registerReceiver(mReceiver, filter);
		
		filter = MediaLoader.load(getApplicationContext(), REQUEST_CODE_LOAD_VIDEO, video);
		registerReceiver(mReceiver, filter);
		
		// Create sub dir to load the same data but in batch 
		root = new File(root, "SUB_TEST_DIR");
		if (!root.exists() && !root.mkdir()) {
			Toast.makeText(this, "Cannot create: " + root.getAbsolutePath(), Toast.LENGTH_LONG).show();
			return;
		}

		// Update media item uris/paths 
		archive.setStorageUri(new File(root, "archive").getAbsolutePath());
		video.setStorageUri(new File(root, "video.mp4").getAbsolutePath());
		
		// Load multiple media items in batch
		filter = MediaLoader.load(getApplicationContext(), REQUEST_CODE_LOAD_MULTIPLE_ITEMS, archive, video);
		registerReceiver(mReceiver, filter);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (null != mReceiver) {
			unregisterReceiver(mReceiver);
		}
	}

	public class TestBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			int requestCode = intent.getIntExtra(MediaLoader.ARG_REQUEST_CODE, -1);
			ArrayList<Result> results = intent.getParcelableArrayListExtra(MediaLoader.ARG_RESULTS);

			StringBuilder messages = new StringBuilder();
			
			CharSequence text = mMessages.getText();
			if (!TextUtils.isEmpty(text)) {
				messages.append(text).append("\n\n");
			}
			
			switch (requestCode) {
			case REQUEST_CODE_LOAD_ARCHIVE:
				
				messages.append("> Archive load completed:\n" + results);
				
				break;
			case REQUEST_CODE_LOAD_VIDEO:
				
				messages.append("> Video load completed:\n" + results);
				
				break;
			case REQUEST_CODE_LOAD_MULTIPLE_ITEMS:
				
				messages.append("> Multiple media items load completed:\n" + results);
				
				break;
				
			default:
				
				messages.append("> Incorrect request code received: " + requestCode);
			}
			
			mMessages.setText(messages);
		}
		
	}

}

