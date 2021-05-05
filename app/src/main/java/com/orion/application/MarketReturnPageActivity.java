package com.orion.application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.orion.adapter.MarketReturnPageItemListAdapter;
import com.orion.adapter.SpinnerListAdapter;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Brand;
import com.orion.entities.MarketReturnItem;
import com.orion.entities.Reason;
import com.orion.entities.Sku;
import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MarketReturnPageActivity extends AppCompatActivity
{		
//    private BrandSpinnerListAdapter brandListAdapter;
    private ArrayAdapter brandListAdapter;
    private ArrayList brandListArray;
    private ArrayList skuListArray;
    private ArrayAdapter skuListAdapter;
    private ArrayAdapter reasonListAdapter;
	private MarketReturnPageItemListAdapter marketReturnItemListAdapter;
    
    private Spinner brandListSpinner;
    private Spinner skuListSpinner;
    private Spinner reasonListSpinner;
    
    private Brand selectedBrand;
    private Sku selectedSku;
    private Reason selectedReason;
	private MarketReturnItem selectedMarketReturnItem;
	
	private EditText qtyText;
	private EditText batchText;
	private ListView returnItemListView;
	private View selectedReturnItemView;
	
	private ArrayList<Brand> brandList;
    private ArrayList<Sku> skuList;
	private HashMap<Integer, Integer> presentInDB;
	private DateComponent dateComponent;
	private OutletVisitPageActivity outletVisitPageActivity;

	private Context context;
	private String outletId;
	private int orderNo;
	private ArrayList<Reason> reasonList;
	private ArrayList reasonListArray;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.market_return_page_layout);
		context = this;

        brandListArray = new ArrayList();
		skuListArray = new ArrayList();
		reasonListArray = new ArrayList();

		outletVisitPageActivity = OutletVisitPageActivity.outletVisitPageActivity;
		brandListSpinner = (Spinner) findViewById(R.id.brand_list_drop_down_spinner);


		skuListSpinner = (Spinner) findViewById(R.id.sku_list_drop_down_spinner);
//		skuListAdapter = new SkuSpinnerListAdapter(context);
		skuListAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, skuListArray);
		skuListSpinner.setAdapter(skuListAdapter);
		skuListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				if (skuList.size() > 0) {
					selectedSku = skuList.get(position-1);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		reasonListSpinner = (Spinner) findViewById(R.id.reason_list_drop_down_spinner);
		reasonListAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, reasonListArray);
		reasonListSpinner.setAdapter(reasonListAdapter);
		reasonListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				selectedReason = reasonList.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		qtyText = (EditText) findViewById(R.id.qty_edit_text);
		batchText = (EditText) findViewById(R.id.batch_edit_text);

		findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String qty = ((qtyText.getText().toString().length() == 0) || (Integer.parseInt(qtyText.getText().toString()) == 0)) ? "" : qtyText.getText().toString();
				String batch = batchText.getText().toString();

				if (selectedBrand.brandId == null) Util.showAlert(context, "Please select a Brand");
				else if (selectedSku.skuId == null) Util.showAlert(context, "Please select an SKU");
				else if (selectedReason.reasonId == -1)
					Util.showAlert(context, "Please select a Reason");
				else if (qty.length() == 0) Util.showAlert(context, "Please enter Quantity");
				else if (batch.length() == 0) Util.showAlert(context, "Please enter Batch");
				else if (dateComponent.monthListSpinner.getSelectedItemPosition() == 0)
					Util.showAlert(context, "Please enter Month");
				else if (dateComponent.dayListSpinner.getSelectedItemPosition() == 0)
					Util.showAlert(context, "Please enter Date");
				else if (dateComponent.yearListSpinner.getSelectedItemPosition() == 0)
					Util.showAlert(context, "Please enter Year");
				else if (selectedSku.IsNSD == 1 & selectedReason.reasonId == 1)
					Util.showAlert(context, "You can not select Expire for NSD items");

				else {
					MarketReturnItem now = new MarketReturnItem();
					now.marketReturnId = DatabaseConstants.MARKET_RETURN_ID++;
					now.expDate = dateComponent.getSelectedDate();
					now.brandId = selectedBrand.brandId;
					now.qty = Integer.parseInt(qty);
					now.batch = batch;
					now.marketReasonId = selectedReason.reasonId;
					now.outletId = outletId;
					now.SKUID = selectedSku.skuId;
					now.skuName = selectedSku.title;
					now.reasonDescription = selectedReason.description;
					now.price = selectedSku.pcsRate;

					if (IsExist(now)) {
						Util.showAlert(context, "Already exist");
						return;
					}

					marketReturnItemListAdapter.addItem(now);
					outletVisitPageActivity.addedMarketReturnItemList.add(now);

					Util.showAlert(context, "Item added successfully");

					qtyText.setText("");
					batchText.setText("");
					skuListSpinner.setSelection(0);
					reasonListSpinner.setSelection(0);
					dateComponent.dayListSpinner.setSelection(0);
					dateComponent.monthListSpinner.setSelection(0);
					dateComponent.yearListSpinner.setSelection(0);
				}
			}
		});

		findViewById(R.id.remove_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selectedReturnItemView == null || !selectedReturnItemView.isSelected()) {
					Util.showAlert(context, "Please select an item to remove");
				} else {
					showRemoveConfirmationDialog();
				}
			}
		});

		returnItemListView = (ListView) findViewById(R.id.market_return_item_list_list_view);
		marketReturnItemListAdapter = new MarketReturnPageItemListAdapter(context);
		returnItemListView.setAdapter(marketReturnItemListAdapter);
		returnItemListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				view.setSelected(true);
				selectedReturnItemView = view;
				selectedMarketReturnItem = marketReturnItemListAdapter.getItem(position);

			}
		});

		initializeFields();

		brandListAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, brandListArray);
		brandListSpinner.setAdapter(brandListAdapter);
		brandListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//				selectedBrand = brandListAdapter.getItem(position);
				selectedBrand = brandList.get(position);
				updateSkuList(selectedBrand);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	protected boolean IsExist(MarketReturnItem now) {

		//outletVisitPageActivity.addedMarketReturnItemList
		for (MarketReturnItem mri : outletVisitPageActivity.addedMarketReturnItemList) 
		{
			if( mri.batch.equalsIgnoreCase(now.batch) && mri.SKUID==now.SKUID && mri.marketReasonId==now.marketReasonId) {
				return true;
			}
		}	
		return false;
	}

	private void updateReasonList() {
//		reasonListAdapter.addItem(new Reason(-1, "--Select Reason--"));
		reasonListArray.add(getString(R.string.select_reason));
		reasonList = DatabaseQueryUtil.getReasonListForMarketReturn(context);
		for (int i = 0; i < reasonList.size(); i++) {
//			reasonListAdapter.addItem(reasonList.get(i));
			reasonListArray.add(reasonList.get(i).description);
		}
	}

	private void updateSkuList(Brand selectedBrand) {
		if (selectedBrand.brandId != null) {
//			Sku firstSku = skuListAdapter.getItem(0);
//			skuListAdapter.removeAll();
            skuListArray.clear();
//			skuListAdapter.addItem(firstSku);
            skuListArray.add(getString(R.string.select_sku));
			skuList = DatabaseQueryUtil.getSkuListForMarketReturnByBrand(context, selectedBrand.brandId);

			int sz = skuList.size();
			for (int i = 0; i < sz; i++) {
//				skuListAdapter.addItem(skuList.get(i));
                skuListArray.add(skuList.get(i).title);
            }
            skuListAdapter.notifyDataSetChanged();
		}
	}

	private void updateBrandList() {
//		brandListAdapter.addItem(new Brand(-1, "--Select Brand--"));
        brandListArray.add( getString(R.string.select_brand) );
		for (int i = 0; i < brandList.size(); i++) {
//			brandListAdapter.addItem(brandList.get(i));
            brandListArray.add( brandList.get(i).name );
        }
	}

	private void initializeFields() {
		presentInDB = new HashMap<Integer, Integer>();
		brandList = DatabaseQueryUtil.getBrandList(context);
		outletId = getIntent().getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID);
		updateBrandList();
		updateReasonList();
		dateComponent = new DateComponent();
		selectedBrand = brandList.get(0);
		skuListArray.add(getString(R.string.select_sku));
//		skuListAdapter.addItem(new Sku(-1, "--Select Sku--"));
//		selectedSku = skuListAdapter.getItem(0);
//		selectedReason = reasonListAdapter.getItem(0);

		updateMarketReturnList();
	}

	private void updateMarketReturnList() {
		for (int i = 0; i < outletVisitPageActivity.previousMarketReturnItemList.size(); i++) {
			marketReturnItemListAdapter.addItem(outletVisitPageActivity.previousMarketReturnItemList.get(i));
			presentInDB.put(outletVisitPageActivity.previousMarketReturnItemList.get(i).marketReturnId, 1);
		}
		if (outletVisitPageActivity.previousMarketReturnItemList.size() > 0) {
			DatabaseConstants.MARKET_RETURN_ID = outletVisitPageActivity.previousMarketReturnItemList.get(outletVisitPageActivity.previousMarketReturnItemList.size() - 1).marketReturnId;
			DatabaseConstants.MARKET_RETURN_ID++;
		}
		for (int i = 0; i < outletVisitPageActivity.addedMarketReturnItemList.size(); i++) {
			marketReturnItemListAdapter.addItem(outletVisitPageActivity.addedMarketReturnItemList.get(i));
		}
	}

	private void showRemoveConfirmationDialog() {
		AlertDialog.Builder removeConfirmationDialog = new AlertDialog.Builder(context);
		removeConfirmationDialog.setTitle("Message");
		removeConfirmationDialog.setMessage("Do you want to remove this?");

		removeConfirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				marketReturnItemListAdapter.removeItem(selectedMarketReturnItem);
				if (presentInDB.containsKey(selectedMarketReturnItem.marketReturnId))
					outletVisitPageActivity.removedMarketReturnItemList.add(selectedMarketReturnItem);
				else
					outletVisitPageActivity.addedMarketReturnItemList.remove(selectedMarketReturnItem);
				dialog.cancel();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		removeConfirmationDialog.show();
	}

	class DateComponent {
		private Spinner monthListSpinner;
		private Spinner dayListSpinner;
		private Spinner yearListSpinner;
		private SpinnerListAdapter monthListAdapter;
		private SpinnerListAdapter dayListAdapter;
		private SpinnerListAdapter yearListAdapter;
		private String selectedDayOfMonth;
		private String selectedYear;

		int dayOfMonth[] = new int[]{0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		String month[] = new String[]{"---", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

		DateComponent() {
			monthListSpinner = (Spinner) findViewById(R.id.date_month_spinner);
			monthListAdapter = new SpinnerListAdapter(context);
			monthListSpinner.setAdapter(monthListAdapter);
			monthListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					updateDayListSpinner();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			dayListSpinner = (Spinner) findViewById(R.id.date_day_spinner);
			dayListAdapter = new SpinnerListAdapter(context);
			dayListSpinner.setAdapter(dayListAdapter);
			dayListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					selectedDayOfMonth = dayListAdapter.getItem(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			yearListSpinner = (Spinner) findViewById(R.id.date_year_spinner);
			yearListAdapter = new SpinnerListAdapter(context);
			yearListSpinner.setAdapter(yearListAdapter);
			yearListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					selectedYear = yearListAdapter.getItem(position);
					updateDayListSpinner();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			initialize();
		}

		private void initialize() {
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);

			for (int i = 0; i <= 12; i++) monthListAdapter.addItem(month[i]);
			monthListSpinner.setSelection(0);

			yearListAdapter.addItem("----");
			for (int i = year - 5; i < year + 6; i++) yearListAdapter.addItem("" + i);
			yearListSpinner.setSelection(0);
			selectedYear = yearListAdapter.getItem(yearListSpinner.getSelectedItemPosition());

			updateDayListSpinner();
			dayListSpinner.setSelection(0);
			selectedDayOfMonth = dayListAdapter.getItem(dayListSpinner.getSelectedItemPosition());
		}

		private void updateDayListSpinner() {
			dayListAdapter.removeAll();
			dayListAdapter.addItem("--");
			if (monthListSpinner.getSelectedItemPosition() == 0) {
				dayListSpinner.setSelection(0);
				selectedDayOfMonth = dayListAdapter.getItem(0);
				return;
			}
			if (yearListSpinner.getSelectedItemPosition() != 0 && isLeapYear(selectedYear))
				dayOfMonth[2] = 29;
			else dayOfMonth[2] = 28;

			for (int i = 1; i <= dayOfMonth[monthListSpinner.getSelectedItemPosition()]; i++)
				dayListAdapter.addItem("" + i);
			if (dayListSpinner.getSelectedItemPosition() == 0 || Integer.parseInt(selectedDayOfMonth) > dayOfMonth[monthListSpinner.getSelectedItemPosition()]) {
				dayListSpinner.setSelection(0);
				selectedDayOfMonth = dayListAdapter.getItem(0);
			} else {
				dayListSpinner.setSelection(Integer.parseInt(selectedDayOfMonth));
			}
		}

		public String getSelectedDate() {
			@SuppressWarnings("deprecation")
			Date date = new Date(Integer.parseInt(selectedYear) - 1900,
					monthListSpinner.getSelectedItemPosition() - 1,
					Integer.parseInt(selectedDayOfMonth));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateAndTime = sdf.format(date);
			currentDateAndTime += " 00:00:00.000";
			System.out.println("Current date and time: = " + currentDateAndTime);
			return currentDateAndTime;
		}

		public boolean isLeapYear(String syear) {
			int year = Integer.parseInt(syear);

			if (year % 4 != 0) {
				return false;
			} else if (year % 400 == 0) {
				return true;
			} else if (year % 100 == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}