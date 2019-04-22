package fr.pds.floralis.server.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

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

	public static void simulationTest() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		Logger logger = Logger.getLogger("test");
		String periodOfDay = "";

		Calendar now = Calendar.getInstance();

		Calendar night = new GregorianCalendar();	
		night.set(Calendar.HOUR_OF_DAY, 19);
		night.set(Calendar.MINUTE, 59);
		night.set(Calendar.SECOND, 59);

		Calendar day = new GregorianCalendar();	
		day.set(Calendar.HOUR_OF_DAY, 9);
		day.set(Calendar.MINUTE, 00);
		day.set(Calendar.SECOND, 00);

		// TODO : get the warning level depending on time and refresh if it changes during simulation

		if(now.before(night) || now.after(day)) {
			periodOfDay = "DAYTIME";
			logger.info("We're in daytime : warning levels of daytime");
		} else {
			periodOfDay = "NIGHTTIME";
			logger.info("We're in nighttime : warning levels of nighttime");
		}


		List<Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>();

		PropertiesReader properties = new PropertiesReader();
		entryList = properties.getPropValues();
		int id = Integer.parseInt(entryList.get(0).getValue());

		entryList.remove(0);
		entryList.remove(0);

		System.out.println(entryList.toString());

		JSONObject sensorId = new JSONObject();
		sensorId.put("id", id);

		Request request = new Request();
		request.setType("FINDBYID");
		request.setEntity("SENSOR");
		request.setFields(sensorId);

		ConnectionClient cc = new ConnectionClient("127.0.0.1", 2412, request.toJSON().toString());
		cc.run();


		String response = cc.getResponse();
		JSONObject sensorFound = new JSONObject();
		sensorFound.put("sensorFound", response);
		System.out.println(sensorFound.get("sensorFound"));

		Sensor sensorGotten =  objectMapper.readValue(sensorFound.get("sensorFound").toString(), Sensor.class);
		List<Sensor> sensorsList = new ArrayList<Sensor>();
		sensorsList.add(sensorGotten); 

		HashMap<String, Integer> sensorsCache = new HashMap<String, Integer>();

		System.out.println(Integer.parseInt(sensorGotten.getMin()));

		int duration = Integer.parseInt(entryList.get(entryList.size() - 1).getKey());
		int value = Integer.parseInt(entryList.get(entryList.size() - 1).getValue());
		int realTimeValue = 1;

		// TODO : instead of 10 --> get the sensor time before alert thanks to the day/nightTime
		// TODO : possible alert again when the value changes but still too high ? 
		// TODO : stop when we find an alert ? 
		// TODO : update only when the state before is not the new one

		if(sensorGotten.getState()) {		
			logger.info("Sensor with the id "+ sensorGotten.getId() + " is on");

			while(!entryList.isEmpty()) {
				duration = Integer.parseInt(entryList.get(entryList.size() - 1).getKey());
				value = Integer.parseInt(entryList.get(entryList.size() - 1).getValue());
				realTimeValue = 1;

				if(Integer.parseInt(sensorGotten.getMax()) < value || Integer.parseInt(sensorGotten.getMin()) > value) {
					while(realTimeValue <= duration) {
						while(realTimeValue < 10) {
							if(sensorsCache.containsKey("POSSIBLEALERT")) {
								sensorsCache.remove("POSSIBLEALERT");
							}

							sensorsCache.put("POSSIBLEALERT", realTimeValue);
							logger.warning("Type alert : POSSIBLEALERT for the sensor : " + sensorGotten.getId()  + " for " + 
									realTimeValue + " seconds with the value " + value);
							Thread.sleep(1000);
							realTimeValue++;
						}



						sensorsCache.remove("POSSIBLEALERT");

						if(Integer.parseInt(sensorGotten.getMax()) < value) {
							if(sensorsCache.containsKey("ALERTHIGHER")) {
								sensorsCache.remove("ALERTHIGHER");
							}

							sensorsCache.put("ALERTHIGHER", realTimeValue);
							logger.warning("Type alert : HIGHERMAX for the sensor : " + sensorGotten.getId()  + " for " + 
									realTimeValue + " seconds with the value " + value);
							realTimeValue++;
							Thread.sleep(1000);
							
							JSONObject newState = new JSONObject();
							sensorGotten.setAlert(true);
							newState.put("id", sensorGotten.getId());
							newState.put("sensorToUpdate", sensorGotten.toJSON());
							
							
							Request secondRequest = new Request();
							secondRequest.setType("UPDATE");
							secondRequest.setEntity("SENSOR");
							secondRequest.setFields(newState);
							

							ConnectionClient ccr = new ConnectionClient("127.0.0.1", 2412, secondRequest.toJSON().toString());
							ccr.run();
							

						} else {

							if(sensorsCache.containsKey("ALERTLOWER")) {
								sensorsCache.remove("ALERTLOWER");
							}

							sensorsCache.put("ALERTLOWER", realTimeValue);
							logger.info("Type alert : BESIDEMIN for the sensor : " + sensorGotten.getId() + " for " + 
									realTimeValue + " seconds with the value " + value);
							realTimeValue++;
							Thread.sleep(1000);
						}
					}
				}

				else {

					if(sensorsCache.containsKey("NOALERT")) {
						sensorsCache.remove("NOALERT");
					}

					while(realTimeValue <= duration) {
						sensorsCache.put("NOALERT", realTimeValue);
						logger.info("No alert for the sensor: " + sensorGotten.getId() + " for " + 
								realTimeValue + " seconds with the value " + value);
						realTimeValue++;
						Thread.sleep(1000);
					}

				}

				entryList.remove(entryList.size() - 1);

			} 
		}
		else {
			logger.info("Sensor with the id "+ sensorGotten.getId() + " is off, no operations done");
		}



		System.out.println(sensorsCache.toString());

	}

	public static void main (String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		Simulation.simulationTest();
	}
}
