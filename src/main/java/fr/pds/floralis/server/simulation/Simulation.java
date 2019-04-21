package fr.pds.floralis.server.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class Simulation {


	static ObjectMapper objectMapper = new ObjectMapper();

	public static void simulationTest() throws JsonParseException, JsonMappingException, JSONException, IOException {
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("SENSOR");
		request.setFields(new JSONObject());
		
		ConnectionClient cc = new ConnectionClient("127.0.0.1", 2412, request.toString());
		cc.run();
		
		// TODO : instead of all sensors, only on the sensor on the configuration
		
		String response = cc.getResponse();
		JSONObject sensorsFound = new JSONObject();
		sensorsFound.put("sensorsFound", response);
		System.out.println(sensorsFound.get("sensorsFound"));
		
		Sensor[] sensorsFoundTab =  objectMapper.readValue(sensorsFound.get("sensorsFound").toString(), Sensor[].class);
		List<Sensor> sensorsList = Arrays.asList(sensorsFoundTab);
		
		List<Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>();
		
		PropertiesReader properties = new PropertiesReader();
		entryList = properties.getPropValues();
		int id = Integer.parseInt(entryList.get(0).getValue());
		
		
		HashMap<String, Integer> sensorsCache = new HashMap<String, Integer>();
		
		for (Sensor sensor : sensorsList) { 
			// Real sensor, sensor gotten thanks to the find by id from the id of the configuration
			if(sensor.getState()) {
				System.out.println("sensor on " + sensor.getId());
			} else {
				System.out.println("sensor off " + sensor.getId());
			}
			
			// TODO : not 10 but the value that we got from the config file
			if(Integer.parseInt(sensor.getMax()) < 10) {
				sensorsCache.put("ALERTHIGHER", sensor.getId());
				System.out.println("Type alert : HIGHERMAX " + sensor.getId());
				// TODO : put it in the cache as an alert type alert : HIGHERMAX
			}
			else if(Integer.parseInt(sensor.getMin()) > 10) {
				sensorsCache.put("ALERTLOWER", sensor.getId());
				System.out.println("Type alert : BESIDEMIN " + sensor.getId());
				// TODO : put it in the cache as an alert, type alert : BESIDEMIN
			}
			else {
				sensorsCache.put("NOALERT", sensor.getId());
				System.out.println("No alert");
				// TODO : in the cache but no alert
			}
		}
		
		
		
	}

	public static void main (String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException {
		Simulation.simulationTest();
		
		
	}
}
