package com.datamyne.mobile.providers;

import java.util.ArrayList;

import com.datamyne.mobile.profile.utils.Item;

public interface IDatabaseProfileProvider {

	public void saveDownloadedProfile(String filePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper);
	
	public ArrayList<Item> loadSavedProfiles(ProfilesSQLiteHelper dbHelper);
	
}
