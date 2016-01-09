package com.sqiwy.menu.util;

import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.lang.ref.WeakReference;

/**
 * Created by abrysov
 */

public final class UIUtils {
    private UIUtils() {
    }

    public static void expandListViewGroup(ExpandableListView listView, int groupPosition) {
        ExpandableListAdapter adapter = listView.getExpandableListAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                if (i == groupPosition) {
                    listView.expandGroup(i);
                } else {
                    listView.collapseGroup(i);
                }
            }
        }
    }

    public static void setViewTemporarilyUnclickable(View view) {
        setViewTemporarilyUnclickable(view, 1000);
    }

    public static void setViewTemporarilyUnclickable(View view, long delayMillis) {
        final WeakReference<View> viewRef = new WeakReference<View>(view);
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = viewRef.get();
                if (view != null) {
                    view.setClickable(true);
                }
            }
        }, delayMillis);
    }
}
