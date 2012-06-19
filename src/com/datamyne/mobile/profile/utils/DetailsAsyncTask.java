package com.datamyne.mobile.profile.utils;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datamyne.mobile.profile.utils.ChartCreator.ChartCreatorException;
import com.datamyne.mobile.profile.utils.ChartCreator.TabTableCreatorException;
import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.ProfilesSQLiteHelper;
import com.datamyne.mobile.xml.R;

/**
 * Clase que gestiona las tareas a ejecutarse en background cuando el
 * usuario elige un elemento del resultado de una busqueda.
 */
public class DetailsAsyncTask extends AsyncTask<String, Integer, String> {

	private IProfileProvider profileProvider;// 	= new ProfileProvider();
	private ProgressDialog dialog;
	private ViewGroup view;
	boolean showDialog= true;
	int page = 0;
	Context context;
	IChartsCreator chartCreator;
	ITabTableCreator tabCreator; 
	private ProfilesSQLiteHelper dbHelper;
	int errorCode = 0;
	String name;
	
	public DetailsAsyncTask(Context context, ViewGroup container, boolean showDialog, int page) {
		super();
		
		SharedPreferences pref	= context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
		String baseServer		= pref.getString("baseServer", "");
		profileProvider 		= new ProfileProvider(baseServer);
		this.view = container;
		this.showDialog = showDialog;  
		this.page = page;
		this.context = context;
		this.chartCreator = new ChartCreator(context);
		this.tabCreator		= (ITabTableCreator) chartCreator;
		this.dbHelper = new ProfilesSQLiteHelper(context);
		
		if(showDialog){
			dialog = new ProgressDialog(context);
		    dialog.setTitle("Connecting to server");
	        dialog.setMessage("Please wait...");
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String tmp = "";
		name = params[3];
		try {
			tmp = profileProvider.loadFullProfile(params[0],params[1],params[2], params[3], dbHelper);
			dbHelper.close();
		}catch (java.net.ConnectException e) {
			Log.e("DetailsAsyncTask", "doInBackground ", e);
			publishProgress(-1);
			cancel(true);
			
		}catch (IOException e) {
			Log.e("DetailsAsyncTask", "doInBackground", e);
			publishProgress(-2);
			cancel(true);
		}
		return tmp;
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		errorCode = values[0]; 
		switch (values[0]) {
		case -1:
			
			dialog.setMessage("Server unreachable, try later or change To offline mode at home screen. Tap to close");
			dialog.setCancelable(true);
			
			break;
		case -2:
			dialog.setMessage("Comunication error, try later. Tap to close");
			dialog.setCancelable(true);
			
			break;
		
		default:
			break;
		}
		
	}
	protected void onPreExecute() {
		if(showDialog){
			dialog.setProgress(0);
			dialog.setMax(100);
			dialog.show(); // Mostramos el diálogo antes de comenzar
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(showDialog){
			dialog.dismiss();
		}
		writeData(result);

	}
	  @Override
      protected void onCancelled()
      {
         super.onCancelled();
      }
	
	
	private void writeData(String payload) {
		
		LinearLayout graficaLayout	= (LinearLayout) view.findViewById(R.id.linearLayoutTChart);
		LinearLayout tableLayout	= (LinearLayout) view.findViewById(R.id.pageDataTable); //layout de tabla para mostrar los datos
		TextView text 				= (TextView) view.findViewById(R.id.textViewData);
		
		String ret  = "";
		if(payload!=null && !payload.trim().isEmpty()){
			try {
				JSONObject obj = new JSONObject(payload);
				JSONObject tmp = obj.getJSONObject("tradeProfileContainer");
				
				switch (page) {
				case 0:
					try {
						tableLayout.addView(tabCreator.crearTablaTabProfile(tmp.getJSONObject("profileTab")));
						view.removeView(graficaLayout);
						ret = "Profile Information for\n"+name;
					}catch (TabTableCreatorException e) {
					
						Log.e("DetailsAsyncTask", "writeData page "+page, e);
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret += "\nNo data available";
					}
					
					
					break;
				case 1:
					try {
						tableLayout.addView(tabCreator.crearTablaTabMonthly(tmp.getJSONObject("totalMonthsTab")));
						graficaLayout.addView(chartCreator.crearGraficaMonthly(tmp.getJSONObject("totalMonthsTab")));
						ret = "Total Annual Imports for\n"+name;
					}catch (ChartCreatorException e) {
						Log.e("DetailsAsyncTask", "writeData page "+page, e);
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret += "\nNo data available";
					}
					break;
				case 2: ret = (ret.isEmpty())? "Top Suppliers for\n"+name : ret;
				case 3: ret = (ret.isEmpty())? "Top Countries for\n"+name : ret;
				case 4: ret = (ret.isEmpty())? "Top Products for\n"+name : ret;
				case 5: ret = (ret.isEmpty())? "Top Ports for\n"+name : ret;
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					try {
						tableLayout.addView(tabCreator.crearTablaTabOther(arr.getJSONObject(page-2)));
						graficaLayout.addView(chartCreator.crearGraficaMulti(arr.getJSONObject(page-2)));
					} catch (ChartCreatorException e) {
						Log.e("DetailsAsyncTask", "writeData page "+page, e);
						ret += "\nNo data available";
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
					}
				}
				break;
				
				default:
					try {
						tableLayout.addView(tabCreator.crearTablaTabProfile(tmp.getJSONObject("profileTab")));
						ret = ":( Profile Information for\n"+name;
					}catch (TabTableCreatorException e) {
						Log.e("DetailsAsyncTask", "writeData page "+page+": default", e);
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret += "\nNo data available";
					}
					break;
				}
				
				text.setText(ret);
			} catch (JSONException e) {
				Log.e("DetailsAsyncTask", "writeData JSONException", e);
				text.setText("Errorr");
			}
		}
	
	}

}
