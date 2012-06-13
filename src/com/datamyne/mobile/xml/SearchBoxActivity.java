package com.datamyne.mobile.xml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SearchBoxActivity extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_box);
	}
	
	
	public void buscar(View v){
		
		TextView detailsFrame = (TextView) findViewById(R.id.targetText);
		Intent intent = new Intent();
		intent.setClass(this, PruebajsonxmlActivity.class);
		intent.putExtra("target", detailsFrame.getText().toString());
		intent.putExtra("type", "consignee");
		startActivity(intent);
	}
	
	public void goPager(View v){
		Intent intent = new Intent();
		intent.setClass(this, ViewPagerActivity.class);
		startActivity(intent);
	}

	public void goPager2(View v){
		Intent intent = new Intent();
		intent.setClass(this, OtroPagerActivity.class);
		startActivity(intent);
	}
	
	public void buscar2(View v){
		TextView detailsFrame = (TextView) findViewById(R.id.targetText);
		Intent intent = new Intent();
		intent.setClass(this, TradeProfilesActivity.class);
		intent.putExtra("target", detailsFrame.getText().toString());
		intent.putExtra("type", "consignee");
		startActivity(intent);
	}
	
	
	
}
