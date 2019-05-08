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

public class FindSensorByBreakdown {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindSensorByBreakdown() {
		super();
	}

	public static List<Sensor> findByBreakdown(Boolean refresh, Boolean breakdown) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Sensor> sensorsBreakdownList;
		
		JSONObject sensorsBreakdown = new JSONObject();
		sensorsBreakdown.put("breakdown", breakdown);
		
		Request request = new Request();
		request.setType("FINDBYBREAKDOWN");
		request.setEntity("SENSOR");
		request.setFields(sensorsBreakdown);
		
		ConnectionClient ccSensorFindByBreakdown= new ConnectionClient(request.toJSON().toString());
		ccSensorFindByBreakdown.run();
		
		Sensor[] sensorsBreakdownTab =  objectMapper.readValue(ccSensorFindByBreakdown.getResponse(), Sensor[].class);
		sensorsBreakdownList = Arrays.asList(sensorsBreakdownTab);
		
		if(refresh) { 
			Thread.sleep(6000);
			findByBreakdown(true, breakdown);
		}
		
		return sensorsBreakdownList;
	}
}
