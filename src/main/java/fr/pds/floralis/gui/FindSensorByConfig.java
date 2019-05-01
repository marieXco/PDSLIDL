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

public class FindSensorByConfig {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindSensorByConfig(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findByConfig(Boolean refresh, Boolean config) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsConfigList;
		
		JSONObject sensorToConfig = new JSONObject();
		sensorToConfig.put("configure", config);
		
		Request request = new Request();
		request.setType("FINDBYCONFIG");
		request.setEntity("SENSOR");
		request.setFields(sensorToConfig);
		
		ConnectionClient ccSensorFindByConfig= new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindByConfig.run();
		
		Sensor[] sensorsConfigTab =  objectMapper.readValue(ccSensorFindByConfig.getResponse(), Sensor[].class);
		sensorsConfigList = Arrays.asList(sensorsConfigTab);
		
		if(refresh) { 
			Thread.sleep(6000);
			findByConfig(true, config);
		}
		
		return sensorsConfigList;
	}
}
