package com.datamyne.mobile.offline;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.datamyne.mobile.xml.R;

public class OfflineProfileList extends Activity {

	private ProfilesSQLiteHelper dbHelper;
	private Cursor profilesCursor;
	private ProfilesAdapter profilesAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_profile_list);

		dbHelper = new ProfilesSQLiteHelper(this);
		
		/*
		dbHelper.insert("consignee", 1, "DOLE", "archivo1");
		dbHelper.insert("consignee", 2, "SONY", "archivo2");
		dbHelper.insert("consignee", 3, "MAERSK", "archivo3");
		*/
		
		ListView list = (ListView)findViewById(R.id.profiles);

		profilesCursor = dbHelper.getAll();
		startManagingCursor(profilesCursor);
		profilesAdapter = new ProfilesAdapter(profilesCursor);
		list.setAdapter(profilesAdapter);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		dbHelper.close();
	}
	
	class ProfilesAdapter extends CursorAdapter {
		
		ProfilesAdapter(Cursor c){
			super(OfflineProfileList.this, c);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			OfflineProfileHolder holder = (OfflineProfileHolder) view.getTag();
			holder.populateFrom(profilesCursor, dbHelper);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			OfflineProfileHolder holder = new OfflineProfileHolder(row);
			row.setTag(holder);
			return row;
		}
		
	}
	
	static class OfflineProfileHolder {
		private TextView name = null;
		private TextView type = null;

		OfflineProfileHolder(View row) {
			name = (TextView)row.findViewById(R.id.name);
			type = (TextView)row.findViewById(R.id.type);
		}

		void populateFrom(Cursor c, ProfilesSQLiteHelper helper) {
			name.setText(helper.getName(c));
			type.setText(helper.getType(c));
		}
	}
	
	
}
