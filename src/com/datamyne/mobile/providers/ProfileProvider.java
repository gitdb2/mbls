package com.datamyne.mobile.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;



public class ProfileProvider implements IProfileProvider {

	boolean onTheflyData;
	private IRestTradeProfileClient client;
	
	
	/**
	 * Estado que cambi en caso que no haya sd a true
	 */
	public boolean isOnTheflyData() {
		return onTheflyData;
	}

	public ProfileProvider(){
		this(new RestTradeProfileClient2());
	}
	
	public ProfileProvider(IRestTradeProfileClient client){
		this.client = client;
		updateExternalStorageState();
	}

	public boolean checkFileExists(String localBasePath, String type, String id){
			boolean ret = false;
			try {
				updateExternalStorageState();
				if(mExternalStorageAvailable && localBasePath != null){
					File root = new File(localBasePath, type + File.separatorChar + id+".json");
					ret = root.exists();
				}
			} catch (Exception e) {
				Log.e("ExternalStorage", "Error checkFileExists" +   localBasePath + " - "+ id, e);
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
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper) throws IOException{
		String result ="";
		updateExternalStorageState();
		
		try {
			String payload = "";
			if(mExternalStorageAvailable){//si la sd esta disponible
				onTheflyData =false;
				File root = new File(localBasePath, type + File.separatorChar + id+ ".json");
				if ( !root.exists()) {
					payload = client.getFullProfileJson(type, id);
					
					//si se salva en la sd entonces se salva en la db
					if(saveToSD(localBasePath, type, id, payload)){
						saveToBD(root.getAbsolutePath(), type, id, name, dbHelper);
					}
			
				}else{
					InputStreamReader isReader = new FileReader(root);
					BufferedReader reader = new BufferedReader(isReader);

					payload = reader.readLine();
					reader.close();
				}
			}else{
				Log.w("ExternalStorage", "No hay SD usando modo onthe fly ");
				payload = client.getFullProfileJson(type, id);
				onTheflyData = true;
			}
			
			if(payload!= null && !payload.trim().isEmpty()){
				result = payload;
			}else{
				Log.e("ExternalStorage", "Error reading " + name + " - "+ id + " Payload is empty");
			}
			
			
		}catch (java.net.ConnectException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.e("ExternalStorage", "loadFullProfile Error de conexion " +   name + " - "+ id, e);
			throw e;
		}catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.e("ExternalStorage", "loadFullProfile Error reading IOException " +   name + " - "+ id, e);
			throw e;
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
		updateExternalStorageState();
		if(isSdPresent()){//si hay sd 
			File root = new File(localBasePath, type);
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
	
				Log.e("ExternalStorage", "Error writing " + root, e);
			}
		}else{
			Log.w("ExternalStorage", "No SD present ");
		}
		return ret;
	}
	
	public boolean isSdPresent() {
	    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}	

	
//	public String loadFullProfileInternalCache(Context context, String type, String id, String name, ProfilesSQLiteHelper dbHelper){
//		String result ="";
//		updateExternalStorageState();
//		
//		try {
//			String payload = "";
//			if(mExternalStorageAvailable){//si la sd esta disponible
//				onTheflyData =false;
//				File root = new File(localBasePath, type + File.separatorChar + id+ ".json");
//				if ( !root.exists()) {
//					payload = client.getFullProfileJson(type, id);
//					
//					//si se salva en la sd entonces se salva en la db
//					if(saveToSD(localBasePath, type, id, payload)){
//						saveToBD(root.getAbsolutePath(), type, id, name, dbHelper);
//					}
//			
//				}else{
//					InputStreamReader isReader = new FileReader(root);
//					BufferedReader reader = new BufferedReader(isReader);
//
//					payload = reader.readLine();
//					reader.close();
//				}
//			}else{
//				Log.w("ExternalStorage", "No hay SD usando modo onthe fly ");
//				payload = client.getFullProfileJson(type, id);
//				onTheflyData = true;
//			}
//			
//			if(payload!= null && !payload.trim().isEmpty()){
//				result = payload;
//			}else{
//				Log.e("ExternalStorage", "Error reading " + name + " - "+ id + " Payload is empty");
//			}
//		} catch (IOException e) {
//			// Unable to create file, likely because external storage is
//			// not currently mounted.
//			Log.e("ExternalStorage", "loadFullProfile Error reading IOException" +   name + " - "+ id, e);
//		}
//		
//		return result;
//	}
	
}
