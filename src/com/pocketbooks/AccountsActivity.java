package com.pocketbooks;



import java.math.BigDecimal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AccountsActivity extends Activity{
	private static final String TAG = "ListActivity: ";
	PocketBooksApplication pb;
	
	AccountData accounts;
	CursorAdapter cursorAdapter;	
	Cursor accountsSum;
	Cursor cursor;
	ListView list;
	LinearLayout mNewAccount;
	LinearLayout header;
	TextView headerId;
	TextView headerSum;
	Intent prefIntent;
	Intent categoriesEditIntent;
	SharedPreferences prefs;
	Boolean hasAccounts;
	BigDecimal sum;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		pb = (PocketBooksApplication) getApplication();
		hasAccounts = false;
		//Log.d(TAG, "Starting account");
		
		// Setup UI
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.accounts_activity_layout);
        
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.pocketbooks_header);
        
        header = (LinearLayout) findViewById(R.id.header);
        header.setBackgroundColor(Color.parseColor("#216C2A")); 
        headerId = (TextView) findViewById(R.id.header_account);
        headerId.setTextColor(Color.WHITE);
        headerId.setText("Pocket Books");
        headerSum = (TextView) findViewById(R.id.header_balance);
        sum = BigDecimal.ZERO;
        
        list = (ListView) findViewById(R.id.accountNameListView);
        
        final Intent newAccountIntent = new Intent(this, NewAccountActivity.class);
        final Intent transactionIntent = new Intent(this, TransactionsActivity.class);
        categoriesEditIntent = new Intent(this, CategoriesEditActivity.class);
        prefIntent = new Intent(this, Prefs.class);
        
//        View v = getLayoutInflater().inflate(R.layout.accounts_activity_header, null);
//        v.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Log.d(TAG, "Clickity Clack: in onClick method in AccountLists starting NewAccountActivity.");
//				startActivity(newAccountIntent);
//				
//			}	
//        });
       
        mNewAccount = (LinearLayout) findViewById(R.id.footer);  
        mNewAccount.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(newAccountIntent);
			}
        	
        }); 
        
        PreferenceManager.setDefaultValues(this, R.xml.settings, true);
       
        //Query current accountNames
        accounts = new AccountData(this);
        //Log.d(TAG, "Starting getTables.");
        cursor = accounts.getAccounts();
        if(cursor.getCount() > 0){
        	hasAccounts = true;
        }
        if(hasAccounts){
        	accountsSum = accounts.getAccountsSum();
        	startManagingCursor(accountsSum);
        }
        startManagingCursor(cursor);
        
        // Construct adapter
        int[] to = {R.id.account_name, R.id.account_balance};
        String[] from = {AccountData.ACCOUNT_NAME, AccountData.ACCOUNT_BALANCE};
        AccountAdapter adapter = new AccountAdapter(this, R.layout.accounts_activity_listview_row, cursor, from, to);
        
        // Link ListView to adapter
        //list.addHeaderView(v);
        list.setAdapter(adapter);
        registerForContextMenu(list);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "id is " + id + " position is " + position + " action is " + getIntent().getAction());
				//Log.d(TAG, "Hopefully _id ");
				
				cursor.moveToPosition(position);
				
				//Log.d(TAG, "" + cursor.getString(cursor.getColumnIndex(AccountData.ACCOUNT_NAME) - 1));
				
				startActivity(transactionIntent.putExtra(AccountData.ACCOUNT_ID, id));
			}
        	
        });
	}
	
	@Override
	public void onResume(){
		super.onResume();
		prefs = pb.getPrefs();
	        
	        if(prefs.getBoolean("category", false)){
	        	
	        	Log.d(TAG, "prefs retained------------------------------------------------------------------");
	        }
		cursor.requery();
		
		if(hasAccounts){
			accountsSum.requery();
			accountsSum.moveToFirst();
		
			sum = new BigDecimal(accountsSum.getString(accountsSum.getColumnIndex(AccountData.ACCOUNT_BALANCE)));
			sum = sum.movePointLeft(2);
		}
		
		headerSum.setText(sum.toPlainString());
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		cursor.deactivate();
		if(hasAccounts){
			accountsSum.deactivate();
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		cursor.close();
		if(hasAccounts){
			accountsSum.close();
		}
		accounts.close();
		
	}
	
	// Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo ){
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	MenuInflater inflater =  getMenuInflater();
    	inflater.inflate(R.menu.accounts_menu, menu);
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	switch(item.getItemId()){
    		case R.id.account_delete:
    			//Log.d(TAG, "Deleting account with id " + info.id);
    			accounts.deleteAccount(info.id);
    			cursor.deactivate();
    			cursor.requery();
    			return true;
    	}
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);

        
        
//        if(prefs.getBoolean("category", false)){
//        	inflater.inflate(R.menu.settings_category_menu, menu);
//        }
//        
        return true;
    }
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
    	if(prefs.getBoolean("category", false)){
    		
    		menu.findItem(R.id.categories).setEnabled(true);
    		menu.findItem(R.id.categories).setVisible(true);
    	} else {
    		menu.findItem(R.id.categories).setEnabled(false);
    		menu.findItem(R.id.categories).setVisible(false);
    	}
    	
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    		case R.id.preferences:
    			startActivity(prefIntent);
    			return true;
    		case R.id.categories:
    			startActivity(categoriesEditIntent);
    			return true;    			
    	}
    	
    	return false;
    }
  
}