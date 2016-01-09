package com.sqiwy.menu;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.sqiwy.menu.R;

/**
 * Created by abrysov
 */

public class ActivityAbout extends BaseActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);

		findViewById(R.id.ironview).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(ActivityMain.class);
					}
				});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public void finish() {
		super.finish();
	}

}
