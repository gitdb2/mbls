package com.datamyne.mobile.providers;

import org.json.JSONObject;

public interface IRestTradeProfileClient {

	public String getFullProfileJson(String type, String id);

	public JSONObject searchRemote(String target, String type);

}
