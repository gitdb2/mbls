package com.datamyne.mobile.xml;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class ViewPagerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_pager);
		context = this;
        configureTvSearch();
        configureButton();

	}
	
	 /** Called when the activity is first created. */
    
	private PagerAdapter pageAdapter;
	private ViewPager pager;
	private TextView tvSearch;
	private Context context;
	
   

	private void configureTvSearch() {
		tvSearch = (TextView) findViewById(R.id.etQuery);
	}

	private void configureButton() {
		final Button btSearch = (Button) findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					pageAdapter = new CustomPageAdapter(context);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pager = (ViewPager) findViewById(R.id.viewPager);
				pager.setAdapter(pageAdapter);
				hideInputMethod();
			}
		});
	}
	
	private void hideInputMethod(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(tvSearch.getWindowToken(), 0);
	}
	
	
}
