package com.datamyne.mobile.xml;

import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomPageAdapter extends PagerAdapter{
	

	private final Context context;
	
	
	public CustomPageAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((LinearLayout) view);
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCount() {
		return 5;
	}

	
	
	@Override
	public Object instantiateItem(View collection, int position) {		
		
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(1);
		
		
		
		chart = new TChart(context);
		linearLayout.addView(chart);
		
		chart.getPanel().setBorderRound(7);
		chart.getAspect().setView3D(false);

		ThemesList.applyTheme(chart.getChart(), 1);

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
		
		
//		final TextView textView = new TextView(context);
//		textView.setText("Position: " + position);
//		
//		
//		
//		linearLayout.addView(textView);
		
		
		((ViewPager) collection).addView(linearLayout,0);
		return linearLayout;
	}

	
	private TChart chart;
	private Series series;
	
	
	
	
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		 return view==((LinearLayout)object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
