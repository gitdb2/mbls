package com.datamyne.mobile.providers;


import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * Clase que gestiona la base de datos, su creacion, insercion,
 * recuperacion y borrado de registros.
 */
public class ProfilesSQLiteHelper extends SQLiteOpenHelper {
	
	final static int SCHEMA_VERSION = 1;
	
	final static String DATABASE = "DB_profiles";
	final static String TABLE_PROFILES = "profiles";
    
	final static String SQL_CREATE_DB = "CREATE TABLE " + TABLE_PROFILES + 
			" (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, code INTEGER, name TEXT, file_path TEXT)";
 
    public ProfilesSQLiteHelper(Context context){
    	super(context, DATABASE, null, SCHEMA_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DB);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL(SQL_CREATE_DB);
    }
    
    public void insert(String type, int code, String name, String file_path) {
    	ContentValues cv = new ContentValues();
    	cv.put("type", type);
    	cv.put("code", code);
    	cv.put("name", name);
    	cv.put("file_path", file_path);
    	getWritableDatabase().insert(TABLE_PROFILES, "name", cv);
    }
    
    public Cursor getAll(){
    	return(getReadableDatabase().rawQuery(
    			"SELECT _id, type, code, name, file_path FROM " + TABLE_PROFILES + " ORDER BY name", null));
    }
    
	public void delete(Collection<Integer> toDelete) {
		for (Integer id : toDelete) {
			getWritableDatabase().delete(TABLE_PROFILES, "_id = ?", new String [] { id.toString() });
		}
	}
    
    public String getType(Cursor c){
		return c.getString(1);
	}
    
	public String getCode(Cursor c){
		return c.getString(2);
	}
	
	public String getName(Cursor c){
		return c.getString(3);
	}
	
	public String getFilePath(Cursor c){
		return c.getString(4);
	}
	
}
