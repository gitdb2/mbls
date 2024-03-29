package com.datamyne.mobile.profile.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.datamyne.mobile.xml.R;
import com.steema.teechart.TChart;
import com.steema.teechart.drawing.Color;
import com.steema.teechart.legend.LegendAlignment;
import com.steema.teechart.styles.Area;
import com.steema.teechart.styles.Line;
import com.steema.teechart.styles.Series;
import com.steema.teechart.themes.ThemesList;

/*
 * Clase para la creacion de graficas y tablas de datos.
 */

public class ChartCreator implements IChartsCreator, ITabTableCreator {
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
	public static class TabTableCreatorException extends ChartCreatorException{
		private static final long serialVersionUID = 4071333937169191949L;

		public TabTableCreatorException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public TabTableCreatorException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable);
			// TODO Auto-generated constructor stub
		}

		public TabTableCreatorException(String detailMessage) {
			super(detailMessage);
			// TODO Auto-generated constructor stub
		}

		public TabTableCreatorException(Throwable throwable) {
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

	/* (non-Javadoc)
	 * @see com.datamyne.mobile.xml.IChartsCreator#crearGraficaMonthly(org.json.JSONObject)
	 */
	public View crearGraficaMonthly(JSONObject data) throws ChartCreatorException{

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
		try {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject entry =  arr.getJSONObject(i);
				String axisX = convertDates("MMM yy", "yyyyMM", entry.getString("year")+entry.getString("month"));
				double value = entry.getDouble("value");
				line.add(value, axisX);
			}
		} catch (JSONException e) {
			throw new ChartCreatorException("No Mostrar generar la grafica, error json", e);
		}

		return chart;
	}

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

	/* (non-Javadoc)
	 * @see com.datamyne.mobile.xml.IChartsCreator#crearGraficaMulti(org.json.JSONObject)
	 */
	public View crearGraficaMulti(JSONObject data)throws ChartCreatorException{

		TChart chart = new TChart(context);
		chart.removeAllSeries();
		chart.getZoom().setAllow(false);
		chart.getPanel().setBorderRound(7);
		chart.getAspect().setView3D(false);

		ThemesList.applyTheme(chart.getChart(), 1);
		try {

			String dimensionName = data.getString("dimensionName");
			boolean showCode = isShowCode(dimensionName);
			
			chart.getLegend().setAlignment(LegendAlignment.BOTTOM);
			chart.getHeader().setText(convertName(dimensionName));
			chart.getHeader().getFont().setSize(14);
			
			
			JSONObject tmp = data.getJSONObject("dimensionItemList");
			if(tmp== null|| !tmp.has("dimensionData")){//si no tiene datos o sea no tiene movimientos
				throw new ChartCreatorException("No Mostrar la grafica porqu no hay datos");
			}

			
			JSONArray arr = tmp.optJSONArray("dimensionData");
			if(arr ==  null){//esto quiere decir que solo viene un resultado entonces hay que tratarlo como objeto
				generateSeries(chart, 0, tmp.getJSONObject("dimensionData"), showCode);
			}else{
				for (int i = 0; i < Math.min(arr.length(), colorArr.length) ; i++) {
					JSONObject seriesEntry 	=  arr.getJSONObject(i);
					generateSeries(chart, i, seriesEntry, showCode);
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
	private void generateSeries(TChart chart, int index, JSONObject seriesEntry , boolean showCode) throws JSONException{
		String seriesName 		= seriesEntry.getString("name");
		
		if(showCode){
			String code 	= seriesEntry.getString("code");
			seriesName = code + " - " + seriesName;
		}
		
		//en este no hay problema de que venga solo un elemento porque por lo menos siempre hay 12 meses, sino esta mal la generacion
		JSONArray seriesData 	= seriesEntry.getJSONObject("monthlyValueList").getJSONArray("simpleMonthData");
		
		Area series = new Area(chart.getChart());
		series.setTitle(seriesName);
		series.setColor(colorArr[index]);
		series.setTransparency(30);
		series.setShowInLegend(false);
		
		for (int j = 0; j < seriesData.length(); j++) {
			JSONObject valueData 	= seriesData.getJSONObject(j);
			String axisX = convertDates("MMM yy", "yyyyMM", valueData.getString("year")+valueData.getString("month"));
			series.add(valueData.getDouble("value"), axisX);
		}
		chart.addSeries(series);
	}
	

	private String convertName(String dimensionName) {
		String found = this.namesDimension.get(dimensionName);
		
		if(found == null){
			found = dimensionName;
		}
		
		return "Top "+ found + " in Teus";
	}

	//////////////////////////////////////////////////////////////////////////////////
	final DecimalFormat formatter = new DecimalFormat("#,##0.0");
	private String convertNameTab(String dimensionName) {
		String found = this.namesDimension.get(dimensionName);
		
		if(found == null){
			found = dimensionName;
		}
		
		return "Top "+ found;
	}
	
	/* (non-Javadoc)
	 * @see com.datamyne.mobile.xml.ITabTableCreator#crearTablaTabProfile(org.json.JSONObject)
	 */
	public View crearTablaTabProfile (JSONObject data) throws TabTableCreatorException {
			
		WebView mWebView1 = new WebView(context);
		mWebView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
	    mWebView1.getSettings().setJavaScriptEnabled(true);
	    
	    mWebView1.getSettings().setPluginState(PluginState.ON);
      
	    StringBuilder sb = new StringBuilder();

	    sb.append("<html>");
	    sb.append("<head>");
	    sb.append("<script type='text/javascript' src=\"file:///android_asset/charts/jquery.js\"></script>");
	    sb.append(generateHTML(data));
	    sb.append("</head>");
	    sb.append("<body style='background-color: #F7F8E5;''>");
	    
	    
	 	sb.append("</body></html>");
    
	    mWebView1.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "UTF-8", null);
		
		return mWebView1;
		
	}

	/* (non-Javadoc)
	 * @see com.datamyne.mobile.xml.ITabTableCreator#crearTablaTabMonthly(org.json.JSONObject)
	 */
	public View crearTablaTabMonthly (JSONObject data) throws TabTableCreatorException{
		TableLayout table = new TableLayout(context);

		// Row de titulos
		TableRow row = new TableRow(context);
		row.addView(createLabelTitles("Month", Gravity.CENTER_HORIZONTAL));
		row.addView(createLabelTitles("Teus", Gravity.LEFT));

		// add the TableRow to the TableLayout
		table.addView(row, new TableLayout.LayoutParams());

		JSONObject tmp =  data.optJSONObject("monthlyValueList");
		if(tmp== null|| !tmp.has("fullMonthData")){//si no tiene datos o sea no tiene movimientos
			throw new TabTableCreatorException("No Mostrar la Tabla porqu no hay datos");
		}

		JSONArray arr = tmp.optJSONArray("fullMonthData");

		BigDecimal suma = BigDecimal.ZERO;
		try {
			for (int i = 0; i < arr.length(); i++) {
				row = new TableRow(context);
				JSONObject entry =  arr.getJSONObject(i);
				String text = convertDates("MMMM yyyy", "yyyyMM", entry.getString("year")+entry.getString("month"));// obtenerYYYYMM(entry));
				double value = entry.getDouble("value");
				
				suma = suma.add(BigDecimal.valueOf(value));
				
				row.addView(createLabel(text, Gravity.LEFT, R.style.WhiteNormalText));
				row.addView(createLabelTitles(formatter.format(value), Gravity.RIGHT));
				table.addView(row, new TableLayout.LayoutParams());
			}

			row = new TableRow(context);
			row.addView(createLabelTitles("Total", Gravity.LEFT));
			row.addView(createLabelTitles(formatter.format(suma.doubleValue()), Gravity.RIGHT));
			table.addView(row, new TableLayout.LayoutParams());
			
		} catch (JSONException e) {
			throw new TabTableCreatorException("No Mostrar generar la Tabla, error json", e);
		}
		
		return table;
	}

	private TextView createLabelTitles(String text, int gravity) {
		TextView t = createLabelTitles(text, gravity, R.style.WhiteBoldText);
		return t;
	}
	
	private TextView createLabelTitles(String text, int gravity, int style) {
		TextView t = createLabel(text, gravity);
		t.setTextAppearance(context, style);
		return t;
	}
	
	private TextView createLabel(String text, int gravity, int style) {
		TextView t = createLabel(text, gravity);
		t.setTextAppearance(context, style);
		return t;
	}
	
	private TextView createLabel(String text, int gravity) {
		TextView t = new TextView(context);
		t.setText(text);
		t.setGravity(gravity);
		int padding = getPadding();
		t.setPadding(padding, 0, padding, 0);
		return t;
	}

	private int getPadding() {
		int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				4, context.getResources().getDisplayMetrics());
		return padding;
	}
	
	/* (non-Javadoc)
	 * @see com.datamyne.mobile.xml.ITabTableCreator#crearTablaTabOther(org.json.JSONObject)
	 */
	public View crearTablaTabOther (JSONObject data) throws TabTableCreatorException{
	
		TableLayout table = new TableLayout(context);

		try {
			
			String dimensionName = data.getString("dimensionName");
			boolean showCode = isShowCode(dimensionName);
			// Row de titulos
			TableRow row = new TableRow(context);
			row.setMinimumHeight(30);
			row.addView(createLabelTitles(convertNameTab(dimensionName), Gravity.CENTER_HORIZONTAL, R.style.TwoColsWhiteBoldText));
			row.addView(createLabelTitles("Teus", Gravity.LEFT));
			table.addView(row, new TableLayout.LayoutParams());
			
			JSONObject tmp = data.getJSONObject("dimensionItemList");
			if(tmp== null|| !tmp.has("dimensionData")){//si no tiene datos o sea no tiene movimientos
				throw new TabTableCreatorException("No Mostrar la Tabla porqu no hay datos");
			}
			
			JSONArray arr = tmp.optJSONArray("dimensionData");
			if(arr ==  null){//esto quiere decir que solo viene un resultado entonces hay que tratarlo como objeto
				generateRow(table, 0, tmp.getJSONObject("dimensionData"), showCode);
			}else{
				for (int i = 0; i < Math.min(arr.length(), colorArr.length) ; i++) {
					JSONObject seriesEntry 	=  arr.getJSONObject(i);
					generateRow(table, i, seriesEntry, showCode);
				}
			}
			
		} catch (JSONException e) {
			throw new TabTableCreatorException("No Mostrar generar la Tabla, error json", e);
		}
		
		return table;
	}

	private boolean isShowCode(String dimensionName) {
		// TODO Auto-generated method stub
		return "product".equalsIgnoreCase(dimensionName);
	}

	private void generateRow(TableLayout table, int index, JSONObject dimensionData, boolean showCode) throws JSONException {
		String name 	= dimensionData.getString("name");
		if(showCode){
			String code 	= dimensionData.getString("code");
			name = code + " - " + name;
		}
		
		double value 	= dimensionData.getDouble("total");
		int color		= colorArr[index].getRGB();
		
		TableRow row = new TableRow(context);
		row.setMinimumHeight(30);
		row.addView(createSquare(color));
		
		if(!isPanatallGrande()){
			int origLength = name.length();
			final int MAX = 20;
			if(origLength > MAX){
				name = name.substring(0, MAX)+"...";	
			}
			
		}
		row.addView(createLabel(name, Gravity.LEFT, R.style.WhiteNormalText));
		row.addView(createLabelTitles(formatter.format(value), Gravity.RIGHT));
		table.addView(row, new TableLayout.LayoutParams());
		
	}

	private View createSquare(int color) {
		DrawView drawView = new DrawView(context,color);
		drawView.setBackgroundColor(android.graphics.Color.BLACK);
		int padding = getPadding();
		drawView.setPadding(padding, padding, padding, padding);
		return drawView;
		
	}
	
	private class ColumnPair{
		String label;
		String value;

		public ColumnPair(String label, String value) {
			super();
			this.label = label;
			this.value = value;
		}
		@Override
		public String toString() {
			return "<td class='label'>" + label + ":</td><td class='value'><b>" + value + "</b></td>";
		}
	}
	
	private StringBuilder generateHTML(JSONObject data) {

		List<List<ColumnPair>> tableData = parseData(data);
		StringBuilder tmp = new StringBuilder();
		StringBuilder ret = new StringBuilder();
		for (List<ColumnPair> row : tableData) {
			tmp.append("<tr>");
			for (ColumnPair column : row) {
				tmp.append(column);
			}
			tmp.append("</tr>");
		}

		if(tmp.length() > 0){
			ret.append("<table style='background-color: #F7F8E5; width:100%'>").append(tmp).append("</table>");
		}else{
			ret.append("<p>No D&B data available for the profile</p>");
		}
		return ret;
	}

	static final List<String> fields = new ArrayList<String>();
	static {
		 fields.add("businessName");
	     fields.add("dunsNumber");
	     fields.add("tradestyleName");
	     fields.add("streetAddress");
	     fields.add("streetAddress2");
	     fields.add("cityName");
	     fields.add("stateProvinceName");
	     fields.add("countryName");
	     fields.add("postalCode");
	     fields.add("chiefExecutiveOfficerName");
	     fields.add("chiefExecutiveOfficerTitle");
	     fields.add("lineOfBusiness");
	     fields.add("us1987Sic1");
	     fields.add("yearStarted");
	     fields.add("salesVolumeUsDollars");
	     fields.add("employeesTotal");
	     fields.add("legalStatusName");
	     fields.add("statusName");
	     fields.add("telephoneNumber");
	     fields.add("facsimileNumber");
	     fields.add("domesticUltimateBusinessName");
	     fields.add("urlDomainName1");
	     fields.add("nyseTicker");
	}
	static final Map<String, String> fieldNames = new HashMap<String, String>();
	static{
		fieldNames.put("businessName", "Company Name");
		fieldNames.put("dunsNumber", "DUNS Number");
		fieldNames.put("tradestyleName", "Style Name");
		fieldNames.put("streetAddress", "Address");
		fieldNames.put("streetAddress2", "Address2");
		fieldNames.put("cityName", "City");
	    fieldNames.put("stateProvinceName", "State");
	    fieldNames.put("countryName", "Country");
	    fieldNames.put("postalCode", "Zip Code");
	    fieldNames.put("chiefExecutiveOfficerName", "Chief Officer");
	    fieldNames.put("chiefExecutiveOfficerTitle", "Officer Title");
	    fieldNames.put("lineOfBusiness", "Line of Business");
	    fieldNames.put("us1987Sic1", "SIC");
	    fieldNames.put("yearStarted", "In business since");
	    fieldNames.put("salesVolumeUsDollars", "Sales Volume (US$)");
	    fieldNames.put("employeesTotal", "Employees");
	    fieldNames.put("legalStatusName", "Legal Status");
	    fieldNames.put("statusName", "Status");
	    fieldNames.put("telephoneNumber", "Phone");
	    fieldNames.put("facsimileNumber", "Fax");
	    fieldNames.put("domesticUltimateBusinessName", "Parent Company");
	    fieldNames.put("urlDomainName1", "Website");
	    fieldNames.put("nyseTicker", "Stock Ticker (NYSE)");
	}
	
	private List<List<ColumnPair>> parseData(JSONObject data) {
		int columnsInRow = 1;
		 if (isLandscape()
				&& isPanatallGrande()) {
			 columnsInRow = 2;
		 }
		
		List<List<ColumnPair>> ret = new ArrayList<List<ColumnPair>>();
		 
		for (String field : fields) {
			ArrayList<ColumnPair> tmp = new ArrayList<ColumnPair>();
			String tmpStr = data.optString(field);
			if(tmpStr == null){
				tmpStr = "";
			}
			tmp.add(new ColumnPair(fieldNames.get(field), tmpStr));
			ret.add(tmp);
		}

		return ret;
	}
	
	private boolean isLandscape(){
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ;
	}
	private boolean isPanatallGrande(){
		return context.getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
	}
	
}

