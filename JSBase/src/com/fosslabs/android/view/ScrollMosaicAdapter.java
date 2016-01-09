package com.fosslabs.android.view;

import java.util.ArrayList;
import java.util.Random;

import com.fosslabs.android.view.ScrollMosaicLayout.ScrollMosaicLayoutParams;
import com.fosslabs.android.utils.Funs;
import com.fosslabs.android.utils.JSImageWorker;
import com.fosslabs.android.utils.JSLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScrollMosaicAdapter  extends BaseAdapter{
	private static final String TAG = "ScrollMosaicAdapter";
	
	private Context mContext;
	private ArrayList<Object> mObjects = new ArrayList<Object>();
	private ArrayList<ScrollMosaicLayoutParams> mParams = new ArrayList<ScrollMosaicLayoutParams>();
	
	private int mResId;
	private int mColumnCount = 0; 
	private boolean mEdit = true;
	private Position mPos;
	
	
	public ArrayList<Object> getObjects() {
		return mObjects;
	}

	public int getColumnCount() {
		return mColumnCount;
	}
	
	public int getRowsCount(){
		return mPos.getRowsCount();
	}
	
	public boolean isEdit() {
		return mEdit;
	}
	
	public int getResId() {
		return mResId;
	}

	
	public ScrollMosaicAdapter(Context context, int resource, int columnCount) {
		mContext = context;
		mResId = resource;
		if(columnCount < 1){
			Funs.getToast(mContext, "ScrollMosaicAdapter: ERROR mColumnCount < 1. Can not work");
			mEdit = false;
			return;
		}
		mColumnCount = columnCount;
		mPos = new Position(columnCount);
	}
	
	public void addObject(Object object, int width, int height){
		
		if(!mEdit){
			Funs.getToast(mContext, "MosaicAdapter: was error, can not add new Object");
			return;
		}
		ScrollMosaicLayoutParams mp = mPos.addObject(width, height);
		if(mp == null){
			Funs.getToast(mContext, "MosaicAdapter: ERROR can not add Object. Width too much");
			mEdit = false;
			return;
		}
		else{
			mParams.add(mp);
			mObjects.add(object);
		}
		
	}
	
	public void setChildBg(View v, int position, int reqWidth, int reqHeight){
		setChildBg(v, position, mObjects, reqWidth, reqHeight);
	}
	//��� ���� ����� ���������� ����������� � ������ - ����������
	protected void setChildBg(View v, int position, ArrayList<Object> objects, int reqWidth, int reqHeight){
		
	}
	
	@Override
	public int getCount() {
		if(mObjects.size() == mParams.size()) return mObjects.size();
		else return 0;
	}
	@Override
	public Object getItem(int position) {
		if(position < mObjects.size()) return mObjects.get(position);
		else return null;
	}
	
	public ScrollMosaicLayoutParams getItemParams(int position){
		if(position < mParams.size()) return mParams.get(position);
		else return null;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = createView(position, convertView, parent, mObjects);
		if(v != null && position < mParams.size()) v.setLayoutParams(mParams.get(position));
		return v;
	}
	
	//��� ���� ����� ���������� ����������� � ������ - ����������
	public View createView(int position, View convertView, ViewGroup parent, ArrayList<Object> objects){
		//converView == null, parent == null
		TextView tv = new TextView(mContext);
		tv.setText("Item = " + position + ": " + ((mObjects.get(position) != null )?  
				mObjects.get(position).toString() : ""));
	
        tv.setBackgroundColor(JSImageWorker.getRandomColor(256));
		return tv;
	}

	public String show (){
		
		String res;
		if(mEdit) res = mPos.show();
		else res= "mEdit == false";
		return res;
	}
	private class Position{
		private static final String TAG = "Position";
		
		private int mN; //����������� ������� = mColumnCount
		private ArrayList<int []> mRows = new ArrayList<int[]>();
		private static final int DEFAULT_VAL = -1;
		private int mX; // ����������� X-����������, � ��� ����� �������� ������ ����. �������
		private int mY; // ����������� Y-����������, � ��� ����� �������� ������ ����. �������
		private int mVal; //������� ������ ��� �����������
		
		public Position (int columnCount){
			mN = columnCount;
			
			mX = 0;
			mY = 0;
			mVal = 0;
		}
		
		public int getRowsCount(){
			return mRows.size();
		}
		private void addRow(){
			JSLog.d(TAG, "addRow() " + mRows.size());
			int [] mas = new int [mN];
			for(int i = 0; i < mN; i++)
				mas[i] = DEFAULT_VAL;
			mRows.add(mas);
			
		}
		
		public ScrollMosaicLayoutParams addObject(int width, int height){
			JSLog.d(TAG, "addObject " + width  + " " + height);
			
			ScrollMosaicLayoutParams mp = null;
			if (mX + width -1 < mN){
				mp = new ScrollMosaicLayoutParams(mX, mY, width, height);
				JSLog.d(TAG, "addObject " + width  + " " + height + " " + mX + " " + mY);
				//��������, ��� ����� ������������� ������
				for(int i = mY; i < mY + height; i++){
					if(mRows.size() == i){
						addRow();
						
					}
					int [] row = mRows.get(i);
					for(int j = mX; j < mX + width; j++){
						row [j] = mVal;
					}
					mRows.remove(i);
					mRows.add(i, row);	
				}
				//���� �������, � ��� �������� ���� ��� ������
				boolean change = false;
				for(int k = mY; k < mRows.size() && !change; k++){
					int [] row = mRows.get(k);
					for(int m = 0; m < mN; m++){
						if (row[m] == DEFAULT_VAL){
							mY = k;
							mX = m;
							change = true;
							break;
						}
					}
				}
				if(!change){
					mX = 0;
					mY++;
				}
				mVal++;
			}
			else{
				JSLog.d(TAG, "ERROR !! mX + width -1 >= mN ");
			}
			JSLog.d(TAG, "END mX mY " +mX + " " + mY + " " + mRows.size());
			return mp;
		}
		
		public String show(){
			String s = "";
			for(int k = 0; k < mRows.size(); k++){
				int [] row = mRows.get(k);
				for(int m = 0; m < mN; m++){
					s += Funs.customLenght(String.valueOf(mVal).length() + 2, "" + row[m], " ", true);
				}
				s += "\n";
			}
			
			return s;
		}
		
		
	}
}

