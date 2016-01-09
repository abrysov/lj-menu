package com.sqiwy.menu.cm;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sqiwy.dashboard.CancelableToast;
import com.sqiwy.dashboard.CancelableToast.VerticalAlignment;
import com.sqiwy.dashboard.ProgressDialogFragment;
import com.sqiwy.dashboard.R;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.model.Modifier;
import com.sqiwy.menu.model.Product;
import com.sqiwy.menu.provider.OrderManager;
import com.sqiwy.menu.provider.OrderManager.Order;
import com.sqiwy.menu.provider.OrderManager.OrderProduct;
import com.sqiwy.menu.view.ProductBillAdapter;

/**
 * Created by abrysov
 */

public class CMBillFragment extends Fragment implements ProductBillAdapter.OnBillChangeListener,
        OrderManager.OnOrderManagerEvent {
	private static final String TAG_DIALOG_WAIT="place_order_wait_dialog_tag";
	
	private OrderManager.Order mOrder = new OrderManager.Order();
    private OrderManager mOrderManager;
	private ProductBillAdapter mProductBillAdapter;
	private RelativeLayout mRemoveConfirmationLayout;
    private ListView mBillListView;
    private TextView mTotalPriceTextView;
    private Button mOrderButton, mClearOrderButton, mRemoveConfirmationNoButton, mRemoveConfirmationYesButton;
    private View mShadowView;

	@Override
    @SuppressWarnings("ConstantConditions")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cm_fragment_bill, container, false);
        mProductBillAdapter = new ProductBillAdapter(getActivity(), this);
        mBillListView = (ListView) view.findViewById(R.id.lv_bill);
        mBillListView.setAdapter(mProductBillAdapter);
        mBillListView.setOnItemClickListener(new BillOnItemClickListener());
        mTotalPriceTextView = (TextView) view.findViewById(R.id.tv_total_price);
        mOrderButton = (Button) view.findViewById(R.id.btn_order);
        mOrderButton.setOnClickListener(new OrderOnClickListener());
        mClearOrderButton=(Button)view.findViewById(R.id.clear_order_btn);
        mClearOrderButton.setOnClickListener(new OnClearOrderClickListener());
        mRemoveConfirmationLayout=(RelativeLayout)view.findViewById(R.id.ll_bill_removeconfirmation_layout);
        mRemoveConfirmationNoButton=(Button)view.findViewById(R.id.cm_fragment_bill_removeconfirmation_no_button);
        mRemoveConfirmationYesButton=(Button)view.findViewById(R.id.cm_fragment_bill_removeconfirmation_yes_button);
        mRemoveConfirmationNoButton.setOnClickListener(new OnOrderRemoveNoConfirmationListener());
        mRemoveConfirmationYesButton.setOnClickListener(new OnOrderRemoveConfirmedListener());
        mShadowView = view.findViewById(R.id.ll_shadow);
        view.findViewById(R.id.ll_bill).setOnDragListener(new BillOnDragListener());
        mOrderManager = new OrderManager((MenuApplication) getActivity().getApplication(), this);
        mOrderManager.readUnplacedOrder(mOrder);
        updateUI();
        return view;
	}
	
	@Override
	public void onDetach() {
		mOrderManager.detachFromApplication();
		super.onDetach();
	}
	
	
	public void addProduct(Product p, List<Modifier> checkedModifiers) {
        // If the product with the given id and modifiers was already added to the order,
        // then its count will be incremented. Otherwise, add a new product to the order.
        OrderManager.OrderProduct op = mOrder.findOrderProduct(p.getId(), checkedModifiers);
        FragmentManager fm = getActivity().getFragmentManager();
        
        if (op != null) {
            if (op.getCount() < OrderManager.MAX_PRODUCT_UNIT_COUNT) {
                mOrderManager.addProduct(mOrder, op);
            } else {
            	String text = getString(R.string.toast_max_product_unit_count, OrderManager.MAX_PRODUCT_UNIT_COUNT);
				new CancelableToast.Config()
					.setText(text)
					.setVerticalAlignment(VerticalAlignment.BOTTOM)
					.show(fm);
            }
        } else {
            if (mOrder.getProductList().size() < OrderManager.MAX_PRODUCT_COUNT) {
                op = new OrderManager.OrderProduct(p.getId(), p.getName(), p.getPrice(), p.getDishWeight());
                for (Modifier modifier : checkedModifiers) {
                    op.addModifier(modifier.getId(), modifier.getName(), modifier.getPrice());
                }
                mOrderManager.addProduct(mOrder, op);
            } else {
            	
            	String text = getString(R.string.toast_max_product_count, OrderManager.MAX_PRODUCT_COUNT);
				new CancelableToast.Config()
					.setText(text)
					.setVerticalAlignment(VerticalAlignment.BOTTOM)
					.show(fm);
            	
            }
        }
	}

    public void updateProduct(long orderProductId, List<Modifier> checkedModifiers) {
        OrderManager.OrderProduct op = mOrder.getOrderProductById(orderProductId);
        if (op != null) {
            op.removeAllModifiers();
            for (Modifier modifier : checkedModifiers) {
                op.addModifier(modifier.getId(), modifier.getName(), modifier.getPrice());
            }
            mOrderManager.updateOrderProduct(op);
        }
    }

    @Override
	public void setCount(int pos, int count) {
        // Add/remove the corresponding order product depending on its count.
        // Note that the actual product count will be updated in OrderManager.
        List<OrderManager.OrderProduct> orderProductList = mOrder.getProductList();
        if (pos >= 0 && pos < orderProductList.size()) {
            OrderManager.OrderProduct op = orderProductList.get(pos);
            int delta = count - op.getCount();
            while (delta != 0) {
                if (delta > 0) {
                    mOrderManager.addProduct(mOrder, op);
                    delta--;
                } else {
                    mOrderManager.removeProduct(mOrder, op);
                    delta++;
                }
            }
        }
    }
    
    public void onClickRemoveProductButton(int position) {
    	List<OrderManager.OrderProduct> orderProductList = mOrder.getProductList();
    	if (position >= 0 && position < orderProductList.size()) {
            OrderManager.OrderProduct op = orderProductList.get(position);
            mOrderManager.removeProduct(mOrder, op);
    	}
    }

    public void placeOrder() {
        if (!mOrder.getProductList().isEmpty()) {
            mOrderManager.placeOrder(mOrder);
            ProgressDialogFragment.newInstance(null).show(getFragmentManager(),TAG_DIALOG_WAIT);
        }
    }

    @Override
    public void onOrderCleared() {
        mOrder = new OrderManager.Order();
        mProductBillAdapter.setOrder(mOrder);
        updateUI();
    }

    @Override
    public void onOrderManagerEvent(OrderManagerEvent event, Object... params) {
        switch (event) {
            case ORDER_READED:
                mProductBillAdapter.setOrder(mOrder);
                updateUI();
                break;
            case PRODUCT_ADDED:
            case PRODUCT_REMOVED:
            case PRODUCT_UPDATED:
            case COUNT_CHANGED:
           		mProductBillAdapter.notifyDataSetChanged();
           		updateUI();
                if (event == OrderManagerEvent.PRODUCT_ADDED) {
                    mBillListView.smoothScrollToPosition(mProductBillAdapter.getCount() - 1);
                }
                break;
            case ORDER_PLACED:
                boolean isSuccess = (Boolean) params[0];
                
                dismissProgressDialog();
                if (isSuccess) {
                    mProductBillAdapter.clearOrder();
                    MessageDialogFragment.show(getString(isSuccess ? R.string.message_order_success
                            : R.string.message_order_failure), getFragmentManager());
                } else {
                	new CancelableToast.Config()
					.setText(R.string.error_placing_order)
					.setVerticalAlignment(VerticalAlignment.CENTER)
					.show(getFragmentManager());
                }
                break;
            case ORDER_CLEARED:
            	dismissProgressDialog();
    			mClearOrderButton.setVisibility(View.VISIBLE);
    			mRemoveConfirmationLayout.setVisibility(View.INVISIBLE);
            	break;
        }
    }
    
    private void dismissProgressDialog() {
        ProgressDialogFragment pdf=(ProgressDialogFragment)getFragmentManager().findFragmentByTag(TAG_DIALOG_WAIT);
        
        if (pdf!=null) {
        	pdf.dismiss();
        }
    }

    private void updateUI(){
        mTotalPriceTextView.setText(mOrder.getTotal());
        mOrderButton.setEnabled(!mOrder.getProductList().isEmpty());
    }

    private class OrderOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            OrderConfirmDialogFragment.show(
            		mOrder, 
            		getFragmentManager()
            	);
        }
    }
    
    private class OnClearOrderClickListener implements OnClickListener {
    	public void onClick(View v) {
    		if (mProductBillAdapter!=null && mProductBillAdapter.getCount()>0) {
    			mClearOrderButton.setVisibility(View.INVISIBLE);
    			mRemoveConfirmationLayout.setVisibility(View.VISIBLE);
    		}
    	}
    }
    
	private class OnOrderRemoveConfirmedListener implements View.OnClickListener {
		public void onClick(View v) {
			ProgressDialogFragment.newInstance(null).show(getFragmentManager(), TAG_DIALOG_WAIT);
			mProductBillAdapter.clearOrder();
			mOrderManager.removeUnplacedOrder(mOrder);
		}
	}
	
	private class OnOrderRemoveNoConfirmationListener implements View.OnClickListener {
		public void onClick(View v) {
			mClearOrderButton.setVisibility(View.VISIBLE);
			mRemoveConfirmationLayout.setVisibility(View.INVISIBLE);
		}
	}

    private class BillOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrderManager.OrderProduct op = (OrderManager.OrderProduct) parent.getItemAtPosition(position);
            if (op != null) {
                CMProductDetailDialog.show(getFragmentManager(), op);
            }
        }
    }

    private class BillOnDragListener implements OnDragListener {
		@Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                	if(mOrder.getProductList().size() == 0)
                		mShadowView.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DROP:
                    Product p = (Product) event.getLocalState();
                    if (p != null) {
                        // Allow a user to select modifiers if a product was dragged.
                        if (p.hasModifiers()) {
                            CMProductDetailDialog.show(getFragmentManager(), p.getId());
                        } else {
                            addProduct(p, Collections.<Modifier>emptyList());
                        }
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    mShadowView.setVisibility(View.INVISIBLE);
                    return true;
            }
            return false;
        }
    }

    
    
    
    /*
     * This dialog will not work correctly if it 
     * gets restored from saved instance state
     */
    public static class OrderConfirmDialogFragment extends DialogFragment implements OnClickListener{
        
    	private LayoutInflater inflater; 
        private OrderManager.Order order;
    	
        public static void show(Order order, FragmentManager fm) {
            OrderConfirmDialogFragment dialogFragment = new OrderConfirmDialogFragment();
            dialogFragment.setOrder(order);
            dialogFragment.show(fm, null);
        }
        
        
        public void setOrder(OrderManager.Order order){
        	this.order = order;
        }
        
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

        	Context context = getActivity();
        	this.inflater = getActivity().getLayoutInflater();
            
            String total = order.getTotal();
            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            
            View view = inflater.inflate(R.layout.cm_dialog_confirmation, null);
            
            Button btnOk = (Button) view.findViewById(R.id.btn_order);
            btnOk.setOnClickListener(this);
            
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(this);
            
            TextView tvTotalPrice = (TextView) view.findViewById(R.id.tv_total_price);
            tvTotalPrice.setText(total);
            
            ListView listView = (ListView) view.findViewById(R.id.list);
            ConfirmListAdapter adapter = new ConfirmListAdapter(order.getProductList());
            listView.setAdapter(adapter);
            
            builder.setView(view);
            
            Dialog dialog = builder.create();
            
            dialog.setCanceledOnTouchOutside(true);
            
            return dialog;
        }
        
        
        
        private class ConfirmListAdapter extends BaseAdapter{
        	
        	private List<OrderProduct> list;
        	
        	public ConfirmListAdapter(List<OrderProduct> list){
        		this.list = list;
        	}
        	
        	@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public Object getItem(int position) {
				return this.list.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder vh;
				
				if(convertView == null){
					
					vh = new ViewHolder();
					
					convertView = inflater.inflate(R.layout.item_confirm_dialog, null);
					
					vh.tvCount = (TextView) convertView.findViewById(R.id.tv_count); 
					vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
					vh.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
//					vh.tvModifiers = (TextView) convertView.findViewById(R.id.tv_modifiers);
					
					convertView.setTag(vh);
					
				} else {
					vh = (ViewHolder) convertView.getTag();
				}
				
				vh.tvCount.setText(String.valueOf(this.list.get(position).getCount()));
				vh.tvName.setText(this.list.get(position).getProductName());
//				vh.tvName.setText("vh.tvName.setText(this.list.get(position).getProductName());vh.tvName.setText(this.list.get(position).getProductName());vh.tvName.setText(this.list.get(position).getProductName());");
				vh.tvPrice.setText(this.list.get(position).getFullPrice());
				
//				StringBuilder sb = new StringBuilder();
//				
//				for(OrderProductModifier opm : this.list.get(position).getModifierList()){
//					if(sb.length() > 0) sb.append(", ");
//					sb.append(opm.getName());
//				}
//				
//				vh.tvModifiers.setText(sb.toString());
				
				return convertView;
			}
        	
			private class ViewHolder{
				public TextView tvCount;
				public TextView tvName;
				public TextView tvPrice;
//				public TextView tvModifiers;
			}
			
        }
        
        
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_order:
				((CMProductListActivity) getActivity()).placeOrder();
				this.dismiss();
				break;
			case R.id.btn_cancel:
				this.dismiss();
				break;

			}
		}
    }

    public static class MessageDialogFragment extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static void show(String message, FragmentManager fm) {
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            MessageDialogFragment dialogFragment = new MessageDialogFragment();
            dialogFragment.setArguments(args);
            dialogFragment.show(fm, null);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(R.string.btn_ok, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }
    }
}
