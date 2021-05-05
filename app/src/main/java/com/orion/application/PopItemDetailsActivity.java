package com.orion.application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.orion.adapter.PopItemListAdapter;
import com.orion.database.DatabaseConstants;
import com.orion.database.DatabaseQueryUtil;
import com.orion.entities.PopItem;
import com.orion.util.Util;

import java.util.ArrayList;

public class PopItemDetailsActivity extends AppCompatActivity {
	private ListView popItemListView;
	private PopItemListAdapter popItemListAdapter;
	private PopItem selectedPopItem;
	private int selectedPopItemIndex;
	private Context context;
	private ArrayList<PopItem> popItemList;
	private int outletId;
	
	public static int POP_ITEM_ENTRY_REQUEST = 100;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Util.cancelWaitingDialog();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_details_page);
		context = this;

		outletId = getIntent().getIntExtra(DatabaseConstants.tblOutlet.OUTLET_ID, -1);

		popItemListView = (ListView) findViewById(R.id.pop_item_list_view);
		popItemListAdapter = new PopItemListAdapter(context);
		popItemListView.setAdapter(popItemListAdapter);
		popItemListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Util.showWaitingDialog(context);
				view.setSelected(true);
				selectedPopItem = popItemListAdapter.getItem(position);
				selectedPopItemIndex = position;
				Intent intent = new Intent(PopItemDetailsActivity.this, PopItemEntryActivity.class);
				intent.putExtra(DatabaseConstants.tblPOPItem.DESCRIPTION, selectedPopItem.description);
				intent.putExtra(DatabaseConstants.tblPOPOutletItem.LAST_QTY, selectedPopItem.lastQty);
				intent.putExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, selectedPopItem.currentQty);
				startActivityForResult(intent, POP_ITEM_ENTRY_REQUEST);
			}
		});
		updatePopItemList();

		Button backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < popItemList.size(); i++) {
					if (popItemList.get(i).currentQty != popItemListAdapter.getItem(i).currentQty) {
						if (popItemList.get(i).currentQty == -1) {
							DatabaseQueryUtil.insertPopItem(context, popItemListAdapter.getItem(i).popItemId, Integer.getInteger(popItemListAdapter.getItem(i).outletId), popItemListAdapter.getItem(i).currentQty);
						} else {
							DatabaseQueryUtil.updatePopItem(context, popItemListAdapter.getItem(i).popItemId, popItemListAdapter.getItem(i).outletId, popItemListAdapter.getItem(i).currentQty);
						}
					}
				}
				onBackPressed();
			}
		});
	}

	private void updatePopItemList() {
		popItemList = DatabaseQueryUtil.getPopItemList(context, String.valueOf(outletId));
		for (int i = 0; i < popItemList.size(); i++) {
			popItemListAdapter.addItem(new PopItem(popItemList.get(i)));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == POP_ITEM_ENTRY_REQUEST) {
			popItemListAdapter.getItem(selectedPopItemIndex).currentQty = data.getIntExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, -1);
			popItemListAdapter.notifyDataSetChanged();
		}
	}
}

