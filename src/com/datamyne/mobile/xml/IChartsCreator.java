package com.datamyne.mobile.xml;

import org.json.JSONObject;

import android.view.View;

import com.datamyne.mobile.xml.ChartCreator.ChartCreatorException;

public interface IChartsCreator {

	//	Color.parseColor(4)
	public abstract View crearGraficaMonthly(JSONObject data)
			throws ChartCreatorException;

	public abstract View crearGraficaMulti(JSONObject data)
			throws ChartCreatorException;

}