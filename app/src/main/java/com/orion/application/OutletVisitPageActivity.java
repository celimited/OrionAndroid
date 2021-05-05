package com.orion.application;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.adapter.ProductListSheetAdapter;
import com.orion.adapter.SkuListAdapter;
import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.AllOrderList;
import com.orion.entities.Bonus;
import com.orion.entities.Image;
import com.orion.entities.MarketReturnItem;
import com.orion.entities.Order;
import com.orion.entities.OrderItem;
import com.orion.entities.Outlet;
import com.orion.entities.Sku;
import com.orion.entities.User;
import com.orion.util.Util;
import com.orion.webservice.Caller;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class OutletVisitPageActivity extends AppCompatActivity {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
    private static final int STORAGE_PERMISSION_REQCODE = 99;

    public static OutletVisitPageActivity outletVisitPageActivity;
	private ArrayList<OrderItem> newOrderList;
	private ArrayList<Integer> syncType;
	private ArrayList<Sku> skuList;
	private AllOrderList previousOrderList;
	public Outlet outlet;
	public Order order;
	private GridView skuListView;
	private SkuListAdapter skuListAdapter;
	private User user;
	private Sku selectedSku;
	private OrderItem selectedOrder;

	private RadioButton notOrderedRadioButton;
	private RadioButton orderedRadioButton;
	private TextView outletNameView;
	private TextView orderValueView;
	private SearchView searchView;
	private int orderStatus = NOT_ORDERED;
	private double orderTotal;
	private Context context;
	private NumberFormat moneyFormat;
	public ArrayList<MarketReturnItem> addedMarketReturnItemList;
	public ArrayList<MarketReturnItem> previousMarketReturnItemList;
	public ArrayList<MarketReturnItem> removedMarketReturnItemList;
	private Location outletLocation;
	private Location orderLocation;

	private SharedPreferences sharedpreferences;
	private String previousLatitudeKey = "PreviousLatitudeKey";
	private String previousLongitudeKey = "PreviousLongitudeKey";
	private String sectionId;
	private int orderNo;
	private String startTime;
	private String endTime;
	private String currentOrderDate;
    private AlertDialog imageSourceDialog;

    private GridLayoutManager lLayout;
    private BottomSheetDialog dialog;
    private List<OrderItem> mBottomSheedProductList = new ArrayList<>();
	private String imagePath = "";


	private static final int INSERT = 5;
	private static final int DELETE = 6;
	private static final int UPDATE = 7;
	private static final int NOT_ORDERED = 0;
	private static final int ORDERED = 1;

	public static final int ORDERED_SKU = 200;
	public static final int NOT_ORDERED_SKU = 100;
	public static final int CHECK_OUT = 300;
	public static final int SPACE_MANAGEMENT = 400;
	private int gpsTryCount=0;
	private int paymentMode = 0;
	private int deliverymode=0;
	private String deliverydate ;
	public static boolean isGPSworkDone;
    private String TAG = "Trace";
    private ProductListSheetAdapter bottomSheetAdapter;
    private int selectedImageType = -1;
    private Uri mImageCaptureUri;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outlet_visit_page_layout);
		context = this;
		outletVisitPageActivity = this;

		moneyFormat = NumberFormat.getNumberInstance();
		moneyFormat.setMinimumFractionDigits(2);
		moneyFormat.setMaximumFractionDigits(2);

		newOrderList = new ArrayList<>();
		syncType = new ArrayList<>();

		outletNameView = (TextView) findViewById(R.id.outlet_name);
		orderValueView = (TextView) findViewById(R.id.order_value);
		this.sectionId = getIntent().getStringExtra(DatabaseConstants.tblSection.SECTION_ID);
		user = DatabaseQueryUtil.getUser(context);
		user.WillTrackGPS = 1;


		skuListView = (GridView) findViewById(R.id.sku_list_view);
		skuListAdapter = new SkuListAdapter(context);
		skuListView .setAdapter(skuListAdapter);
		skuListView .setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
									int position, long id) {

				selectedSku = skuListAdapter.getItem(position);
				view.setSelected(true);
				view.setPressed(true);
				skuSelectedAction(selectedSku);

			}
		});

		searchView = (SearchView) findViewById(R.id.sku_search_bar);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				if(Objects.equals(s, "")) {
					updateSkuList();
					skuListView.setAdapter(skuListAdapter);
				}
				else
					updateSkuList(s);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				if(Objects.equals(s, "") || s.isEmpty()) {
					skuListAdapter = new SkuListAdapter(context);
					updateSkuList();
					skuListView.setAdapter(skuListAdapter);
				}
				else {
					skuListAdapter = new SkuListAdapter(context);
					//updateSkuList();
					updateSkuList(s);
					skuListView.setAdapter(skuListAdapter);
				}
				return true;
			}
		});


		sharedpreferences = getSharedPreferences("PreviousStoredLocation",
				Context.MODE_PRIVATE);


		notOrderedRadioButton = (RadioButton) findViewById(R.id.not_ordered_radio_button);
		orderedRadioButton = (RadioButton) findViewById(R.id.ordered_radio_button);

		notOrderedRadioButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
												 boolean arg1) {
						if (button.isChecked()) {
							orderStatus = NOT_ORDERED;
							updateItemList(selectedSku, orderStatus);
						}
					}
				});
		notOrderedRadioButton.setChecked(true);
		orderedRadioButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
												 boolean arg1) {
						if (button.isChecked()) {
							orderStatus = ORDERED;
							updateItemList(selectedSku, orderStatus);
						}
					}
				});
		Button orderedButton = (Button) findViewById(R.id.ordered_button);
        orderedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
                updateItemList(selectedSku, orderStatus);
                createDialog(ORDERED);

			}
		});
		Button viewMemoButton = (Button) findViewById(R.id.view_memo_button);
		viewMemoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewMemoButtonClickedAction();

			}
		});

		Button outButton = (Button) findViewById(R.id.out_button);
		outButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				outButtonClickedAction();
			}
		});
		(new updatePage()).execute();

        // Create the fragment and show it as a dialog.
        //DialogFragment newFragment = PromotionsListActivity.newInstance();
        //newFragment.show(getSupportFragmentManager(), "dialog");

        createImagePickDialog();
    }



	public class updatePage extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Util.showWaitingDialog(context);
		}

		@Override
		protected void onPostExecute(Void result) {
			Util.cancelWaitingDialog();
			Calendar clndr = Calendar.getInstance();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			currentOrderDate = sd.format(clndr.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			startTime = sdf.format(clndr.getTime());
			outletNameView.setText(outlet.description);
			orderTotal = DatabaseQueryUtil.getOrderTotalFromTblOrder(context, getIntent()
					.getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID), user.visitDate);
			orderValueView.setText(moneyFormat.format(orderTotal));
			addedMarketReturnItemList = new ArrayList<>();
			removedMarketReturnItemList = new ArrayList<>();
			updateSkuList();
		}

		@Override
		protected Void doInBackground(Void... params) {
			initializeFields();
			return null;
		}
	}

	@Override
	protected void onResume() {
		updateItemList(selectedSku, orderStatus);
		if(selectedSku != null) {
			skuList.remove(selectedSku);
			skuListAdapter.removeItem(selectedSku);
		}
		skuListView .setAdapter(skuListAdapter);
		//updateSkuList();
		super.onResume();
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.outlet_visit_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_ordered){
            orderStatus = ORDERED;
            updateItemList(selectedSku, orderStatus);
            createDialog(ORDERED);
            orderStatus = NOT_ORDERED;
        }else if (menuItemId == R.id.action_picture_outlet){
            selectedImageType = Image.IMAGE_TYPE_OUTLET_FRONT;
            getPicture();
		}else if (menuItemId == R.id.action_picture_products){
            selectedImageType = Image.IMAGE_TYPE_PRODUCT_SHELF;
            getPicture();
		}else if (menuItemId == R.id.action_picture_program){
            selectedImageType = Image.IMAGE_TYPE_PROMO_PROGRAM;
            getPicture();
		}
		return true;
	}


	private void outButtonClickedAction() {
		Bonus bonus = null;
		if (bonus == null) {
			checkOut();
		} else {
			Util.showWaitingDialog(context);
			Intent intent = new Intent(OutletVisitPageActivity.this,
					SpaceManagementPageActivity.class).putExtra(
					DatabaseConstants.tblOutlet.OUTLET_ID, outlet.outletId);
			startActivityForResult(intent, SPACE_MANAGEMENT);
		}
	}

	public void checkOut() {
		Util.showWaitingDialog(context);

		double riv = 0;
		for (MarketReturnItem item : previousMarketReturnItemList)
			riv += item.price * item.qty;

		for (MarketReturnItem item : removedMarketReturnItemList)
			riv -= item.price * item.qty;

		for (MarketReturnItem item : addedMarketReturnItemList)
			riv += item.price * item.qty;

		Intent intent = new Intent(OutletVisitPageActivity.this,
				CheckOutActivity.class)
				.putExtra(DatabaseConstants.tblOutlet.DESCRIPTION,
						outlet.description)
				.putExtra(
						DatabaseConstants.tblOrder.ORDER_TOTAL,
						previousOrderList.orderedList.size() != 0 ? orderTotal
								: 0)
				.putExtra(DatabaseConstants.tblOrder.OUTLET_ID, outlet.outletId)
				.putExtra(DatabaseConstants.otherFields.RETURN_ITEM_VALUE, riv);

		startActivityForResult(intent, CHECK_OUT);
	}

	private void viewMemoButtonClickedAction() {
		Util.showWaitingDialog(context);
		Intent intent = new Intent(OutletVisitPageActivity.this,
				ViewMemoActivity.class).putExtra(
				DatabaseConstants.tblOutlet.OUTLET_ID, outlet.outletId)
				.putExtra(DatabaseConstants.tblOutlet.CHANNEL_ID,
						outlet.channelId)
				.putExtra(DatabaseConstants.tblSection.SECTION_ID, sectionId);;
		startActivity(intent);
	}

	private void orderedItemSelectedAction(OrderItem selectedOrder) {
		Util.showWaitingDialog(context);
		String PromoStatus = DatabaseQueryUtil.PromotionalDescription(
				context,
				selectedOrder.sku.skuId,
				getIntent().getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID));

		Log.v("Promo", PromoStatus);

		Intent intent = new Intent(OutletVisitPageActivity.this,
				OrderDetailPageActivity.class)
				.putExtra(DatabaseConstants.tblSKU.TITLE, selectedOrder.sku.title)
				.putExtra(DatabaseConstants.tblSKU.CTN_RATE, selectedOrder.sku.ctnRate)
				.putExtra(DatabaseConstants.tblSKU.PCS_RATE, selectedOrder.sku.pcsRate)
				.putExtra(DatabaseConstants.tblSKU.PCS_PER_CTN, selectedOrder.sku.pcsPerCtn)
				.putExtra(DatabaseConstants.tblSKU.MESSAGE_FOR_HHT, selectedOrder.sku.MessageForHHT)
				.putExtra(DatabaseConstants.tblSKU.CRITICAL_STOCK, selectedOrder.sku.CriticalStock)
				.putExtra(DatabaseConstants.tblSKU.MESSAGE_FOR_HHT, PromoStatus)
				.putExtra(DatabaseConstants.tblSKU.SKUID, selectedOrder.sku.skuId)
				.putExtra(DatabaseConstants.otherFields.ORDER_STATUS, ORDERED_SKU)
				.putExtra(DatabaseConstants.tblOrderItem.CARTON, selectedOrder.Carton)
				.putExtra(DatabaseConstants.tblOrderItem.PIECE, selectedOrder.Piece)
				.putExtra(DatabaseConstants.tblOrder.OUTLET_ID, outlet.outletId)
				.putExtra(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY, selectedOrder.Suggested)
				.putExtra(DatabaseConstants.tblOutletSKU.MAXORDERQTY, 0)
				.putExtra(DatabaseConstants.tblOutletSKU.COLORID, 0)
				.putExtra(DatabaseConstants.tblOrderItem.TK_OFF, (int) selectedOrder.TkOff)
				.putExtra(DatabaseConstants.tblOrderItem.PIECE_ORG, selectedOrder.Piece)
				.putExtra(DatabaseConstants.tblOrderItem.CARTON_ORG, selectedOrder.Carton);

		startActivityForResult(intent, ORDERED_SKU);
	}

	private void notOrderedItemSelectedAction(OrderItem selectedOrder) {
		Util.showWaitingDialog(context);

		String PromoStatus = DatabaseQueryUtil.PromotionalDescription(
				context,
				selectedOrder.sku.skuId,
				getIntent().getStringExtra(DatabaseConstants.tblOrder.OUTLET_ID));

		Log.d(TAG, "notOrderedItemSelectedAction " + selectedOrder.sku.pcsPerCtn);

		Intent intent = new Intent(OutletVisitPageActivity.this,
				OrderDetailPageActivity.class)
				.putExtra(DatabaseConstants.tblSKU.TITLE, selectedOrder.sku.title)
				.putExtra(DatabaseConstants.tblSKU.CTN_RATE, selectedOrder.sku.ctnRate)
				.putExtra(DatabaseConstants.tblSKU.PCS_RATE, selectedOrder.sku.pcsRate)
				.putExtra(DatabaseConstants.tblSKU.PCS_PER_CTN, selectedOrder.sku.pcsPerCtn)
				.putExtra(DatabaseConstants.tblSKU.MESSAGE_FOR_HHT, selectedOrder.sku.MessageForHHT)
				.putExtra(DatabaseConstants.tblSKU.CRITICAL_STOCK, selectedOrder.sku.CriticalStock)
				.putExtra(DatabaseConstants.tblSKU.SKUID, selectedOrder.sku.skuId)
				.putExtra(DatabaseConstants.tblSKU.MESSAGE_FOR_HHT, "")
				.putExtra(DatabaseConstants.tblOutlet.OUTLET_ID, outlet.outletId)
				.putExtra(DatabaseConstants.otherFields.ORDER_STATUS, NOT_ORDERED_SKU)
				.putExtra(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY, selectedOrder.Suggested)
				.putExtra(DatabaseConstants.tblOutletSKU.MAXORDERQTY, 0)
				.putExtra(DatabaseConstants.tblOutletSKU.COLORID, 0);

		startActivityForResult(intent, NOT_ORDERED_SKU);
	}

	private void updateItemList(Sku selectedSku, int orderStatus) {
        Log.v(TAG, "update item list: " + orderStatus + ", " + (selectedSku != null));

        if (mBottomSheedProductList == null)mBottomSheedProductList = new ArrayList<>();
        else mBottomSheedProductList.clear();

		if (orderStatus == ORDERED) // no brand filtering
		{
			int sz = previousOrderList.orderedList.size();
			for (int i = 0; i < sz; i++) {
				OrderItem nowOrder = previousOrderList.orderedList.get(i);
                mBottomSheedProductList.add(nowOrder);
			}
		} else {
			if (selectedSku != null) {
				int sz = previousOrderList.notOrderedList.size();
				for (int i = 0; i < sz; i++) {
					OrderItem nowOrder = previousOrderList.notOrderedList.get(i);
					if (Objects.equals(nowOrder.SKUID, selectedSku.skuId)) {
                        mBottomSheedProductList.add(nowOrder);
					}
				}
			}
		}

        if (bottomSheetAdapter == null) bottomSheetAdapter = new ProductListSheetAdapter(mBottomSheedProductList);
        else bottomSheetAdapter.notifyDataSetChanged();
	}

    private void getPicture(){
        // check for storage permission
        int hasStoragePermission = ContextCompat.checkSelfPermission(OutletVisitPageActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(OutletVisitPageActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                Snackbar.make(this.getCurrentFocus(), R.string.prompt_permission_storage, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.action_settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showPermissionsSettings();
                            }
                        })
                        .show();
                return;
            }

            ActivityCompat.requestPermissions(OutletVisitPageActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQCODE);
            return;
        }

        imageSourceDialog.show();
    }

    private void showPermissionsSettings(){
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

	private void initializeFields() {
		try {
			skuList = DatabaseQueryUtil.getSkuListFromTblSkuByBrand(context, -1);
			outlet = DatabaseQueryUtil.getOutlet(context, getIntent()
					.getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID));

			previousOrderList = DatabaseQueryUtil.getAllOrder(
					context, outlet.outletId, outlet.channelId, user.visitDate);

			previousMarketReturnItemList = DatabaseQueryUtil
					.getMarketReturnItemListFromTblMarketReturn(context,
							outlet.outletId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateSkuList(String skuName) {

		int sz = skuList.size();
		for (int i = 0; i < sz; i++)
			for (int j = 0; j < previousOrderList.notOrderedList.size(); j++)
				if (Objects.equals(previousOrderList.notOrderedList.get(j).SKUID, skuList.get(i).skuId)) {
					if (skuList.get(i).title.toLowerCase().startsWith(skuName.toLowerCase())) {
						skuListAdapter.addItem(skuList.get(i));
						break;
					}
				}
	}

	private void updateSkuList() {
		int sz = skuList.size();
		for (int i = 0; i < sz; i++)
			for (int j = 0; j < previousOrderList.notOrderedList.size(); j++)
				if (Objects.equals(previousOrderList.notOrderedList.get(j).SKUID, skuList.get(i).skuId)) {
					skuListAdapter.addItem(skuList.get(i));
					break;
				}
	}

	private void skuSelectedAction(Sku selectedSku) {
        Log.v(TAG, "Order brandSelectedAction" );
		updateItemList(selectedSku, orderStatus);
		selectedOrder = mBottomSheedProductList.get(0);
		notOrderedItemSelectedAction(selectedOrder);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "OutletVisitPageActivity::onActivityResult: " + requestCode);

		if (resultCode == RESULT_OK && requestCode == SPACE_MANAGEMENT) {
            Log.v(TAG, "Order NOT_ORDERED_SKU");
			checkOut();
            skuSelectedAction(selectedSku);
		} else if (resultCode == RESULT_OK && requestCode == NOT_ORDERED_SKU) {
            Log.v(TAG, "Order NOT_ORDERED_SKU");

			int ctnNumber = data.getIntExtra(DatabaseConstants.tblOrderItem.CARTON, 0);
			int pcsNumber = data.getIntExtra(DatabaseConstants.tblOrderItem.PIECE, 0);
			int allocationQty = data.getIntExtra(DatabaseConstants.tblOrderItem.SUGGESTED, 0);
			int ctnNumberOrg = data.getIntExtra(DatabaseConstants.tblOrderItem.CARTON_ORG, 0);
			int pcsNumberOrg = data.getIntExtra(DatabaseConstants.tblOrderItem.PIECE_ORG, 0);
			int discountValue = data.getIntExtra(DatabaseConstants.tblOrderItem.TK_OFF, 0);
			double value = data.getDoubleExtra(DatabaseConstants.tblOrderItem.TOTAL, 0);

			previousOrderList.notOrderedList.remove(selectedOrder);
			selectedOrder.Carton = ctnNumber;
			selectedOrder.Piece = pcsNumber;
			selectedOrder.TkOff = discountValue;
			selectedOrder.Total = value;
			selectedOrder.Suggested = allocationQty;
			selectedOrder.Carton_Org = ctnNumber;
			selectedOrder.Piece_Org = pcsNumber;
			previousOrderList.orderedList.add(selectedOrder);

			newOrderList.add(selectedOrder);
			syncType.add(Integer.valueOf(INSERT));
			orderTotal += selectedOrder.Total;
			orderValueView.setText(moneyFormat.format(orderTotal));

		} else if (resultCode == RESULT_OK && requestCode == ORDERED_SKU) {
            Log.d(TAG, "Order completee");

            int removeStatus = data.getIntExtra(
					DatabaseConstants.otherFields.REMOVE_STATUS, 0);

			if (removeStatus == 1) {

                newOrderList.add(selectedOrder);
				syncType.add(Integer.valueOf(DELETE));

				previousOrderList.orderedList.remove(selectedOrder);
				previousOrderList.notOrderedList.add(selectedOrder);

				orderTotal -= selectedOrder.Total;
				orderValueView.setText(moneyFormat.format(orderTotal));
			} else {
				int ctnNumber = data.getIntExtra(
						DatabaseConstants.tblOrderItem.CARTON, 0);
				int pcsNumber = data.getIntExtra(
						DatabaseConstants.tblOrderItem.PIECE, 0);
				int discountValue = data.getIntExtra(
						DatabaseConstants.tblOrderItem.TK_OFF, 0);
				double value = data.getDoubleExtra(
						DatabaseConstants.tblOrderItem.TOTAL, 0);

				if (ctnNumber == 0 && pcsNumber == 0) {
					newOrderList.add(selectedOrder);
					syncType.add(Integer.valueOf(DELETE));

					previousOrderList.orderedList.remove(selectedOrder);
					previousOrderList.notOrderedList.add(selectedOrder);

					orderTotal -= selectedOrder.Total;
					orderValueView.setText(moneyFormat.format(orderTotal));
				} else {
					orderTotal -= selectedOrder.Total;
					previousOrderList.orderedList.remove(selectedOrder);
					selectedOrder.Carton = ctnNumber;
					selectedOrder.Piece = pcsNumber;
					selectedOrder.Carton_Org = ctnNumber;
					selectedOrder.Piece_Org = pcsNumber;
					selectedOrder.TkOff = discountValue;
					selectedOrder.Total = value;
					previousOrderList.orderedList.add(selectedOrder);

					newOrderList.add(selectedOrder);
					syncType.add(Integer.valueOf(UPDATE));

					orderTotal += selectedOrder.Total;
					orderValueView.setText(moneyFormat.format(orderTotal));
				}
			}
		} else if (resultCode == RESULT_OK && requestCode == CHECK_OUT) {
            Log.v(TAG, "Order CHECK_OUT " + outlet.visited);

			paymentMode = data.getIntExtra(DatabaseConstants.tblOrder.PAYMENT_MODE, 0);
			deliverymode = data.getIntExtra(DatabaseConstants.tblOrder.DELIVERY_MODE, 0);
			deliverydate = data.getStringExtra(DatabaseConstants.tblOrder.DELIVERY_DATE);


			boolean isOutletExists = DatabaseQueryUtil.IsOutletExists(outlet.outletId, context);

			if (!isOutletExists)
			{
				/**
				 * Outlet found...... need to insert as usual
				 */
				if (outlet.visited == 0) {
					DatabaseQueryUtil.insertOrderToTblOrder(context, outlet.outletId, sectionId, currentOrderDate);
					orderNo = DatabaseQueryUtil.getOrderNo(context, outlet.outletId, currentOrderDate);
					DatabaseQueryUtil.updateSentStatusOfTblOrder(context, outlet.outletId, orderNo);
				}
//
//			else if (outlet.visited == 1) {
//				orderNo = DatabaseQueryUtil.getOrderNo(context, outlet.outletId, currentOrderDate);
//				DatabaseQueryUtil.updateSentStatusOfTblOrder(context, outlet.outletId, orderNo);
//			}

				if (previousOrderList.orderedList.size() == 0) {
					String reasonId = data.getStringExtra(DatabaseConstants.tblMarketReason.MARKET_REASON_ID);
					DatabaseQueryUtil.updateTblOrderWithNotOrderCoz(context, outlet.outletId, reasonId, orderNo);
					orderTotal = 0;
				}

				Log.d(TAG, "newOrderList " + newOrderList.size());
				// sync with web
				for (int i = 0; i < newOrderList.size(); i++) {
					if (syncType.get(i) == DELETE) {

						DatabaseQueryUtil.deleteOrderFromTblOrderItem(context, newOrderList.get(i).OutletID, newOrderList.get(i).SKUID);

					} else if (syncType.get(i) == INSERT) {

						DatabaseQueryUtil.insertOrderItemToTblOrderItem(context, orderNo,
								newOrderList.get(i).OutletID,
								newOrderList.get(i).SKUID,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece,
								newOrderList.get(i).BrandID,
								newOrderList.get(i).Total,
								newOrderList.get(i).TkOff,
								newOrderList.get(i).Suggested,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece);

					} else if (syncType.get(i) == UPDATE) {

						DatabaseQueryUtil.updateOrderItemFromTblOrderItem(context, orderNo,
								newOrderList.get(i).OutletID,
								newOrderList.get(i).SKUID,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece,
								newOrderList.get(i).Total,
								newOrderList.get(i).TkOff,
								newOrderList.get(i).Suggested,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece);

						Log.d(TAG, "update block : " + orderNo);
					}
				}

				Log.d(TAG, "details: " + orderTotal + ", orderNo: " + orderNo + ", visited: " + outlet.visited);
			}else {

				/**
				 * Outlet found for this order, need to update existing.
				 */

				orderNo = DatabaseQueryUtil.getExistingOrderNo(outlet.outletId, context);

				if (outlet.visited == 0) {
					DatabaseQueryUtil.insertOrderToTblOrder(context, outlet.outletId, sectionId, currentOrderDate);
					DatabaseQueryUtil.updateSentStatusOfTblOrder(context, outlet.outletId, orderNo);
				}

				if (previousOrderList.orderedList.size() == 0) {
					String reasonId = data.getStringExtra(DatabaseConstants.tblMarketReason.MARKET_REASON_ID);
					DatabaseQueryUtil.updateTblOrderWithNotOrderCoz(context, outlet.outletId, reasonId, orderNo);
					orderTotal = 0;
				}

				Log.d(TAG, "newOrderList " + newOrderList.size());
				// sync with web
				for (int i = 0; i < newOrderList.size(); i++) {
					if (syncType.get(i) == DELETE) {

						DatabaseQueryUtil.deleteOrderFromTblOrderItem(context, newOrderList.get(i).OutletID, newOrderList.get(i).SKUID);

					} else if (syncType.get(i) == INSERT) {

						DatabaseQueryUtil.insertOrderItemToTblOrderItem(context, orderNo,
								newOrderList.get(i).OutletID,
								newOrderList.get(i).SKUID,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece,
								newOrderList.get(i).BrandID,
								newOrderList.get(i).Total,
								newOrderList.get(i).TkOff,
								newOrderList.get(i).Suggested,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece);

					} else if (syncType.get(i) == UPDATE) {

						DatabaseQueryUtil.updateOrderItemFromTblOrderItem(context, orderNo,
								newOrderList.get(i).OutletID,
								newOrderList.get(i).SKUID,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece,
								newOrderList.get(i).Total,
								newOrderList.get(i).TkOff,
								newOrderList.get(i).Suggested,
								newOrderList.get(i).Carton,
								newOrderList.get(i).Piece);

						Log.d(TAG, "update block : " + orderNo);
					}
				}

				Log.d(TAG, "details: " + orderTotal + ", orderNo: " + orderNo + ", visited: " + outlet.visited);


			}



            // updating orderTotal for
			DatabaseQueryUtil.updateTblOrderOrderTotal(context, outlet.outletId, orderTotal, paymentMode, orderNo, deliverymode, deliverydate);

			// this outlet
			newOrderList = new ArrayList<OrderItem>();
			if (orderTotal > 0) {
                // If there is no order but return item is entered system will not save those return
                // item.
				for (int i = 0; i < addedMarketReturnItemList.size(); i++) {
                    DatabaseQueryUtil.insertMarketReturnItemToTblMarketReturn(context, addedMarketReturnItemList.get(i), orderNo);
                }
				for (int i = 0; i < removedMarketReturnItemList.size(); i++) {
                    DatabaseQueryUtil.deleteMarketReturnItemFromTblMarketReturn(context,
                            removedMarketReturnItemList.get(i));
                }

				DatabaseQueryUtil.updateTblOrderWithNotOrderCoz(context, outlet.outletId, null, orderNo);
			}


			if (outlet.visited == 0) {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				endTime = sdf.format(c.getTime());
				//DatabaseQueryUtil.insertOrderToTblOrder(context, outlet.outletId, sectionId, currentOrderDate, startTime);
				DatabaseQueryUtil.updateTblOrderWithFirstEntryExitTime(context, outlet.outletId, startTime, endTime);
				DatabaseQueryUtil.updateTblOutletVisitedField(context, outlet.outletId, 1);
			}

			ArrayList<OrderItem> ordersOfSelectedOutlet = DatabaseQueryUtil
					.getAllOrderByOutlet(context, outlet.outletId, orderNo);
			DatabaseQueryUtil.updateTblOutletTotalLineSoldandSentStatus(
					context, outlet.outletId, ordersOfSelectedOutlet.size(), 0);
			String lat = String.valueOf(data.getDoubleExtra(DatabaseConstants.tblOrder.ORDER_LATITUDE, 0));
			String lon = String.valueOf(data.getDoubleExtra(DatabaseConstants.tblOrder.ORDER_LONGITUDE, 0));
			DatabaseQueryUtil.updateTblOrderOrderLocation(context, outlet.outletId, lat, lon, orderNo);
			//uploadOrder();

            onBackPressed();
		}else if (resultCode == RESULT_OK ){

            if (requestCode == PICK_FROM_FILE) {
                mImageCaptureUri = data.getData();
                imagePath = getRealPathFromURI(mImageCaptureUri); //from Gallery

                if (imagePath == null) {
                    imagePath = mImageCaptureUri.getPath(); //from File Manager
                }
            } else if (requestCode == PICK_FROM_CAMERA) {
                imagePath	= mImageCaptureUri.getPath();
            }

            Image image = new Image();
            image.imageType = selectedImageType;
            image.imageUrl = imagePath;
            image.outletId = outlet.outletId;
            DatabaseQueryUtil.addNewImage(OutletVisitPageActivity.this, new Image[]{image});

        }
	}

	private void uploadOrder(){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Web Upload");
			builder.setMessage("Do you want to upload data to web?");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					isGPSworkDone = true;
					System.out.println("Calling Webservice");
					//	if (user.WillTrackGPS == 0 && user.WillUploadOrderToWeb == 1 && isGPSworkDone) {
					if (user.WillTrackGPS == 1) {
						try {
							Caller caller = new Caller();
							caller.UploadDataToWeb(context, OrderUploadHandler, user.mobile_No, user.password, outlet.outletId);
							setResult(RESULT_OK);
							finish();
						} catch (Exception ex) {
							Toast.makeText(context, "Error occured while uploading order", Toast.LENGTH_LONG).show();
							setResult(RESULT_OK);
							finish();
						}
						finish();
					}
					Log.e("End", "END OF Segment");
				}
			});

			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setResult(RESULT_OK);
					finish();
					Log.e("End", "END OF Segment");
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
}
	@Override
	public void onBackPressed() {
		this.finish();
		Intent intent = new Intent(OutletVisitPageActivity.this,
				OutletListPageActivity.class).putExtra(
				DatabaseConstants.tblDSRBasic.SECTION_ID, sectionId);
		startActivity(intent);
		super.finish();
	}

	@Override
	public void finish() {
		super.finish();
	}

	public ArrayList<OrderItem> getOrderedItemList() {
		return previousOrderList.orderedList;
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			setResult(RESULT_OK);
			finish();
		}
	};
	Handler OrderUploadHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(context,
					"Pending Orders Uploading Attempt Completed ",
					Toast.LENGTH_SHORT).show();
		}
	};



    private boolean dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }

        return false;
    }

    private void createDialog(final int dialogOrderStatus) {
        if (dismissDialog()) {
            return;
        }

        if (mBottomSheedProductList.size() > 0) { // no brand filtering
            bottomSheetAdapter.setOnItemClickListener(new ProductListSheetAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ProductListSheetAdapter.ItemHolder item, int position) {
                    selectedOrder = mBottomSheedProductList.get(position);
                    if (dialogOrderStatus == ORDERED) {
                        orderedItemSelectedAction(selectedOrder);
                    } else {
                        notOrderedItemSelectedAction(selectedOrder);
                    }
                }
            });

            lLayout = new GridLayoutManager(context, 3);

            View view = getLayoutInflater().inflate(R.layout.product_list_bottom_sheet, null);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager( lLayout );
            recyclerView.setAdapter(bottomSheetAdapter);

            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.show();

        } else {
            dismissDialog();
            Toast.makeText(context, R.string.product_list_empty, Toast.LENGTH_SHORT).show();
        }
    }

	public String getRealPathFromURI(Uri contentUri) {
		String [] proj 		= {MediaStore.Images.Media.DATA};
		Cursor cursor 		= managedQuery( contentUri, proj, null, null,null);

		if (cursor == null) return null;

		int column_index 	= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}


    private void createImagePickDialog(){
        final String [] items			= new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file		 = new File(Environment.getExternalStorageDirectory(),
                            "tmp_image_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        imageSourceDialog = builder.create();

    }

}