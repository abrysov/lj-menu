package com.sqiwy.menu;

import android.animation.LayoutTransition;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;

import com.sqiwy.menu.util.SystemUiHider;

/**
 * Created by abrysov
 */

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ActivityGames extends BaseActivity {

    FrameLayout layout;
    LayoutTransition transitioner = new LayoutTransition();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_games);

        GridLayout g = (GridLayout) findViewById(R.id.activity_games_grid);
        g.removeAllViews();

        addGame("com.halfbrick.fruitninjafree");
        addGame("com.rovio.angrybirds");
        addGame("com.badlogic.androidgames.tank1990");
        addGame("com.dq.zombieskater.main");
        addGame("com.byril.battleship");
        addGame("com.halfbrick.jetpackjoyride");
        addGame("com.alonsoruibal.chessdroid.lite");
        addGame("org.aastudio.games.longnards");
    }

    public Drawable getFullResIcon(String app) {
        try {
            return getPackageManager().getApplicationIcon(app);
        } catch (NameNotFoundException e1) {
            return null;
        }
    }

    Drawable getDrawable(String name) {
        // R.drawable.name == drwable/name
        String uri = "drawable/game_" + name.replace(".", "_");

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

        try {
            return getResources().getDrawable(imageResource);
        } catch (NotFoundException e) {
            return null;
        }
    }

    void addGame(String app) {
        GridLayout g = (GridLayout) findViewById(R.id.activity_games_grid);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int s = size.x / (g.getColumnCount() + 1);

        LayoutParams lp = new LayoutParams();
        lp.width = s;
        lp.height = s;
        lp.setMargins(5, 5, 5, 5);

        ImageView v = new ImageView(this);
        v.setTag(app);
        v.setLayoutParams(lp);

        Drawable icon = getFullResIcon(app);

        if (icon != null) {
            if (icon.getIntrinsicWidth() < s) {
                icon = null;
            }
        }

        if (icon == null) {
            icon = getDrawable(app);
        }

        if (icon == null) {
            icon = getFullResIcon(app);
        }

        if (icon == null) {
            icon = getResources().getDrawable(R.drawable.main_0010_games);
        }

        v.setImageDrawable(icon);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGame(v);
            }
        });

        g.addView(v);
    }

    public void buttonGame(View v) {
        String app = (String) v.getTag();

        runOrInstall(app);
    }

}
