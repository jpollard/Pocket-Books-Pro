package com.happyrobotics.pocketbookspro;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;



public class TransactionActivity extends SherlockActivity implements ActionBar.OnNavigationListener{
	// final static String TAG = EditTransactionActivity.class.getSimpleName();
	private static final int DATE_DIALOG = 0;

	//test
	PocketBooksApplication pb;
	AccountData transaction;
	Spinner accountFromName;
	Spinner accountToName;
	Spinner actionSpinner;
	EditText editTransactionName;
	EditText editTransactionAmount;
	EditText editTransactionDate;
	Spinner editTransactionCategory;
	EditText editTransactionMemo;
	Cursor accountsInfoCursor;
	Cursor editTransactionInfo;
	Cursor actions;
	Intent transactionIntent;
	
	LinearLayout header;
	TextView headerAccount;
	long catId;
	long id;
	int year;
	int month;
	int day;
	Cursor incomeCursor;
	Cursor expenseCursor;
	SimpleCursorAdapter incomeAdapter;
	SimpleCursorAdapter expenseAdapter;
	SimpleCursorAdapter accountsAdapter;
	SharedPreferences prefs;
	boolean catEnabled;
	boolean intentHasExtras;

	@Override
	public void onCreate(Bundle SavedInstance) {
		super.onCreate(SavedInstance);
		final ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionbar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
		
		
		setContentView(R.layout.new_transaction_activity_layout);

		transaction = new AccountData(this);
		pb = (PocketBooksApplication) this.getApplication();
		
		
		accountsInfoCursor = transaction.getAccounts();
		startManagingCursor(accountsInfoCursor);
		String[] fromAccountInfo = {AccountData.ACCOUNT_NAME, AccountData.ACCOUNT_BALANCE};
		int[] toAccountInfo = {R.id.account_name, R.id.account_balance, };
		accountsAdapter = new AccountAdapter(this,
				R.layout.actionbar_accounts_list_row, accountsInfoCursor, fromAccountInfo, toAccountInfo);
		
		actionbar.setListNavigationCallbacks(accountsAdapter, this);
		prefs = pb.getPrefs();
		catEnabled = prefs.getBoolean("category", false);

		actionSpinner = (Spinner) findViewById(R.id.actionSpinner);
		accountFromName = (Spinner) findViewById(R.id.account_From_Spinner);
		accountToName = (Spinner) findViewById(R.id.account_To_Spinner);
		editTransactionName = (EditText) findViewById(R.id.Payee_editText);
		editTransactionAmount = (EditText) findViewById(R.id.amount_EditText);
		editTransactionDate = (EditText) findViewById(R.id.date_EditText);
		editTransactionCategory = (Spinner) findViewById(R.id.category_Spinner);
		editTransactionMemo = (EditText) findViewById(R.id.note_EditText);
		intentHasExtras = false;

		transactionIntent = getIntent();		

		editTransactionDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "date focus" + hasFocus);

				if (hasFocus) {
					showDialog(DATE_DIALOG);
				}

			}

		});

		editTransactionDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "date clickity clack");
				showDialog(DATE_DIALOG);
			}

		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG :
				return new DatePickerDialog(this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int mYear,
							int mMonth, int mDay) {
						// TODO Auto-generated method stub
						year = mYear;
						month = mMonth;
						day = mDay;
						updateDate();
					}
				}, year, month, day);
		}
		return null;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(transactionIntent.getBooleanExtra("edit", false)){
			editTransactionInfo.deactivate();
		}
		prefs = pb.getPrefs();
		if (incomeCursor != null) {
			incomeCursor.deactivate();
		}
		if (expenseCursor != null) {
			expenseCursor.deactivate();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);
		int positionId = 0;
		
		prefs = pb.getPrefs();
		catEnabled = prefs.getBoolean("category", false);
		
		if (catEnabled) {
			((FrameLayout) editTransactionCategory.getParent()).setVisibility(View.VISIBLE);
			
			editTransactionCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long id) {
							// TODO Auto-generated method stub
							catId = id;
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							catId = 0;
						}

			});

			incomeCursor = transaction.getCategories("I");
			startManagingCursor(incomeCursor);
			expenseCursor = transaction.getCategories("E");
			startManagingCursor(expenseCursor);

			String[] from = {AccountData.TRANSACTION_CATEGORY};
			int[] to = {R.id.category_name};

			incomeAdapter = new SimpleCursorAdapter(this,
					R.layout.category_listview_row, incomeCursor, from, to);
			expenseAdapter = new SimpleCursorAdapter(this,
					R.layout.category_listview_row, expenseCursor, from, to);

			editTransactionCategory.setAdapter(incomeAdapter);
			
		}
		if (incomeCursor != null) {
			incomeCursor.requery();
		}
		if (expenseCursor != null){
			expenseCursor.requery();
		}
		
		if(!transactionIntent.getBooleanExtra("edit", false)){
			Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			
			updateDate();
		}
		
		
		editTransactionCategory.setSelection(positionId);
		if (transactionIntent.getBooleanExtra("edit", false)) {
			if(editTransactionInfo == null){

				editTransactionInfo = transaction.getTransactionInfo(id);
				startManagingCursor(editTransactionInfo);
			}
			
			if(catEnabled){
				int incomeCount = incomeAdapter.getCount();
				for (int i = 0; i < incomeCount; i++) {
					if (catId == incomeAdapter.getItemId(i)) {
						positionId = i;
					}
				}
				editTransactionCategory.setSelection(positionId);
			}

			editTransactionInfo.requery();
			editTransactionInfo.moveToFirst();

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo
					.getColumnIndex(AccountData.TRANSACTION_DATE)));
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo
					.getColumnIndex(AccountData.TRANSACTION_DATE)));
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);

			updateDate();

			BigDecimal amount = new BigDecimal(
					editTransactionInfo.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
			amount = amount.movePointLeft(2);

			if (amount.signum() < 0) {

				//editWithdrawl.setChecked(true);
				amount = amount.abs();

				if (catEnabled) {
					expenseCursor.moveToFirst();
					editTransactionCategory.setAdapter(expenseAdapter);
					int expenseCount = expenseAdapter.getCount();
					for (int i = 0; i < expenseCount; i++) {
						if (catId == expenseAdapter.getItemId(i)) {
							positionId = i;
						}
					}
					editTransactionCategory.setSelection(positionId);
				}
			}
			editTransactionName.setText(editTransactionInfo.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_NAME)));
			editTransactionAmount.setText(amount.toString());
			editTransactionDate.setText(new StringBuilder()
					.append(cal.get(Calendar.MONTH)).append("/")
					.append(cal.get(Calendar.DAY_OF_MONTH)).append("/")
					.append(cal.get(Calendar.YEAR)));
			editTransactionMemo.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_MEMO)));

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(transactionIntent.getBooleanExtra("edit", false)){
			editTransactionInfo.close();
			transaction.close();
		}
		prefs = pb.getPrefs();
		if (prefs.getBoolean("category", false)) {
			incomeCursor.close();
			expenseCursor.close();

		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflate = getSupportMenuInflater();
		inflate.inflate(R.menu.new_transaction_activity_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.Done:
			BigDecimal newAmount = new BigDecimal("0.00").setScale(2,
					BigDecimal.ROUND_HALF_UP);
			if (editTransactionAmount.length() > 0) {
				newAmount = new BigDecimal(editTransactionAmount.getText().toString());
//				if (editWithdrawl.isChecked()) {
//					newAmount = newAmount.negate();
//				}
			}
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day);

			// Log.d(TAG, "Trying to update");
			if(transactionIntent.getBooleanExtra("edit", false)){
				transaction.updateTransaction(id, editTransactionName.getText()
					.toString(), newAmount, cal.getTimeInMillis(), catId,
					editTransactionMemo.getText().toString());
			} else {
				transaction.addTransaction(id, editTransactionName.getText().toString(), newAmount, 
						cal.getTimeInMillis(), catId, editTransactionMemo.getText().toString());
			}
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Updates the date displayed in the dateEditText.
	 */
	public void updateDate() {

		editTransactionDate.setText(new StringBuilder().append(month + 1)
				.append("/").append(day).append("/").append(year));
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

}
