package com.sqiwy.dashboard;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by abrysov
 */

public class ProgressDialogFragment extends DialogFragment {
	private static final String ARG_MESSAGE="progress_dialog_message";
	
	public static ProgressDialogFragment newInstance(String message) {
		ProgressDialogFragment ret=new ProgressDialogFragment();
		Bundle args=new Bundle();
		
		if (!TextUtils.isEmpty(message)) {
			args.putString(ARG_MESSAGE, message);
		}
		ret.setArguments(args);
		
		return ret;
	}
	
	@Override
	public void onStart() {
        super.onStart();
        Resources res = getResources();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        lp.width = res.getDimensionPixelSize(R.dimen.fragment_progress_window_width);
        lp.height = res.getDimensionPixelSize(R.dimen.fragment_progress_window_height);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(lp);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false); setStyle(DialogFragment.STYLE_NO_FRAME,0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args=getArguments();
		LayoutInflater li=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup layout=(ViewGroup)li.inflate(R.layout.fragment_progress, null);
		TextView textView=(TextView)layout.findViewById(R.id.fragment_progress_text);
		
		// ret.setMessage(args.containsKey(ARG_MESSAGE)?args.getString(ARG_MESSAGE):getActivity().getResources().getString(R.string.message_wait));
		// ret.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// ret.setCancelable(false);
		// ret.setCanceledOnTouchOutside(false);
		if (args.containsKey(ARG_MESSAGE)) {
			textView.setText(args.getString(ARG_MESSAGE));
		}
		
		return layout;
	}
}
