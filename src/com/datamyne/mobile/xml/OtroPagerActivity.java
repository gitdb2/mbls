package com.datamyne.mobile.xml;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

public class OtroPagerActivity extends FragmentActivity{

	private static final int NUMBER_OF_PAGES = 10;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;

	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.view_pager2);  
		mViewPager = (ViewPager) findViewById(R.id.viewPager);  
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());  
		mViewPager.setAdapter(mMyFragmentPagerAdapter);  
	}  



	private static class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {  

		public MyFragmentPagerAdapter(FragmentManager fm) {  
			super(fm);  
		}  

		@Override  
		public Fragment getItem(int index) {  
			return PageFragment.newInstance("My Message " + index);
		}  

		@Override  
		public int getCount() {  
			return NUMBER_OF_PAGES;  
		}  
	}  	


	public static class PageFragment extends Fragment {  

		public static PageFragment newInstance(String title) {

			PageFragment pageFragment = new PageFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", title);
			pageFragment.setArguments(bundle);
			return pageFragment;
		}


		public String getTitle() {
			return getArguments().getString("title");
		}

		@Override  
		public void onCreate(Bundle savedInstanceState) {  
			super.onCreate(savedInstanceState);  
		}  

		@Override  
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  

			ScrollView scroller = new ScrollView(getActivity());
//			TextView text = new TextView(getActivity());
//			int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//					4, getActivity().getResources().getDisplayMetrics());
//			text.setPadding(padding, padding, padding, padding);
//			scroller.addView(text);
//
//			text.setText(getTitle());

		
			   TChart chart = new TChart(this.getActivity());
			   scroller.addView(chart);
				
				chart.getPanel().setBorderRound(7);
				chart.getAspect().setView3D(false);

		//tema 1
				ThemesList.applyTheme(chart.getChart(), 1);

		//piechart
				chart.removeAllSeries();
				
				try {
					
					Series bar = new Bar(chart.getChart());
					chart.getAxes().getBottom().setIncrement(1);
					bar.add(123, "Apples", Color.red);
					bar.add(456, "Oranges", Color.ORANGE);
					bar.add(321, "Kiwis", Color.green);
					bar.add(78, "Bananas", Color.yellow);
					
				
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			
			return scroller;  
		}  
	}  

}