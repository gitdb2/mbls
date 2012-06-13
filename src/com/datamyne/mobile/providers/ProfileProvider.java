package com.datamyne.mobile.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;
import android.util.Log;



public class ProfileProvider implements IProfileProvider {

	
	//private IRestTradeProfileClient client = new RestTradeProfileClient();
	private IRestTradeProfileClient client;
	
	public ProfileProvider(){
		 client = new RestTradeProfileClient2();
	}
	
	public ProfileProvider(IRestTradeProfileClient client){
		this.client = client;
	}

	public boolean checkFileExists(String localBasePath, String type, String id){
			boolean ret = false;
			try {
				File root = new File(localBasePath, type + File.separatorChar + id+".json");
				ret = root.exists();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = false;
			}
			return ret;
	}
	
	
	private boolean mExternalStorageAvailable = false;
	private boolean mExternalStorageWriteable = false;

	private void updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}
	
	/**
	 * Busca un profile completo a partir del tipo y del id y retorna la representacion json del profile
	 * @param localBasePath
	 * @param type
	 * @param id
	 * @return
	 */
	@Deprecated
	public String loadFullProfile(String localBasePath, String type, String id){
		String result ="";
		
		
		File root = new File(localBasePath, type + File.separatorChar + id+".json");
		
		updateExternalStorageState();
		try {

			if(mExternalStorageAvailable){
				String payload = "";
				if (!root.exists()) {
					payload = client.getFullProfileJson(type, id);
					saveToSD(localBasePath, type, id, payload);
				}else{
					InputStreamReader isReader = new FileReader(root);
					BufferedReader reader = new BufferedReader(isReader);

					payload = reader.readLine();
					reader.close();
				}

				if(payload!= null && !payload.trim().isEmpty()){
					result = payload;
				}else{
					Log.w("ExternalStorage", "Error reading " + root + " Payload is empty");
				}
			}else{
				Log.w("ExternalStorage", "Error reading " + root + " Payload is empty");
			}
		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error reading " + root, e);
		}
		return result;
	}
	
	/**
	 * Busca un profile completo a partir del tipo y del id y retorna la representacion json del profile
	 * @param localBasePath
	 * @param type
	 * @param id
	 * @return
	 */
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper){
		String result ="";
		
		File root = new File(localBasePath, type + File.separatorChar + id+ ".json");
		
		updateExternalStorageState();
		try {

			if(mExternalStorageAvailable){
				String payload = "";
				if (!root.exists()) {
					payload = client.getFullProfileJson(type, id);
					saveToSD(localBasePath, type, id, payload);
					saveToBD(root.getAbsolutePath(), type, id, name, dbHelper);
				}else{
					InputStreamReader isReader = new FileReader(root);
					BufferedReader reader = new BufferedReader(isReader);

					payload = reader.readLine();
					reader.close();
				}

				if(payload!= null && !payload.trim().isEmpty()){
					result = payload;
				}else{
					Log.w("ExternalStorage", "Error reading " + root + " Payload is empty");
				}
			}else{
				Log.w("ExternalStorage", "Error reading " + root + " Payload is empty");
			}
		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error reading " + root, e);
		}
		return result;
	}
	
	private void saveToBD(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper) {
		IDatabaseProfileProvider dbProvider = new DataBaseProfileProvider();
		dbProvider.saveDownloadedProfile(localBasePath, type, id, name, dbHelper);
	}

	/**
	 * Guarda en la SD el payload	
	 * @param localBasePath
	 * @param type
	 * @param id
	 * @param payload
	 * @return retorna true si todo ok y false si hubo error
	 */
	private boolean saveToSD(String localBasePath, String type, String id, String payload) {
		boolean ret = false;
		File root = new File(localBasePath, type);
		updateExternalStorageState();
		try {

			if(mExternalStorageWriteable){
				if (!root.exists()) {
					root.mkdirs();
				}
				File gpxfile = new File(root, id+".json");
				if(!gpxfile.exists()){
					FileWriter writer = new FileWriter(gpxfile);
					writer.append(payload);
					writer.flush();
					writer.close();
					Log.w("ExternalStorage", root + " Saved");
				}else{
					Log.w("ExternalStorage", root + " Already Saved");
				}
				ret = true;
			}else{
				Log.w("ExternalStorage", root + " No se puede escribir en la SD");
			}
		} catch (IOException e) {

			Log.w("ExternalStorage", "Error writing " + root, e);
		}
		return ret;
	}
	
		

}
