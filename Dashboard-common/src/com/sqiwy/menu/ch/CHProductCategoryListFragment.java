package com.sqiwy.menu.ch;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sqiwy.dashboard.R;
import com.sqiwy.menu.model.ProjectConstants;
import com.sqiwy.menu.view.ProductCategoryAdapter;

/**
 * Created by abrysov
 */

public class CHProductCategoryListFragment extends Fragment {
	private static final String TAG = "CMProductCategoryListFragment";
	private static final String EXTRA_SELECTED_POS = "EXTRA_SELECTED_POS";
	
	private int mSelectedPos = -1;
	private ProductCategoryAdapter mAdapter;
	private boolean mStart = true;
	
	private class ViewHolder {
		ListView lv;
		LinearLayout ll_parent;
	}

	private ViewHolder vh = new ViewHolder();
	private Callbacks mCallbacks;

	public interface Callbacks {
		void changeProductCategory(int id);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;

	}

	public static CHProductCategoryListFragment newInstance(int selected_pos) {
		Bundle args = new Bundle();
		args.putInt(EXTRA_SELECTED_POS, selected_pos);
		CHProductCategoryListFragment fragment = new CHProductCategoryListFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectedPos = getArguments().getInt(EXTRA_SELECTED_POS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.cm_fragment_categories, null);
		createView(v);
		return v;
	}

	private void createView(View v) {
		vh.ll_parent = (LinearLayout) v.findViewById(R.id.ll_parent);
		vh.lv = (ListView) v.findViewById(R.id.lv_categories);
		
		mAdapter = new ProductCategoryAdapter(getActivity(),
				R.layout.list_item_product_category,
				//JSTODO ServerQuery.getProductListCategory()
				null, mSelectedPos);
		
		vh.lv.setVisibility(View.VISIBLE);
		vh.lv.setDivider(null);
		vh.lv.setAdapter(mAdapter);
		vh.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				mAdapter.setSelected(pos, view);
				mCallbacks.changeProductCategory(pos);
				//mAdapter.notifyDataSetChanged();
			}
		});
	}


	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if(mStart) animTop(vh.ll_parent);
		mStart = false;
	}
	private void animTop(View v){
		long anim = ProjectConstants.getAnimDuration(getActivity());
		
		ObjectAnimator animator_x = ObjectAnimator
				.ofFloat(v, View.ALPHA, 0f, 1f);
		animator_x.setDuration(anim);
		
		int y= v.getTop();
		//if(mStart ) y += vh.ll_parent.getPaddingTop();
		ObjectAnimator animator_y = ObjectAnimator
				.ofFloat(v, "y", -1400, y);
		animator_y.setDuration(anim);
				
		AnimatorSet set = new AnimatorSet();
		set.setInterpolator(new LinearInterpolator());
		set.playTogether(animator_x, animator_y);
		set.start();
	}
}
