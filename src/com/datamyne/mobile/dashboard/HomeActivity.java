
package com.datamyne.mobile.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.datamyne.mobile.xml.R;
import com.datamyne.mobile.xml.TradeProfilesActivity;
import com.datamyne.mobile.xml.TradeProfilesOfflineActivity;

/**
 * Esta activity representa el Dashboard de la aplicacion, 
 * desde aca se lanzan las diferentes funcionalidades.
 */ 
public class HomeActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String WORK_MODE = "workMode";
	private boolean workOffline;
	
	private String baseServerTag = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);


  /**
   *se setea el modo de trabajo que estaba guardado en las preferencias
   *esto se setea si se viene desde otra activity presionando HOME 
   *o si se inicia la aplicacion desde cero
   */
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        workOffline = settings.getBoolean(WORK_MODE, false);
        
        SharedPreferences pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        if(baseServerTag == null || baseServerTag.isEmpty()){
        	baseServerTag = pref.getString("baseServer", null);
        	if(baseServerTag == null){
        		baseServerTag = "http://200.40.197.173:8082/system";
        		pref.edit().putString("baseServer", baseServerTag).commit();
        		Toast.makeText(this, "Setting base Server: "+ baseServerTag, Toast.LENGTH_LONG).show();
        	}
        }
        
		
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.home_menu, menu);
	    
	    if (workOffline) {
	    	menu.getItem(0).setTitle(R.string.work_online);
	    	Drawable icon = getResources().getDrawable(R.drawable.disconnect);
	    	menu.getItem(0).setIcon(icon);
		} else {
			menu.getItem(0).setTitle(R.string.work_offline);
			Drawable icon = getResources().getDrawable(R.drawable.connect);
			menu.getItem(0).setIcon(icon);
		}
	    
		menu.add(0, Menu.FIRST + 1, Menu.NONE, R.string.exit_application);
		
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (R.id.menu_offline):
			workOffline = !workOffline;
			showCurrentWorkMode();
			saveWorkMode();
			item.setTitle(workOffline? R.string.work_online: R.string.work_offline);
			Drawable icon = getResources().getDrawable(workOffline?R.drawable.disconnect:R.drawable.connect);
			item.setIcon(icon);
			break;
		case (R.id.menu_servers):
			openSelectServerDialog();
			break;
		case (Menu.FIRST + 1):
			this.finish();
			return true;
		}
		return false;
	}

	/**
	 * Muestra un dialogo con los servidores a seleccionar, y permite agregar, modifica un archivo de config con el string seleccionado. 
	 */
	private void openSelectServerDialog() {
         LayoutInflater inflater	= LayoutInflater.from(this);
         View dialogView 			= inflater.inflate(R.layout.dialog_select_server, null);
         RadioGroup radioGroup 		= (RadioGroup) dialogView.findViewById(R.id.radioGroupServers);
         
         for (int i = 0; i < radioGroup.getChildCount(); i++) {
        	 RadioButton radio = (RadioButton) radioGroup.getChildAt(i);
        	 radio.setOnClickListener(	new RadioButton.OnClickListener() {
							        		 public void onClick(View v) {
							        			 HomeActivity.this.onClickServerRadio(v);
							
							        		 }
							        	 });
        	 String tag = (String) radio.getTag();
        	 if(tag!= null && tag.equalsIgnoreCase(baseServerTag)){
        		 radio.setChecked(true);
        	 }else{
        		 radio.setChecked(false);
        	 }
         }
         
         
         new AlertDialog.Builder(this)
           .setTitle("Select server to connect")
           .setView(dialogView)
           .setCancelable(true)
           .show();
	}

	public void onClickServerRadio(View v) {
		baseServerTag = (String) v.getTag();
		
		SharedPreferences pref = HomeActivity.this.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
		pref.edit().putString("baseServer", baseServerTag).commit();
		Toast.makeText(this, "Setting base Server: "+ baseServerTag, Toast.LENGTH_LONG).show();
	}
	
	
	
	
	private void showCurrentWorkMode() {
		String message;
		if (workOffline) {
			message = getResources().getString(R.string.work_offline_message);
		} else {
			message = getResources().getString(R.string.work_online_message);
		}
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	private void saveWorkMode() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      	SharedPreferences.Editor editor = settings.edit();
      	editor.putBoolean(WORK_MODE, workOffline);
      	editor.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}
	
	public void onClickMyAccount(View v) {
		startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
	}
	
	public void onClickTradeProfiles(View v) {
		if (workOffline) {
			startActivity(new Intent(getApplicationContext(), TradeProfilesOfflineActivity.class));
		} else {
			startActivity(new Intent(getApplicationContext(), TradeProfilesActivity.class));
		}
	}
	
	public void onClickFeature(View v) {
		startActivity(new Intent(getApplicationContext(), FeatureActivity.class));
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

} 
