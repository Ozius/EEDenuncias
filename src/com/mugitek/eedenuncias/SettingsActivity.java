package com.mugitek.eedenuncias;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockPreferenceActivity {
	private ActionBar mActionBar;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        
        mActionBar = this.getSupportActionBar();
        mActionBar.setIcon(R.drawable.ic_actionbar);
        
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mActionBar.setTitle("Configuración");
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//this.setResult(RESULT_CANCELED);
			finish();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
