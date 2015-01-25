package com.happyrobotics.pocketbookspro;

import java.math.BigDecimal;
import java.util.Calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class OverviewActivity extends SherlockActivity {
	
	private static String TAG = "PocketBOOKSPro  ";
	
	PocketBooksApplication pb;
	AccountData accountData;
	LinearLayout pendingLayout;
	LinearLayout overviewLayout;
	TextView fundsTextView;
	ListView currentMonthListView;
	SharedPreferences prefs;
	BigDecimal sum;
	Cursor accountCountCursor;
	Cursor accountsSumCursor;
	Cursor currentMonthTransactionsCursor;
	boolean hasAccounts;
	Calendar cal;
	long monthInMillis;
	Intent newAccountIntent;
	Intent accountsIntent;
	Intent transactionIntent;
	Intent preferencesIntent;
	Intent overviewIntent;
	long accountId;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		accountData = new AccountData(this);
		newAccountIntent = new Intent(this, NewAccountActivity.class);
		accountsIntent = new Intent(this, AccountsActivity.class);
		transactionIntent = new Intent(this, TransactionActivity.class);
		preferencesIntent = new Intent(this, Prefs.class);
		overviewIntent = new Intent(this, OverviewActivity.class);
		
		accountCountCursor = accountData.getAccounts();
		startManagingCursor(accountCountCursor);
		
		if(0 == accountCountCursor.getCount()){
			startActivity(newAccountIntent);
		}
		else {
			hasAccounts = true;
		}
		
		getSupportActionBar();
		setContentView(R.layout.overview_activity_layout_test);
		
		overviewLayout = (LinearLayout) findViewById(R.id.overview);
		overviewLayout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(accountsIntent);
				
			}
			
		});
		
		
		fundsTextView = (TextView) findViewById(R.id.fundsTextView);
		currentMonthListView = (ListView) findViewById(R.id.listView1);
		pb = (PocketBooksApplication) this.getApplication();
		prefs = pb.getPrefs();
		sum = BigDecimal.ZERO;
		
		fundsTextView.setText(sum.toString());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.overview_activity_menu, menu);
		
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.new_transaction:
			startActivity(transactionIntent);
			break;
		case R.id.categories:
			// categories Intent
			break;
		case R.id.preferences:
			startActivity(preferencesIntent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		accountId = overviewIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
		monthInMillis = getCurrentMonth();
		accountCountCursor = accountData.getAccounts();
		
		if(accountCountCursor.getCount() > 0){
			hasAccounts = true;
		}
		
		if(hasAccounts && accountId == 0){
			accountsSumCursor = accountData.getAccountsSum();
			currentMonthTransactionsCursor = accountData.getTransactions();
		} else {
			accountsSumCursor = accountData.getAccountInfo(accountId);
			currentMonthTransactionsCursor = accountData.getTransactions(accountId);
		}
			startManagingCursor(accountsSumCursor);
			accountsSumCursor.requery();
			accountsSumCursor.moveToFirst();
		
			sum = new BigDecimal(accountsSumCursor.getString(accountsSumCursor.getColumnIndex(AccountData.ACCOUNT_BALANCE)));
			startManagingCursor(currentMonthTransactionsCursor);
			currentMonthTransactionsCursor.moveToFirst();
			int[] to = {R.id.transaction_name, R.id.transaction_amount, R.id.transaction_category, R.id.transaction_date, R.id.transaction_memo};
			String[] from = {AccountData.TRANSACTION_NAME, AccountData.TRANSACTION_AMOUNT, AccountData.TRANSACTION_CATEGORY, AccountData.TRANSACTION_DATE, AccountData.TRANSACTION_MEMO};
			TransactionAdapter adapter = new TransactionAdapter(this, R.layout.transactions_activity_listview_row, currentMonthTransactionsCursor, from, to, false);
			currentMonthListView.setAdapter(adapter);
		
		
		sum = sum.movePointLeft(2);
		fundsTextView.setText(sum.toPlainString());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private long getCurrentMonth(){
		cal = Calendar.getInstance();
		
		//Initialize calendar to first day of current month
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
	}
}
