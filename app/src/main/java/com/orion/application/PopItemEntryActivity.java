package com.orion.application;

import com.orion.database.DatabaseConstants;
import com.orion.util.Util;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class PopItemEntryActivity extends AppCompatActivity
{	
	private EditText nowValue;
	private Button backButton;
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
		Util.cancelWaitingDialog();
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.pop_entry_page);     
        ((TextView) findViewById(R.id.item_name)).setText(getIntent().getStringExtra(DatabaseConstants.tblPOPItem.DESCRIPTION));
        ((TextView) findViewById(R.id.last_value)).setText(getIntent().getIntExtra(DatabaseConstants.tblPOPOutletItem.LAST_QTY, -1) < 0 ? "" : Integer.toString(getIntent().getIntExtra(DatabaseConstants.tblPOPOutletItem.LAST_QTY, -1)) );
        nowValue = (EditText) findViewById(R.id.now_value);
        nowValue.setText(getIntent().getIntExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, -1) < 0 ? "" : Integer.toString(getIntent().getIntExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, -1)) );
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener()
        {			
			@Override
			public void onClick(View arg0) 
			{
				setResult(RESULT_CANCELED);
				onBackPressed();				
			}
		});
        
        Button okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new OnClickListener()
        {			
			@Override
			public void onClick(View arg0)
			{
				if( nowValue.getText().toString().equalsIgnoreCase(""))
				{
					int prevNowValue = getIntent().getIntExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, -1);
					if(prevNowValue == -1)
					{
						backButton.performClick();					
						return;
					}
					else
					{
						Intent returnIntent = new Intent();
						returnIntent.putExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, 0);				
						setResult(RESULT_OK, returnIntent);       
						finish();
					}
				}
				else
				{
					Intent returnIntent = new Intent();
					returnIntent.putExtra(DatabaseConstants.tblPOPOutletItem.CURRENT_QTY, Integer.parseInt(nowValue.getText().toString()));				
					setResult(RESULT_OK, returnIntent);       
					finish();
				}
			}
		});
    }
}