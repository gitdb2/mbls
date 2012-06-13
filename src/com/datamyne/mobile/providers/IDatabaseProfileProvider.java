package com.datamyne.mobile.providers;

import com.datamyne.mobile.offline.ProfilesSQLiteHelper;

public interface IDatabaseProfileProvider {

	public void saveDownloadedProfile(String filePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper);
	
}
