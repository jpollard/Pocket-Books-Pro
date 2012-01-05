package com.happyrobotics.pocketbookspro;

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
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NewTransactionActivity extends Activity {
	private static final int DATE_DIALOG = 0;
	//private static String TAG = "NewTransactionActivity";
	
	PocketBooksApplication pb;
	
	EditText payeeEditText;
	EditText amountEditText;
	EditText dateEditText;
	EditText noteEditText;
	Spinner	categorySpinner;
	RadioGroup radioGroup;
	RadioButton depositRadioButton;
	RadioButton withdrawlRadioButton;
	Button doneButton;
	AccountData accounts;
	int year;
	int month;
	int day;
	long id;
	Intent transactionIntent;
	TextView headerAccount;
	private SimpleCursorAdapter incomeAdapter;
	Cursor cursor;
	int catId;
	
	SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		pb = (PocketBooksApplication) this.getApplication();
		prefs = pb.getPrefs();
		
		setContentView(R.layout.new_transaction_activity_layout);
		
		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText(R.string.new_transaction);
		transactionIntent = getIntent();
		id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
		accounts = new AccountData(this);
		
		cursor = accounts.getIncomeCategories();
		startManagingCursor(cursor);
		String[] from = {AccountData.INCOME_CATEGORIES};
		int[] to = {R.id.category_name};
		incomeAdapter = new SimpleCursorAdapter(this, R.layout.category_listview_row, cursor, from, to);
		
		payeeEditText = (EditText) findViewById(R.id.Payee_editText);
		radioGroup = (RadioGroup) findViewById(R.id.Deposit_Or_Withdrawl);
		depositRadioButton = (RadioButton) findViewById(R.id.desposit_RadioButton);
		withdrawlRadioButton = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		amountEditText = (EditText) findViewById(R.id.amount_EditText);
		amountEditText.setFilters(new InputFilter[] {new DecimalInputFilter(2)});
		dateEditText = (EditText) findViewById(R.id.date_EditText);
		noteEditText = (EditText) findViewById(R.id.note_EditText);
		categorySpinner = (Spinner) findViewById(R.id.category_Spinner);
		catId = 0;
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == depositRadioButton.getId()){
					categorySpinner.setAdapter(incomeAdapter);
				}
			}
			
		});

		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int itemId, long arg3) {
				catId = itemId + 1; //Add one since the spinner is zero indexed whereas sql is one indexed.
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		if(!prefs.getBoolean("category", false)){
			((TableRow)categorySpinner.getParent()).setVisibility(View.GONE);
		}
		
		doneButton = (Button) findViewById(R.id.new_transaction_activity_done_Button);
		
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		updateDate();
		
		
		
		dateEditText.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "date focus" + hasFocus);
				
				if(hasFocus){
					showDialog(DATE_DIALOG);
				}
				
			}
			
		});
		
		dateEditText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			}
			
		});
		
		doneButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String payeeString = payeeEditText.getEditableText().toString();
				
				BigDecimal amountBD = new BigDecimal(0.00);
				
				
				
				if(amountEditText.length() > 0){
					amountBD = new BigDecimal(amountEditText.getEditableText().toString());
					amountBD = amountBD.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					if(withdrawlRadioButton.isChecked()){
						amountBD = amountBD.negate();
					}
				}
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				
				String memoString = noteEditText.getEditableText().toString();
				
				accounts.addTransaction(id, payeeString, amountBD, cal.getTimeInMillis(), catId, memoString);
				finish();
			}
		});
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG:
	        return new DatePickerDialog(this, new OnDateSetListener(){

							@Override
							public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
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
	public void updateDate(){
		
		dateEditText.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		cursor.close();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		cursor.deactivate();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		cursor.requery();
	}
	
}
