package com.datamyne.mobile.providers;


public interface IProfileProvider {

	//public String loadFullProfile(String localBasePath, String type, String id);
	
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper);
	
	boolean checkFileExists(String localBasePath, String type, String id);

}
