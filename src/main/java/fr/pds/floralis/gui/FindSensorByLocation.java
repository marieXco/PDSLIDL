package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

/*
 * Récupérer les capteurs en fonction des localisations
 * Va être utile après un clique sur une partie commune pour rafraîchir la liste des capteurs 
 */

public class FindSensorByLocation {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;
	private static List<Sensor> sensorsLocationList;

	public FindSensorByLocation(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findByLocation(Boolean refresh,
			int idLocation) throws JsonParseException,
			JsonMappingException, JSONException, IOException,
			InterruptedException {

		sensorsLocationList = null; 
		objectMapper = new ObjectMapper();
		

		JSONObject sensorsLocation = new JSONObject();
		sensorsLocation.put("idLocation", idLocation);

		Request request = new Request();
		request.setType("FINDBYLOCATION");
		request.setEntity("SENSOR");
		request.setFields(sensorsLocation);

		ConnectionClient ccSensorFindByLocation = new ConnectionClient(request
				.toJSON().toString());
		ccSensorFindByLocation.run();

		System.out.println(ccSensorFindByLocation.getResponse());
		Sensor[] sensorsLocationTab = objectMapper.readValue(
				ccSensorFindByLocation.getResponse(), Sensor[].class);
		sensorsLocationList = Arrays.asList(sensorsLocationTab);

		if (refresh) {
			Thread.sleep(6000);
			findByLocation(true, idLocation);
		}

		return sensorsLocationList;
	}

}
