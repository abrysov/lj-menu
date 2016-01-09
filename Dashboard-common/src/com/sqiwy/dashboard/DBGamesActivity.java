package com.sqiwy.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.fosslabs.android.utils.Funs;
import com.sqiwy.dashboard.DBGrandFragment.TopMenuCallbacks;
import com.sqiwy.dashboard.DBGrandFragment.DBGrandFragmentType;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.util.CommonUtils;
import com.sqiwy.menu.util.SystemControllerHelper;
import com.sqiwy.menu.util.UIUtils;

/**
 * Created by abrysov
 */

public class DBGamesActivity extends Activity implements TopMenuCallbacks {
    private static final String[] GAME_PACKAGE_NAMES = {
            "net.elvista.checkers",
            "com.rovio.angrybirds",
            "com.halfbrick.fruitninjafree",
            "com.hedgehogacademy.logicspatialintelligencelite",
            "com.nekki.shadowfight",
            "com.byril.seabattle"
    };

    private final GameOnClickListener mGameOnClickListener = new GameOnClickListener();
    private boolean mReveseLandscape;
    private GridLayout mGamesGridLayout;

    public static void start(Context context) {
        Intent intent = new Intent(context, DBGamesActivity.class);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Funs.hiddeTitleBar(this, mReveseLandscape);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
		setContentView(R.layout.activity_games);
        mGamesGridLayout = (GridLayout) findViewById(R.id.gl_games);
        for (String packageName : GAME_PACKAGE_NAMES) {
            addGameView(packageName);
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_grand_db,
                    DBGrandFragment.newInstance(DBGrandFragmentType.EXTRA_TYPE_CAFE)).commit();
        }
	}

    private void addGameView(String packageName) {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int size = displaySize.x / (mGamesGridLayout.getColumnCount() + 1);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = lp.height = size;
        lp.setMargins(5, 5, 5, 5);
        ImageView imageView = new ImageView(this);
        imageView.setTag(packageName);
        imageView.setLayoutParams(lp);

        Drawable icon;
        Resources res = getResources();

        if (packageName == "net.elvista.checkers") {
            icon = res.getDrawable(R.drawable.game_01_chess);

        } else if (packageName == "com.rovio.angrybirds") {
            icon = res.getDrawable(R.drawable.game_02_abirds);

        } else if (packageName == "com.halfbrick.fruitninjafree") {
            icon = res.getDrawable(R.drawable.game_03_fninza);

        } else if (packageName == "com.hedgehogacademy.logicspatialintelligencelite") {
            icon = res.getDrawable(R.drawable.game_04_kids);

        } else if (packageName == "com.nekki.shadowfight") {
            icon = res.getDrawable(R.drawable.game_05_sfight);

        } else if (packageName == "com.byril.seabattle") {
            icon = res.getDrawable(R.drawable.game_06_mbuttle);

        }else {

            icon = getGameDrawable(packageName);
            if (icon == null) {
                icon = getAppIcon(packageName);
                if (icon == null) {
                    icon = getResources().getDrawable(R.drawable.main_0010_games);
                }
            }
        }


        imageView.setImageDrawable(icon);
        imageView.setOnClickListener(mGameOnClickListener);
        mGamesGridLayout.addView(imageView);
    }

    private Drawable getGameDrawable(String packageName) {
        Resources res = getResources();
        String resName = "drawable/game_" + packageName.replace(".", "_");
        int resId = res.getIdentifier(resName, null, getPackageName());
        try {
            return res.getDrawable(resId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Drawable getAppIcon(String packageName) {
        try {
            return getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e1) {
            return null;
        }
    }

	@Override
	public void onScreenRotate() {
		UIUtils.toggleOrientation(this);
	}

	@Override
	public void onLock() {
		Intent intent = new Intent(this, DBLockActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBack() {
		finish();
	}

    private class GameOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String packageName = (String) v.getTag();
            CommonUtils.runOrInstallApp(packageName);
            MenuApplication.trackLaunchedApp(packageName);
            SystemControllerHelper.setSystemUiMode(SystemControllerHelper.SYSTEM_UI_MODE_ENABLE_NAVIGATION);
        }
    }
}
