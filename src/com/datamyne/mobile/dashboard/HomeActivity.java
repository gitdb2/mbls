/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datamyne.mobile.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.datamyne.mobile.xml.R;
import com.datamyne.mobile.xml.TradeProfilesActivity;
import com.datamyne.mobile.xml.TradeProfilesOfflineActivity;

public class HomeActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String WORK_MODE = "workMode";
	private boolean workOffline;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        workOffline = settings.getBoolean(WORK_MODE, false);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (workOffline) {
			menu.add(0, Menu.FIRST, 0, R.string.work_online);
		} else {
			menu.add(0, Menu.FIRST, 0, R.string.work_offline);
		}
		menu.add(0, Menu.FIRST + 1, Menu.NONE, R.string.exit_application);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (Menu.FIRST):
			if (workOffline) {
				workOffline = false;
			} else {
				workOffline = true;
			}
			showCurrentWorkMode();
			saveWorkMode();
			break;
		case (Menu.FIRST + 1):
			this.finish();
			return true;
		}
		return false;
	}
	
	private void showCurrentWorkMode() {
		String message;
		if (workOffline) {
			message = getResources().getString(R.string.work_offline_message);
		} else {
			message = getResources().getString(R.string.work_online_message);
		}
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void saveWorkMode() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      	SharedPreferences.Editor editor = settings.edit();
      	editor.putBoolean(WORK_MODE, workOffline);
      	editor.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}
	
	public void onClickMyAccount(View v) {
		startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
	}
	
	public void onClickTradeProfiles(View v) {
		if (workOffline) {
			startActivity(new Intent(getApplicationContext(), TradeProfilesOfflineActivity.class));
		} else {
			startActivity(new Intent(getApplicationContext(), TradeProfilesActivity.class));
		}
	}
	
	public void onClickFeature(View v) {
		startActivity(new Intent(getApplicationContext(), FeatureActivity.class));
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

} 
