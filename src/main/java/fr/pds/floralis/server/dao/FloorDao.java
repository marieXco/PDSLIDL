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

import fr.pds.floralis.commons.bean.entity.Floor;

public class FloorDao extends DAO<Floor> {
	Connection connect = null;
	
	public FloorDao(Connection connect) throws ClassNotFoundException, SQLException {
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
		
		// Faire notre insertion 
		try {
			connect.setAutoCommit(false);

			//requete 
			String sql = "INSERT INTO floors (data) VALUES (?);";

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
		JSONObject floorCreated = new JSONObject();
		floorCreated.put("successCreate", success);
		System.out.println(floorCreated.toString());
		return floorCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int floorId = jsonObject.getInt("id"); //traduction de mon id en int 

		//la suppression de ma ligne, fonctionne de la meme manière que pour le create
		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM floors where (data -> 'id')::json::text = '" + floorId + "'::json::text;";

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
		JSONObject floorDeleted = new JSONObject();
		floorDeleted.put("successDelete", success);
		System.out.println(floorDeleted.toString());
		return floorDeleted;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;
		
		int floorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE floors SET data = '" + jsonObject + "' WHERE (data -> 'id')::json::text = '" + floorId + "'::json::text;";

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

		JSONObject floorUpdated = new JSONObject();
		floorUpdated.put("successUpdate", success);
		System.out.println(floorUpdated.toString());
		return floorUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Floor floor = new Floor(0, null);

		int floorId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT data FROM floors where (data -> 'id')::json::text = '" + floorId + "'::json::text;" );

			// traitement effectué tnat qu'il y a des lignes
			while (rs.next()) {
				//Poser questions sur ça 
				floor = mapper.readValue(rs.getObject(1).toString(), Floor.class);
			}			

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		//on vérifie qu'on ai bien trouver ce que l'on recherche
		 if (floor.getName() != null) { // a vérifier je suis pas sur 
			System.out.println("find success");
		}
		
		JSONObject floorFound = new JSONObject();
		floorFound.put("floorFound", floor.toString());
		return floorFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Floor> floors = new ArrayList<Floor>();
		Floor floor = new Floor(0, null);

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM floors;");


			while (rs.next()) {
				floor = mapper.readValue(rs.getObject(1).toString(), Floor.class);
				floors.add(floor);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}	
		// on regarde si mon tableau est vide ou pas pr voir si ça a fonctionné 
		if (floors != null) {
			System.out.println("findAll success");
		}

		JSONObject floorList = new JSONObject();
		floorList.put("floorList", floors.toString());
		
		return floorList;
	}
	

}
