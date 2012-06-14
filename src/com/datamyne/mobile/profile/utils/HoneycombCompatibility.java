package com.datamyne.mobile.profile.utils;

import java.lang.reflect.Method;

import android.app.ActionBar;
import android.util.Log;

/*
 * Clase para permitir compatibilidad entre Android 3.x y 4.x
 */
public class HoneycombCompatibility {

	public static void actionBarSetLogo(ActionBar actionBar, int logiId) {
		try {
			Method setLogo = ActionBar.class.getMethod("setLogo",
					new Class[] { int.class });
			setLogo.invoke(actionBar, new Object[] { logiId });
		} catch (Exception e) {
			Log.w("HoneycombCompatibility", e.getMessage());
		}

	}
}
