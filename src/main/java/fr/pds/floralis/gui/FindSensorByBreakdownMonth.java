package fr.pds.floralis.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

public class FindSensorByBreakdownMonth {
	
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindSensorByBreakdownMonth(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	/*public static List<Sensor> findByMonthYear(Boolean refresh, Date date) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorList;
		
		Request request = new Request();
		request.setType("FINDBYBREAKDOWN");
		request.setEntity("SENSOR");
		request.setFields(new JSONObject());
		
		ConnectionClient ccSensorFindSBM = new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindSBM.run();
		
		Sensor[] sensorFoundTab =  objectMapper.readValue(ccSensorFindSBM.getResponse(),Sensor[].class);
		sensorList = Arrays.asList(sensorFoundTab);
		
		for (Sensor sensor :sensorList) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM");
	        sdf.format(date);
	        sdf.format(sensor.getDate());
	        System.out.println(date);
	        System.out.println(alert);

			if(alert.equals(date)) {
				alertList.remove(alert);
			}
		}
		
		if(refresh) { 
			Thread.sleep(6000);
			findByMonthYear(true, date);
		}
		
		return alertList;
	} */
	

}
