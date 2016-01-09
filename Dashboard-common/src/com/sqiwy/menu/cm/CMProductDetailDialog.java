package com.sqiwy.menu.cm;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sqiwy.dashboard.R;
import com.sqiwy.dashboard.util.ProductLoader;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Modifier;
import com.sqiwy.menu.model.ModifierGroup;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.OrderManager;
import com.sqiwy.menu.resource.ResourcesManager;
import com.sqiwy.menu.resource.ResourcesManager.Category;
import com.sqiwy.menu.util.CommonUtils;
import com.sqiwy.menu.view.TypefaceTextView;
import com.squareup.picasso.Picasso;

/**
 * Created by abrysov
 */

public class CMProductDetailDialog extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Product> {
	private static final String ARG_PRODUCT_ID = "product_id";
	private static final String ARG_ORDER_PRODUCT_ID = "order_product_id";
	private static final String ARG_CHECKED_MODIFIER_IDS = "checked_modifier_ids";

	private Product mProduct;
    private long mOrderProductId;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private ImageView mProductImageView;
    private TextView mContentTextView;
    private TextView mWeightTextView, mCaloriesTextView;
    private ModifiersAdapter mModifiersAdapter;
    private View mModifiersListViewLayout;
    private ListView mModifiersListView;
    private Button mAddButton;
    private FrameLayout mAddButtonLayout;
    private String mCaloricityUnitString;

    public static void show(FragmentManager fm, int productId) {
        show(fm, productId, -1, null);
    }

    public static void show(FragmentManager fm, OrderManager.OrderProduct op) {
        int[] modifierIds = null;
        List<OrderManager.OrderProductModifier> modifiers = op.getModifierList();
        if (modifiers != null && !modifiers.isEmpty()) {
            modifierIds = new int[modifiers.size()];
            int i = 0;
            for (OrderManager.OrderProductModifier opm : modifiers) {
                modifierIds[i++] = opm.getModifierId();
            }
        }
        show(fm, (int) op.getProductId(), op.getId(), modifierIds);
    }

    private static void show(FragmentManager fm, int productId, long orderProductId,
                             int[] checkedModifierIds) {
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        args.putLong(ARG_ORDER_PRODUCT_ID, orderProductId);
        args.putIntArray(ARG_CHECKED_MODIFIER_IDS, checkedModifierIds);
        CMProductDetailDialog dialogFragment = new CMProductDetailDialog();
        dialogFragment.setArguments(args);
        dialogFragment.setStyle(STYLE_NO_TITLE, 0);
        dialogFragment.show(fm, (String) null);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mCaloricityUnitString=MenuApplication.getContext().getResources().getString(R.string.caloricityUnit);
        mOrderProductId = getArguments().getLong(ARG_ORDER_PRODUCT_ID);
        getDialog().setCanceledOnTouchOutside(true);
        View view = inflater.inflate(R.layout.cm_fragment_dialog_product_detail_wrapper,container,false); // inflater.inflate(R.layout.cm_fragment_dialog_product_detail, container, false);
        return view;
    }

    private void initializeSubViews(int detailsLayoutId) {
    	ViewGroup viewGroup = (ViewGroup) getView();
    	viewGroup.removeAllViews();
		View view = LayoutInflater.from(getActivity()).inflate(detailsLayoutId, viewGroup);
    	
    	View closeButton;
    	ViewGroup closeButtonClickExtensionLayout;
    	CloseOnClickListener closeListener;

        mNameTextView = (TextView) view.findViewById(R.id.tv_name);
        mPriceTextView = (TextView) view.findViewById(R.id.tv_price);
        mProductImageView = (ImageView) view.findViewById(R.id.iv_image);
        mContentTextView = (TextView) view.findViewById(R.id.tv_content);
        mWeightTextView = (TextView) view.findViewById(R.id.details_tv_weight);
        mCaloriesTextView=(TextView)view.findViewById(R.id.details_tv_calories);
        mAddButton = (Button) view.findViewById(R.id.btn_add);
        mAddButton.setOnClickListener(new AddOnClickListener());
        
        closeButtonClickExtensionLayout=(ViewGroup)view.findViewById(R.id.cm_fragment_detail_close_button_clickextension_layout);
        closeButton=closeButtonClickExtensionLayout.findViewById(R.id.btn_close);
        closeListener=new CloseOnClickListener();
        closeButtonClickExtensionLayout.setOnClickListener(closeListener); closeButton.setOnClickListener(closeListener);
        
        mModifiersAdapter = new ModifiersAdapter();
        mModifiersListView = (ListView) view.findViewById(R.id.lv_modifiers);
        mModifiersListView.setAdapter(mModifiersAdapter);
        mModifiersListView.setOnItemClickListener(new ModifiersOnItemClickListener());
        mModifiersListViewLayout=view.findViewById(R.id.modifiers_list_layout);
        mAddButtonLayout=(FrameLayout)view.findViewById(R.id.cm_fragment_detail_addbutton_layout);
        updateAddButton(false);
    }


    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onStart() {
        super.onStart();
        Resources res = getResources();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        lp.width = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_width);
        lp.height = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_height);;
        // lp.x = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_position);
        window.setAttributes(lp);
    }
    
    @Override
    @SuppressWarnings("ConstantConditions")
    public Loader<Product> onCreateLoader(int id, Bundle args) {
        return new ProductLoader(getActivity(), getArguments().getInt(ARG_PRODUCT_ID));
    }

    @Override
    public void onLoadFinished(Loader<Product> loader, Product data) {
        if (data != null) {
            mProduct = data;
            
            Resources res = getResources();
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            CMProductListActivity parentActivity=(CMProductListActivity)getActivity();
            int layoutId=-1;
            
            if ((!data.hasModifiers()) && (TextUtils.isEmpty(data.getImgUrl()))) {
                lp.width = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_width);
                lp.height = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_height);
                layoutId=R.layout.cm_fragment_dialog_product_detail_no_image_no_modifiers;
            } else if ((!data.hasModifiers()) && (!TextUtils.isEmpty(data.getImgUrl()))) {
                lp.width = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_image_width);
                lp.height = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_image_height);
                layoutId=R.layout.cm_fragment_dialog_product_detail_with_image_no_modifiers;
            } else if (data.hasModifiers() && TextUtils.isEmpty(data.getImgUrl())) {
                lp.width = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_modifiers_width);
                lp.height = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_modifiers_height);
                layoutId=R.layout.cm_fragment_dialog_product_detail_no_image_with_modifiers;
            } else if (data.hasModifiers() && (!TextUtils.isEmpty(data.getImgUrl()))) {
                lp.width = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_image_and_modifiers_width);
                lp.height = res.getDimensionPixelSize(R.dimen.cm_fragment_detail_with_image_and_modifiers_height);
                layoutId=R.layout.cm_fragment_dialog_product_detail;
            }
            lp.gravity=Gravity.CENTER_HORIZONTAL|Gravity.TOP;
            lp.y=parentActivity.getMenuGrandFragmentHeight()+(parentActivity.getProductListActivityHeight()-parentActivity.getMenuGrandFragmentHeight()-lp.height)/2;
            window.setAttributes(lp);
            initializeSubViews(layoutId);
            updateViews();
        }
    }

    @Override
    public void onLoaderReset(Loader<Product> loader) {
    }

    @SuppressWarnings("ConstantConditions")
    private void updateViews() {
        mNameTextView.setText(mProduct.getName());
        mWeightTextView.setText(getDishWeightOfFirstModifierWeight(mProduct));
        
        String caloricityRaw = mProduct.getCaloricity(), caloricityFormatted;
        int caloricity;
        
        if (caloricityRaw!=null && !caloricityRaw.trim().equals("")) {
        	try {
        		caloricity=Integer.parseInt(caloricityRaw);
        		caloricityFormatted=Integer.toString(Math.round(caloricity/1000)).concat(" ").concat(mCaloricityUnitString);
        	} catch (NumberFormatException ex) {
        		caloricity=0; caloricityFormatted="";
        	}
        	if (caloricity!=0) {
        		mCaloriesTextView.setVisibility(View.VISIBLE); mCaloriesTextView.setText(caloricityFormatted);
        	} else {
        		mCaloriesTextView.setVisibility(View.INVISIBLE);
        	}
        } else {
        	mCaloriesTextView.setVisibility(View.INVISIBLE);
        }

        String description = mProduct.getDesc();
        if (!TextUtils.isEmpty(description)) {
            mContentTextView.setText(description);
        } else {
            mContentTextView.setVisibility(View.GONE);
        }

        String imageUrl = mProduct.getImgUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            String name = CommonUtils.extractMenuProductImageName(imageUrl);
            File resourcePath = ResourcesManager.getResourcePath(name, Category.MENU_ORIGINAL);
            Picasso.with(getActivity()).load(resourcePath).into(mProductImageView);
        } else {
            mProductImageView.setVisibility(mProduct.hasModifiers() ? View.INVISIBLE : View.GONE);
        }

        if (mProduct.hasModifiers()) {
            mModifiersAdapter.setModifiers(mProduct.getModifierGroups(),
                    getArguments().getIntArray(ARG_CHECKED_MODIFIER_IDS));
        } else {
        	mModifiersListViewLayout.setVisibility(View.GONE);
            mModifiersListView.setVisibility(View.GONE);
            mAddButtonLayout.setBackgroundColor(Color.TRANSPARENT);
        }

        updatePrice();
    }

    /**
     * Return product dish weight or weight of the first product modifier if dish weight empty.
     * @param product
     * @return weight value or null if both weights empty
     */
    public static String getDishWeightOfFirstModifierWeight(Product product) {
        String ret=null;
        List<ModifierGroup> modGroups;
    	Iterator<ModifierGroup> it;
    	ModifierGroup modGroup;
    	List<Modifier> modifiers;
    	
        ret=product.getDishWeight();
        if (ret==null || ret.trim().equals("")) {
        	modGroups=product.getModifierGroups();
        	if (modGroups!=null && modGroups.size()>0) {
        		it=modGroups.iterator();
        		while (it.hasNext()) {
        			modGroup=it.next();
        			if (modGroup.isRequired()) {
        				modifiers=modGroup.getModifiers();
        				if (modifiers!=null && modifiers.size()>0) {
        					ret=modifiers.get(0).getWeight();
        					break;
        				}
        			}
        		}
        	}
        }
    	
    	return ret;
    }

    private void updatePrice() {
        float price = mProduct.getPrice();
        for (Modifier modifier : mModifiersAdapter.getCheckedModifiers()) {
            price += modifier.getPrice();
        }
        mPriceTextView.setText(CommonUtils.formatPrice(price));
    }

    private void updateAddButton(boolean areModifiersChanged) {
        mAddButton.setText(mOrderProductId == -1 ? R.string.btn_add
                : areModifiersChanged ? R.string.btn_change : R.string.btn_ok);
    }

    private void onModifiersChanged() {
        updatePrice();
        updateAddButton(true);
        mModifiersAdapter.notifyDataSetChanged();
    }

    private class CloseOnClickListener implements View.OnClickListener {
        @Override
        @SuppressWarnings("ConstantConditions")
        public void onClick(View v) {
            dismiss();
        }
    }

    private class AddOnClickListener implements View.OnClickListener {
        @Override
        @SuppressWarnings("ConstantConditions")
        public void onClick(View v) {
            String addButtonText = mAddButton.getText().toString();
            if (addButtonText.equals(getString(R.string.btn_add))) {
                ((CMProductListActivity) getActivity()).addProduct(
                        mProduct, mModifiersAdapter.getCheckedModifiers());
            } else if (addButtonText.equals(getString(R.string.btn_change))) {
                ((CMProductListActivity) getActivity()).updateProduct(
                        mOrderProductId, mModifiersAdapter.getCheckedModifiers());
            }
            dismiss();
        }
    }

    private class ModifiersOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mModifiersAdapter.onModifierClicked(position);
        }
    }

    private class ModifiersAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_GROUP_NAME = 0;
        private static final int VIEW_TYPE_CHECKBOX = 1;
        private static final int VIEW_TYPE_RADIO_BUTTON = 2;
        private static final int VIEW_TYPE_COUNT = 3;

        private final Context mContext;
        private final LayoutInflater mLayoutInflater;
        private final List<ModifierItem> mModifierItems = new ArrayList<ModifierItem>();

        @SuppressWarnings("ConstantConditions")
        public ModifiersAdapter() {
            mContext = getActivity();
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public void setModifiers(List<ModifierGroup> modifierGroups, int[] checkedModifierIds) {
            mModifierItems.clear();
            for (ModifierGroup modifierGroup : modifierGroups) {
                mModifierItems.add(new ModifierItem(VIEW_TYPE_GROUP_NAME, null, modifierGroup, false));

                int viewType = modifierGroup.isAndType() ? VIEW_TYPE_CHECKBOX : VIEW_TYPE_RADIO_BUTTON;
                List<Modifier> modifiers = modifierGroup.getModifiers();
                for (int i = 0; i < modifiers.size(); i++) {
                    Modifier modifier = modifiers.get(i);
                    boolean isChecked = false;
                    if (checkedModifierIds != null) {
                        int modifierId = modifier.getId();
                        for (int id : checkedModifierIds) {
                            if (id == modifierId) {
                                isChecked = true;
                                break;
                            }
                        }
                    } else {
                        isChecked = i == 0
                                && (modifierGroup.isRequired() || viewType == VIEW_TYPE_RADIO_BUTTON);
                    }
                    mModifierItems.add(new ModifierItem(viewType, modifier, modifierGroup, isChecked));
                }
            }
            notifyDataSetChanged();
        }

        public List<Modifier> getCheckedModifiers() {
            List<Modifier> modifiers = new ArrayList<Modifier>();
            for (ModifierItem item : mModifierItems) {
                if (item.isChecked) {
                    modifiers.add(item.modifier);
                }
            }
            return modifiers;
        }

        public void onModifierClicked(int position) {
            ModifierItem item = mModifierItems.get(position);
            switch (item.viewType) {
                case VIEW_TYPE_CHECKBOX:
                    item.isChecked = !item.isChecked;
                    onModifiersChanged();
                    break;
                case VIEW_TYPE_RADIO_BUTTON:
                    if (!item.isChecked) {
                        // Reset the currently checked modifier in the given group, if any.
                        for (ModifiersAdapter.ModifierItem item2 : mModifierItems) {
                            if (item2.modifierGroup == item.modifierGroup && item2.isChecked) {
                                item2.isChecked = false;
                                break;
                            }
                        }

                        item.isChecked = true;
                        onModifiersChanged();
                    }
                    break;
            }
        }

        @Override
        public int getCount() {
            return mModifierItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mModifierItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            return mModifierItems.get(position).viewType;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) != VIEW_TYPE_GROUP_NAME;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_GROUP_NAME:
                    return getGroupNameView(position, convertView, parent);
                case VIEW_TYPE_CHECKBOX:
                    return getCheckBoxView(position, convertView, parent);
                case VIEW_TYPE_RADIO_BUTTON:
                    return getRadioButtonView(position, convertView, parent);
            }
            return null;
        }

        @SuppressWarnings("ConstantConditions")
        private View getGroupNameView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(
                        R.layout.list_item_modifier_group_name, parent, false);
            }

            ModifierItem item = mModifierItems.get(position);
            ((TextView) convertView).setText(item.modifierGroup.getName() + ":");
            return convertView;
        }

        @SuppressWarnings("ConstantConditions")
        private View getCheckBoxView(int position, View convertView, ViewGroup parent) {
            CheckBoxViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(
                        R.layout.list_item_modifier_checkbox, parent, false);
                holder = new CheckBoxViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheckBoxViewHolder) convertView.getTag();
            }

            ModifierItem item = mModifierItems.get(position);
            holder.nameCheckBox.setText(item.modifier.getName());
            holder.nameCheckBox.setChecked(item.isChecked);
            setPrice(holder.priceTextView, item.modifier.getPrice());
            return convertView;
        }

        @SuppressWarnings("ConstantConditions")
        private View getRadioButtonView(int position, View convertView, ViewGroup parent) {
            RadioButtonViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(
                        R.layout.list_item_modifier_radio_button, parent, false);
                holder = new RadioButtonViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (RadioButtonViewHolder) convertView.getTag();
            }

            ModifierItem item = mModifierItems.get(position);
            holder.nameRadioButton.setText(item.modifier.getName());
            holder.nameRadioButton.setChecked(item.isChecked);
            setPrice(holder.priceTextView, item.modifier.getPrice());
            return convertView;
        }

        private void setPrice(TypefaceTextView priceTextView, float price) {
            if (price != 0) {
                priceTextView.setVisibility(View.VISIBLE);
                priceTextView.setText(CommonUtils.formatPrice(price));
            } else {
                priceTextView.setVisibility(View.GONE);
            }
        }

        private class ModifierItem {
            public int viewType;
            public Modifier modifier;
            public ModifierGroup modifierGroup;
            public boolean isChecked;

            public ModifierItem(int viewType, Modifier modifier, ModifierGroup modifierGroup,
                                boolean isChecked) {
                this.viewType = viewType;
                this.modifier = modifier;
                this.modifierGroup = modifierGroup;
                this.isChecked = isChecked;
            }
        }

        private class CheckBoxViewHolder {
            public final CheckBox nameCheckBox;
            public final TypefaceTextView priceTextView;

            public CheckBoxViewHolder(View view) {
                nameCheckBox = (CheckBox) view.findViewById(R.id.cb_name);
                priceTextView = (TypefaceTextView) view.findViewById(R.id.tv_price);
            }
        }

        private class RadioButtonViewHolder {
            public final RadioButton nameRadioButton;
            public final TypefaceTextView priceTextView;

            public RadioButtonViewHolder(View view) {
                nameRadioButton = (RadioButton) view.findViewById(R.id.rb_name);
                priceTextView = (TypefaceTextView) view.findViewById(R.id.tv_price);
            }
        }
    }
}
