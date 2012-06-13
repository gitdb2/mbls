package com.datamyne.mobile.providers;

import com.datamyne.mobile.offline.ProfilesSQLiteHelper;

public interface IProfileProvider {

	public String loadFullProfile(String localBasePath, String type, String id);
	
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper);
	
	boolean checkFileExists(String localBasePath, String type, String id);

}
