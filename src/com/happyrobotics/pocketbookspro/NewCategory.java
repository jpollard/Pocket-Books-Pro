package com.happyrobotics.pocketbookspro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NewCategory extends Activity{
	private static String TAG ="PocketBooks";
	
	
	private AccountData data;
	private Button buttonDone;
	private EditText editTextCategoryName;
	private RadioGroup radioGroupExpenseOrIncome;
	private RadioButton radioButtonExpense;
	private RadioButton radioButtonIncome;
	private char categoryType;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		setContentView(R.layout.new_category_activity_layout);
		
		
		data = new AccountData(this);
		editTextCategoryName = (EditText) findViewById(R.id.editTextCategoryName);
		radioGroupExpenseOrIncome = (RadioGroup) findViewById(R.id.radioGroupExpenseOrIncome);
		radioButtonExpense = (RadioButton) findViewById(R.id.radioButtonExpense);
		radioButtonIncome = (RadioButton) findViewById(R.id.radioButtonIncome);
		buttonDone = (Button) findViewById(R.id.buttonDone);
		if(buttonDone.isEnabled()){
			buttonDone.setEnabled(false);
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		radioGroupExpenseOrIncome.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int radioButton) {
				
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
				String categoryName = editTextCategoryName.getText().toString();
				data.addCategory(categoryName, categoryType);
				
				finish();
				
			}
			
		});
		
	}

}
