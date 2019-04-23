package fr.pds.floralis.server.simulation;

import java.io.IOException;
import static java.util.concurrent.TimeUnit.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
	private Sensor sensorFound = new Sensor();
	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);

	public void simulationTest() throws IOException, InterruptedException {
		Logger logger = Logger.getLogger("Simulaiton Logger");

		/**
		 * Taking care of the warning levels depending of the period of day 
		 * From 09:00:00 to 19:59:00 --> Daytime
		 */
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

		System.out.println(periodOfDay);

		/**
		 * We get the .properties properties
		 * We stock the id and the type in two String and remove it from the propertiesList
		 */
		int propertiesId = 0;
		PropertiesReader properties = new PropertiesReader();
		List<Entry<String, String>> propertiesList = properties.getPropValues();

		if(propertiesList == null) {
			logger.warning("We get no messages at all, something went wrong");
			return;
		} else {
			propertiesId = Integer.parseInt(propertiesList.get(0).getValue());
			String propertiesType = propertiesList.get(1).getValue();

			propertiesList.remove(0);
			propertiesList.remove(0);

			System.out.println("Type : " + propertiesType + "\nId : " + propertiesId);
		}


		/**
		 * With the id from the properties, we find the id
		 */
		refreshSensorInfos();

		HashMap<String, Integer> sensorsCache = new HashMap<String, Integer>();

		System.out.println(getSensorFound().toJSON());

		// TODO : instead of 10 --> get the sensor time before alert thanks to the day/nightTime
		// TODO : possible alert again when the value changes but still too high ? 
		// TODO : stop when we find an alert ? 
		// TODO : update only when the state before is not the new one

		Thread.sleep(4000);
		if(getSensorFound().getState()) {
			logger.info("Sensor with the id "+ sensorFound.getId() + " is on");

			int breakdownTrigger = 10;
			int waitingToConfirmBreakdown = 1;

			if(propertiesList.isEmpty()) {
				while (waitingToConfirmBreakdown <= breakdownTrigger) {
					logger.warning("The sensor is on but we get no messages, possible breakdown for " + waitingToConfirmBreakdown + " seconds");
					Thread.sleep(1000);
					waitingToConfirmBreakdown++;
				}

				if(sensorFound.getBreakdown() == false) {
					JSONObject newStateOnBreakdown = new JSONObject();
					sensorFound.setBreakdown(true);
					newStateOnBreakdown.put("id", sensorFound.getId());
					newStateOnBreakdown.put("sensorToUpdate", sensorFound.toJSON());

					Request thirdRequest = new Request();
					thirdRequest.setType("UPDATE");
					thirdRequest.setEntity("SENSOR");
					thirdRequest.setFields(newStateOnBreakdown);

					ConnectionClient ccr = new ConnectionClient("127.0.0.1", 2412, thirdRequest.toJSON().toString());
					ccr.run();
				}
			}

			else {
				// TODO : vérifier qu'il est toujours allumé, test en changeant les valeurs en direct
				// TODO : changer le logger pour qu'il soit moins visible ainisi que dans le connectionclient
				while(!propertiesList.isEmpty()) {

					int messageDuration = Integer.parseInt(propertiesList.get(propertiesList.size() - 1).getKey());
					int messageValue = Integer.parseInt(propertiesList.get(propertiesList.size() - 1).getValue());
					int realTimeValue = 1;

					if(Integer.parseInt(sensorFound.getMax()) < messageValue || Integer.parseInt(sensorFound.getMin()) > messageValue) {
						
						while(realTimeValue <= messageDuration) {

							while(realTimeValue < 10) {
								if(sensorsCache.containsKey("POSSIBLEALERT")) {
									sensorsCache.remove("POSSIBLEALERT");
								}

								sensorsCache.put("POSSIBLEALERT", realTimeValue);

								if(realTimeValue % 5 == 0 || realTimeValue == 10  || realTimeValue == 1) {
									logger.warning("Type alert : POSSIBLEALERT for the sensor : " + sensorFound.getId()  + " for " + 
											realTimeValue + " seconds with the value " + messageValue);
								}

								Thread.sleep(1000);
								realTimeValue++;
							}

							sensorsCache.remove("POSSIBLEALERT");

							if(sensorsCache.containsKey("ALERT")) {
								sensorsCache.remove("ALERT");
							}

							sensorsCache.put("ALERT", realTimeValue);

							if(realTimeValue % 5 == 0 || realTimeValue == messageDuration || realTimeValue == 1) {
								logger.warning("Type alert : HIGHERMAX for the sensor : " + sensorFound.getId()  + " for " + 
										realTimeValue + " seconds with the value " + messageValue);
							}


							if(sensorFound.getAlert() == false) {
								JSONObject newStateOnAlert = new JSONObject();
								sensorFound.setAlert(true);
								newStateOnAlert.put("id", sensorFound.getId());
								newStateOnAlert.put("sensorToUpdate", sensorFound.toJSON());

								Request secondRequest = new Request();
								secondRequest.setType("UPDATE");
								secondRequest.setEntity("SENSOR");
								secondRequest.setFields(newStateOnAlert);

								ConnectionClient ccr = new ConnectionClient("127.0.0.1", 2412, secondRequest.toJSON().toString());
								ccr.run();
							}

							realTimeValue++;
							Thread.sleep(1000);

						}
					}

					else {

						if(sensorsCache.containsKey("NOALERT")) {
							sensorsCache.remove("NOALERT");
						}

						while(realTimeValue <= messageDuration) {


							sensorsCache.put("NOALERT", realTimeValue);
							if(realTimeValue % 5 == 0 || realTimeValue == messageDuration - 1 || realTimeValue == 1) {
								logger.info("No alert for the sensor: " + sensorFound.getId() + " for " + 
										realTimeValue + " seconds with the value " + messageValue);
							}

							if(sensorFound.getAlert()) {
								JSONObject newStateNoAlert = new JSONObject();
								sensorFound.setAlert(false);
								newStateNoAlert.put("id", sensorFound.getId());
								newStateNoAlert.put("sensorToUpdate", sensorFound.toJSON());

								Request thridRequest = new Request();
								thridRequest.setType("UPDATE");
								thridRequest.setEntity("SENSOR");
								thridRequest.setFields(newStateNoAlert);


								ConnectionClient ccrr = new ConnectionClient("127.0.0.1", 2412, thridRequest.toJSON().toString());
								ccrr.run();

							}

							realTimeValue++;
							Thread.sleep(1000);

						}

					}

					if(!propertiesList.isEmpty()) {
						propertiesList.remove(propertiesList.size() - 1);
					} 

				} 
			}
		}
		else {
			logger.info("Sensor with the id "+ sensorFound.getId() + " is off, but we get messages");
		}



		System.out.println("Cache at the end of the simulation : " + sensorsCache.toString());

	}

	public Sensor refreshSensorInfos() {
		final Runnable refresh = new Runnable() {

			public void run() { 
				JSONObject sensorId = new JSONObject();
				sensorId.put("id", 1);

				Request request = new Request();
				request.setType("FINDBYID");
				request.setEntity("SENSOR");
				request.setFields(sensorId);

				ConnectionClient cc = new ConnectionClient("127.0.0.1", 2412, request.toJSON().toString());
				cc.run();

				String response = cc.getResponse();
				try {
					sensorFound = objectMapper.readValue(response.toString(), Sensor.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		final ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresh, 1 , 3, SECONDS);

		scheduler.schedule(
				new Runnable() {
					public void run() { 
						refreshHandle.cancel(true); 
					}
				}, 60 * 60, SECONDS
				);

		return sensorFound;
	}

	/**
	 * @return the sensorFound
	 */
	public Sensor getSensorFound() {
		return sensorFound;
	}

	/**
	 * @param sensorFound the sensorFound to set
	 */
	public void setSensorFound(Sensor sensorFound) {
		this.sensorFound = sensorFound;
	}

	public static void main (String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		Simulation simu = new Simulation() ;
		simu.simulationTest();
	}
}
