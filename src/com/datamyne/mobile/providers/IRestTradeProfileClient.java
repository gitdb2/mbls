package com.datamyne.mobile.providers;

import java.io.IOException;

import org.json.JSONObject;

public interface IRestTradeProfileClient {

	public String getFullProfileJson(String type, String id)throws IOException;

	public JSONObject searchRemote(String target, String type)throws IOException;

}
