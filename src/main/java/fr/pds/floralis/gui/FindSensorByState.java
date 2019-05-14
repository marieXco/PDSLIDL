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

public class FindSensorByState {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindSensorByState(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findByState(Boolean state) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsStateList;
		
		JSONObject sensorState = new JSONObject();
		sensorState.put("state", state);
		
		Request request = new Request();
		request.setType("FINDBYSTATE");
		request.setEntity("SENSOR");
		request.setFields(sensorState);
		
		ConnectionClient ccSensorFindByState= new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindByState.run();
		
		Sensor[] sensorsStateTab =  objectMapper.readValue(ccSensorFindByState.getResponse(), Sensor[].class);
		sensorsStateList = Arrays.asList(sensorsStateTab);
		
		return sensorsStateList;
	}
}

