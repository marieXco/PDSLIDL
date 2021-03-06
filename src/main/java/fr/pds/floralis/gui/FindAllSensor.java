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

public class FindAllSensor  {
	
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllSensor(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findAll() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("SENSOR");
		request.setFields(new JSONObject());
		
		ConnectionClient ccSensorFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindAll.run();
		
		Sensor[] sensorsFoundTab =  objectMapper.readValue(ccSensorFindAll.getResponse(), Sensor[].class);
		sensorsList = Arrays.asList(sensorsFoundTab);
		
		return sensorsList;
	}

}
