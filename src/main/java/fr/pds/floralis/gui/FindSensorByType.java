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

public class FindSensorByType {

	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindSensorByType(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findByType(String type) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsConfigList;
		
		JSONObject sensorToType = new JSONObject();
		sensorToType.put("type", type);
		
		Request request = new Request();
		request.setType("FINDBYTYPE");
		request.setEntity("SENSOR");
		request.setFields(sensorToType);
		
		ConnectionClient ccSensorFindByType= new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindByType.run();
		
		Sensor[] sensorsConfigTab =  objectMapper.readValue(ccSensorFindByType.getResponse(), Sensor[].class);
		sensorsConfigList = Arrays.asList(sensorsConfigTab);
		
		return sensorsConfigList;
	}

}
