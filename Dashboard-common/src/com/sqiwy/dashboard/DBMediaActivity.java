package com.sqiwy.dashboard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.sqiwy.dashboard.util.StatsUtils.StatsContext;
import com.sqiwy.menu.advertisement.Advertisement;
import com.sqiwy.menu.advertisement.Advertisement.Type;

/**
 * Created by abrysov
 */

public class DBMediaActivity extends BaseActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_media);

		FragmentManager manager = getFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragment);

		if (null == fragment) {
			// fragment = new DBMediaFragment();
			fragment = DBGalleryCommercialFragment
					.newInstacne(Type.VIDEO, StatsContext.VIDEOSCREEN, false, Advertisement.Places.VIDEO_SCREEN);

			manager.beginTransaction().add(R.id.fragment, fragment).commit();
		}
		
		findViewById(R.id.enter_app).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent in = new Intent(DBMediaActivity.this, DBStartActivity.class);
		// i.putExtra(ProductListActivity.EXTRA_ID_MENU_CATEGORY, id);
		startActivity(in);
	}

}
