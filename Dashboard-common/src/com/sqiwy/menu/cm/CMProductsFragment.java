package com.sqiwy.menu.cm;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.util.ProductsLoader;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.model.WeightBlock;
import com.sqiwy.menu.model.WeightBlockCM;
import com.sqiwy.menu.model.WeightItem;
import com.sqiwy.menu.server.DBHelperProductCM;
import com.sqiwy.menu.view.CMWeightBlockViewHolder;

/**
 * Created by abrysov
 */

public class CMProductsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>> {
    private static final String ARG_CATEGORY_ID = "category_id";

    private CMProductsAdapter mProductsAdapter;
    private ListView mProductsListView;

	@Override
    @SuppressWarnings("ConstantConditions")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        mProductsAdapter = new CMProductsAdapter();
        mProductsListView = (ListView) view.findViewById(R.id.lv_products);
        mProductsListView.setAdapter(mProductsAdapter);
        ArrowOnClickListener listener = new ArrowOnClickListener();
        view.findViewById(R.id.iv_top).setOnClickListener(listener);
        view.findViewById(R.id.iv_bottom).setOnClickListener(listener);
        return view;
	}

    @SuppressWarnings("ConstantConditions")
    public void showProducts(int categoryId) {
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        return new ProductsLoader(getActivity(), args.getInt(ARG_CATEGORY_ID));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> products) {
        if (products != null) {
            List<WeightBlockCM> blocks = WeightBlockCM.buildBlocks(products);
            mProductsAdapter.setBlocks(blocks);
            mProductsListView.setSelectionAfterHeaderView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {
    	mProductsAdapter.setBlocks(null);
    }

    private class ArrowOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int distance = v.getId() == R.id.iv_top
                    ? -mProductsListView.getHeight() : mProductsListView.getHeight();
            mProductsListView.smoothScrollBy(distance,
                    getResources().getInteger(android.R.integer.config_mediumAnimTime));
        }
    }

    private class CMProductsAdapter extends BaseAdapter {
        private List<WeightBlockCM> mBlocks = new ArrayList<WeightBlockCM>();

        public void setBlocks(List<WeightBlockCM> blocks) {
            mBlocks = blocks != null ? blocks : new ArrayList<WeightBlockCM>();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mBlocks.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlocks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return WeightBlockCM.Type.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return mBlocks.get(position).getType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            int type = getItemViewType(position);
            if (rowView == null || type != ((CMWeightBlockViewHolder) rowView.getTag()).getType()) {
                rowView = CMWeightBlockViewHolder.createView(type, parent);
                rowView.setFocusable(false);
            }
            WeightBlock wb = (WeightBlock) getItem(position);
            ((CMWeightBlockViewHolder) rowView.getTag()).populate(wb);
            return rowView;
        }
    }
}
