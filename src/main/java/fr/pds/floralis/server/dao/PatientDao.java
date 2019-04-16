package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONObject;

import fr.pds.floralis.commons.bean.entity.Patient;

public class PatientDao extends DAO<Patient> {
	
	//create a connection 
	Connection connect = null;
	
	//constructor
	public PatientDao(Connection connection) throws ClassNotFoundException, SQLException {
		this.connect = connect; 
	}

	@Override
	// for insert 
	public JSONObject create(JSONObject jsonObject) {
		
		return null;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
