package com.sqiwy.menu.model;

import android.database.Cursor;

import com.sqiwy.dashboard.util.ProductLoader.ModifierGroupsQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abrysov
 */

public class ModifierGroup {
	private static final String TYPE_AND = "and";
	private static final String TYPE_OR = "or";

	protected int mId;
    protected int mParentId;
	protected String mName;
    protected String mType;
    protected String mImgUrl;
    protected int mSortIndex;
    protected boolean mEnabled;
    protected boolean mRequired;
    protected List<Modifier> mModifiers = new ArrayList<Modifier>();

    public ModifierGroup() {
    }

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public int getParentId() {
		return mParentId;
	}

	public void setParentId(int parentId) {
		mParentId = parentId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
	}

    public boolean isAndType() {
        return TYPE_AND.equalsIgnoreCase(mType);
    }

    public boolean isOrType() {
        return TYPE_OR.equalsIgnoreCase(mType);
    }

	public String getImgUrl() {
		return mImgUrl;
	}

	public void setImgUrl(String imgUrl) {
		mImgUrl = imgUrl;
	}

	public int getSortIndex() {
		return mSortIndex;
	}

	public void setSortIndex(int sortIndex) {
		mSortIndex = sortIndex;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public boolean isRequired() {
		return mRequired;
	}

	public void setRequired(boolean required) {
		mRequired = required;
	}

    public List<Modifier> getModifiers() {
        return mModifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        mModifiers = modifiers != null ? modifiers : new ArrayList<Modifier>();
    }

    public static ModifierGroup fromCursor(Cursor cursor) {
        ModifierGroup mg = new ModifierGroup();
        mg.mEnabled = true;
        mg.mId = cursor.getInt(ModifierGroupsQuery.SERVER_ID);
        mg.mParentId = cursor.getInt(ModifierGroupsQuery.PARENT_SERVER_ID);
        mg.mType = cursor.getString(ModifierGroupsQuery.TYPE);
        mg.mName = cursor.getString(ModifierGroupsQuery.NAME);
        mg.mImgUrl = cursor.getString(ModifierGroupsQuery.IMG_URL);
        mg.mSortIndex = cursor.getInt(ModifierGroupsQuery.SORT_INDEX);
        mg.mRequired = cursor.getInt(ModifierGroupsQuery.REQUIRED) != 0;
        return mg;
    }
}
