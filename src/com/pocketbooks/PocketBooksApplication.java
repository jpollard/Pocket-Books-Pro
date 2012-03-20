package com.pocketbooks;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class PocketBooksApplication extends Application implements OnPreferenceChangeListener{

	SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
	}
	
	public SharedPreferences getPrefs(){
		return prefs;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		preference.
		return false;
	}
}
