package com.datamyne.mobile.providers;

import java.util.ArrayList;

import android.database.Cursor;

import com.datamyne.mobile.offline.ProfilesSQLiteHelper;
import com.datamyne.mobile.xml.TradeProfilesOfflineActivity.Item;

public class DataBaseProfileProvider implements IDatabaseProfileProvider {

	public void saveDownloadedProfile(String filePath, String type,
			String id, String name, ProfilesSQLiteHelper dbHelper) {
		
		dbHelper.insert(type, Integer.parseInt(id), name, filePath);

	}

	public ArrayList<Item> loadSavedProfiles(ProfilesSQLiteHelper dbHelper) {
		ArrayList<Item> res = new ArrayList<Item>();
		Cursor cur = dbHelper.getAll();
		if (cur != null) {
		    if (cur.moveToFirst()) {
		        do {
		        	res.add(new Item(cur.getString(2), cur.getString(3)));                 
		        } while (cur.moveToNext());
		    }
		}
		return res;
	}

}
