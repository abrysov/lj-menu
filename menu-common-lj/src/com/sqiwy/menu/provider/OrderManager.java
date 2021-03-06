package com.sqiwy.menu.provider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sqiwy.backend.api.BackendException;
import com.sqiwy.menu.MenuApplication;
import com.sqiwy.menu.MenuApplication.OnOrderEventListener;
import com.sqiwy.menu.provider.OrderManager.OnOrderManagerEvent.OrderManagerEvent;
import com.sqiwy.menu.util.CommonUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 	On the server side and in ContentProvider orders represented by the following tables
 * 	Here is the simplified scheme of relation between those tables.
 * 
 *	ORDER					ORDER_PRODUCT			ORDER_PRODUCT_MODIFIER
 *	+-----------+           +-----------+			+-------------------+
 *	|ID			|---		|ID			|---		|ID					|
 *	|...		|	|_______|ORDER_ID	|	|_______|ORDER_PRODUCT_ID	|
 *	+-----------+			|PRODUCT_ID	|---		|MODIFIER_ID		|-------(...)
 *							|...		|	|		|...				|
 *							+-----------+	|		+-------------------+
 *											|	
 *										  (...)		
 */



/**
 * Use this class to save and read order state into ContentProvider,
 * and perform order placement to server.
 * All operations is asynchronous and will be notified about their completion
 * trough {@link OnOrderManagerEvent}.
 * <br/><br/>
 * You must call {@link OrderManager#readUnplacedOrder(Order) readUnplacedOrder(Order)}
 * before any other operation. 
 * The best place to do that - right after OrderManager instantiation.
 * <br/><br/>
 * Use {@link OrderManager#addProduct(Order, OrderProduct)}
 * to add product to order. The products with modifiers always will be treated as unique
 * and stored into new record. Products without modifiers will be compared 
 * with already placed products and if similar product founded, the <code>count</code>
 * fields will be incremented<br/>
 * Use {@link OrderManager#removeProduct(Order, OrderProduct)}
 * to remove product from order. If product have <code>count</code> more than one,
 * than <code>count</code> will be decremented.<br/>
 * Use {@link OrderManager#placeOrder(Order)}
 * to send order to server.<br/>
 * 
 * @version 1.0
 */

/**
 * Created by abrysov
 */

public class OrderManager implements OnOrderEventListener{

    public static final int MAX_PRODUCT_COUNT = 20;
    public static final int MAX_PRODUCT_UNIT_COUNT = 7;

    private static MenuApplication app;
	
	private OnOrderManagerEvent listener;
	
	/**
	 * Creates OrderManager instance.
	 * @param app - MenuApplication that provides access to OperationService
	 * @param listener - {@link OnOrderManagerEvent}
	 */
	public OrderManager(MenuApplication app, OnOrderManagerEvent listener){
		OrderManager.app = app;
		app.addOnOrderEventListener(this);
		this.listener = listener;
	}
	
	
	
	/**
	 * Deleted all data about Orders.
	 * Operation is asynchronous and will not provide callback.
	 * Operation cannot be rolled back. 
	 */
	public static void deleteSessionOrders(){
		class DeleteOrder extends AsyncTask<Void, Void, Void>{
			@Override
			protected Void doInBackground(Void... params) {
                ContentResolver resolver = MenuApplication.getContext().getContentResolver();
				resolver.delete(MenuProvider.URI_ORDER_PRODUCT_MODIFIER, null, null);
				resolver.delete(MenuProvider.URI_ORDER_PRODUCT, null, null);
				resolver.delete(MenuProvider.URI_ORDER, null, null);
				return null;
			}
		}
		DeleteOrder task = new DeleteOrder();
		task.execute();
	}
	
	

	
	/*
	 * ADD PRODUCT SECTION
	 */
	
	/**
	 * Adds OrderProduct to Order. 
	 * If OrderProduct without modifications and already has been 
	 * added with the same product id to the Order - it will increase count by 1,
	 * instead of adding  new OrderProduct
	 * Please note: After this method call the changes in
	 * OrderProduct will not take effect.
	 * Method returns immediately if one of parameters is null. 
	 * @param order - the Order object with the correct 
	 * @param op 
	 */
	public void addProduct(final Order order, final OrderProduct op){
		
		if(order == null || op == null) return;
		
		class AddTask extends AsyncTask<Void, Void, Void>{

			private long opId = 0;
			
			@Override
			protected Void doInBackground(Void... params) {
				
				/*Set processed OrderProduct*/
				op.setProcessed();
				
				long orderId = findUnplacedOrderId();
				
				if(orderId != 0){
				
					order.setId((int) orderId); // TODO : ASK IF ID TYPE CAN BE CHANGED IN BACKEND
						
					this.opId = getIdenticalOrderProductId(op);
					
					if(this.opId == 0){
						insertOrderProduct(order, op);
					} else {
						op.setId(this.opId);
						incrementOrderProduct(order, op);
					}
					
				} else {
					
					ContentValues cv = new ContentValues();
					
					cv.put(DBHelper.F_TABLE_SESSION_ID, 0);
					cv.put(DBHelper.F_TIME, order.getTime());
					cv.put(DBHelper.F_COMPLETE, 0);
					cv.put(DBHelper.F_STAFF_ID, order.getStaffId());
					
					Uri uri = app.getContentResolver().insert(MenuProvider.URI_ORDER, cv);
					
					orderId = ContentUris.parseId(uri);
					order.setId((int) orderId);
					
					insertOrderProduct(order, op);

				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if(opId > 0){
					OrderManager.this.listener.onOrderManagerEvent(OrderManagerEvent.COUNT_CHANGED, opId);
				} else {
					OrderManager.this.listener.onOrderManagerEvent(OrderManagerEvent.PRODUCT_ADDED);
				}
			}
			
		}
		
		AddTask task = new AddTask();
		task.execute();
		
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	private long findUnplacedOrderId(){
		long orderId = 0;
		Cursor c = findUnplacedOrder();
		int count = c.getCount();
		if(count > 0){
			c.moveToFirst();
			orderId = c.getLong(c.getColumnIndex(DBHelper.F_ID));
			c.close();
		}
		return orderId;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	private Cursor findUnplacedOrder(){
		// SELECT _ID FROM ORDER WHERE complete = 0;
		Cursor c = app.getContentResolver().query(
				MenuProvider.URI_ORDER, 
				null, 
				DBHelper.F_COMPLETE + " = ?", 
				new String[] {"0"}, 
				null);
		return c;
	}
	
	
	
	/**
	 * Compares passed OrderProduct 
	 * with the already saved OrderProducts.
	 * OrderProduct with modifiers always will be 
	 * treated as unique.
	 * @param op
	 * @return The ID of OrderProduct or -1 if no records found
	 */
	private long getIdenticalOrderProductId(OrderProduct op){
		long result = 0;
		
		// Here is the fuck;
		if(op.getModifierList().size() > 0) return result;
		
		String productId = String.valueOf(op.getProductId());
		
		Cursor cur = OrderManager.app.getContentResolver().query(
				MenuProvider.URI_ORDER_JOIN_ORDER_PRODUCT, 
				new String[] {DBHelper.T_ORDER_PRODUCT+"."+DBHelper.F_ID + ""}, 
				DBHelper.F_PRODUCT_ID + "=? AND " + DBHelper.F_MODIFIED + "=? AND " + DBHelper.F_COMPLETE + "=?",
				new String[] {productId, "0", "0"}, 
				null);
		
		int count = cur.getCount();
		
		if(count > 0){
			cur.moveToFirst();
			int colIndex =  cur.getColumnIndex(DBHelper.F_ID);
			result = cur.getLong(colIndex);
		}
		
		Log.e("ADD OPERATION", "GET IDENTICAL = " + String.valueOf(result) );
		
		return result;
		
	}
	
	
	
	/**
	 * 
	 * @param order
	 * @param op
	 * @return
	 */
	private float insertOrderProduct(final Order order, final OrderProduct op){
		Log.e("ADD OPERATION", "INSERT");
		// Set order id in OrderProduct
		op.setOrderId((int) order.getId());
		float price = op.getPrice();
		float modifierPrice = 0;
		List<OrderProductModifier> opms = op.getModifierList();
		
		int modified = opms.size() > 0 ? 1 : 0; 
		
		/*SAVE OrderProduct*/
		ContentValues cv = new ContentValues();
		cv.put(DBHelper.F_ORDER_ID, order.getId());
		cv.put(DBHelper.F_PRODUCT_ID, op.getProductId());
		cv.put(DBHelper.F_PRICE, op.getPrice());
		cv.put(DBHelper.F_MODIFIED, modified);
		cv.put(DBHelper.F_COUNT, 1);
		
		Uri uri = app.getContentResolver().insert(MenuProvider.URI_ORDER_PRODUCT, cv);
		
		long orderProductId = ContentUris.parseId(uri);
		
		op.setId((int) orderProductId);
		
		/*SAVE OrderProductModifier-s*/
		
		for(OrderProductModifier opm : opms){
			
			cv.clear();
			
			cv.put(DBHelper.F_ORDER_PRODUCT_ID, orderProductId);
			cv.put(DBHelper.F_MODIFIER_ID, opm.getModifierId());
			
			//PRICES====================
			cv.put(DBHelper.F_PRICE, opm.getPrice());
			//==========================
			
			cv.put(DBHelper.F_COUNT, opm.getCount());
			
			uri = app.getContentResolver().insert(MenuProvider.URI_ORDER_PRODUCT_MODIFIER, cv);
			
			modifierPrice += (opm.getPrice() * opm.getCount());
			
		}
		
		order.addProduct(op);
		
		return price + modifierPrice;
		
	}
	
	
	
	/**
	 * Increments OrderProduct count by one 
	 * @param order the Order
	 * @param op that needs to be incremented
	 * @return The price value that this increment will lead to
	 */
	private float incrementOrderProduct(Order order, OrderProduct op){
		
		long ordrderProductId = op.getId();
		
		op = order.getOrderProductById(op);
		
		op.changeCount(1);
		
		Log.e("ADD OPERATION", "INCREMENT. COUNT = " + String.valueOf(op.getCount()));
		// Increase COUNT
		ContentValues cv = new ContentValues();

		cv.put(DBHelper.F_COUNT, String.valueOf(op.getCount()));
		
		int updated = app.getContentResolver().update(
				MenuProvider.URI_ORDER_PRODUCT, 
				cv,
				DBHelper.F_ID + "=?",
				new String[] {String.valueOf(ordrderProductId)});

		return updated == 1 ? op.getPrice() : 0;
		
	}


	
	/**
	 * Removes OrderProduct from Order.
	 * If OrderProduct count was more than 1, this operation
	 * will decrease count by 1.
	 * Method returns immediately if one of parameters is null.
	 * @param order
	 * @param op
	 */
	public void removeProduct(final Order order, final OrderProduct op){
		
		if(order == null || op == null) return;
		
		final long opId = op.getId();
		
		if(opId == 0) return;
		
		
		class RemoveTask extends AsyncTask<Void, Void, Void>{

			OrderManagerEvent event = null;
			
			@Override
			protected Void doInBackground(Void... params) {

				int count = op.getCount();
				
				
				if(count > 1){
					
					event = OrderManagerEvent.COUNT_CHANGED;
					
					op.changeCount(-1);
					
					ContentValues cv = new ContentValues();
					cv.put(DBHelper.F_COUNT, op.getCount());
					
					// Decrement
					OrderManager.app.getContentResolver().update(
							MenuProvider.URI_ORDER_PRODUCT, 
							cv, 
							DBHelper.F_ID + "=?", 
							new String[] {String.valueOf(opId)});
					
					
				} else {
					
					event = OrderManagerEvent.PRODUCT_REMOVED;
					
					int mCount = op.getModifierList().size();
					
					if(mCount > 0){
						
						// 1. Remove modifiers
						OrderManager.app.getContentResolver().delete(
								MenuProvider.URI_ORDER_PRODUCT_MODIFIER, 
								DBHelper.F_ORDER_PRODUCT_ID + "=?", 
								new String[] {String.valueOf(opId)}
								);
						
					}
					
					// 2. Remove product
					OrderManager.app.getContentResolver().delete(
							MenuProvider.URI_ORDER_PRODUCT, 
							DBHelper.F_ID + "=?", 
							new String[] {String.valueOf(opId)}
							);
					
					order.removeProduct(op);
					
				}
				return null;
			}
			
			@SuppressWarnings("incomplete-switch")
			@Override
			protected void onPostExecute(Void result) {
				
				switch (this.event) {
				case COUNT_CHANGED:
					OrderManager.this.listener.onOrderManagerEvent(this.event, opId);	
					break;

				case PRODUCT_REMOVED:
					OrderManager.this.listener.onOrderManagerEvent(this.event);
					break;
				}
				
			}
			
		}
		
		RemoveTask task = new RemoveTask();
		task.execute();
		
	}
	
	
	
	
	/**
	 * Read unplaced order state data. 
	 * All OrderProducts that was previously added to
	 * order but order was not placed will be returned.
	 * @param order
	 */
	public void readUnplacedOrder(final Order order){
		
		class ReadTask extends AsyncTask<Void, Void, Void>{
			
			@Override
			protected Void doInBackground(Void...params) {
				
				Cursor c = findUnplacedOrder();
				if(c.getCount() > 0){
					
					c.moveToFirst();
					
					long orderId = c.getLong(c.getColumnIndex(DBHelper.F_ID)); 
					
					order.setId(orderId);
					order.setStaffId(c.getInt(c.getColumnIndex(DBHelper.F_STAFF_ID)));
					order.setTime(c.getLong(c.getColumnIndex(DBHelper.F_TIME)));
					
					c.close();
					
					c = app.getContentResolver().query(
							MenuProvider.URI_ORDER_PRODUCT_JOIN_PRODUCT, 
//							MenuProvider.URI_ORDER_PRODUCT,
							new String[] {
									DBHelper.T_ORDER_PRODUCT+"."+DBHelper.F_ID, 
									DBHelper.F_ORDER_ID,
									DBHelper.F_PRODUCT_ID,
									DBHelper.T_PRODUCT+"."+DBHelper.F_PRICE,
									DBHelper.F_COUNT,
									DBHelper.F_NAME}, 
							DBHelper.F_ORDER_ID + "=?", 
							new String[] {String.valueOf(order.getId())}, 
							null);
					
					while(c.moveToNext()){
						
						OrderProduct op = new OrderProduct();
						
						op.setId(c.getLong(c.getColumnIndex(DBHelper.F_ID)));
						op.setOrderId(c.getLong(c.getColumnIndex(DBHelper.F_ORDER_ID)));
						op.setProductId(c.getLong(c.getColumnIndex(DBHelper.F_PRODUCT_ID)));
						op.setPrice(c.getFloat(c.getColumnIndex(DBHelper.F_PRICE)));
						op.setCount(c.getInt(c.getColumnIndex(DBHelper.F_COUNT)));
						op.setProductName(c.getString(c.getColumnIndex(DBHelper.F_NAME)));
						
                        Cursor cOpm = app.getContentResolver().query(
								MenuProvider.URI_ORDER_PRODUCT_MODIFIER, 
								null, 
								DBHelper.F_ORDER_PRODUCT_ID + "=?", 
								new String[] {String.valueOf(op.getId())}, 
								null);
						
						while(cOpm.moveToNext()){
							
							OrderProductModifier opm = new OrderProductModifier();
							
							opm.setId(cOpm.getLong(cOpm.getColumnIndex(DBHelper.F_ID)));
							opm.setModifierId(cOpm.getInt(cOpm.getColumnIndex(DBHelper.F_MODIFIER_ID)));
							opm.setOrderProductId(cOpm.getLong(cOpm.getColumnIndex(DBHelper.F_ORDER_PRODUCT_ID)));
							opm.setCount(cOpm.getInt(cOpm.getColumnIndex(DBHelper.F_COUNT)));
							opm.setPrice(cOpm.getFloat(cOpm.getColumnIndex(DBHelper.F_PRICE)));
							
							op.addModifier(opm);
							
						}
						cOpm.close();
						order.addProduct(op);
						
						op.setProcessed();
						
					}
					
				}

                c.close();
				
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void result) {
				OrderManager.this.listener.onOrderManagerEvent(OrderManagerEvent.ORDER_READED);
			}
		}
		
		ReadTask task = new ReadTask();
		task.execute();
		
	}
	
	
	
	/**
	 * Performs order placement.
	 * In other words - sends Order to the server.
	 */
	public void placeOrder(final Order order){
		
		class PlaceOrderTask extends AsyncTask<Void, Void, Boolean>{
			
			private String message = null;
			
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean result = true;
				com.sqiwy.backend.data.Order prepearedOrder = order.convert();
				try {
					OrderManager.app.getOperationService().placeOrder(prepearedOrder);
				} catch (BackendException e) {
					result = false;
					this.message = e.getMessage();
					e.printStackTrace();
				} catch (IOException e) {
					result = false;
					this.message = e.getMessage();
					e.printStackTrace();
				}
				
				if(result){
					
					ContentValues cv = new ContentValues();
					cv.put(DBHelper.F_COMPLETE, 1);
					
					app.getContentResolver().update(
							MenuProvider.URI_ORDER, 
							cv, 
							DBHelper.F_ID + "=?", 
							new String[] {String.valueOf(order.getId())}
//							DBHelper.F_COMPLETE + "=?",
//							new String[] {"0"}
							);
					
				}
				 
				return result;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				OrderManager.this.listener.onOrderManagerEvent(OrderManagerEvent.ORDER_PLACED, result, this.message);
			}
			
		}
		
		PlaceOrderTask task = new PlaceOrderTask();
		task.execute();
		
	}
	
	
	
	/**
	 * Interface definition for a callback methods to be invoked
	 * when OrderManager asynchronous operations is completed.
	 * This interface is functional. And provides additional information about
	 * event through method parameters.
	 * @see OrderManagerEvent
	 */
	public interface OnOrderManagerEvent{
		
		/**
		 * OrderManager event list.
		 * Some events will be supplied with additional parameters.<br/><br/>
		 * {@link OrderManagerEvent#COUNT_CHANGED} - Supplied with the Id of OrderProduct with changed count.
		 * <br/>
		 * {@link OrderManagerEvent#ORDER_PLACED} - Supplied with the boolean true if order successfully placed
		 * (false otherwise).
		 * <br/>
		 */
		public enum OrderManagerEvent{
			/**
			 * Product added event occurs when 
			 * OrderProduct was saved as new record in ContentProvider
			 */
			PRODUCT_ADDED,
			/**
			 * Product removed event occurs when 
			 * OrderProduct was deleted as a record in ContentProvider
			 */
			PRODUCT_REMOVED,
			/**
			 * Count changed event occurs when
			 * OrderProduct count was increased or decreased
			 */
			COUNT_CHANGED,
			/**
			 * Order readed event occurs when
			 * process of restoring order state into Order object is completed
			 */
			ORDER_READED,
			/**
			 * Order placed event occurs when
			 * process of sending order to server is complete.
			 * This event supplied with additional parameters that will
			 * tell information about success.
			 * <br/><br/>
			 * onOrderManagerEvent(OrderManagerEvent.ORDER_PLACED, Boolean, String);<br/>
			 * Boolean - true if success, false otherwise.<br/>
			 * String - null if success or message if any supplied with occurred exception.<br/> 
			 */
			ORDER_PLACED,
			/**
			 * This event should be ignored for now
			 */
			@Deprecated
			ORDER_ADDED,
			/**
			 * This event should be ignored for now
			 */
			@Deprecated
			ORDER_LIST_CHANGED
		}
		
		/**
		 * Called after asynchronous operation complete.
		 * @param event - indicates the 
		 * @param params
		 */
		void onOrderManagerEvent(OrderManagerEvent event, Object...params);
		
	}
	
	
	
	/**
	 * Stores data about order and product list that this order consist of.
	 * <br/><br/>
	 * Use {@link Order#getTotal()} to get order total amount.<br/>
	 * Use {@link Order#getProductList()} to get list of products.<br/>
	 * <br/>
	 * NOTE: OrderProduct cannot be added to or removed from Order directly, use
	 * {@link OrderManager#addProduct(Order, OrderProduct)} to add and 
	 * {@link OrderManager#removeProduct(Order, OrderProduct)} to remove
	 * OrderProduct.
	 */
	public static class Order{
		
		private long id;
		private int staffId;
		private long time;
		private List<OrderProduct> productList = new ArrayList<OrderProduct>();
		
		
		public Order(){
            this(0, System.currentTimeMillis());
		}


		public Order(int staffId, long time){
			this.staffId = staffId;
			this.time = time;
		}

		
		
		public long getId() {
			return id;
		}
		
		
		
		protected void setId(long id) {
			this.id = id;
		}
		
		
		
		public int getStaffId() {
			return staffId;
		}
		
		
		
		public void setStaffId(int staffId) {
			this.staffId = staffId;
		}
		
		
		
		public long getTime() {
			return time;
		}
		
		
		
		public void setTime(long time) {
			this.time = time;
		}
		
		
		public String getTotal() {
			float total = 0;
			for(OrderProduct op : productList){
				total += op.getRealPrice();
			}
            return CommonUtils.formatPrice(total);
		}
		
		
		public List<OrderProduct> getProductList() {
			return productList;
		}

		
		
		protected void addProduct(OrderProduct op){
			this.productList.add(op);
		}
		
		
		
		protected void removeProduct(OrderProduct op){
			this.productList.remove(op);
		}
		
		
		
		protected void removeProduct(int opId){
			for(int i=0; i < productList.size(); i++){
				if(productList.get(i).getId() == opId){
					productList.remove(i);
					return;
				}
			}
		}

		
		
		protected OrderProduct getOrderProductById(OrderProduct foreignOp){
			for(OrderProduct op : productList){
				if(op.getId() == foreignOp.getId()){
					return op;
				}
			}
			return null;
		}



		public OrderProduct getOrderProductByProductId(long productId){
			for(OrderProduct op : productList){
				if(op.getProductId() == productId){
					return op;
				}
			}
			return null;
		}

		
		/**
		 * Converts OrderManager.Order into {@link com.sqiwy.backend.data.Order}.
		 * @return
		 */
		protected com.sqiwy.backend.data.Order convert(){
			
			com.sqiwy.backend.data.Order beOrder = new com.sqiwy.backend.data.Order();
			
			beOrder.setId((int) id);
			beOrder.setStaffId(staffId);
			beOrder.setTime(new Date(time));
			
			List<com.sqiwy.backend.data.OrderProduct> beOps = 
					new ArrayList<com.sqiwy.backend.data.OrderProduct>();
			
			for(OrderProduct op : productList){
				for(int i=0; i<op.getCount(); i++){
					beOps.add(op.convert());
				}
			}
			
			beOrder.setProductList(beOps);
			
			return beOrder;
		}
	}
	
	
	
	/**
	 * OrderProduct class stores data about product and product modifiers
	 * <br/><br/>
	 * Use {@link OrderProduct#addModifier(OrderProductModifier) }
	 * 
	 */
	public static class OrderProduct{
		
		// Indicates that OrderProduct was processed and
		// cannot be modified;
		private boolean isProcessed = false;
		
		private long id;
		private long orderId;
		private long productId;
		private String productName;
		private float price;
		private int count = 1;
		private List<OrderProductModifier> opms = new ArrayList<OrderProductModifier>();
		
		
		
		public OrderProduct(){
			
		}
		
		
		
		public OrderProduct(long productId, String productName, float price){
			this.productId = productId;
			this.productName = productName;
			this.price = price;
		}
		
		
		
		protected OrderProduct(com.sqiwy.backend.data.OrderProduct op){
			
		}
		
		
		
		protected void setProcessed(){
			this.isProcessed = true;
		}
		
		
		
		public long getId() {
			return id;
		}
		
		
		
		protected void setId(long id) {
			this.id = id;
		}
		
		
		
		public long getOrderId() {
			return orderId;
		}
		
		
		
		protected void setOrderId(long orderId) {
			this.orderId = orderId;
		}
		
		
		
		public long getProductId() {
			return productId;
		}
		
		
		
		public void setProductId(long productId) {
			if(this.isProcessed) return; 
			this.productId = productId;
		}



		public String getProductName() {
			return productName;
		}



		public void setProductName(String productName) {
			if(this.isProcessed) return;
			this.productName = productName;
		}

		
		
		public float getPrice() {
			return price;
		}
		
		
		
		public void setPrice(float price) {
			if(this.isProcessed) return;
			this.price = price;
		}

		
		
		public int getCount() {
			return count;
		}
		
		
		
		protected void setCount(int count) {
			this.count = count;
		}
		
		
		
		protected void changeCount(int incrementor){
			this.count += incrementor;
		}
		
		
		
		protected float getRealPrice() {
			float realPrice = price * count;
			for(OrderProductModifier opm : opms){
				realPrice += (opm.getPrice() * opm.getCount());
			}
			return realPrice;
		}
		
		
		
		@SuppressLint("DefaultLocale")
		public String getFullPrice(){
            return CommonUtils.formatPrice(getRealPrice());
		}
		
		
		/**
		 * Returns the OrderProductModifier list.
		 * @return
		 */
		public List<OrderProductModifier> getModifierList() {
			return this.opms;
		}
		
		
		/**
		 * Adds modifier to OrderProduct
		 * @param opm
		 */
		protected void addModifier(OrderProductModifier opm){
			if(this.isProcessed) return;
			if(opm == null) return;
			this.opms.add(opm);
		}
		
		
		/**
		 * Creates and adds OrderProductModifier with the 
		 * specified <code>modifierId</code>, <code>count</code> and <code>price</code>.
		 * This method returns immediately if modifierId or count is 0.
		 * If same modifier was added before, it will increase count.
		 * @param modifierId
		 * @param price
		 */
		public void addModifier(int modifierId, float price){
			if(this.isProcessed) return;
			if(modifierId <= 0 || count <= 0) return;
			
			// Increase count if same modifier was previously added
			for(OrderProductModifier opm : opms){
				if(opm.getModifierId() == modifierId){
					opm.setCount(opm.getCount()+1);
					return;
				}
			}
			
			// Add new modifier
			this.opms.add(new OrderProductModifier(modifierId, price));
		}
		
		
		
		/**
		 * Removes modifier from product, or decrease modifier count
		 * if count greater than 1.
		 * @param modifierId
		 */
		public void removeModifier(int modifierId){
			if(this.isProcessed) return;
			if(modifierId <= 0) return;
			
			for(int i = 0; i < opms.size(); i++){
				if(opms.get(i).getModifierId() == modifierId){
					if(opms.get(i).getCount() > 1){
						opms.get(i).setCount(opms.get(i).getCount() - 1);
					} else {
						opms.remove(i);
					}
					return;
				}
			}
		}
		
		
		protected com.sqiwy.backend.data.OrderProduct convert(){
			
			com.sqiwy.backend.data.OrderProduct op =
					new com.sqiwy.backend.data.OrderProduct();
			
			op.setPrice(new BigDecimal(this.getPrice()));
			op.setProductId((int) this.getProductId());
			
			List<com.sqiwy.backend.data.OrderProductModifier> opms = 
					new ArrayList<com.sqiwy.backend.data.OrderProductModifier>();
			
			for(OrderProductModifier opm : this.opms){
				for(int i=0; i<opm.getCount(); i++){
					opms.add(opm.convert());
				}
			}
			
			return op;
		}
		
		
		
		
	}
	
	public static class OrderProductModifier{
		
		private long id;
		private long orderProductId;
		private int modifierId;
		private float price;
		private int count = 1;
		
		
		/* Default constructor */
		public OrderProductModifier(){}
		
		
		
		public OrderProductModifier(int modifierId, float price){
			if(modifierId <= 0 || count <= 0 || price < 0) return;
			this.modifierId = modifierId;
			this.price = price;
		}
		
//		protected OrderProductModifier(com.sqiwy.backend.data.OrderProductModifier opm){
//			this.id = opm.getId();
//			this.orderProductId = opm.getOrderProductId();
//			this.modifierId = opm.getModifierId();
//			this.price = opm.getPrice().floatValue();
//		}
		
		protected com.sqiwy.backend.data.OrderProductModifier convert(){
			
			com.sqiwy.backend.data.OrderProductModifier opm = 
					new com.sqiwy.backend.data.OrderProductModifier();
			
			opm.setModifierId(this.modifierId);
			opm.setPrice(new BigDecimal(this.price));
			return opm;
			
		}

		
		
		public long getId() {
			return id;
		}
		
		
		
		protected void setId(long id) {
			this.id = id;
		}
		
		
		
		public long getOrderProductId() {
			return orderProductId;
		}
		
		
		
		protected void setOrderProductId(long orderProductId) {
			this.orderProductId = orderProductId;
		}
		
		
		
		public int getModifierId() {
			return modifierId;
		}
		
		
		
		public void setModifierId(int modifierId) {
			this.modifierId = modifierId;
		}
		
		
		
		public float getPrice() {
			return price;
		}
		
		
		
		public void setPrice(float price) {
			this.price = price;
		}
		
		
		
		public int getCount() {
			return count;
		}
		
		
		
		protected void setCount(int count) {
			this.count = count;
		}
		
	}


	
	/*
	 * Delegation of Back-end order events
	 */

	@Override
	public void orderEvent(OrderEvent event) {
		switch (event) {
		
		case ORDER_ADDED:
			this.listener.onOrderManagerEvent(OrderManagerEvent.ORDER_ADDED);
			break;
			
		case ORDER_LIST_CHANGED:
			this.listener.onOrderManagerEvent(OrderManagerEvent.ORDER_LIST_CHANGED);
			break;
			
		}
		
	}
	
	
}


