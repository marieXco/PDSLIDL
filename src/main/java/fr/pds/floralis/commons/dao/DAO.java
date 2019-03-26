package fr.pds.floralis.commons.dao;

import java.sql.Connection;

import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;


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
	 * @return boolean
	 */


	public abstract boolean create(JSONObject jsonObject);


	/**
	 * 
	 * Méthode pour effacer
	 * 
	 * @param obj
	 * 
	 * @return boolean
	 */


	public abstract boolean delete(JSONObject jsonObject);


	/**
	 * 
	 * Méthode de mise à jour
	 * 
	 * @param obj
	 * 
	 * @return boolean
	 */

	public abstract boolean update(JSONObject jsonObject);

	/**
	 * 
	 * Méthode de recherche des informations
	 * 
	 * @param id
	 * 
	 * @return T
	 */

	public abstract T find(JSONObject jsonObject);

	
	/**
	 * 
	 * Méthode qui renvoie la liste de tous
	 * 
	 * 
	 * @return List<T>
	 */
	public abstract List<T> findAll();


}
