package com.orion.application;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.Bonus;
import com.orion.util.Util;

import java.text.NumberFormat;

public class SpaceManagementPageActivity extends AppCompatActivity
{	
	private static final int UPDATED = 1;
	private static final int NOT_UPDATED = 0;
	private String outletId;
	private Button yesButton;
	private Button noButton;	
	private Context context;
	private NumberFormat twoDigDecimalFormat;
	private Bonus bonus;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_management_page_layout);
		context = this;

		twoDigDecimalFormat = NumberFormat.getNumberInstance();
		twoDigDecimalFormat.setMinimumFractionDigits(2);
		twoDigDecimalFormat.setMaximumFractionDigits(2);

		initializeFields();

		((TextView) findViewById(R.id.bonus_month_info)).setText("Bonus for the month of  " + bonus.spMonth);
		((TextView) findViewById(R.id.bonus_value)).setText(twoDigDecimalFormat.format(bonus.bonusQty));

		yesButton = (Button) findViewById(R.id.yes_button);
		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DatabaseQueryUtil.updateTblSpMgtReturn(context, Integer.getInteger(outletId), UPDATED);
				setResult(RESULT_OK);
				finish();
			}
		});
		noButton = (Button) findViewById(R.id.no_button);
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseQueryUtil.updateTblSpMgtReturn(context, Integer.getInteger(outletId), NOT_UPDATED);
				setResult(RESULT_OK);
				finish();
			}
		});

	}

   	private void initializeFields() {
		outletId = getIntent().getStringExtra(DatabaseConstants.tblOutlet.OUTLET_ID);
		bonus = DatabaseQueryUtil.getBonus(context, outletId);
	}
}