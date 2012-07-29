package com.mugitek.eedenuncias;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {
	//private ActionBar mActionBar;
	private final int SPLASH_DISPLAY_LENGTH = 2000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	new Handler().postDelayed(new Runnable()
    	{
    		public void run() {
    			//Finish the splash activity so it can't be returned to.
    			MainActivity.this.finish();
    			// Create an Intent that will start the main activity.
    			Intent mainIntent = new Intent(MainActivity.this, DenunciaActivity.class);
    			MainActivity.this.startActivity(mainIntent);
    		}
    	}, SPLASH_DISPLAY_LENGTH);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		//ignore orientation change
		super.onConfigurationChanged(newConfig);
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
