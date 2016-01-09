package com.sqiwy.dashboard;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sqiwy.menu.advertisement.Advertisement;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

/**
 * Created by abrysov
 */

public class AdvertisementImageFragment extends AdvertisementBaseFragment implements Target {

	private ImageView mImageView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.advertisement_image_fragment, null);
		mImageView = (ImageView) view.findViewById(R.id.ad_image);
		mImageView.setVisibility(View.INVISIBLE);
		return view;
	}

	@Override
	protected void showAdImpl(Advertisement ad) {
		mImageView.setVisibility(View.INVISIBLE);

		if(isInternalResource(getAd().getName())) {
			
			Picasso.with(getActivity()).load(Uri.parse(getAd().getName())).noFade().into(this);
		}
		else {
		
			File image = ResourcesManager.getResourcePath(getAd().getName(), Category.ADVERTISEMENT);
			Picasso.with(getActivity()).load(image).noFade().into(this);
		}
	}

	@Override
	public void onBitmapFailed(Drawable arg) {
		mHandler.sendEmptyMessage(MSG_WHAT_COMPLETED);
	}

	@Override
	public void onBitmapLoaded(Bitmap arg, LoadedFrom from) {
		mImageView.setImageBitmap(arg);
		mImageView.setVisibility(View.VISIBLE);

		mHandler.sendEmptyMessage(MSG_WHAT_SHOWN);
		mHandler.sendEmptyMessageDelayed(MSG_WHAT_COMPLETED, getAd().getTimeShow());
	}

	@Override
	public void onPrepareLoad(Drawable arg) {}

}
