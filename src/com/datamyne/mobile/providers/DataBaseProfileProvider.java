package com.datamyne.mobile.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;

import com.datamyne.mobile.xml.TradeProfilesOfflineActivity.Item;

public class DataBaseProfileProvider implements IDatabaseProfileProvider {

	public void saveDownloadedProfile(String filePath, String type,
			String id, String name, ProfilesSQLiteHelper dbHelper) {
		
		dbHelper.insert(type, Integer.parseInt(id), name, filePath);

	}

	public ArrayList<Item> loadSavedProfiles(ProfilesSQLiteHelper dbHelper) {
		
		//removal of saved profiles without its correspondant .json file
		deleteProfilesWithoutFile(dbHelper);
		
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

	private void deleteProfilesWithoutFile(ProfilesSQLiteHelper dbHelper) {
		Cursor cur = dbHelper.getAll();
		Collection<Integer> toDelete = new ArrayList<Integer>();
		File file;
		if (cur != null) {
		    if (cur.moveToFirst()) {
		        do {
		        	try {
			        	file = new File(cur.getString(4));
			        	if (!file.exists()) {
			        		toDelete.add(cur.getInt(0));
			        	}
					} catch (Exception e) {
						toDelete.add(cur.getInt(0));
					}
		        } while (cur.moveToNext());
		    }
		}
		dbHelper.delete(toDelete);
	}

}
