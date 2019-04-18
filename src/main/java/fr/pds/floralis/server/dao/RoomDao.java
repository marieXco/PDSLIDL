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

public class RoomDao implements DAO<Room> {
	Connection connect = null;
	
	public RoomDao(Connection connect) throws ClassNotFoundException, SQLException {
		this.connect = connect;
	}

	@Override
	public boolean create(Room roomToCreate) {
		JSONObject jsonObject = roomToCreate.toJSON();
		// retourner un int pour évaluer le succès 
		int success = 0;
		
		// création d'un objet pour PostGresSQL de type JSON
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
			
			//executeupdate compte le nombre le lignes affectées
			// si 0 alors a n'a pas marché
			success = statement.executeUpdate(); 
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if(success > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public boolean delete(int id) {
		
		int success = 0;

		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM rooms where (data -> 'id')::json::text = '" + id + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		if(success > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public boolean update(int id, Room roomToUpdate) {
		int success = 0;
		
		JSONObject jsonObject = roomToUpdate.toJSON();
		
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
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public Room find(int id) {
		ObjectMapper mapper = new ObjectMapper();
		Room room = new Room(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM rooms where (data -> 'id')::json::text = '" + id + "'::json::text;" );

			while (rs.next()) {
				room = mapper.readValue(rs.getObject(1).toString(), Room.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return room;
	}

	@Override
	public List<Room> findAll() { 
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
		
		return rooms;
	}
	

}
