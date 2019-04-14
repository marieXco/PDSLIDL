package fr.pds.floralis.server.dao;

import java.sql.SQLException;

import org.json.JSONObject;

public abstract class DAO<T> {

	public DAO() throws ClassNotFoundException, SQLException {
		
	}

	/**
	 * 
	 * Creation method
	 * 
	 * @param JSONObject
	 * 
	 * @return JSONObject
	 */


	public abstract JSONObject create(JSONObject jsonObject);


	/**
	 * 
	 * Erasing method
	 * 
	 * @param JSONObject
	 * 
	 * @return JSONObject
	 */


	public abstract JSONObject delete(JSONObject jsonObject);


	/**
	 * 
	 * Update method
	 * 
	 * @param JSONObject
	 * 
	 * @return JSONObject
	 */

	public abstract JSONObject update(JSONObject jsonObject);

	/**
	 * 
	 * Finding by Id method 
	 * 
	 * @param JSONObject
	 * 
	 * @return JSONObject
	 */

	public abstract JSONObject find(JSONObject jsonObject);

	
	/**
	 * 
	 * Finding all method
	 * 
	 * 
	 * @return JSONObject
	 */
	public abstract JSONObject findAll();
}
