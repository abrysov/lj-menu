package com.sqiwy.menu;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sqiwy.menu.R;
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
public class ActivityMain extends BaseActivity {

    FrameLayout layout;
    LayoutTransition transitioner = new LayoutTransition();
    GestureLibrary mLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            throw new RuntimeException("unable to laod geustres");
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {

            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                ArrayList predictions = mLibrary.recognize(gesture);

                // We want at least one prediction
                if (predictions.size() > 0) {
                    Prediction prediction = (Prediction) predictions.get(0);
                    // We want at least some confidence in the result
                    if (prediction.score > 1.0) {
                        Toast.makeText(ActivityMain.this, prediction.name, Toast.LENGTH_SHORT).show();

                        if (prediction.name.equals("settings")) {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                        }

                    }
                }
            }
        });
    }

    void put(int i, int ii) {
        map.put(findViewById(i), ii);
    }

    public void mainMenu(View v) {
        startActivity(ActivityMenuMain.class);
    }

    public void animate() {
        long startDelay = 0;
        long next = 100;
        flipOut(findViewById(R.id.button_sushi), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_hot), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_drings), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_children), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_desert), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_blunch), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_ad), startDelay);
        startDelay += next;
        flipOut(findViewById(R.id.button_discont), startDelay);
        startDelay += next;
    }

    public void closeButton(View v) {
        animate();
    }

    HashMap<View, Integer> map = new HashMap<View, Integer>();

    public void buttonClick(View v) {
/*
        finish();

        Intent intent = new Intent(this, ActivityMenuSub.class);
        intent.putExtra("type", map.get(v));
        startActivity(intent);
        overridePendingTransition(R.anim.move_down_enter, R.anim.move_down_exit);
*/
    }

    /*public void buttonAd(View v) {
        startActivity(ActivityCommercial.class);
    }*/

    public void buttonChat(View v) {
        startActivity(ActivityChat.class);
    }

    public void buttonAbout(View v) {
        startActivity(ActivityAbout.class);
    }

    public void buttonInternet(View v) {
        runOrInstall("com.android.browser");
    }

    public void buttonTaxi(View v) {
        startActivity(ActivityTaxi.class);
    }

    public void buttonSocial(View v) {
        startActivity(ActivitySocial.class);
    }

    public void buttonGames(View v) {
        startActivity(ActivityGames.class);
    }

}
