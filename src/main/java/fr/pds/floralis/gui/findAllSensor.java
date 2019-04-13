package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class findAllSensor  {
	
	private ObjectMapper objectMapper;
	private String host;
	private int port;

	public findAllSensor(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public List<Sensor> findAll(Boolean refresh) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsList;
		
		ConnectionClient ccSensorFindAll = new ConnectionClient(host, port, "SENSOR", "FINDALL", null);
		ccSensorFindAll.run();
		
		String retoursCcSensorFindAll = ccSensorFindAll.getResponse();
		JSONObject sensorsFound = new JSONObject();
		sensorsFound.put("sensorsFound", retoursCcSensorFindAll);
		System.out.println(sensorsFound.get("sensorsFound"));
		
		Sensor[] sensorsFoundTab =  objectMapper.readValue(sensorsFound.get("sensorsFound").toString(), Sensor[].class);
		sensorsList = Arrays.asList(sensorsFoundTab);
		
		if(refresh) {
			Thread.sleep(6000);
			findAll(true);
		}
		
		return sensorsList;
	}

}
