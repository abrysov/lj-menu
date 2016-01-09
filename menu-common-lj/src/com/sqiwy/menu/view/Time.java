package com.sqiwy.menu.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abrysov
 */

public class Time extends TextView {

    public Time(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TextView t = (TextView) this;

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, HH:mm", new Locale("ru", "RU"));
                String asWeek = dateFormat.format(new Date());

                t.setText(asWeek);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                mHandler.sendMessage(msg);
            }
        }, 0, 1000);
    }

}
