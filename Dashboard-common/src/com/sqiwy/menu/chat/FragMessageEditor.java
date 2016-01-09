package com.sqiwy.menu.chat;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.sqiwy.dashboard.R;

/**
 * Created by abrysov
 */

@SuppressLint("ValidFragment")
public class FragMessageEditor extends DialogFragment implements OnClickListener {

	EditText messageEditor;
	OnMessageEditorEventListener listener;

	public FragMessageEditor() {

	}

	public FragMessageEditor(OnMessageEditorEventListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		View view = inflater.inflate(R.layout.frag_message_editor, container);
		
		view.findViewById(R.id.btn_send_message).setOnClickListener(this);
		
		messageEditor = (EditText) view.findViewById(R.id.messageEditor);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_send_message:
				if (messageEditor.getText().toString().length() > 0) {
					listener.messageSendRequest(messageEditor.getText().toString());
					super.dismiss();
				}
				break;
		}
	}

	public interface OnMessageEditorEventListener {
		public void messageSendRequest(String mess);
	}

}
