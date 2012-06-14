package com.datamyne.mobile.providers;

import java.io.IOException;


public interface IProfileProvider {

	//public String loadFullProfile(String localBasePath, String type, String id);
	
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper)  throws IOException;
	
	boolean checkFileExists(String localBasePath, String type, String id);

	public boolean isSdPresent();

	boolean isOnTheflyData();

}
