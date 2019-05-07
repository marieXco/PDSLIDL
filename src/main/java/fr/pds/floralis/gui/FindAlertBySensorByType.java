package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAlertBySensorByType {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAlertBySensorByType(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Alert> findByType(Boolean refresh, String type) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorList;
		
		JSONObject sensorToType = new JSONObject();
		sensorToType.put("type", type);
		
		Request request = new Request();
		request.setType("FINDBYTYPE");
		request.setEntity("SENSOR");
		request.setFields(sensorToType);
		
		ConnectionClient ccSensorFindByType= new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindByType.run();
		
		Sensor[] sensorsConfigTab =  objectMapper.readValue(ccSensorFindByType.getResponse(), Sensor[].class);
		sensorList = Arrays.asList(sensorsConfigTab);
		
		
		objectMapper = new ObjectMapper();
		List<Alert> alertList;
		
		Request request1 = new Request();
		request1.setType("FINDALL");
		request1.setEntity("HISTORY_ALERTS");
		request1.setFields(new JSONObject());
		
		ConnectionClient ccAlertFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccAlertFindAll.run();
		
		Alert[] alertFoundTab =  objectMapper.readValue(ccAlertFindAll.getResponse(), Alert[].class);
		alertList = Arrays.asList(alertFoundTab);
		
		List<Alert> alertResult = null;
		
		for (Alert alert :alertList) {
			for (Sensor sensor: sensorList ) {

			if(!sensor.getAlert().equals(alert.getId())) {
				alertList.remove(alert);
			}
			}
		}
		if(refresh) { 
			Thread.sleep(6000);
			findByType(true, type);
		}
		
		return alertResult;
	}

}