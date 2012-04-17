package com.pocketbooks;

import java.math.BigDecimal;
import java.util.Calendar;

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

public class TransactionActivity extends Activity {
	// final static String TAG = EditTransactionActivity.class.getSimpleName();
	private static final int DATE_DIALOG = 0;

	PocketBooksApplication pb;
	AccountData transaction;
	EditText editTransactionName;
	EditText editTransactionAmount;
	EditText editTransactionDate;
	Spinner editTransactionCategory;
	EditText editTransactionMemo;
	Button editTransactionDone;
	Cursor editTransactionInfo;
	Intent transactionIntent;
	RadioGroup editRadioGroup;
	RadioButton editDeposit;
	RadioButton editWithdrawl;
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
	SharedPreferences prefs;
	boolean catEnabled;
	boolean intentHasExtras;

	@Override
	public void onCreate(Bundle SavedInstance) {
		super.onCreate(SavedInstance);

		setContentView(R.layout.new_transaction_activity_layout);

		header = (LinearLayout) findViewById(R.id.header);
		header.setBackgroundColor(AccountData.GREEN);

		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText(R.string.edit_transaction);

		transaction = new AccountData(this);
		pb = (PocketBooksApplication) this.getApplication();

		prefs = pb.getPrefs();
		catEnabled = prefs.getBoolean("category", false);

		editTransactionName = (EditText) findViewById(R.id.Payee_editText);
		editTransactionAmount = (EditText) findViewById(R.id.amount_EditText);
		editTransactionDate = (EditText) findViewById(R.id.date_EditText);
		editTransactionCategory = (Spinner) findViewById(R.id.category_Spinner);
		((TableRow) editTransactionCategory.getParent())
				.setVisibility(View.GONE);
		editTransactionMemo = (EditText) findViewById(R.id.note_EditText);
		editRadioGroup = (RadioGroup) findViewById(R.id.Deposit_Or_Withdrawl);
		editDeposit = (RadioButton) findViewById(R.id.desposit_RadioButton);
		editWithdrawl = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		editTransactionDone = (Button) findViewById(R.id.new_transaction_activity_done_Button);
		intentHasExtras = false;

		transactionIntent = getIntent();

		if (transactionIntent.hasExtra(AccountData.TRANSACTION_ID)) {
			intentHasExtras = true;
			id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);

			editTransactionInfo = transaction.getTransactionInfo(id);
			editTransactionInfo.moveToFirst();
			startManagingCursor(editTransactionInfo);

			catId = editTransactionInfo.getLong(editTransactionInfo
					.getColumnIndex(AccountData.TRANSACTION_CATEGORY));
		}
		editDeposit.setChecked(true);

		if (catEnabled) {
			((TableRow) editTransactionCategory.getParent()).setVisibility(View.VISIBLE);
			
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

		editRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == editDeposit.getId() && catEnabled) {
					editTransactionCategory.setAdapter(incomeAdapter);
				}
				if (checkedId == editWithdrawl.getId() && catEnabled) {
					editTransactionCategory.setAdapter(expenseAdapter);
				}

			}
		});
		// Log.d(TAG, "GOT ID " + id);

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

		editTransactionDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				BigDecimal newAmount = new BigDecimal("0.00").setScale(2,
						BigDecimal.ROUND_HALF_UP);
				if (editTransactionAmount.length() > 0) {
					newAmount = new BigDecimal(editTransactionAmount.getText()
							.toString());
					if (editWithdrawl.isChecked()) {
						newAmount = newAmount.negate();
					}
				}
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);

				// Log.d(TAG, "Trying to update");
				transaction.updateTransaction(id, editTransactionName.getText()
						.toString(), newAmount, cal.getTimeInMillis(), catId,
						editTransactionMemo.getText().toString());
				finish();
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
		editTransactionInfo.deactivate();
		prefs = pb.getPrefs();
		if (prefs.getBoolean("category", false)) {
			incomeCursor.deactivate();
			expenseCursor.deactivate();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		int positionId = 0;
		prefs = pb.getPrefs();
		if (prefs.getBoolean("category", false)) {
			incomeCursor.requery();
			expenseCursor.requery();
		}
		
		int incomeCount = incomeAdapter.getCount();
		for (int i = 0; i < incomeCount; i++) {
			if (catId == incomeAdapter.getItemId(i)) {
				positionId = i;
			}
		}
		
		editTransactionCategory.setSelection(positionId);
		if (intentHasExtras) {

			editTransactionInfo.requery();

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

				editWithdrawl.setChecked(true);
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
			editTransactionName.setText(editTransactionInfo
					.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_NAME)));
			editTransactionAmount.setText(amount.toString());
			editTransactionDate.setText(new StringBuilder()
					.append(cal.get(Calendar.MONTH)).append("/")
					.append(cal.get(Calendar.DAY_OF_MONTH)).append("/")
					.append(cal.get(Calendar.YEAR)));
			editTransactionMemo.setText(editTransactionInfo
					.getString(editTransactionInfo
							.getColumnIndex(AccountData.TRANSACTION_MEMO)));

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		editTransactionInfo.close();
		prefs = pb.getPrefs();
		if (prefs.getBoolean("category", false)) {
			incomeCursor.close();
			expenseCursor.close();

		}
	}

	/**
	 * Updates the date displayed in the dateEditText.
	 */
	public void updateDate() {

		editTransactionDate.setText(new StringBuilder().append(month + 1)
				.append("/").append(day).append("/").append(year));
	}

}
