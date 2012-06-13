package com.datamyne.mobile.profile.utils;

import org.json.JSONObject;

import com.datamyne.mobile.profile.utils.ChartCreator.TabTableCreatorException;

import android.view.View;

public interface ITabTableCreator {

	public abstract View crearTablaTabProfile(JSONObject data) throws TabTableCreatorException;

	/**
	 * Crea una tablelayout con la tabla de datos de la grafica
	 * @param data
	 * @return
	 * @throws TabTableCreatorException
	 */
	public abstract View crearTablaTabMonthly(JSONObject data) throws TabTableCreatorException;

	public abstract View crearTablaTabOther(JSONObject data) throws TabTableCreatorException;

}