package com.datamyne.mobile.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*
 * Clase que gestiona las peticiones de profiles al servidor remoto en The Datamyne
 */
public class RestTradeProfileClient2  implements IRestTradeProfileClient{

	
//	public RestTradeProfileClient2(){
//		BASE_SERVER = "http://200.40.197.173:8082/system";//beta
//		//BASE_SERVER = "http://www.datamyne20.com/system";//Produccion
//		//BASE_SERVER = "http://capilon.homeip.net/system"; //8082 PROXY
//	}
	public RestTradeProfileClient2(String baseServer) {
		super();
		this.baseServer = baseServer;
	}

	//Servidor beta 
	private String baseServer;// =   "http://200.40.197.173:8082/system";

	//Servidor produccion
    //final private String BASE_SERVER =   "http://www.datamyne20.com/system";

	public String getFullProfileJson(String type, String id) throws IOException{

		 String urlStr = baseServer+"/rest/fullTradeprofile/"+type+"/"+id;

		Log.i("RestTradeProfileClient2:getFullProfileJson","url ="+urlStr);
		HttpURLConnection con = null;
		StringBuilder payload = new StringBuilder();
		try {
			con = getRest(urlStr, payload);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;

		}finally{
			if(con != null)
				con.disconnect();
		}

		return payload.toString();

	}
	
	/**
	 * 
	 * @param urlStr
	 * @param output
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection getRest(String urlStr, StringBuilder output) throws IOException{
		URL url 	= new URL(urlStr);

		HttpURLConnection con =(HttpURLConnection) url.openConnection();
		con.setReadTimeout(20000);
		con.setConnectTimeout(25000);
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		con.setDoInput(true);
		con.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

		String payload = reader.readLine();
		output.append(payload);
		reader.close();

		return con;
	}
	
	/**
	 * Realiza una busqueda remota en el autocomplete de tradeprofiles
	 * @param target
	 * @param type
	 * @return
	 */
	public JSONObject searchRemote(String target, String type) throws SocketTimeoutException, IOException{
		HttpURLConnection con = null;
		JSONObject result = null;
		try {
	
			String q 	= URLEncoder.encode(target, "UTF-8");
						URL url 	= new URL(baseServer+"/rest/autocomplete?Base=usa_hid12&idComponent=402&compositeid=402&targetTerm="+q);

						
			Log.i("RestTradeProfileClient2:searchRemote","url ="+url.toString());
			
			con =(HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));

			String payload = reader.readLine();
			reader.close();
			con.disconnect();
			con = null;

			if(!payload.trim().isEmpty()){
				result = new JSONObject(payload);
			}
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw e;
		//SocketTimeoutException//failed to connect to
		}catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(con != null)
				con.disconnect();
		}
		return result;
	}
}
