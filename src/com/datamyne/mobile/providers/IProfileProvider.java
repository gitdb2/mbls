package com.datamyne.mobile.providers;

import java.io.IOException;


public interface IProfileProvider {

	/**
	 * Busca un profile completo a partir del tipo y del id y retorna la representacion json del profile
	 * @param localBasePath
	 * @param type
	 * @param id
	 * @param name
	 * @param dbHelper
	 * @return
	 * @throws IOException
	 */
	public String loadFullProfile(String localBasePath, String type, String id, String name, ProfilesSQLiteHelper dbHelper)  throws IOException;
	
	/**
	 * Chequea si un archivo existe en la SD
	 * @param localBasePath
	 * @param type
	 * @param id
	 * @return
	 */
	boolean checkFileExists(String localBasePath, String type, String id);
	/**
	 * Checquea si existe un SD
	 * @return
	 */
	public boolean isSdPresent();

	/**
	 * indica si se esta usando el sistema on he fly sin cache
	 * @return
	 */
	boolean isOnTheflyData();

}
