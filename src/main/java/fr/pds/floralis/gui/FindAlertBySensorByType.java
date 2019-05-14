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

	public FindAlertBySensorByType() {

	}

	public static int findByType(String type) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorList;
		
		JSONObject sensorToType = new JSONObject();
		sensorToType.put("type", type);
		
		
		// this the request, this request is sending  to the request Handler --> to Dao Sensor --> to server 
		Request request = new Request();
		request.setType("FINDBYTYPE");
		request.setEntity("SENSOR");
		request.setFields(sensorToType);
		
		
		// to make socket 
		ConnectionClient ccSensorFindByType= new ConnectionClient(request.toJSON().toString());
		ccSensorFindByType.run();
		
		Sensor[] sensorsConfigTab =  objectMapper.readValue(ccSensorFindByType.getResponse(), Sensor[].class);
		sensorList = Arrays.asList(sensorsConfigTab);
		
		
		objectMapper = new ObjectMapper();
		List<Alert> alertList;
		
		// this the request, this request is sending  to the request Handler --> to Dao Sensor --> to server 
		Request request1 = new Request();
		request1.setType("FINDALL");
		request1.setEntity("HISTORY_ALERTS");
		request1.setFields(new JSONObject());
		
		ConnectionClient ccAlertFindAll = new ConnectionClient(request1.toJSON().toString());
		ccAlertFindAll.run();
		
		Alert[] alertFoundTab =  objectMapper.readValue(ccAlertFindAll.getResponse(), Alert[].class);
		alertList = Arrays.asList(alertFoundTab);
		// an in integer which count 
		int alertResult = 0;
		
		// this loop is here to test the sensor Id which match with the sensor Id in alert 
		for (Alert alert :alertList) {
			for (Sensor sensor: sensorList ) {

			if(alert.getSensor() == sensor.getId()) {
				alertResult ++;
			}
			}
		}
		
		return alertResult;
	}

}
