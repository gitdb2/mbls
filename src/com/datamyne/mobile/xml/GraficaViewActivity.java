package com.datamyne.mobile.xml;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

public class GraficaViewActivity extends Activity {

	
	private TChart chart;
	private Series series;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chartview);
        
        
        LinearLayout group = (LinearLayout) findViewById(R.id.linearLayoutTchart);
		chart = new TChart(this);
		group.addView(chart);
		
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

        
    }
	
	
	
}
