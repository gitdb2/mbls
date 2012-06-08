package com.datamyne.mobile.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

public class RestTradeProfileClient  implements IRestTradeProfileClient{

	public String getFullProfileJson(String type, String id) throws IOException{

		 String urlStr = "http://200.40.197.173:8082/system/rest/fullTradeprofile/"+type+"/"+id;

		HttpURLConnection con = null;
		StringBuilder payload = new StringBuilder();
		try {
			if(Thread.interrupted())
				throw new InterruptedException();

			con = getRest(urlStr, payload);

			if(Thread.interrupted())
				throw new InterruptedException();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;

		} catch (Exception e) {
			e.printStackTrace();

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
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000);
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
			if(Thread.interrupted())
				throw new InterruptedException();

			String q 	= URLEncoder.encode(target, "UTF-8");
						URL url 	= new URL("http://200.40.197.173:8082/system/rest/autocomplete?" +
								"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);

			/*
			{"list": [{
			    "second": "6795958",
			    "third": "DOLE FOOD COMPANY INC (CA)",
			    "first": 402
			  }, {
			    "second": "6719778",
			    "third": "DOLE FOOD COMPANY INC (DE)",
			    "first": 402
			  }]}
			 */

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

			if(Thread.interrupted())
				throw new InterruptedException();
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw e;
		//SocketTimeoutException//failed to connect to
		}catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();

		}finally{
			if(con != null)
				con.disconnect();
		}
		return result;
	}
}
