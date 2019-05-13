package fr.pds.floralis.server.dao;

import java.util.List;

import fr.pds.floralis.commons.bean.entity.Sensor;

public interface DAO<T> {


	/**
	 * Creation method
	 * 
	 * @param objToUpdate
	 * 
	 */

	public abstract boolean create(T objToCreate);
	
	/**
	 * Deleting method
	 * 
	 * @param id
	 * 
	 */
	
	public abstract boolean delete(int id);
	
	/**
	 * Updating method
	 * 
	 * @param id
	 * 
	 */

	public abstract boolean update(int id, T objToUpdate);

	/**
	 * Finding by id method
	 * 
	 * @param id
	 * 
	 * @return
	 * 
	 */

	public abstract Object find(int id);

	
	
	/**
	 * FindingAll method
	 * 
	 * @return
	 */
	
	public abstract List<T> findAll();

}
