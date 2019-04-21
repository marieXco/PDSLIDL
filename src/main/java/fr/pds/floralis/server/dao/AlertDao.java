package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import fr.pds.floralis.commons.bean.entity.Alert;

public class AlertDao extends DAO<Alert> {
	Connection connect = null; 

	public AlertDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect; 
	}

	@Override
	public JSONObject create(JSONObject jsonObject) {
		int success = 0;

		PGobject object = new PGobject();
		try {
			object.setValue(jsonObject.toString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		object.setType("json");

		try {
			connect.setAutoCommit(false);

			String sql = "INSERT INTO history_alerts (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			success = statement.executeUpdate();
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject alertCreated = new JSONObject();
		
		if (success > 0) {
			alertCreated.put("successCreate", "true");
		} else {
			alertCreated.put("successCreate", "false");
		}
		
		
		return alertCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int alertId = jsonObject.getInt("id"); 

		
		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM history_alerts where (data -> 'id')::json::text = '"
					+ alertId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject alertDeleted = new JSONObject();
		
		if (success > 0) {
			alertDeleted.put("successDelete", "true");
		} else {
			alertDeleted.put("successDelete", "false");
		}
	
		return alertDeleted;
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
