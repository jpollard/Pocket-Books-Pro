package com.happyrobotics.pocketbookspro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class CategoriesEditActivity extends Activity{
	
	private PocketBooksApplication pb;
	
	private AccountData accountData;
	private RadioGroup radioGroupIncomeExpense;
	private RadioButton radioButtonIncome;
	private RadioButton radioButtonExpense;
	private TextView textViewHeaderTitle;
	private ListView listViewCategories;
	private SimpleCursorAdapter cursorAdapterIncome;
	private SimpleCursorAdapter cursorAdapterExpense;
	private Cursor cursorIncome;
	private Cursor cursorExpense;
	private LinearLayout footer;
	private Intent addCategory;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		pb = (PocketBooksApplication) getApplication();
		
		addCategory = new Intent(this, NewCategory.class);
		
		setContentView(R.layout.categories_list_activity);
		
		accountData = new AccountData(this);
		radioGroupIncomeExpense = (RadioGroup) findViewById(R.id.radioGroupExpenseOrIncome);
		radioButtonIncome = (RadioButton) findViewById(R.id.radioButtonIncome);
		radioButtonExpense = (RadioButton) findViewById(R.id.radioButtonExpense);
		textViewHeaderTitle = (TextView) findViewById(R.id.header_account);
		listViewCategories = (ListView) findViewById(R.id.listViewCategories);
		footer = (LinearLayout) findViewById(R.id.footer);
		
		radioButtonExpense.setChecked(true);
		
		cursorIncome = accountData.getCategories("I");
		startManagingCursor(cursorIncome);
		cursorExpense = accountData.getCategories("E");
		startManagingCursor(cursorExpense);
		int[] to = {R.id.category_name};
		String[] from = {AccountData.TRANSACTION_CATEGORY};
		cursorAdapterIncome = new SimpleCursorAdapter(this, R.layout.category_listview_row, cursorIncome, from, to);
		cursorAdapterExpense = new SimpleCursorAdapter(this, R.layout.category_listview_row, cursorExpense, from, to);
		listViewCategories.setAdapter(cursorAdapterExpense);
		
	}//End onCreate(...)
	
	@Override
	public void onStart(){
		super.onStart();
		
		
	}//End onStart()
	
	@Override
	public void onResume(){
		super.onResume();
		
		cursorIncome.requery();
		cursorExpense.requery();
		
		radioGroupIncomeExpense.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				switch(checkedId){
					case R.id.radioButtonExpense:
						listViewCategories.setAdapter(cursorAdapterExpense);
						break;
					case R.id.radioButtonIncome:
						listViewCategories.setAdapter(cursorAdapterIncome);
						break;
				} //End switch(checkedId)
				
			}//End onCheckedChanged(...)
			
		});//End radioGroupIncomeExpense.setOnCheckedChangeListener(...)
		
		footer.setClickable(true);
		footer.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(addCategory);
			}
			
		});
	
	
	}//End onResume()
	
	@Override
	public void onPause(){
		super.onPause();
		
		cursorIncome.deactivate();
		cursorExpense.deactivate();
	}//End onPause()

	@Override
	public void onDestroy(){
		super.onDestroy();
		
		cursorIncome.close();
		cursorExpense.close();
		accountData.close();
	}//End onDestroy()
}
