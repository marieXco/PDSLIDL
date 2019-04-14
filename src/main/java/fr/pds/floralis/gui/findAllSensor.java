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

public class findAllSensor  {
	
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public findAllSensor(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Sensor> findAll(Boolean refresh) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsList;
		
		String[] emptyTab = new String[0];
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("SENSOR");
		request.setFields(emptyTab);
		request.setValues(emptyTab);
		
		ConnectionClient ccSensorFindAll = new ConnectionClient(host, port, request.toString());
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
	
	public static void main( String args[] ) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {		
		String host = "127.0.0.1";
		int port = 2412;
		findAllSensor fs = new findAllSensor(host, port);
		fs.findAll(false);

	}

}
