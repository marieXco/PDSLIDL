package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.util.List;

import org.json.JSONObject;


public abstract class DAO<T> {

	protected Connection connect = null;

	public DAO(Connection conn) {
		//TODO : connectionPool
		this.connect = conn;

	}

	/**
	 * 
	 * Méthode de création
	 * 
	 * @param obj
	 * 
	 * @return JSONObject
	 */


	public abstract JSONObject create(JSONObject jsonObject);


	/**
	 * 
	 * Méthode pour effacer
	 * 
	 * @param obj
	 * 
	 * @return JSONObject
	 */


	public abstract JSONObject delete(JSONObject jsonObject);


	/**
	 * 
	 * Méthode de mise à jour
	 * 
	 * @param obj
	 * 
	 * @return JSONObject
	 */

	public abstract JSONObject update(JSONObject jsonObject);

	/**
	 * 
	 * Méthode de recherche des informations
	 * 
	 * @param id
	 * 
	 * @return T
	 */

	public abstract JSONObject find(JSONObject jsonObject);

	
	/**
	 * 
	 * Méthode qui renvoie la liste de tous
	 * 
	 * 
	 * @return List<T>
	 */
	public abstract JSONObject findAll();


}
