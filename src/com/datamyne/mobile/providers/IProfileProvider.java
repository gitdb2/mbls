package com.datamyne.mobile.providers;

public interface IProfileProvider {

	public String loadFullProfile(String localBasePath, String type, String id);

	boolean checkFileExists(String localBasePath, String type, String id);

}
