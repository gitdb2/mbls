package com.datamyne.mobile.xml;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.steema.teechart.DateTime;
import com.steema.teechart.DateTimeStep;
import com.steema.teechart.TChart;
import com.steema.teechart.axis.Axis;
import com.steema.teechart.axis.AxisLabelsItems;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.legend.LegendAlignment;
import com.steema.teechart.styles.Area;
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


	private final Map<String, String> namesDimension = new HashMap<String, String>();
	
	public ChartCreator(Context context){
		this.context = context;
		namesDimension.put("partner", context.getString(R.string.consignee_partner));
		namesDimension.put("country", context.getString(R.string.consignee_country));
		namesDimension.put("product", context.getString(R.string.consignee_product));
		namesDimension.put("port", context.getString(R.string.consignee_port));
	}


	final Color[] colorArr ={
			new Color( Color.parseColor("#AFD8F8")),
			new Color( Color.parseColor("#F6BD0F")),
			new Color( Color.parseColor("#8BBA00")),
			new Color( Color.parseColor("#FF8E46")),
			new Color( Color.parseColor("#008E8E")),
			new Color( Color.parseColor("#CA4C4C")),
			new Color( Color.parseColor("#8E468E")),
			new Color( Color.parseColor("#527C23")),
			new Color( Color.parseColor("#B0A700")),
			new Color( Color.parseColor("#008ED6")),
	};
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
		chart.getZoom().setAllow(false);
		//tema 1
		//	ThemesList.applyTheme(chart.getChart(), 1);

		chart.removeAllSeries();

		Series line = new Line(chart.getChart());
		line.setColor(new Color( Color.parseColor("#362B36")));
		chart.getAxes().getBottom().setIncrement(1);
		line.setTitle("Total Annual imports in teus");
		((Line)chart.getSeries(0)).getLinePen().setWidth(3);
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

	public View crearGraficaMulti(JSONObject data)throws ChartCreatorException{

		//		"tabDimension":[ //no viene
		//		            	{	viene este objeto
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


		TChart chart = new TChart(context);
		chart.removeAllSeries();
		chart.getZoom().setAllow(false);
		chart.getPanel().setBorderRound(7);
		chart.getAspect().setView3D(false);

		ThemesList.applyTheme(chart.getChart(), 1);
		try {

			String dimensionName = data.getString("dimensionName");
			chart.getLegend().setAlignment(LegendAlignment.BOTTOM);
			chart.getHeader().setText(convertName(dimensionName));
			chart.getHeader().getFont().setSize(14);
			
			
			JSONObject tmp = data.getJSONObject("dimensionItemList");
			if(tmp== null|| !tmp.has("dimensionData")){//si no tiene datos o sea no tiene movimientos
				throw new ChartCreatorException("No Mostrar la grafica porqu no hay datos");
			}

			
			JSONArray arr = tmp.optJSONArray("dimensionData");
			if(arr ==  null){//esto quiere decir que solo viene un resultado entonces hay que tratarlo como objeto
				generateSeries(chart, 0, tmp.getJSONObject("dimensionData"));
			}else{
				for (int i = 0; i < Math.min(arr.length(), colorArr.length) ; i++) {
					JSONObject seriesEntry 	=  arr.getJSONObject(i);
					generateSeries(chart, i, seriesEntry);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ChartCreatorException("No Mostrar generar la grafica, error json", e);
		}
		return chart;
	}
	
	/**
	 * Crea una series y la agrega a la chart
	 * @param chart
	 * @param index
	 * @param seriesEntry
	 * @return
	 * @throws JSONException
	 */
	private void generateSeries(TChart chart, int index, JSONObject seriesEntry ) throws JSONException{
		String seriesName 		= seriesEntry.getString("name");
		//en este no hay problema de que venga solo un elemento porque por lo menos siempre hay 12 meses, sino esta mal la generacion
		JSONArray seriesData 	= seriesEntry.getJSONObject("monthlyValueList").getJSONArray("simpleMonthData");
		
		Area series = new Area(chart.getChart());
		series.setTitle(seriesName);
		series.setColor(colorArr[index]);
		series.setTransparency(50);
		
		for (int j = 0; j < seriesData.length(); j++) {
			JSONObject valueData 	= seriesData.getJSONObject(j);
			String axisX = convertDates("MMM yy", "yyyyMM", valueData.getString("year")+valueData.getString("month"));
			series.add(valueData.getDouble("value"), axisX);
		}
		chart.addSeries(series);
	}
	

	private String convertName(String dimensionName) {
		String found = this.namesDimension.get(dimensionName);
		
		if(found != null){
			found = dimensionName;
		}
		
		return "Top "+ found + " in Teus";
	}

}
