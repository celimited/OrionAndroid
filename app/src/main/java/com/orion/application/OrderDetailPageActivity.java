package com.orion.application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import java.text.NumberFormat;

public class OrderDetailPageActivity extends AppCompatActivity {
	public String TAG = "Trace";

	private String skuName;
	private String priceValue;
	private int suggested;
	private int colorid;
	private String promotionInfoMessage;
	private EditText ctnNumberEditText;
	private EditText pcsNumberEditText;
	private EditText ctnNumberEditTextOrg;
	private EditText pcsNumberEditTextOrg;
	private TextView suggestedText;
	private double pcsRate;
	private double ctnRate;
	private int allocationQty;
	private int pcsPerCtn;
	private int criticalStock; //for test
	private int orderedStatus;
	private TextView valueNumberText;
	private TextView discountValueText;
	private NumberFormat twoDigDecimalFormat;
	private int ctnNumber;
	private int pcsNumber;
	private int ctnNumberOrg;
	private int pcsNumberOrg;
	private String outletId;
	private int maxOrderQty;
	private String skuId;
	private Button calculateButton;
	private double valueNumber;
	private int discountValue;
	private int flag;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail_page_layout);


        Log.d(TAG, "OrderDetailPageActivity::onCreate");

		context = this;

		twoDigDecimalFormat = NumberFormat.getNumberInstance();
		twoDigDecimalFormat.setMinimumFractionDigits(2);
		twoDigDecimalFormat.setMaximumFractionDigits(2);

		initializeFields();

		((TextView) findViewById(R.id.sku_name)).setText(skuName);
		((TextView) findViewById(R.id.SuggQty_text)).setText(String.valueOf(suggested));
		((TextView) findViewById(R.id.price_value)).setText(priceValue);
		((TextView) findViewById(R.id.promotion_info_message))
				.setText(promotionInfoMessage);

		ctnNumberEditText = (EditText) findViewById(R.id.ctn_edit_text);
		pcsNumberEditText = (EditText) findViewById(R.id.pcs_edit_text);

		ctnNumberEditTextOrg = (EditText) findViewById(R.id.OrigCtn_edit_text);
		pcsNumberEditTextOrg = (EditText) findViewById(R.id.OrigPcs_edit_text);
		ctnNumberEditTextOrg.setEnabled(true);

		suggestedText = (TextView) findViewById(R.id.SuggQty_text);
		valueNumberText = (TextView) findViewById(R.id.value_text);
		discountValueText = (TextView) findViewById(R.id.discount_edit_text);

		if (colorid == 0) {
			ctnNumberEditText.setEnabled(true);
			pcsNumberEditText.setEnabled(true);
			findViewById(R.id.idcolor).setBackgroundColor(0xffffffff);
		} else if (colorid == 1) {
			ctnNumberEditText.setEnabled(true);
			pcsNumberEditText.setEnabled(true);
			findViewById(R.id.idcolor).setBackgroundColor(0xff0000ff);
		} else if (colorid == 2) {
			ctnNumberEditText.setEnabled(false);
			pcsNumberEditText.setEnabled(false);
			findViewById(R.id.idcolor).setBackgroundColor(0xfff00000);
		}
		ctnNumberEditTextOrg.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (colorid != 2) {
						ctnNumberEditText.setText(ctnNumberEditTextOrg.getText().toString());
						pcsNumberEditText.setText(pcsNumberEditTextOrg.getText().toString());
					} else {
						ctnNumberEditText.setText("0");
						pcsNumberEditText.setText("0");
					}
				}
			}
		});
		pcsNumberEditTextOrg.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (colorid != 2) {
						ctnNumberEditText.setText(ctnNumberEditTextOrg.getText().toString());
						pcsNumberEditText.setText(pcsNumberEditTextOrg.getText().toString());
					} else {
						ctnNumberEditText.setText("0");
						pcsNumberEditText.setText("0");
					}
				}
			}
		});
		Button cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				OrderDetailPageActivity.this.onBackPressed();
			}
		});
		calculateButton = (Button) findViewById(R.id.calculate_button);
		calculateButton.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				ctnNumber = ((ctnNumberEditText
						.getText().toString().length() == 0)) ? 0 : Integer
						.parseInt((ctnNumberEditText.getText().toString()));
				pcsNumber = ((pcsNumberEditTextOrg
						.getText().toString().length() == 0)) ? 0 : Integer
						.parseInt((pcsNumberEditTextOrg.getText().toString()));
				ctnNumberOrg = ((ctnNumberEditTextOrg
						.getText().toString().length() == 0)) ? 0 : Integer
						.parseInt((ctnNumberEditTextOrg.getText().toString()));
				pcsNumberOrg = ((pcsNumberEditTextOrg
						.getText().toString().length() == 0)) ? 0 : Integer
						.parseInt((pcsNumberEditTextOrg.getText().toString()));
				suggested = ((suggestedText.getText().toString().equals("-1")) || (suggestedText
						.getText().toString().length() == 0)) ? -1 : Integer
						.parseInt((suggestedText.getText().toString()));

                Log.d(TAG, "pcsPerCtn:" + pcsPerCtn);

				if (pcsPerCtn != 0) {
//					ctnNumber += pcsNumber / pcsPerCtn;
//					pcsNumber = pcsNumber % pcsPerCtn;
//
//					ctnNumberEditText.setText("" + ctnNumber);
//					pcsNumberEditText.setText("" + pcsNumber);
//
//					ctnNumberOrg += pcsNumberOrg / pcsPerCtn;
//					pcsNumberOrg = pcsNumberOrg % pcsPerCtn;
//
//					ctnNumberEditTextOrg.setText("" + ctnNumberOrg);
//					pcsNumberEditTextOrg.setText("" + pcsNumberOrg);
				}

				discountValue = ((discountValueText.getText().toString().length() == 0)) ? 0 : Integer
						.parseInt((discountValueText.getText().toString()));

				valueNumber = ctnNumber * ctnRate + pcsNumber * pcsRate - discountValue;
				if(valueNumber < 0){

					discountValueText.setText("");
					discountValue = 0;
					valueNumber = ctnNumber * ctnRate + pcsNumber * pcsRate;
					valueNumberText.setText(twoDigDecimalFormat.format(valueNumber));
					flag = 1;
					showPleaseEnterOrderDialog(4);

				}
				else {
					valueNumberText.setText(twoDigDecimalFormat.format(valueNumber));
					flag = 0;
				}
			}
		});

		Button orderButton = (Button) findViewById(R.id.order_button);
		orderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateButton.performClick();
//				allocationQty = DatabaseQueryUtil.getAllocationQty(context,
//						skuId, outletId);
				allocationQty = 0;
				if (pcsNumberOrg == 0 ) {
					showPleaseEnterOrderDialog(1);
				}
				else
					placeOrder();
				/*if (ctnNumberOrg == 0 && pcsNumberOrg == 0) {
					showPleaseEnterOrderDialog(1);
				}
				else if(valueNumber < 0)
					showPleaseEnterOrderDialog(4);
				else if (ctnNumber == 0 && pcsNumber == 0) {
					showPleaseEnterOrderDialog(1);
					//if (ctnNumber == 0 && pcsNumber == 0) {
					//showPleaseEnterOrderDialog(1);
				} else if ((ctnNumber * (long) pcsPerCtn
						+ pcsNumber) > (ctnNumberOrg * (long) pcsPerCtn
						+ pcsNumberOrg)) {
					showPleaseEnterOrderDialog(3);

				} else if (maxOrderQty > 0 && (ctnNumberOrg * (long) pcsPerCtn
						+ pcsNumberOrg) > maxOrderQty) {
					showPleaseEnterOrderDialog(2);
				}
				//else if (criticalStock == 0) {	//showStockNotAvailableDialog();				 				}
				else if (criticalStock > 0 && flag == 0) {
					Intent data = DatabaseQueryUtil
							.totalOrderedQuantityForSkuInOtherVisitedOutlets(
									context, outletId, skuId);
					int usedCtn = data.getIntExtra(
							DatabaseConstants.tblOrderItem.CARTON, 0);
					int usedPcs = data.getIntExtra(
							DatabaseConstants.tblOrderItem.PIECE, 0);

					int available = criticalStock
							- (usedCtn * pcsPerCtn + usedPcs);*/
					//if ((long) available < ctnNumber * (long) pcsPerCtn + pcsNumber) {
						//showStockNotAvailableDialog();
					//} else {
						//showAllocationQtyMessageDialog();
					//	if(flag == 0)
							//placeOrder();
				//	}
				//} else {
					//showAllocationQtyMessageDialog();
					//if(flag == 0)


			}
		});

		Button removeButton = (Button) findViewById(R.id.remove_button);
		removeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(
						DatabaseConstants.otherFields.REMOVE_STATUS, 1);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});

		if (orderedStatus == OutletVisitPageActivity.ORDERED_SKU) {
			ctnNumberEditText.setText(ctnNumberOrg + "");
			pcsNumberEditText.setText(pcsNumber + "");
			ctnNumberEditTextOrg.setText(ctnNumberOrg + "");
			pcsNumberEditTextOrg.setText(pcsNumberOrg + "");
			discountValueText.setText(discountValue + "");
			double valueNumber = ctnNumber * ctnRate + pcsNumber * pcsRate - discountValue;
			valueNumberText.setText(twoDigDecimalFormat.format(valueNumber));
			removeButton.setVisibility(View.VISIBLE);
		}
	}

	private void placeOrder() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.CARTON, ctnNumberOrg);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.PIECE, pcsNumberOrg);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.CARTON_ORG, ctnNumberOrg);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.PIECE_ORG, pcsNumberOrg);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.TK_OFF, discountValue);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.TOTAL, valueNumber);
		returnIntent.putExtra(DatabaseConstants.tblOrderItem.SUGGESTED,	suggested);
		returnIntent.putExtra(DatabaseConstants.otherFields.REMOVE_STATUS, 0);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	private void showAllocationQtyMessageDialog() {
		AlertDialog.Builder allocationQtyMessageDialog = new AlertDialog.Builder(
				context);
		allocationQtyMessageDialog.setTitle("Allocation Quantity");
		allocationQtyMessageDialog.setMessage("Your Allocation Qty "
				+ this.allocationQty + " pcs" + "\r\n Your Ordered Qty "
				+ this.ctnNumber + " pcs" + "\r\n Do you want to continue?");

		allocationQtyMessageDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						placeOrder();
						dialog.cancel();
					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ctnNumberEditText.setText("");
						pcsNumberEditText.setText("");
						discountValueText.setText("");
						valueNumberText.setText("");
						dialog.cancel();
					}
				});
		allocationQtyMessageDialog.show();
	}

	private void showStockNotAvailableDialog() {
		AlertDialog.Builder messageForSkuDialog = new AlertDialog.Builder(
				context);
		messageForSkuDialog.setTitle("Stock Not Available");
		messageForSkuDialog.setMessage("Stock not available for this sku.");

		messageForSkuDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		messageForSkuDialog.show();
	}

	private void initializeFields() {
		orderedStatus = getIntent().getIntExtra(
				DatabaseConstants.otherFields.ORDER_STATUS, 0);
		ctnRate = getIntent().getDoubleExtra(DatabaseConstants.tblSKU.CTN_RATE,
				0);
		pcsRate = getIntent().getDoubleExtra(DatabaseConstants.tblSKU.PCS_RATE,
				0);
		pcsPerCtn = getIntent().getIntExtra(DatabaseConstants.tblSKU.PCS_PER_CTN, 0);
		outletId = getIntent().getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID);
		skuId = getIntent().getStringExtra(DatabaseConstants.tblSKU.SKUID);

		if (orderedStatus == OutletVisitPageActivity.ORDERED_SKU) {
			ctnNumber = getIntent().getIntExtra(
					DatabaseConstants.tblOrderItem.CARTON, 0);
			pcsNumber = getIntent().getIntExtra(
					DatabaseConstants.tblOrderItem.PIECE, 0);
			ctnNumberOrg = getIntent().getIntExtra(
					DatabaseConstants.tblOrderItem.CARTON_ORG, 0);
			pcsNumberOrg = getIntent().getIntExtra(
					DatabaseConstants.tblOrderItem.PIECE_ORG, 0);
			discountValue = getIntent().getIntExtra(
					DatabaseConstants.tblOrderItem.TK_OFF, 0);
		}

		skuName = getIntent().getStringExtra(DatabaseConstants.tblSKU.TITLE);
		criticalStock = getIntent().getIntExtra(
				DatabaseConstants.tblSKU.CRITICAL_STOCK, -1);
		//priceValue = twoDigDecimalFormat.format(ctnRate) + " per pcs, "
				//+ twoDigDecimalFormat.format(pcsRate) + " per pcs";
		priceValue = twoDigDecimalFormat.format(pcsRate) + " per Box";
		promotionInfoMessage = getIntent().getStringExtra(
				DatabaseConstants.tblSKU.MESSAGE_FOR_HHT);
	//	OrderItem tempOrder=DatabaseQueryUtil.getOrderSkuOrderItem(context, outletId, skuId);
//		suggested=(tempOrder.Suggested);
		suggested=getIntent().getIntExtra(DatabaseConstants.tblOutletSKU.SUGGESTEDQTY,0);
		maxOrderQty=getIntent().getIntExtra(DatabaseConstants.tblOutletSKU.MAXORDERQTY, 0);
		colorid=getIntent().getIntExtra(DatabaseConstants.tblOutletSKU.COLORID,0);
		if (promotionInfoMessage != null && promotionInfoMessage.length() != 0) {
			showPromotionMessageDialog();
		}
	}

//	@Override
//	public void onBackPressed() {
//		Log.d(TAG, "OrderDetailPageActivity::onBackPressed:");
//		super.onBackPressed();
//	}

	// svn checking
	private void showPromotionMessageDialog() {
		AlertDialog.Builder messageForSkuDialog = new AlertDialog.Builder(
				context);
		messageForSkuDialog.setTitle("Message");
		messageForSkuDialog.setMessage(promotionInfoMessage);

		messageForSkuDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		messageForSkuDialog.show();
	}

	private void showPleaseEnterOrderDialog(int msgnumber) {
		AlertDialog.Builder messageForSkuDialog = new AlertDialog.Builder(context);
		messageForSkuDialog.setTitle("Message");

		if(msgnumber==1) {

			messageForSkuDialog
					.setMessage("Please enter some carton or piece amount.");

		} else if(msgnumber==2) {

            messageForSkuDialog
					.setMessage("Maximum Original Order quantity is: "+maxOrderQty);

		} else if(msgnumber==3) {
			messageForSkuDialog
					.setMessage("Maximum Actual Order quantity is: "+ctnNumberOrg + "-" + pcsNumberOrg);
		}
		else if(msgnumber==4)
		{
			messageForSkuDialog
					.setMessage("Total value cannot be negative!");
		}
		messageForSkuDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		messageForSkuDialog.show();
	}
}
