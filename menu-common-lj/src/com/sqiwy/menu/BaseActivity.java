package com.sqiwy.menu;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/*import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;*/
import com.sqiwy.menu.util.MenuControllerHelper;

/**
 * Created by abrysov
 */

public class BaseActivity extends SlidingFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBehindContentView(R.layout.menu_drop);

        Intent intent = new Intent();
        intent.setAction("SqiwyUIBroadcast/show_bar");
        intent.putExtra("show", "false");
        sendBroadcast(intent);

        SlidingMenu sm = getSlidingMenu();
        // sm.setShadowWidth(20);
        // sm.setShadowDrawable(20);
        // sm.setBehindOffset(10);
        // sm.setFadeDegree(0.35f);
        sm.setBehindScrollScale(0.0f);
        // sm.setBehindCanvasTransformer(new CanvasTransformer() {
        // @Override
        // public void transformCanvas(Canvas canvas, float percentOpen) {
        // float scale = (float) (percentOpen * 0.25 + 0.75);
        // canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight()
        // / 2);
        // }
        // });
        // sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setMode(SlidingMenu.TOP);
        // sm.setBehindOffsetX(300);

    }

    public void startActivity(Class<?> cls) {
        finish();
        setContentView(new View(this));
        System.gc();

        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        View v = findViewById(android.R.id.content);
        ((ViewGroup) v.getParent()).removeAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuControllerHelper.setSystemUiMode(MenuControllerHelper.SYSTEM_UI_MODE_DISABLE_ALL);
    }

    public void buttonCloseDrop(View v) {
        toggle();
    }

    public void closeButton(View v) {
        startActivity(ActivityMain.class);
    }

    public void buttonClick(View v) {
/*
        topmenuMenuClose(v);

        Intent intent = new Intent(this, ActivityMenuSub.class);
        intent.putExtra("type", v.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
*/
    }

    public void topmenuRotate(View v) {
        Intent intent = new Intent();
        intent.setAction("SqiwyUIBroadcast/rotate");
        sendBroadcast(intent);
    }

    public void topmenuMenu(View v) {
        View vv = findViewById(R.id.menu_topmenu_drop_layout);
        vv.setVisibility(View.VISIBLE);
    }

    public void topmenuMenuClose(View v) {
        View vv = findViewById(R.id.menu_topmenu_drop_layout);
        vv.setVisibility(View.GONE);
    }

    public void buttonShock(View v) {
        AnimatorSet shake = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.shake);
        shake.setTarget(v);
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(v.getHeight() / 2);
        shake.start();
    }

    void flipOut(View v, long startDelay) {
        v.setAlpha(0);

        AnimatorSet shake = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_down);
        shake.setTarget(v);
        v.setPivotX(v.getWidth() / 2);
        v.setPivotY(0);
        shake.setStartDelay(startDelay);
        shake.start();
    }

    public void buttonToggle(View v) {
        toggle();
    }

    public void closeButtonMenu(View v) {
        startActivity(ActivityMain.class);
    }

    public boolean isInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void runOrInstall(String appName) {
        if (isInstalled(appName)) {
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(appName);
            startActivity(LaunchIntent);
        } else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                // google play is not installed, open a browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
                        + appName)));
            }
        }
        MenuApplication.trackLaunchedApp(appName);
        MenuControllerHelper.setSystemUiMode(MenuControllerHelper.SYSTEM_UI_MODE_ENABLE_NAVIGATION);
    }
}
