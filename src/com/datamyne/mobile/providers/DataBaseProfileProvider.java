package com.datamyne.mobile.providers;

import com.datamyne.mobile.offline.ProfilesSQLiteHelper;

public class DataBaseProfileProvider implements IDatabaseProfileProvider {

	public void saveDownloadedProfile(String filePath, String type,
			String id, String name, ProfilesSQLiteHelper dbHelper) {
		
		dbHelper.insert(type, Integer.parseInt(id), name, filePath);

	}

}
