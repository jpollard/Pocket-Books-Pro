package com.happyrobotics.pocketbookspro;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CategoriesEditActivity extends Activity {

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pb = (PocketBooksApplication) getApplication();


		setContentView(R.layout.categories_list_activity);

		accountData = new AccountData(this);
		radioGroupIncomeExpense = (RadioGroup) findViewById(R.id.radioGroupExpenseOrIncome);
		radioButtonIncome = (RadioButton) findViewById(R.id.radioButtonIncome);
		radioButtonExpense = (RadioButton) findViewById(R.id.radioButtonExpense);
		textViewHeaderTitle = (TextView) findViewById(R.id.header_account);
		listViewCategories = (ListView) findViewById(R.id.listViewCategories);
		footer = (LinearLayout) findViewById(R.id.footer);

		textViewHeaderTitle.setText(R.string.edit_categories);

		radioButtonExpense.setChecked(true);

		cursorIncome = accountData.getCategories("I");
		startManagingCursor(cursorIncome);
		cursorExpense = accountData.getCategories("E");
		startManagingCursor(cursorExpense);
		int[] to = {R.id.category_name};
		String[] from = {AccountData.TRANSACTION_CATEGORY};
		cursorAdapterIncome = new SimpleCursorAdapter(this,
				R.layout.category_listview_row, cursorIncome, from, to);
		cursorAdapterExpense = new SimpleCursorAdapter(this,
				R.layout.category_listview_row, cursorExpense, from, to);
		listViewCategories.setAdapter(cursorAdapterExpense);
		registerForContextMenu(listViewCategories);

	}// End onCreate(...)

	@Override
	public void onStart() {
		super.onStart();

	}// End onStart()

	@Override
	public void onResume() {
		super.onResume();
		
		addCategory = new Intent(this, NewCategory.class);


		cursorIncome.requery();
		cursorExpense.requery();
		
		radioGroupIncomeExpense.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub

						switch (checkedId) {
							case R.id.radioButtonExpense :
								listViewCategories
										.setAdapter(cursorAdapterExpense);
								break;
							case R.id.radioButtonIncome :
								listViewCategories
										.setAdapter(cursorAdapterIncome);
								break;
						} // End switch(checkedId)

					}// End onCheckedChanged(...)

				});// End
					// radioGroupIncomeExpense.setOnCheckedChangeListener(...)

		footer.setClickable(true);
		footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(addCategory);
			}

		});

	}// End onResume()

	@Override
	public void onPause() {
		super.onPause();

		cursorIncome.deactivate();
		cursorExpense.deactivate();
	}// End onPause()

	@Override
	public void onDestroy() {
		super.onDestroy();

		cursorIncome.close();
		cursorExpense.close();
		accountData.close();
	}// End onDestroy()

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long id = info.id;
		Log.d("POCKETBOOKS _______----------", "ID  is " + id );
		switch(item.getItemId()){
			case R.id.delete:
				//TODO delete code
				cursorExpense.deactivate();
				cursorIncome.deactivate();
				accountData.deleteCategory(id);
				cursorExpense.requery();
				cursorIncome.requery();
				
				return true;
			case R.id.edit:
				
				addCategory.putExtra("id", id);
				
				startActivity(addCategory);
				return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onContextMenuClosed(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_edit_delete, menu);
	}

	

}
