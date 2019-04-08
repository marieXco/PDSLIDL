package fr.pds.floralis.server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Location;

public class LocationDao extends DAO<Location> {

	public LocationDao() throws ClassNotFoundException, SQLException {
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

			// requete
			String sql = "INSERT INTO location (data) VALUES (?);";

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

		// vérifier le fonctionnement
		if (success > 0) { // si on a plus d'une ligne exécuté
			System.out.println("create success");
		}

		// JSON
		JSONObject locationCreated = new JSONObject();
		locationCreated.put("successCreate", success);
		System.out.println(locationCreated.toString());
		return locationCreated;
	}

	@Override
	public JSONObject delete(JSONObject jsonObject) {
		int success = 0;
		int locationId = jsonObject.getInt("id"); // traduction de mon id en int

		// la suppression de ma ligne, fonctionne de la meme manière que pour le
		// create
		try {
			connect.setAutoCommit(false);

			String sql = "DELETE FROM location where (data -> 'id')::json::text = '"
					+ locationId + "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();
			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// on regarde si la requète fonctionne
		if (success > 0) {
			System.out.println("delete success");
		}
		// la partie JSON
		JSONObject locationDeleted = new JSONObject();
		locationDeleted.put("successDelete", success);
		System.out.println(locationDeleted.toString());
		return locationDeleted;
	}

	@Override
	public JSONObject update(JSONObject jsonObject) {
		int success = 0;

		int locationId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			String sql = "UPDATE location SET data = '" + jsonObject
					+ "' WHERE (data -> 'id')::json::text = '" + locationId
					+ "'::json::text;";

			PreparedStatement statement = connect.prepareStatement(sql);

			success = statement.executeUpdate();
			connect.commit();

			statement.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		if (success > 0) {
			System.out.println("update success");
		}

		JSONObject locationUpdated = new JSONObject();
		locationUpdated.put("successUpdate", success);
		System.out.println(locationUpdated.toString());
		return locationUpdated;
	}

	@Override
	public JSONObject find(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		Location location = new Location(0, null, 0, 0, 0);

		int locationId = jsonObject.getInt("id");

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT data FROM location where (data -> 'id')::json::text = '"
							+ locationId + "'::json::text;");

			// traitement effectué tnat qu'il y a des lignes
			while (rs.next()) {
				// Poser questions sur ça
				location = mapper.readValue(rs.getObject(1).toString(),
						Location.class);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// on vérifie qu'on ai bien trouver ce que l'on recherche
		if (location.getRoom() != 0) { // a vérifier je suis pas sur
			System.out.println("find success");
		}

		JSONObject locationFound = new JSONObject();
		locationFound.put("locationFound", location.toString());
		return locationFound;
	}

	@Override
	public JSONObject findAll() {
		ObjectMapper mapper = new ObjectMapper();
		List<Location> allLocation = new ArrayList<Location>();
		Location location = new Location();

		try {
			connect.setAutoCommit(false);
			Statement stmt = connect.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT data FROM location;");

			while (rs.next()) {
				location = mapper.readValue(rs.getObject(1).toString(),
						Location.class);
				allLocation.add(location);
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// on regarde si mon tableau est vide ou pas pr voir si ça a fonctionné
		if (allLocation != null) {
			System.out.println("findAll success");
		}

		JSONObject locationList = new JSONObject();
		locationList.put("locationList", allLocation.toString());

		return locationList;
	}

}
