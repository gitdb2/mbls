package com.datamyne.mobile.profile.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datamyne.mobile.profile.utils.ChartCreator.ChartCreatorException;
import com.datamyne.mobile.profile.utils.ChartCreator.TabTableCreatorException;
import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.ProfilesSQLiteHelper;
import com.datamyne.mobile.xml.R;


public class DetailsAsyncTask extends AsyncTask<String, Float, String> {

	private IProfileProvider profileProvider 	= new ProfileProvider();
	private ProgressDialog dialog;
	private ViewGroup view;
	boolean showDialog= true;
	int page = 0;
	Context context;
	IChartsCreator chartCreator;
	ITabTableCreator tabCreator; 
	private ProfilesSQLiteHelper dbHelper;
	
	public DetailsAsyncTask(Context context, ViewGroup container, boolean showDialog, int page) {
		super();
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
		String tmp = profileProvider.loadFullProfile(params[0],params[1],params[2], params[3], dbHelper);
		dbHelper.close();
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
		
		LinearLayout graficaLayout	= (LinearLayout) view.findViewById(R.id.linearLayoutTChart);
		LinearLayout tableLayout	= (LinearLayout) view.findViewById(R.id.pageDataTable); //layout de tabla para mostrar los datos
		TextView text = (TextView) view.findViewById(R.id.textViewData);
		
		String ret  = "";
		if(payload!=null && !payload.trim().isEmpty()){
			try {
				JSONObject obj = new JSONObject(payload);
				JSONObject tmp = obj.getJSONObject("tradeProfileContainer");
				
				switch (page) {
				case 0:
//					ret = tmp.getJSONObject("profileTab").toString();
					try {
						tableLayout.addView(tabCreator.crearTablaTabProfile(tmp.getJSONObject("profileTab")));
						view.removeView(graficaLayout);
					}catch (TabTableCreatorException e) {
						e.printStackTrace();
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret = "No data available";
					}
					
					
					break;
				case 1:
//					ret = tmp.getJSONObject("totalMonthsTab").toString();
					try {
						tableLayout.addView(tabCreator.crearTablaTabMonthly(tmp.getJSONObject("totalMonthsTab")));
						graficaLayout.addView(chartCreator.crearGraficaMonthly(tmp.getJSONObject("totalMonthsTab")));
//					} catch (TabTableCreatorException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
					}catch (ChartCreatorException e) {
						e.printStackTrace();
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret = "No data available";
					}
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				{
					JSONArray arr = tmp.getJSONObject("dimensionTabList").getJSONArray("tabDimension");
					//ret = arr.getString(page-2);
					try {
						tableLayout.addView(tabCreator.crearTablaTabOther(arr.getJSONObject(page-2)));
						graficaLayout.addView(chartCreator.crearGraficaMulti(arr.getJSONObject(page-2)));
					} catch (ChartCreatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ret = "No data available";
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
					}
				}
				break;
				
				default:
					try {
						tableLayout.addView(tabCreator.crearTablaTabProfile(tmp.getJSONObject("profileTab")));
					}catch (TabTableCreatorException e) {
						e.printStackTrace();
						//En caso que de error ver la causa y si es que no hay datos escribir que no hay datos
						ret = "No data available";
					}
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