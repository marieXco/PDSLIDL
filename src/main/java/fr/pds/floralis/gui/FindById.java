package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindById {

	private static String host;
	private static int port;

	public FindById(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static Sensor findById(int id) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		JSONObject sensorIdFindById = new JSONObject();
		sensorIdFindById.put("id", id);

		Request request = new Request();
		request.setType("FINDBYID");
		request.setEntity("SENSOR");
		request.setFields(sensorIdFindById);

		ConnectionClient ccSensorFindById = new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindById.run();

		String retourSensorFindById = ccSensorFindById.getResponse();
		JSONObject sensorFoundJson = new JSONObject();
		sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

		ObjectMapper objectMapper = new ObjectMapper();
		Sensor sensorFound =  objectMapper.readValue(sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);
		
		return sensorFound;
	}
}
