package com.datamyne.mobile.dashboard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.datamyne.mobile.profile.utils.HoneycombCompatibility;
import com.datamyne.mobile.xml.R;

/*
 * Activity dummy para representar un feature a agregar 
 * en el futuro.
 */
public class FeatureActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feature);
		
		ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        HoneycombCompatibility.actionBarSetLogo(actionBar, R.drawable.title_home_default);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 Intent intent = new Intent(this, HomeActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
