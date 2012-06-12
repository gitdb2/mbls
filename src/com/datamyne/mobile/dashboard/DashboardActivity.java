package com.datamyne.mobile.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datamyne.mobile.xml.PruebajsonxmlActivityPagerSearchable;

/**
 * This is the base class for activities in the dashboard application. It
 * implements methods that are useful to all top level activities. That
 * includes: (1) stub methods for all the activity lifecycle methods; (2)
 * onClick methods for clicks on home, search, feature 1, feature 2, etc. (3) a
 * method for displaying a message to the screen via the Toast class.
 * 
 */

public abstract class DashboardActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	/**
	 * Handle the click on the home button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */
	public void onClickHome(View v) {
		goHome(this);
	}

	/**
	 * Handle the click on the About button.
	 * 
	 * @param v
	 *            View
	 * @return void
	 */
	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}
	
	public void onClickMyAccount(View v) {
		startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
	}
	
	public void onClickTradeProfiles(View v) {
		startActivity(new Intent(getApplicationContext(), PruebajsonxmlActivityPagerSearchable.class));
	}
	
	public void onClickFeature(View v) {
		startActivity(new Intent(getApplicationContext(), FeatureActivity.class));
	}

	/**
	 * Go back to the home activity.
	 * 
	 * @param context
	 *            Context
	 * @return void
	 */
	public void goHome(Context context) {
		final Intent intent = new Intent(context, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 * 
	 * <p>
	 * This method is needed because we have a custom title bar rather than the
	 * default Android title bar. See the theme definitons in styles.xml.
	 * 
	 * @param textViewId
	 *            int
	 * @return void
	 */

	public void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText(getTitle());
	} 
	
	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} 
	
	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace(String msg) {
		Log.d("Demo", msg);
		toast(msg);
	}

} 
