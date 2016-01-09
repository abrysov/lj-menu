package com.sqiwy.menu.cm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.util.CategoriesLoader;
import com.sqiwy.menu.model.Category;
import com.sqiwy.menu.model.ProductCategory;
import com.sqiwy.menu.model.TopProductCategory;

/**
 * Created by abrysov
 */

public class CMCategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TopProductCategory>>,
															  OnGroupClickListener, 
															  OnChildClickListener{
	
    private static final String STATE_SELECTED_CATEGORY_ID = "selected_category_id";
	private CMCategoriesAdapter mCategoriesAdapter;
    private ExpandableListView mCategoriesListView;
    private int mSelectedCategoryId;
    private OnFragCategoryEventListener listener;
    
    
    /**
     * 
     */
    public interface OnFragCategoryEventListener {
        public void onCategoryChanged(int categoryId);
    }
    
    
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	try {
			this.listener = (OnFragCategoryEventListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("Activity must implement OnFragCategoryEventListener");
		}
    }
    
    
    
    /*
     * 
     */
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cm_fragment_categories, container, false);
        mCategoriesAdapter = new CMCategoriesAdapter();
        mCategoriesListView = (ExpandableListView) view.findViewById(R.id.lv_categories);
        mCategoriesListView.setAdapter(mCategoriesAdapter);
        mCategoriesListView.setOnGroupClickListener(this);
        mCategoriesListView.setOnChildClickListener(this);
        mSelectedCategoryId = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_SELECTED_CATEGORY_ID)
                : ((CMProductListActivity) getActivity()).getInitialCategoryId();
        return view;
	}

    
    
    /*
     * 
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    
    
    /*
     * 
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_CATEGORY_ID, mSelectedCategoryId);
    }

    
    
    /*
     * 
     */
    @Override
    public Loader<List<TopProductCategory>> onCreateLoader(int id, Bundle args) {
        return new CategoriesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<TopProductCategory>> loader, List<TopProductCategory> data) {
    	TopProductCategory startCategory=null;
    	ArrayList<ProductCategory> levelTwoCats;
    	int startCategoryPos=0, minSortingValuePos=-1;
    	boolean isExpandingSubcatRequired=false;
        mCategoriesAdapter.setCategories(data);
		if (mSelectedCategoryId == -1) {
			startCategory=data.get(0);
			mSelectedCategoryId = startCategory.getId();
		} else {
			Iterator<TopProductCategory> catIt=data.iterator();
			TopProductCategory nextCat;
			
			while (catIt.hasNext()) {
				nextCat=catIt.next();
				if (nextCat.getId()==mSelectedCategoryId) {
					startCategory=nextCat; break;
				}
				startCategoryPos++;
			}
		}
		if (startCategory!=null) {
			// Set ExpandableView selection to 1st subcategory of selected top-level category (if subcategories exists)
			// Assuming that only one categories nesting level present (top level category -> subcategory -> final products)
			levelTwoCats=startCategory.getList();
			if (levelTwoCats!=null && levelTwoCats.size()>0) {
				int minSortingValue=Integer.MAX_VALUE, sortingValue, pos=0;
				Iterator<ProductCategory> subcatIt=levelTwoCats.iterator();
				ProductCategory subcat;
				
				while (subcatIt.hasNext()) {
					subcat=subcatIt.next(); sortingValue=subcat.getSortIndex();
					if (sortingValue<minSortingValue) {
						minSortingValue=sortingValue; minSortingValuePos=pos;
					}
					pos++;
				}
				mCategoriesListView.expandGroup(startCategoryPos);
				mSelectedCategoryId=levelTwoCats.get(minSortingValuePos).getId();
				isExpandingSubcatRequired=true;
			}
		}
		mCategoriesAdapter.notifyDataSetChanged();
		if (isExpandingSubcatRequired) {
			mCategoriesListView.post(new ClickEmulateRunnable(mCategoriesListView,startCategoryPos,minSortingValuePos));
		}
		this.listener.onCategoryChanged(mSelectedCategoryId);
    }
    
    private class ClickEmulateRunnable implements Runnable {
    	private ExpandableListView mParent;
    	private int mTopCategoryPos, mSubcatPos;
    	
    	public ClickEmulateRunnable(ExpandableListView parent, int topCategoryPos, int subcatPos) {
    		mParent=parent; mTopCategoryPos=topCategoryPos; mSubcatPos=subcatPos;
    	}
    	
    	public void run() {
			int childrenCount = mCategoriesAdapter.getChildrenCount(mTopCategoryPos);
			int scrollPosition = mParent.getFlatListPosition(
	                ExpandableListView.getPackedPositionForChild(mTopCategoryPos, childrenCount - 1));
	        
			int boundPosition = mParent.getFlatListPosition(
	                ExpandableListView.getPackedPositionForGroup(mTopCategoryPos));
			
	        
			setCurrentPosition(mTopCategoryPos);
			mParent.smoothScrollToPosition(scrollPosition, boundPosition);
    	}
    }

    @Override
    public void onLoaderReset(Loader<List<TopProductCategory>> loader) {
    }

    

    /**
     * 
     *
     */
    private class CMCategoriesAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater mLayoutInflater;
        private List<TopProductCategory> mTopCategories = new ArrayList<TopProductCategory>();

        public CMCategoriesAdapter() {
            mLayoutInflater = LayoutInflater.from(getActivity());
        }

        public void setCategories(List<TopProductCategory> categories) {
            mTopCategories = categories != null ? categories : new ArrayList<TopProductCategory>();
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return mTopCategories.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mTopCategories.get(groupPosition).getList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mTopCategories.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mTopCategories.get(groupPosition).getList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return mTopCategories.get(groupPosition).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return mTopCategories.get(groupPosition).getList().get(childPosition).getId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            
        	ViewHolder holder;
            
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_product_category, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            Category category = mTopCategories.get(groupPosition);
            boolean isSelected = category.getId() == mSelectedCategoryId;
            
            /* Expander icon */
            holder.expander.setVisibility(getChildrenCount(groupPosition) > 1 ? View.VISIBLE : View.INVISIBLE);
            holder.categoryTextView.setText(category.getName());
            
            
            /* Text view color */
            
            if(isExpanded && isSelected){
            	holder.categoryTextView.setTextColor(Color.WHITE);
            	holder.categoryLayout.setBackgroundResource(R.drawable.menu_list_child_active);
            } else if(isExpanded) {
            	holder.categoryTextView.setTextColor(getResources().getColor(R.color.category_group_text_selected));
            	holder.categoryLayout.setBackgroundResource(R.drawable.menu_list);
            	if(holder.expander.getVisibility() == View.VISIBLE){
            		holder.expander.setImageDrawable(getResources().getDrawable(R.drawable.arrow_top));
            	}
            }else if(isSelected){
            	holder.categoryTextView.setTextColor(Color.WHITE);
            	holder.categoryLayout.setBackgroundResource(R.drawable.menu_list_child_active);
            } else  {
            	holder.categoryTextView.setTextColor(Color.BLACK);
            	holder.categoryLayout.setBackgroundResource(R.drawable.menu_list);
            	if(holder.expander.getVisibility() == View.VISIBLE){
            		holder.expander.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
            	}
            	
            }
            
            
            holder.isExpanded = isExpanded;
            
            return convertView;
        }

        
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_product_category_child, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            Category category = mTopCategories.get(groupPosition).getList().get(childPosition);
            boolean isSelected = category.getId() == mSelectedCategoryId;
            
            holder.categoryTextView.setText(category.getName());
            holder.categoryLayout.setBackgroundResource(isSelected ? R.drawable.menu_list_child_active : R.drawable.menu_list_active);
            
            return convertView;
            
        }


    }
    
    
    public static class ViewHolder {
        public final RelativeLayout categoryLayout;
        public final TextView categoryTextView;
        public final ImageView expander;
        public boolean isExpanded;
        public ViewHolder(View view) {
            categoryLayout = (RelativeLayout) view.findViewById(R.id.layout_category);
            categoryTextView = (TextView) view.findViewById(R.id.tv_category);
            expander = (ImageView) view.findViewById(R.id.expander);
        }
    }
    
    /**
     * Dirty hack method to set top-category position when emulating click from dashboard 
     * @param p
     */
    private void setCurrentPosition(int p) {
    	currentGroupPosition=p;
    }
    
    /* CLICK HANDLING */
    private int currentGroupPosition;
    
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long categoryId) {
		
		ViewHolder vh = (ViewHolder) v.getTag();

		int childrenCount = mCategoriesAdapter.getChildrenCount(groupPosition);
		
		boolean isEmpty = childrenCount > 1 ? false : true;
		
		if(currentGroupPosition == groupPosition && (vh.isExpanded && !isEmpty)){
			mSelectedCategoryId = (int) mCategoriesAdapter.getGroupId(groupPosition);
			parent.collapseGroup(this.currentGroupPosition);
			return true;
		}
		
		TopProductCategory top = (TopProductCategory) mCategoriesAdapter.getGroup(groupPosition);
		
		mSelectedCategoryId = isEmpty ? (int) mCategoriesAdapter.getGroupId(groupPosition) : top.getList().get(0).getId();
		
		parent.collapseGroup(this.currentGroupPosition);
		parent.expandGroup(this.currentGroupPosition = groupPosition);
		
		if(!isEmpty){
			
			int scrollPosition = parent.getFlatListPosition(
	                ExpandableListView.getPackedPositionForChild(groupPosition, childrenCount - 1));
	        
			int boundPosition = parent.getFlatListPosition(
	                ExpandableListView.getPackedPositionForGroup(groupPosition));
	        
	        parent.smoothScrollToPosition(scrollPosition, boundPosition);
		}
        
		this.listener.onCategoryChanged(mSelectedCategoryId);
        return true;
	}
    
    
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		mSelectedCategoryId = (int) mCategoriesAdapter.getChildId(groupPosition, childPosition);
		mCategoriesAdapter.notifyDataSetChanged();
		this.listener.onCategoryChanged(mSelectedCategoryId);
        return true;
	}
	
}
