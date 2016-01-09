package com.sqiwy.menu;

import java.util.HashMap;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.os.Bundle;
import android.widget.FrameLayout;

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
public class ActivityMenuMain extends BaseActivity {

    FrameLayout layout;
    LayoutTransition transitioner = new LayoutTransition();
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_main);

        layout = (FrameLayout) findViewById(R.id.first_screen_layout);

        AnimatorSet shake = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.shake);
        transitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, shake);

        layout.setLayoutTransition(transitioner);

        animate();
    }

    void put(int i, int ii) {
        map.put(i, ii);
    }

    public void animate() {
        long startDelay = 500;
        long next = 200;
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
}
