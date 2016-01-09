package com.sqiwy.menu.model;

/**
 * Created by abrysov
 */

public class Table {
	private static final String TAG = "Room";
	
	protected int mId;
    protected int mRoomId;
    protected String mCode;
    protected int mX;
    protected int mY;
    protected int mWidth;
    protected int mHeight;

    public Table() {
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getRoomId() {
		return mRoomId;
	}

	public void setRoomId(int roomId) {
		mRoomId = roomId;
	}

	public String getCode() {
		return mCode;
	}

	public void setCode(String code) {
		mCode = code;
	}

	public int getX() {
		return mX;
	}

	public void setX(int x) {
		mX = x;
	}

	public int getY() {
		return mY;
	}

	public void setY(int y) {
		mY = y;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	public int getHeight() {
		return mHeight;
	}

	public void setHeight(int height) {
		mHeight = height;
	}
    
    
}
