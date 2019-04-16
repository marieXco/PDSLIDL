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

import fr.pds.floralis.commons.bean.entity.Room;

public class RoomDao extends DAO<Room> {
	Connection connect = null;
	
	public RoomDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public JSONObject create(JSONObject jsonObject) {
		// retourner un int pour évaluer le succès 
		int success = 0;
		
		// création d'un objet de type PostGresSQL
		PGobject object = new PGobject();
		try {
			object.setValue(jsonObject.toString());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		object.setType("json");
		
		try {
			connect.setAutoCommit(false);

			String sql = "INSERT INTO rooms (data) VALUES (?);";

			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			success = statement.executeUpdate(); 
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		JSONObject roomCreated = new JSONObject();
		
		if (success > 0) {
			roomCreated.put("successCreate", "true");
		} else {
			roomCreated.put("successCreate", "false");
		}
		
		return roomCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		
		int success = 0;
		int roomId = jsonObject.getInt("id"); 

		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM rooms where (data -> 'id')::json::text = '" + roomId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject roomDeleted = new JSONObject();
		
		if (success > 0) {
			roomDeleted.put("successDelete", "true");
		} else {
			roomDeleted.put("successDelete", "false");
		}
		
		return roomDeleted;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		
		int roomId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE rooms SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + roomId + "'::json::text;";

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

		JSONObject roomUpdated = new JSONObject();
		
		if (success > 0) {
			roomUpdated.put("successUpdate", "true");
		} else {
			roomUpdated.put("successUpdate", "false");
		}
		
		return roomUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Room room = new Room(0, null);

		int locationId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM rooms where (data -> 'id')::json::text = '" + locationId + "'::json::text;" );

			while (rs.next()) {
				room = mapper.readValue(rs.getObject(1).toString(), Room.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		JSONObject roomFound = new JSONObject();
		roomFound.put("roomFound", room.toString());
		return roomFound;
	}

	@Override
	public JSONObject findAll() { 
		ObjectMapper mapper = new ObjectMapper();
		List<Room> rooms = new ArrayList<Room>();
		Room room = new Room(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM rooms;");

			while (rs.next()) {
				room = mapper.readValue(rs.getObject(1).toString(), Room.class);
				rooms.add(room);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	

		JSONObject roomList = new JSONObject();
		roomList.put("roomList", rooms.toString());
		
		return roomList;
	}
	

}
