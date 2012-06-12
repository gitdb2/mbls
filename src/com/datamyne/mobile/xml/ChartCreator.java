package com.datamyne.mobile.xml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.IInterface;
import android.util.Log;
import android.view.View;

import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.styles.Bar;
import com.steema.teechart.styles.Line;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

public class ChartCreator {
	private static final String TAG = "ChartCreator";
	public static class ChartCreatorException extends Exception{
		private static final long serialVersionUID = 7145759565927589212L;
		public ChartCreatorException() {
			super();
		}
		public ChartCreatorException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
			// TODO Auto-generated constructor stub
		}
		public ChartCreatorException(String detailMessage) {
			super(detailMessage);
			// TODO Auto-generated constructor stub
		}
		public ChartCreatorException(Throwable throwable) {
			super(throwable);
			// TODO Auto-generated constructor stub
		}
	}
	private Context context;


	public ChartCreator(Context context){
		this.context = context;
	}
	
	
	  String[] colorArr = {"#AFD8F8"
	  ,"#F6BD0F"
	  ,"#8BBA00"
	  ,"#FF8E46"
	  ,"#008E8E"
	  ,"#CA4C4C"
	  ,"#8E468E"
	  ,"#527C23"
	  ,"#B0A700"
	  ,"#008ED6"};
	//	Color.parseColor(4)
	public View crearGraficaMonthly(JSONObject data) throws ChartCreatorException{

		/*
	
		{"monthlyValueList":
		{"fullMonthData":[
			{	"year":"2011",
				"month":"4",
				"value":"6174",
				"name":"APRIL",
				"code":"3"},
		
		fullMonthData
		*/
		JSONObject tmp =  data.optJSONObject("monthlyValueList");
		if(tmp== null|| !tmp.has("fullMonthData")){//si no tiene datos o sea no tiene movimientos
			throw new ChartCreatorException("No Mostrar la grafica porqu no hay datos");
		}
				
		JSONArray arr = tmp.optJSONArray("fullMonthData");
		
		TChart chart = new TChart(context);
		chart.getPanel().setBorderRound(7);
		chart.getAspect().setView3D(false);
		chart.setBackgroundColor(Color.parseColor("#cccccc"));

		//tema 1
	//	ThemesList.applyTheme(chart.getChart(), 1);

		chart.removeAllSeries();

		Series line = new Line(chart.getChart());
		line.setColor(new Color( Color.parseColor("#362B36")));
		chart.getAxes().getBottom().setIncrement(1);
		line.setTitle("Total Annual imports in teus");
		//line.setShowInLegend(false);
		try {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject entry =  arr.getJSONObject(i);
				String axisX = convertDates("MMM yy", "yyyyMM", entry.getString("year")+entry.getString("month"));// obtenerYYYYMM(entry));
				double value = entry.getDouble("value");
				line.add(value, axisX);
			}
		} catch (JSONException e) {
			throw new ChartCreatorException("No Mostrar generar la grafica, error json", e);
		}
		
		return chart;
	}
	
//	private final NumberFormat numberFormatter = new DecimalFormat("00");
//	
//	private String obtenerYYYYMM(JSONObject entry) throws JSONException {
//		
//		
//		int num = entry.getInt("month");
//		return entry.getString("year")+numberFormatter.format(num);
//	}

	private String convertDates(String outPattern, String inPattern, String inDate){
		String out = inDate;
		SimpleDateFormat inSDF = new SimpleDateFormat(inPattern, Locale.US);
		SimpleDateFormat outSDF = new SimpleDateFormat(outPattern, Locale.US);
		
		try {
			out = outSDF.format(inSDF.parse(inDate));
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, "No se puede formatear la fecha "+inDate+ " con el inPattern "+inPattern+ "y outPattern "+outPattern +", se deja como viene");
		}
		
		return out;
	}
	
	public View crearGraficaMulti(JSONObject data){

//		"tabDimension":[
//		            	{	
//		            		"dimensionName":"partner",
//		            		"dimensionItemList":
//		            			{	"dimensionData":
//		            						[
//		            							{
//		            							"name":"UNION DE BANANEROS ECUATORIANOS S A (EC)","code":"1583080","total":"24854",
//		            							"monthlyValueList":
//		            									{"simpleMonthData":
//		            												[
//		            													{"year":"2011","month":"4","value":"2407"},
//		            													{"year":"2011","month":"5","value":"2937.67"},
//		            													{"year":"2011","month":"6","value":"2006"},
//		            													{"year":"2011","month":"7","value":"2487"},
//		            													{"year":"2011","month":"8","value":"1594"},
//		            													{"year":"2011","month":"9","value":"1207.67"},
//		            													{"year":"2011","month":"10","value":"1849"},
//		            													{"year":"2011","month":"11","value":"2359"},
//		            													{"year":"2011","month":"12","value":"1433"},
//		            													{"year":"2012","month":"1","value":"2115"},
//		            													{"year":"2012","month":"2","value":"1998"},
//		            													{"year":"2012","month":"3","value":"2460.67"}
//		            												]
//		            									}
//		            								},
		
		
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
