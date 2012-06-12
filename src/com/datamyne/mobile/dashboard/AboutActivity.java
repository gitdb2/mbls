package com.datamyne.mobile.dashboard;

import com.datamyne.mobile.xml.R;

import android.os.Bundle;

/**
 * This is the About activity in the dashboard application. It displays some
 * text and provides a way to get back to the home activity.
 * 
 */

public class AboutActivity extends DashboardActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		setTitleFromActivityLabel(R.id.title_text);
	}

}
