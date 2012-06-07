package com.datamyne.mobile.xml;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.datamyne.mobile.providers.HttpClientTaskActivity;


public class PruebaAsyncTAsk extends Activity {

	private ProgressDialog dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.async_test);
        
        new HttpClientTaskActivity(this).execute("aaaaaaa");
        
     /*   dialog = new ProgressDialog(this);
        
        
        dialog.setMessage("Descargando...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//STYLE_HORIZONTAL);
        //dialog.setCancelable(false);
        
        new HttpClientTask(dialog).execute("aaaaaaa");
       */ 
        //Realizamos cualquier otra operación necesaria
        //Creamos una nueva instancia y llamamos al método ejecutar
        //pasándole el string.
        //new MiTarea().execute("http://www.ejemplo.com/file.zip");
        
        //dialog = ProgressDialog.show(this, "Downloading Data..", "Please wait", true,false);  
        
        
        

      }
    
   /* 
    private class MiTarea extends AsyncTask<String, Float, Integer>{

   	 protected void onPreExecute() {
   		 dialog.setProgress(0);
   		 dialog.setMax(100);
   		 
        dialog.show(); //Mostramos el diálogo antes de comenzar
     //           dialog.setContentView(R.layout.loading_dialog); 
     }

         protected Integer doInBackground(String... urls) {
       

             for (int i = 0; i < 250; i++) {
                   //Simulamos cierto retraso
           	    try {Thread.sleep(200); }
                   catch (InterruptedException e) {}

                   publishProgress(i/250f); //Actualizamos los valores
               }

             return 250;
         }

         protected void onProgressUpdate (Float... valores) {
       	  int p = Math.round(100*valores[0]);
       	  dialog.setProgress(p);
         }

         protected void onPostExecute(Integer bytes) {
       	  dialog.dismiss();
         }
   }
   */
	
}
