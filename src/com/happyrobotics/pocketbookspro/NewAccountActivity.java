package com.happyrobotics.pocketbookspro;

import java.math.BigDecimal;

import com.pocketbooks.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewAccountActivity extends Activity {
	//private static String TAG = "newAccount";
	EditText accountName;
	EditText accountBalance;
	Button done;
	AccountData accounts;  
	TextView headerAccount;
	Intent accountIntent;
	long id;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		accounts = new AccountData(this);
		
		// Setup GUI
		setContentView(R.layout.new_account_activity_layout);
		
		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText(R.string.new_account_title);
		accountIntent = getIntent();
		
		accountName = (EditText) findViewById(R.id.new_account_name_edit_text);
		accountBalance = (EditText) findViewById(R.id.new_account_balance_edit_text);
		accountBalance.setFilters(new InputFilter[] {new DecimalInputFilter(2)} );
		done = (Button) findViewById(R.id.new_account_done_button);
		
		long id = 0;
		
		done.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BigDecimal balance = new BigDecimal("0.00");
				
				if(accountName.getText() != null){
					if(accountBalance.getText().length() != 0){
						//Log.d(TAG, "this is the getText..." + accountBalance.getEditableText());
						try{
							balance = new BigDecimal(accountBalance.getText().toString());
							balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
						} catch(NumberFormatException e){
							//Log.d(TAG, "ERROR! ERROR! \"balance\" not a number!!!!");
						}
					}
					if(accountIntent.getBooleanExtra("edit", false)){
						long id = accountIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
						accounts.updateAccount(id, accountName.getText().toString());
						
					} else {
					
						//Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
					
						accounts.createAccount(accountName.getText().toString(), balance);
					}
				}
				finish();
			}	
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(accountIntent.getBooleanExtra("edit", false)){
			
			accountBalance.setEnabled(false);
			accountBalance.setVisibility(EditText.INVISIBLE);
			
			id = accountIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
			Cursor accountCursor = accounts.getAccountInfo(id);
			startManagingCursor(accountCursor);
			
			accountCursor.moveToFirst();
			String accountNameString = accountCursor.getString(accountCursor.getColumnIndex(AccountData.ACCOUNT_NAME));
			
			accountName.setText(accountNameString);
			done.setText("Update");
		}
	}
	
	
}
