package com.datamyne.mobile.providers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class HttpClientTaskActivity extends AsyncTask<String, Float, String> {

	private IRestTradeProfileClient client 		= new RestTradeProfileClient();
	private ProgressDialog dialog;

	public HttpClientTaskActivity(Activity activity) {
		super();
		dialog = new ProgressDialog(activity);
        dialog.setMessage("Descargando...");
        dialog.setTitle("Progreso");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
	}

	@Override
	protected String doInBackground(String... params) {
//		for (int i = 0; i < 250; i++) {
//			// Simulamos cierto retraso
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//			}
//
//			publishProgress(i / 250f); // Actualizamos los valores
//		}

		
		
		return "250";
	}

	protected void onPreExecute() {
		dialog.setProgress(0);
		dialog.setMax(100);

		dialog.show(); // Mostramos el diálogo antes de comenzar

	}

	protected void onProgressUpdate(Float... valores) {
		int p = Math.round(100 * valores[0]);
		dialog.setProgress(p);
	}

	protected void onPostExecute(Integer bytes) {
		dialog.dismiss();
	}

}
