package com.happyrobotics.pocketbookspro;

import java.math.BigDecimal;
import java.util.Calendar;

import com.pocketbooks.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EditTransactionActivity extends Activity {
	// final static String TAG = EditTransactionActivity.class.getSimpleName();
	private static final int DATE_DIALOG = 0;

	PocketBooksApplication pb;

	AccountData transaction;
	EditText transactionName;
	EditText transactionAmount;
	EditText transactionDate;
	Spinner transactionCategory;
	EditText transactionMemo;
	Button transactionDone;
	RadioGroup radioGroup;
	RadioButton radioDeposit;
	RadioButton radioWithdrawl;

	Intent transactionIntent;
	LinearLayout header;
	TextView headerAccount;
	long catId;
	long id;
	int year;
	int month;
	int day;
	int categoryPostionId;

	Cursor editTransactionInfo;
	Cursor incomeCursor;
	Cursor expenseCursor;

	SimpleCursorAdapter incomeAdapter;
	SimpleCursorAdapter expenseAdapter;
	SharedPreferences prefs;
	boolean categoriesEnabled;
	boolean editingTransaction;

	@Override
	public void onCreate(Bundle SavedInstance) {
		super.onCreate(SavedInstance);

		categoryPostionId = 0;
		setContentView(R.layout.new_transaction_activity_layout);

		transactionIntent = getIntent();

		id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);

		header = (LinearLayout) findViewById(R.id.header);
		header.setBackgroundColor(AccountData.GREEN);

		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText(R.string.new_transaction);

		transaction = new AccountData(this);
		pb = (PocketBooksApplication) this.getApplication();

		prefs = pb.getPrefs();
		categoriesEnabled = prefs.getBoolean("category", false);

		transactionName = (EditText) findViewById(R.id.Payee_editText);
		transactionAmount = (EditText) findViewById(R.id.amount_EditText);
		transactionDate = (EditText) findViewById(R.id.date_EditText);
		transactionCategory = (Spinner) findViewById(R.id.category_Spinner);
		((TableRow) transactionCategory.getParent()).setVisibility(View.GONE);
		transactionMemo = (EditText) findViewById(R.id.note_EditText);
		radioGroup = (RadioGroup) findViewById(R.id.Deposit_Or_Withdrawl);
		radioDeposit = (RadioButton) findViewById(R.id.desposit_RadioButton);
		radioWithdrawl = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		transactionDone = (Button) findViewById(R.id.new_transaction_activity_done_Button);

		radioWithdrawl.setChecked(true);

		if (categoriesEnabled) {
			((TableRow) transactionCategory.getParent())
					.setVisibility(View.VISIBLE);

			transactionCategory
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long id) {
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

			String[] from = { AccountData.TRANSACTION_CATEGORY };
			int[] to = { R.id.category_name };

			incomeAdapter = new SimpleCursorAdapter(this,
					R.layout.category_listview_row, incomeCursor, from, to);
			expenseAdapter = new SimpleCursorAdapter(this,
					R.layout.category_listview_row, expenseCursor, from, to);

			transactionCategory.setAdapter(incomeAdapter);
			int incomeCount = incomeAdapter.getCount();
			for (int i = 0; i < incomeCount; i++) {
				if (catId == incomeAdapter.getItemId(i)) {
					categoryPostionId = i;
				}
			}
			transactionCategory.setSelection(categoryPostionId);
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radioDeposit.getId() && categoriesEnabled) {
					transactionCategory.setAdapter(incomeAdapter);
				}
				if (checkedId == radioWithdrawl.getId() && categoriesEnabled) {
					transactionCategory.setAdapter(expenseAdapter);
				}
			}

		});
		// Log.d(TAG, "GOT ID " + id);

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		updateDate();

		transactionDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "date focus" + hasFocus);

				if (hasFocus) {
					showDialog(DATE_DIALOG);
				}

			}

		});

		transactionDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "date clickity clack");
				showDialog(DATE_DIALOG);
			}

		});

		transactionDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				BigDecimal newAmount = new BigDecimal("0.00").setScale(2,
						BigDecimal.ROUND_HALF_UP);
				if (transactionAmount.length() > 0) {
					newAmount = new BigDecimal(transactionAmount.getText()
							.toString());
					if (radioWithdrawl.isChecked()) {
						newAmount = newAmount.negate();
					}
				}
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);

				if (editingTransaction) {
					// Log.d(TAG, "Trying to update");
					transaction.updateTransaction(id, transactionName.getText()
							.toString(), newAmount, cal.getTimeInMillis(),
							catId, transactionMemo.getText().toString());

				} else {
					transaction.addTransaction(id, transactionName.getText()
							.toString(), newAmount, cal.getTimeInMillis(),
							catId, transactionMemo.getText().toString());
				}
				
				if (editTransactionInfo != null) {
					editTransactionInfo.close();
				}
				
				if (incomeCursor != null) {
					incomeCursor.close();
				}
				if (expenseCursor != null){
					expenseCursor.close();
				}
				finish();
			}

		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (editTransactionInfo != null) {
			editTransactionInfo.deactivate();
		}

	
		if (prefs.getBoolean("category", false)) {
			incomeCursor.deactivate();
			expenseCursor.deactivate();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		editingTransaction = transactionIntent.getBooleanExtra("edit", false);

		if (editingTransaction && editTransactionInfo == null) {
			editTransactionInfo = transaction.getTransactionInfo(id);
			startManagingCursor(editTransactionInfo);
		}

		// EDITING TRANSACTION
		if (editingTransaction) {
			headerAccount.setText(R.string.edit_transaction);

			prefs = pb.getPrefs();
			if (prefs.getBoolean("category", false)) {
				incomeCursor.requery();
				expenseCursor.requery();
			}
			
			editTransactionInfo.requery();
			editTransactionInfo.moveToFirst();

			catId = editTransactionInfo.getLong(editTransactionInfo
					.getColumnIndex(AccountData.TRANSACTION_CATEGORY));

			BigDecimal amount = new BigDecimal(
					editTransactionInfo.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
			amount = amount.movePointLeft(2);

			if (amount.signum() > 0) {

				radioDeposit.setChecked(true);

				if (categoriesEnabled) {
					incomeCursor.moveToFirst();
					transactionCategory.setAdapter(incomeAdapter);
					int incomeCount = incomeAdapter.getCount();
					for (int i = 0; i < incomeCount; i++) {
						if (catId == incomeAdapter.getItemId(i)) {
							categoryPostionId = i;
						}
					}
					transactionCategory.setSelection(categoryPostionId);
				}
			}
			amount = amount.abs();

			// c.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_DATE)));

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo
					.getColumnIndex(AccountData.TRANSACTION_DATE)));

			transactionName.setText(editTransactionInfo
					.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_NAME)));
			transactionAmount.setText(amount.toString());
			transactionDate.setText(new StringBuilder()
					.append(cal.get(Calendar.MONTH)).append("/")
					.append(cal.get(Calendar.DAY_OF_MONTH)).append("/")
					.append(cal.get(Calendar.YEAR)));
			transactionMemo.setText(editTransactionInfo
					.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_MEMO)));

		}
		// ---------------------------------------------------------------------
		// ---------------------------------------------------------------------

		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			return new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int mYear, int mMonth,
						int mDay) {
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

	/**
	 * Updates the date displayed in the dateEditText.
	 */
	public void updateDate() {

		transactionDate.setText(new StringBuilder().append(month + 1)
				.append("/").append(day).append("/").append(year));
	}

}
