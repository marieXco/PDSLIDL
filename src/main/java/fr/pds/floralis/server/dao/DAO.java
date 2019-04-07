package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public abstract class DAO<T> {

	protected JDBCConnectionPool jdbc = new JDBCConnectionPool();
	protected Connection connect = null;
	

	public DAO() throws ClassNotFoundException, SQLException {
		DataSource.createPool(jdbc);
		connect = DataSource.getConnectionFromPool(jdbc);	
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

	public JDBCConnectionPool getJdbc() {
		return jdbc;
	}

	public void setJdbc(JDBCConnectionPool jdbc) {
		this.jdbc = jdbc;
	}

	public Connection getConnect() {
		return connect;
	}

	public void setConnect(Connection connect) {
		this.connect = connect;
	}
	
}
