package fr.pds.floralis.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;

public class SensorDao extends DAO<Sensor> {
	Connection connect = null;
	
	public SensorDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	public JSONObject create(JSONObject jsonObject) {
		// int retourné pour savoir si il a fonctionne
		int success = 0;

		PGobject object1 = new PGobject();
		try {
			object1.setValue(jsonObject.toString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		object1.setType("json");

		try {
			connect.setAutoCommit(false);

			// Sql a éxécuter
			String sql = "INSERT INTO sensors (data) VALUES (?);";

			// On utilise un PreparedStatement pour pouvoir précompilé 
			// avant d'ajouter la colonne 
			PreparedStatement statement = connect.prepareStatement(sql);

			// On ajoute le jsonObject à la place du 1er point d'interrogation dans la string sql
			statement.setObject(1, object1);

			// On execute le tout et on commit
			success = statement.executeUpdate(); 
			connect.commit();

			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			System.out.println("create success");
		}

		JSONObject sensorCreated = new JSONObject();
		sensorCreated.put("successCreate", success);
		System.out.println(sensorCreated.toString());
		return sensorCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int sensorId = jsonObject.getInt("id");


		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			System.out.println();
			connect.commit();

			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			System.out.println("delete success");
		}
		
		JSONObject sensorDeleted = new JSONObject();
		sensorDeleted.put("successDelete", success);
		System.out.println(sensorDeleted.toString());
		return sensorDeleted;
	}


	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		
		int idSensor = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE sensors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + idSensor + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();


			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			System.out.println("update success");
		}

		JSONObject sensorUpdated = new JSONObject();
		sensorUpdated.put("successUpdate", success);
		System.out.println(sensorUpdated.toString());
		return sensorUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Sensor sensor = new Sensor();

		int sensorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);

			// Ici, vu qu'on ajoute aucune valeur dans notre BDD
			// On utilise un createStatement
			Statement stmt = connect.createStatement();

			// ResultSet est utilisé et contiendra tout ce que la BDD renvoie sous forme de lignes qui se suivent
			ResultSet rs = stmt.executeQuery( "SELECT data FROM sensors where (data -> 'id')::json::text = '" + sensorId + "'::json::text;" );

			// Ici, tant qu'il va trouver des lignes
			while (rs.next()) {
				// Il va ajouter au capteur l'object de la colonne numéro 1 dans la requête (colonne data)
				// l'objet sensor contiendra quelque chose du type { "caracteristics" : "toto", "id" : 1234..} 
				sensor = mapper.readValue(rs.getObject(1).toString(), Sensor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if (sensor.getBrand() != null) {
			System.out.println("find success");
		}
		
		JSONObject sensorFound = new JSONObject();
		sensorFound.put("sensorFound", sensor.toString());
		
		return sensorFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Sensor> sensors = new ArrayList<Sensor>();
		Sensor sensor = new Sensor();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM sensors;");


			while (rs.next()) {
				// Même principe de le find normal mais on ajoute chaque capteur à la liste de capteurs
				sensor = mapper.readValue(rs.getObject(1).toString(), Sensor.class);
				sensors.add(sensor);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	

		if (sensors != null) {
			System.out.println("findAll success");
		}

		JSONObject sensorsList = new JSONObject();
		sensorsList.put("sensorsList", sensors.toString());
		
		return sensorsList;
	}
}
