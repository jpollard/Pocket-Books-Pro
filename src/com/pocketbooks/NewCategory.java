package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class NewCategory extends Activity{
	private static String TAG ="PocketBooks";
	
	
	private AccountData data;
	private Button buttonDone;
	private TextView headerAccount;
	private EditText editTextCategoryName;
	private RadioGroup radioGroupExpenseOrIncome;
	private RadioButton radioButtonExpense;
	private RadioButton radioButtonIncome;
	private char categoryType;
	private long categoryId;
	private boolean extras;
	private Cursor categoryCursor;
	private String categoryName;
	private String categoryTypeString;
	
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		extras = false;
		data = new AccountData(this);
		
		setContentView(R.layout.new_category_activity_layout);
		
		Intent myIntent = getIntent();
		
		editTextCategoryName = (EditText) findViewById(R.id.editTextCategoryName);
		radioGroupExpenseOrIncome = (RadioGroup) findViewById(R.id.radioGroupExpenseOrIncome);
		radioButtonExpense = (RadioButton) findViewById(R.id.radioButtonExpense);
		radioButtonIncome = (RadioButton) findViewById(R.id.radioButtonIncome);
		buttonDone = (Button) findViewById(R.id.buttonDone);
		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText("Add Category");
		
		if(buttonDone.isEnabled()){
			buttonDone.setEnabled(false);
		}
		
		if(myIntent.hasExtra("id")){
			categoryId = myIntent.getLongExtra("id", 0);
			headerAccount.setText("Update Category");
			buttonDone.setText("Update");
			Log.d("POCKET BOOKS BLLAHALLAHAHHAHHA", "id is ==============" + categoryId);
			
			extras = true;
			categoryCursor = data.getCategoryInfo(categoryId);
			categoryCursor.moveToFirst();
			startManagingCursor(categoryCursor);
			
			categoryName = categoryCursor.getString(categoryCursor.getColumnIndex(AccountData.TRANSACTION_CATEGORY));
			categoryTypeString = categoryCursor.getString(categoryCursor.getColumnIndex(AccountData.CATEGORY_TYPE));
			Log.d("CATEGORY STRING", "" + categoryTypeString);
			categoryCursor.close();
			
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		// If extras is true, setup the activity for updating a previous category
		if(extras){
			editTextCategoryName.setText(categoryName);
			if(categoryTypeString.contentEquals("I")){
				buttonDone.setEnabled(true);
				radioGroupExpenseOrIncome.check(R.id.radioButtonIncome);
			}else if(categoryTypeString.contentEquals("E")){
				buttonDone.setEnabled(true);
				radioGroupExpenseOrIncome.check(R.id.radioButtonExpense);
			}
		}
		
		radioGroupExpenseOrIncome.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int radioButton) {
				Log.d("CHANING THE ENDO FHTEJ", "BLAH");
				switch(radioButton){
					case R.id.radioButtonExpense:
						buttonDone.setEnabled(true);
						categoryType = 'E';
						
						break;
					case R.id.radioButtonIncome:
						buttonDone.setEnabled(true);
						categoryType = 'I';
						
						break;
				}
				
			}
			
		});

		buttonDone.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				categoryName = editTextCategoryName.getText().toString();
				
				if(extras){
					categoryTypeString = String.valueOf(categoryType);
					Log.d("UDPATEDjKFJDKJ", categoryTypeString);
					data.updateCategory(categoryId, categoryName, categoryTypeString);
				} else {
					data.addCategory(categoryName, categoryType);
				}
				
				finish();
			}
			
		});
		
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	

}
