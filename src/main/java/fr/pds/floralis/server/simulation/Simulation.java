package fr.pds.floralis.server.simulation;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.TypeSensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class Simulation {

	static ObjectMapper objectMapper = new ObjectMapper();
	private Sensor sensorFound = new Sensor();
	private String periodOfDay = "";	
	private int propertiesId = 0;
	private int sensitivity = 0;
	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);
	Logger logger = Logger.getLogger("Logger");

	@SuppressWarnings("deprecation")
	public void simulationTest() throws IOException, InterruptedException {

		try {
			FileHandler fh=new FileHandler("%hsimulationLogger.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



		/**
		 * Taking care of the warning levels depending of the period of day 
		 * From 09:00:00 to 19:59:00 --> Daytime
		 */
		refreshPeriodOfDay();

		/**
		 * We get the .properties properties
		 * We stock the id and the type in two String and remove it from the propertiesList
		 */


		/**
		 * With the id from the properties, we find the id; we refresh it so we get the last infos from the sensor
		 */
		refreshSensorInfos();


		for(int i = 0; i < 2; i++) { 
			System.out.println(" IIIII -->" + i);
			String propertiesType = "";
			PropertiesReader properties = new PropertiesReader();
			ArrayList<Entry<String, String>>[] propertiesList = properties.getPropValues();

			HashMap<String, Integer> sensorsCache = new HashMap<String, Integer>();
			
			System.out.println(propertiesList[i].get(0).getValue() + "toto");
			
			propertiesId = Integer.parseInt((propertiesList[i].get(0)).getValue());
			System.out.println(propertiesId + "idididid");
			propertiesType = propertiesList[i].get(1).getValue();

			propertiesList[i].remove(0);
			propertiesList[i].remove(0);

			JSONObject requestSensitivities = new JSONObject();
			requestSensitivities.put("type", propertiesType.toUpperCase());

			Request forthRequest = new Request();
			forthRequest.setType("FINDSENSITIVITY");
			forthRequest.setEntity("TYPESENSOR");
			forthRequest.setFields(requestSensitivities);

			ConnectionClient ccRequestSensitivities = new ConnectionClient("127.0.0.1", 2412, forthRequest.toJSON().toString());
			ccRequestSensitivities.run();

			String response = ccRequestSensitivities.getResponse();
			TypeSensor typeFound = objectMapper.readValue(response.toString(), TypeSensor.class);

			if (periodOfDay.equals("DAYTIME")) {
				sensitivity = typeFound.getDaySensitivity();
				logger.info("We're in daytime : sensitivity of daytime --> " + sensitivity + " seconds");
			} else {
				sensitivity = typeFound.getNightSensitivity();
				logger.info("We're in nighttime : sensitivity of nighttime --> " + sensitivity + " seconds");
			}



			Thread.sleep(4000);
			// TODO : possible alert again when the value changes but still too high ? 
			// TODO : continuer a tester si il est éteint et qu'on reçoit des messages 
			
			
			
			
			if(sensorFound.getState() != null) {
				logger.info("Sensor with the id "+ sensorFound.getId() + " is on\nThe sensor will be put in alert after "+ sensitivity + " of seconds");


				if(!sensorFound.getState()) {
					logger.info("Sensor with the id "+ sensorFound.getId() + " is off, but we get messages");
					// TODO : to test
					return;
				}

				if(sensorFound.getState() && propertiesList[i].get(0).getValue() == null) {
					int breakdownTrigger = 10;
					int waitingToConfirmBreakdown = 1;

					while (waitingToConfirmBreakdown <= breakdownTrigger) {
						logger.warning("The sensors are on but we get no messages, possible breakdown for " + waitingToConfirmBreakdown + " seconds");
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
					while(!propertiesList[i].isEmpty() && sensorFound.getState()) {
						logger.info("Sensor with the id "+ sensorFound.getId() + " is on and we get messages");
						int messageDuration = Integer.parseInt(((Entry<String, String>) propertiesList[i].get(propertiesList[i].size() - 1)).getKey());
						int messageValue = Integer.parseInt(((Entry<String, String>) propertiesList[i].get(propertiesList[i].size() - 1)).getValue());
						int realTimeValue = 1;
						Boolean alertCreated = false;

						if(Integer.parseInt(sensorFound.getMax()) < messageValue || Integer.parseInt(sensorFound.getMin()) > messageValue) {
							alertCreated = false;
							while(realTimeValue <= messageDuration && (Integer.parseInt(sensorFound.getMax()) < messageValue || Integer.parseInt(sensorFound.getMin()) > messageValue) && sensorFound.getState()) {

								while(realTimeValue < sensitivity) {
									if(sensorsCache.containsKey("POSSIBLEALERT")) {
										sensorsCache.remove("POSSIBLEALERT");
									}

									sensorsCache.put("POSSIBLEALERT", realTimeValue);

									if(realTimeValue % 5 == 0 || realTimeValue == 1) {
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

							if(alertCreated == false) {
								Calendar dateNow = Calendar.getInstance();
								Date today = new Date(dateNow.get(Calendar.YEAR) - 1900, dateNow.get(Calendar.MONTH), dateNow.get(Calendar.DAY_OF_MONTH));
								Time endOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY), dateNow.get(Calendar.MINUTE), dateNow.get(Calendar.SECOND) - 1);

								Time beginningOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY), dateNow.get(Calendar.MINUTE), dateNow.get(Calendar.SECOND));

								if(messageDuration > 60) {
									beginningOfAlert.setMinutes(dateNow.get(Calendar.MINUTE) - messageDuration / 60);
									beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration % 60);
								} else {
									beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration);
								}

								Alert alerte = new Alert(2, sensorFound.getId(),beginningOfAlert, endOfAlert , today);

								Request fifthRequest = new Request();
								fifthRequest.setType("CREATE");
								fifthRequest.setEntity("HISTORY_ALERTS");
								fifthRequest.setFields(alerte.toJSON());

								ConnectionClient ccSensorInAlert = new ConnectionClient("127.0.0.1", 2412, fifthRequest.toJSON().toString());
								ccSensorInAlert.run();
								alertCreated = true;
							}

						}

						else {

							if(sensorsCache.containsKey("NOALERT")) {
								sensorsCache.remove("NOALERT");
							}

							while(realTimeValue <= messageDuration && sensorFound.getState()) {


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


									ConnectionClient ccUpdateSensor = new ConnectionClient("127.0.0.1", 2412, thridRequest.toJSON().toString());
									ccUpdateSensor.run();

								}

								realTimeValue++;
								Thread.sleep(1000);

							}

						}

						if(!propertiesList[i].isEmpty() && sensorFound.getState()) {
							propertiesList[i].remove(propertiesList[i].size() - 1);
						} 

					}
				}
			}
		}
		System.exit(0);
	}





	//System.out.println("Cache at the end of the simulation : " + sensorsCache.toString());





	public Sensor refreshSensorInfos() {
		final Runnable refresh = new Runnable() {

			public void run() { 
				System.out.println("ID -->" + propertiesId);
				JSONObject sensorId = new JSONObject();
				sensorId.put("id", getPropertiesId());

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
					
				}
			}

		};

		final ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresh, 1 , 3, SECONDS);

		scheduler.schedule(
				new Runnable() {
					public void run() { 
						refreshHandle.cancel(true); 
					}
				}, 360, SECONDS
				);

		return sensorFound;
	}

	public String refreshPeriodOfDay() {
		final Runnable refresh = new Runnable() {

			public void run() { 
				Calendar night = new GregorianCalendar();	
				night.set(Calendar.HOUR_OF_DAY, 20);
				night.set(Calendar.MINUTE, 00);
				night.set(Calendar.SECOND, 00);

				Calendar day = new GregorianCalendar();	
				day.set(Calendar.HOUR_OF_DAY, 8);
				day.set(Calendar.MINUTE, 59);
				day.set(Calendar.SECOND, 59);

				Calendar now = Calendar.getInstance();
				if(now.before(night) && now.after(day)) {
					periodOfDay = "DAYTIME";
				} else {
					periodOfDay = "NIGHTTIME";
				}
			}

		};

		final ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresh, 0 , 30, SECONDS);

		scheduler.schedule(
				new Runnable() {
					public void run() { 
						refreshHandle.cancel(true); 
					}
				}, 360, SECONDS
				);

		return periodOfDay;
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

	/**
	 * @return the propertiesId
	 */
	public int getPropertiesId() {
		return propertiesId;
	}

	/**
	 * @param propertiesId the propertiesId to set
	 */
	public void setPropertiesId(int propertiesId) {
		this.propertiesId = propertiesId;
	}

	public static void main (String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		Simulation simu = new Simulation() ;
		simu.simulationTest();

		//		PropertiesReader properties = new PropertiesReader();
		//		ArrayList<Entry<String, String>>[] propertiesList = properties.getPropValues();
		//		System.out.println(propertiesList[0].toString());
	}
}
