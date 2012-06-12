package com.datamyne.mobile.xml;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

public class ChartCreator {
	
	private Context context;


	public ChartCreator(Context context){
		this.context = context;
	}
	
	public View crearGraficaMonthly(JSONObject data){

		//	LinearLayout group = (LinearLayout) layout.findViewById(R.id.linearLayoutTchart);
			TChart chart = new TChart(context);
		//	group.addView(chart);

			chart.getPanel().setBorderRound(7);
			chart.getAspect().setView3D(false);

			//tema 1
			ThemesList.applyTheme(chart.getChart(), 1);

			//piechart
			chart.removeAllSeries();
			try {

				Series bar = new Bar(chart.getChart());
				chart.getAxes().getBottom().setIncrement(1);
				bar.add(123, "Apples", Color.BLUE);
				bar.add(456, "Oranges", Color.CYAN);
				bar.add(321, "Kiwis", Color.GOLD);
				bar.add(78, "Bananas", Color.LIME);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return chart;
		}
	
	public View crearGraficaMulti(JSONObject data){

		//	LinearLayout group = (LinearLayout) layout.findViewById(R.id.linearLayoutTchart);
			TChart chart = new TChart(context);
		//	group.addView(chart);

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
			return chart;
		}
}
