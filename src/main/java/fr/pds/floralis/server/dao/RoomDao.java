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

	public RoomDao() throws ClassNotFoundException, SQLException {
		connect = super.connect;
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
		
		// Faire notre insertion 
		try {
			connect.setAutoCommit(false);

			//requete 
			String sql = "INSERT INTO rooms (data) VALUES (?);";

			// On utilise un PreparedStatement pour pouvoir précompilé 
			// avant d'ajouter la colonne 
			PreparedStatement statement = connect.prepareStatement(sql);

			statement.setObject(1, object);
			 // calcule le nombre de ligne exécuter
			success = statement.executeUpdate(); 
			connect.commit();
			statement.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		//vérifier le fonctionnement 
		if(success > 0) { //si on a plus d'une ligne exécuté 
			System.out.println("create success");
		}
		
		//JSON 
		JSONObject roomCreated = new JSONObject();
		roomCreated.put("successCreate", success);
		System.out.println(roomCreated.toString());
		return roomCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int roomId = jsonObject.getInt("id"); //traduction de mon id en int 

		//la suppression de ma ligne, fonctionne de la meme manière que pour le create
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
		// on regarde si la requète fonctionne 
		if(success > 0) {
			System.out.println("delete success");
		}
		// la partie JSON 
		JSONObject roomDeleted = new JSONObject();
		roomDeleted.put("successDelete", success);
		System.out.println(roomDeleted.toString());
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
		roomUpdated.put("successUpdate", success);
		System.out.println(roomUpdated.toString());
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

			// traitement effectué tnat qu'il y a des lignes
			while (rs.next()) {
				//Poser questions sur ça 
				room = mapper.readValue(rs.getObject(1).toString(), Room.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		//on vérifie qu'on ai bien trouver ce que l'on recherche
		 if (room.getTypeRoom() != null) { // a vérifier je suis pas sur 
			System.out.println("find success");
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
		// on regarde si mon tableau est vide ou pas pr voir si ça a fonctionné 
		if (rooms != null) {
			System.out.println("findAll success");
		}

		JSONObject roomList = new JSONObject();
		roomList.put("roomsList", rooms.toString());
		
		return roomList;
	}
	

}
