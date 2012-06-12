package com.datamyne.mobile.dashboard;

import android.os.Bundle;

import com.datamyne.mobile.xml.R;

public class MyAccountActivity extends DashboardActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		setTitleFromActivityLabel(R.id.title_text);
	}
	
}
