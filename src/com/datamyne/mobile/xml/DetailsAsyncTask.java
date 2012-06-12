package com.datamyne.mobile.xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.xml.ChartCreator.ChartCreatorException;


public class DetailsAsyncTask extends AsyncTask<String, Float, String> {

	private IProfileProvider profileProvider 	= new ProfileProvider();
	private ProgressDialog dialog;
	private ViewGroup view;
	boolean showDialog= true;
	int page = 0;
	Context context;
	ChartCreator chartCreator; 
	
	public DetailsAsyncTask(Context context, ViewGroup container, boolean showDialog, int page) {
		super();
		this.view = container;
		this.showDialog = showDialog;  
		this.page = page;
		this.context = context;
		this.chartCreator = new ChartCreator(context);
		
		
		if(showDialog){
			dialog = new ProgressDialog(context);
	        dialog.setMessage("Please wait...");
	        dialog.setTitle("Connecting with server");
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String tmp = profileProvider.loadFullProfile(params[0],params[1],params[2]);
		return tmp;
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
	
	
	private void writeData(String payload) {
		
		LinearLayout graficaLayout = (LinearLayout) view.findViewById(R.id.linearLayoutTChart);
		TextView text = (TextView) view.findViewById(R.id.textViewData);
		
		String ret  = "";
		if(payload!=null && !payload.trim().isEmpty()){
			try {
				JSONObject obj = new JSONObject(payload);
				JSONObject tmp = obj.getJSONObject("tradeProfileContainer");
				
				switch (page) {
				case 0:
					ret = tmp.getJSONObject("profileTab").toString();
					break;
				case 1:
					ret = tmp.getJSONObject("totalMonthsTab").toString();
					try {
						graficaLayout.addView(chartCreator.crearGraficaMonthly(tmp.getJSONObject("totalMonthsTab")));
					} catch (ChartCreatorException e) {
						e.printStackTrace();
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
					}
					break;
				case 2:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(0);
					graficaLayout.addView(chartCreator.crearGraficaMulti(obj));
				}
				break;
				case 3:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(1);
					graficaLayout.addView(chartCreator.crearGraficaMulti(obj));
				
				}
				break;
				case 4:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(2);
					graficaLayout.addView(chartCreator.crearGraficaMulti(obj));
				}
				break;					
				case 5:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(3);
					graficaLayout.addView(chartCreator.crearGraficaMulti(obj));
				}
				break;
				default:
					ret = tmp.getJSONObject("profileTab").toString();	
					break;
				}
				
				text.setText(ret);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				text.setText("Errorr");
			}
		}
	
	}

	private String getPageData(String payload){
		String ret  = "";
		if(payload!=null && !payload.trim().isEmpty()){
			try {
				JSONObject obj = new JSONObject(payload);
				JSONObject tmp = obj.getJSONObject("tradeProfileContainer");
				
				switch (page) {
				case 0:
					ret = tmp.getJSONObject("profileTab").toString();
					break;
				case 1:
					ret = tmp.getJSONObject("totalMonthsTab").toString();
					break;
				case 2:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(0);
				}
				break;
				case 3:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(1);
				
				}
				break;
				case 4:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(2);
				
				}
				break;					
				case 5:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					ret = arr.getString(3);
				
				}
				break;
				default:
					ret = tmp.getJSONObject("profileTab").toString();	
					break;
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	

}