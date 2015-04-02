package com.happyrobotics.pocketbookspro;

import java.math.BigDecimal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NewAccountActivity extends SherlockActivity {
	//private static String TAG = "newAccount";
	EditText accountName;
	EditText accountBalance;
	AccountData accounts;
	Intent accountIntent;
	long id;
	Button FIXME;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		getSupportActionBar();
		accounts = new AccountData(this);
		
		// Setup GUI
		setContentView(R.layout.new_account_activity_layout);
		
		accountIntent = getIntent();
		
		accountName = (EditText) findViewById(R.id.new_account_name_edit_text);
		accountBalance = (EditText) findViewById(R.id.new_account_balance_edit_text);
		accountBalance.setFilters(new InputFilter[] {new DecimalInputFilter(2)} );
		
		id = 0;
		
//		FIXME.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				BigDecimal balance = new BigDecimal("0.00");
//				
//				if(accountName.getText() != null){
//					if(accountBalance.getText().length() != 0){
//						//Log.d(TAG, "this is the getText..." + accountBalance.getEditableText());
//						try{
//							balance = new BigDecimal(accountBalance.getText().toString());
//							balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
//						} catch(NumberFormatException e){
//							//Log.d(TAG, "ERROR! ERROR! \"balance\" not a number!!!!");
//						}
//					}
//					if(accountIntent.getBooleanExtra("edit", false)){
//						long id = accountIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
//						accounts.updateAccount(id, accountName.getText().toString());
//						
//					} else {
//					
//						//Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
//					
//						accounts.createAccount(accountName.getText().toString(), balance);
//					}
//				}
//				finish();
//			}	
//		});
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
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.ok_menu, menu);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.OK:
			addAccount();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addAccount(){
		
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
				id = accountIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
				accounts.updateAccount(id, accountName.getText().toString());
				
			} else {
			
				//Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
			
				accounts.createAccount(accountName.getText().toString(), balance);
			}
		}
		finish();
	}
	
}
