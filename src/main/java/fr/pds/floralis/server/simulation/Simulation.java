package fr.pds.floralis.server.simulation;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Breakdown;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.TypeSensor;

public class Simulation {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	HashMap<String, Integer> sensorsCache;
	ArrayList<Entry<String, String>>[] propertiesTab;

	private Sensor sensorFound[];
	static Boolean[] threadState;
	static ScheduledFuture<?> refreshHandle[];

	private String periodOfDay = "";
	private int propertiesLength;
	static ObjectMapper objectMapper = new ObjectMapper();

	public Simulation(HashMap<String, Integer> sensorsCache, int propertiesLength) {
		this.sensorsCache = sensorsCache;
		this.propertiesLength = propertiesLength;
		this.sensorFound = new Sensor[propertiesLength];
		Simulation.refreshHandle = new ScheduledFuture<?>[propertiesLength];
		Simulation.threadState = new Boolean[propertiesLength];
	}

	@SuppressWarnings("deprecation")
	public void simulationTest(ArrayList<Entry<String, String>> propertiesList, Logger sensorLogger, Logger mainLogger,
			int sensorIndex) throws IOException, InterruptedException {

		/*
		 * ThreadState is an array made to know when 
		 * all the sensors finished their simulation
		 */
		threadState[sensorIndex] = false;

		/*
		 * simulationOn is a boolean made to know 
		 * if the simulation should keep going
		 */
		boolean simulationOn = true;

		/*
		 * Taking care of the warning levels depending of the period of day From
		 * 09:00:00 to 19:59:00 --> Daytime
		 */
		refreshPeriodOfDay(sensorIndex);

		/*
		 * We catch the exception that would be raised if theconfig.properties wasn't
		 * fulled properly : String has id / No messages
		 */
		try {
			Integer.parseInt((propertiesList.get(0)).getValue());
		} catch (java.lang.NumberFormatException e1) {
			sensorLogger.warning("The sensor id is not of integer type;\n Exit");
			refreshHandle[sensorIndex].cancel(false);
			 
			threadState[sensorIndex] = true;			
			simulationOn = false;
		}

		int propertiesId = Integer.parseInt((propertiesList.get(0)).getValue());
		propertiesList.remove(0);

		/*
		 * We get the sensor infos every 3 seconds thanks to the id
		 */
		refreshSensorInfos(propertiesId, sensorIndex);

		/*
		 * This sleep is made to wait for the findById from all the sensor We wait 4
		 * seconds for each sensor
		 */
		Thread.sleep(propertiesLength * 4000);

		/*
		 * We request the sensor sensitivity thanks to the type of the sensor
		 */
		JSONObject requestSensitivityByTypeJson = new JSONObject();
		requestSensitivityByTypeJson.put("type", sensorFound[sensorIndex].getType().toUpperCase());

		Request requestSensitivityByType = new Request();
		requestSensitivityByType.setType("FINDSENSITIVITY");
		requestSensitivityByType.setEntity("TYPESENSOR");
		requestSensitivityByType.setFields(requestSensitivityByTypeJson);

		ConnectionSimulation ccRequestSensitivity = new ConnectionSimulation(
				requestSensitivityByType.toJSON().toString());
		ccRequestSensitivity.run();

		String responseRequestSensitivity = ccRequestSensitivity.getResponse();
		TypeSensor typeFound = objectMapper.readValue(responseRequestSensitivity.toString(), TypeSensor.class);
		// End of requestSensitivities

		if (!sensorFound[sensorIndex].getConfigure()) {
			sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
					+ " doesn't have any warning limits;\nExiting for this sensor");
			refreshHandle[sensorIndex].cancel(false);
			 
			threadState[sensorIndex] = true;			
		}

		String sensorType;


		/*
		 * If the sensor is a light sensor or a presence sensor then it'll send
		 * us only 0 or 1 has value for one duration (see the config.properties files)
		 * However, if it's a light or a presence sensor, the warning levels min/max
		 * that are contained for the sensor refers to the max levels dependings on the
		 * period of day 
		 * Min --> Maximum for daytime 
		 * Max --> Maximum for nighttime
		 * 
		 * It's made like this so the user can change the time before an alert when he
		 * configures a light or a presence sensor 
		 */
		if (sensorFound[sensorIndex].getType().toUpperCase().equals("LIGHT")
				|| sensorFound[sensorIndex].getType().toUpperCase().equals("PRESENCE")) {
			sensorType = "BOOLEAN";
		} else if (sensorFound[sensorIndex].getType().toUpperCase().equals("FIRE")){
			sensorType = "FIRE";
		} else {
			sensorType = "VALUE";
		}

		for(Entry<String, String> entry : propertiesList) {
			/*
			 * This type of sensors only send 1 or 0 as values If 1 is sent, it means that
			 * the sensor is triggered 
			 * As the level of these sensors are seconds, our
			 * duration become our message when the sensor is triggered
			 */
			if (sensorType.equals("BOOLEAN") || sensorType.equals("FIRE")) {
				if (Integer.parseInt(entry.getValue()) == 1) {
					entry.setValue(entry.getKey());
				} else {
					if(periodOfDay.equals("DAYTIME")) {
						entry.setValue(String.valueOf(sensorFound[sensorIndex].getMin() - 1));
					} else {
						entry.setValue(String.valueOf(sensorFound[sensorIndex].getMax() - 1));
					}

				}
			}
		}

		/*
		 * We retrieve the last messages from the sensor and we initialize an integer
		 * that will create a "fake" real time
		 */
		int messageDuration = Integer
				.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getKey());
		int messageValue = Integer
				.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getValue());
		int realTimeSensors = 1;


		/*
		 * While the sensor is on and that the simulation should keep going
		 */
		while (sensorFound[sensorIndex].getState() && !threadState[sensorIndex]) {
			/*
			 * The sensor is on and configured (has warning levels)
			 */
			if (sensorFound[sensorIndex].getState() && sensorFound[sensorIndex].getConfigure()) {

				boolean alertCreated = false;

				/*
				 * The sensor sensitivity depends on the period of the day
				 */
				int sensorSensitivity = 0;
				if (periodOfDay.equals("DAYTIME")) {
					sensorSensitivity = typeFound.getDaySensitivity();
					sensorLogger
					.info("We're in daytime : sensitivity of daytime --> " + sensorSensitivity + " seconds");
				} else {
					sensorSensitivity = typeFound.getNightSensitivity();
					sensorLogger.info(
							"We're in nighttime : sensitivity of nighttime --> " + sensorSensitivity + " seconds");
				}

				/*
				 * If the sensor doesn't have any messages, then it'll be put in breakdown after
				 * 2 minutes
				 */
				// TODO : adding a breakdown
				try {
					propertiesList.get(0).getValue();
				} catch (IndexOutOfBoundsException e) {
					int breakdownTriggerTime = 120;
					int realTimeBreakdown = 1;

					while (realTimeBreakdown <= breakdownTriggerTime && realTimeBreakdown % 5 == 0 && sensorFound[sensorIndex].getState()) {
						sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
								+ " is on but we get no messages, possible breakdown for " + realTimeBreakdown
								+ " seconds");

						Thread.sleep(1000);
						realTimeBreakdown++;
					}

					/*
					 * If the sensor is not already in a broken state, we put the sensor in
					 * breakdown
					 */
					if (!sensorFound[sensorIndex].getBreakdown()) {
						sensorFound[sensorIndex].setBreakdown(true);

						JSONObject switchToBreakdownJson = new JSONObject();
						switchToBreakdownJson.put("id", sensorFound[sensorIndex].getId());
						switchToBreakdownJson.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

						Request switchToBreakdown = new Request();
						switchToBreakdown.setType("UPDATE");
						switchToBreakdown.setEntity("SENSOR");
						switchToBreakdown.setFields(switchToBreakdownJson);

						ConnectionSimulation ccSwitchToBreakdown = new ConnectionSimulation(
								switchToBreakdown.toJSON().toString());
						ccSwitchToBreakdown.run();

						sensorLogger.info("The sensor : "
								+ sensorFound[sensorIndex].getId() + " is now in a breakdown state in the database");

						/*
						 * Then, we put this breakdown in history breakdown, once the breakdown the 120
						 * seconds passed 
						 * We don't stock the end of the breakdown as we don't know when
						 * it'll be finished
						 */
						Calendar dateNow = Calendar.getInstance();

						Date today = new Date(dateNow.get(Calendar.YEAR) - 1900, dateNow.get(Calendar.MONTH),
								dateNow.get(Calendar.DAY_OF_MONTH));

						Time beginningOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY),
								dateNow.get(Calendar.MINUTE) - 2, dateNow.get(Calendar.SECOND));

						Breakdown breakdownForHistory = new Breakdown();
						breakdownForHistory.setId(1);
						breakdownForHistory.setSensor(sensorFound[sensorIndex].getId());
						breakdownForHistory.setDate(today);
						breakdownForHistory.setStart(beginningOfAlert);

						Request breakdownForHistoryRequest = new Request();
						breakdownForHistoryRequest.setType("CREATE");
						breakdownForHistoryRequest.setEntity("HISTORY_BREAKDOWN");

						JSONObject breakdownForHistoryJson = new JSONObject(breakdownForHistory);
						breakdownForHistoryRequest.setFields(breakdownForHistoryJson);

						ConnectionSimulation ccBreakdownForHistory = new ConnectionSimulation(
								breakdownForHistoryRequest.toJSON().toString());
						ccBreakdownForHistory.run();

						sensorLogger.info("The sensor : "
								+ sensorFound[sensorIndex].getId() + " is now in a history_breakdown");

					}
				}

				/*
				 * The sensor still has messages and is still on
				 */
				if (!propertiesList.isEmpty() && sensorFound[sensorIndex].getState()) {

					/*
					 * See lines : 121
					 */
					boolean request = refreshRequest(sensorType, messageValue, sensorIndex);

					/*
					 * If the sensor is in a broken state, we put the sensor in no breakdown state
					 * As he's sending infos, it means that the sensor is functioning and not in breakdown anymore
					 */ 
					if (sensorFound[sensorIndex].getBreakdown()) {
						sensorFound[sensorIndex].setBreakdown(false);

						JSONObject switchToNoBreakdownJson = new JSONObject();
						switchToNoBreakdownJson.put("id", sensorFound[sensorIndex].getId());
						switchToNoBreakdownJson.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

						Request switchToNoBreakdown = new Request();
						switchToNoBreakdown.setType("UPDATE");
						switchToNoBreakdown.setEntity("SENSOR");
						switchToNoBreakdown.setFields(switchToNoBreakdownJson);

						ConnectionSimulation ccSwitchToNoBreakdown = new ConnectionSimulation(
								switchToNoBreakdown.toJSON().toString());
						ccSwitchToNoBreakdown.run();

						sensorLogger.info("The sensor : "
								+ sensorFound[sensorIndex].getId() + " is now in a no breakdown state in the database");
					}


					/*
					 * The sensor isn't between the warning levels, alert
					 */
					while (request && sensorFound[sensorIndex].getState()) {
						/*
						 * While the duration time didin't fully passed and that the request is true
						 * (the sensor is not between the warning levels)
						 */
						while (realTimeSensors < messageDuration && (request) && sensorFound[sensorIndex].getState()) {
							/*
							 * We see if it's not a fake alert, thanks to the sensitivity (time before we
							 * get a real alert, changes for evry type of sensors)
							 */

							while (realTimeSensors <= sensorSensitivity && realTimeSensors < messageDuration && sensorFound[sensorIndex].getState()) {

								/*
								 * We remove the last "possible alert" entry on the cache (for this sensor) to
								 * put a new one 
								 * So we'll only have the last entries for the sensors in the
								 * sensors cache
								 */
								if (sensorsCache.containsKey("POSSIBLEALERT" + sensorFound[sensorIndex].getId())) {
									sensorsCache.remove("POSSIBLEALERT" + sensorFound[sensorIndex].getId());
								}

								sensorsCache.put("POSSIBLEALERT" + sensorFound[sensorIndex].getId(), realTimeSensors);

								/*
								 * We log something every 5 seconds, or when we know that we will exit the
								 * "while" on the next loop
								 */
								if (realTimeSensors % 5 == 0 || realTimeSensors == sensorSensitivity) {
									sensorLogger.warning("Alert type : POSSIBLEALERT for the sensor : "
											+ sensorFound[sensorIndex].getId() + "\nFor " + realTimeSensors
											+ " seconds with the value " + messageValue + "\n" + sensorFound[sensorIndex].getMin());
								}

								Thread.sleep(1000);
								realTimeSensors++;
							}

							sensorsCache.remove("POSSIBLEALERT" + sensorFound[sensorIndex].getId());

								/*
								 * We remove the last "alert" entry on the cache (for this sensor) to put a new
								 * one So we'll only have the last entries for the sensors in the sensors cache
								 */
								if (sensorsCache.containsKey("ALERT" + sensorFound[sensorIndex].getId())) {
									sensorsCache.remove("ALERT" + sensorFound[sensorIndex].getId());
								}

								sensorsCache.put("ALERT" + sensorFound[sensorIndex].getId(), realTimeSensors);

								/*
								 * We log something every 5 seconds, or when we know that we will exit the
								 * "while" on the next loop
								 */
								if (realTimeSensors % 5 == 0 || realTimeSensors == messageDuration - 1) {
									sensorLogger.warning("Alert type : ALERT for the sensor : "
											+ sensorFound[sensorIndex].getId() + "\nFor " + realTimeSensors
											+ " seconds with the value " + messageValue + "\n" + sensorFound[sensorIndex].getMin());
								}

								/*
								 * If the sensor isn't in a alert state, we put the sensor in an alert state
								 */
								if (!sensorFound[sensorIndex].getAlert()) {
									sensorFound[sensorIndex].setAlert(true);

									JSONObject switchToAlertJson = new JSONObject();
									switchToAlertJson.put("id", sensorFound[sensorIndex].getId());
									switchToAlertJson.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

									Request switchToAlertRequest = new Request();
									switchToAlertRequest.setType("UPDATE");
									switchToAlertRequest.setEntity("SENSOR");
									switchToAlertRequest.setFields(switchToAlertJson);

									ConnectionSimulation ccSwitchToAlert = new ConnectionSimulation(
											switchToAlertRequest.toJSON().toString());
									ccSwitchToAlert.run();

									sensorLogger.info("The sensor : "
											+ sensorFound[sensorIndex].getId() + " is now in alert state in the database");
								}

								realTimeSensors++;
								Thread.sleep(1000);

								request = refreshRequest(sensorType, messageValue, sensorIndex);
							
						}

						/*
						 * We put the alert in history alert when 
						 * the duration alert fully passed
						 */
						if (!alertCreated) {
							Calendar dateNow = Calendar.getInstance();

							Date today = new Date(dateNow.get(Calendar.YEAR) - 1900, dateNow.get(Calendar.MONTH),
									dateNow.get(Calendar.DAY_OF_MONTH));

							Time beginningOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY),
									dateNow.get(Calendar.MINUTE), dateNow.get(Calendar.SECOND));
							Time endOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY), dateNow.get(Calendar.MINUTE),
									dateNow.get(Calendar.SECOND) - 1);

							/*
							 * This might raise a problem if the messages starts at, for exemple 15:59:40
							 * and finishes at 16:01:02 
							 * A weird date would be put for beginning of alert
							 * FIXME
							 */
							if (messageDuration > 60) {
								beginningOfAlert.setMinutes(dateNow.get(Calendar.MINUTE) - messageDuration / 60);
								beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration % 60);
							} else {
								beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration);
							}

							Alert alertForHistory = new Alert(2, sensorFound[sensorIndex].getId(), beginningOfAlert,
									endOfAlert, today);

							Request alertForHistoryRequest = new Request();
							alertForHistoryRequest.setType("CREATE");
							alertForHistoryRequest.setEntity("HISTORY_ALERTS");
							alertForHistoryRequest.setFields(alertForHistory.toJSON());

							ConnectionSimulation ccAlertForHistory = new ConnectionSimulation(
									alertForHistoryRequest.toJSON().toString());
							ccAlertForHistory.run();

							alertCreated = true;
							sensorLogger.info("Alert for the sensor : "
									+ sensorFound[sensorIndex].getId() + " is now in history_alerts");
						}

						/*
						 * If the sensor messages fully passed, we go to
						 * another message
						 */
						if (!propertiesList.isEmpty() && realTimeSensors == messageDuration) {
							propertiesList.remove(propertiesList.size() - 1);
							alertCreated = false;

							/*
							 * If by removing the messages that passed
							 * makes the config.properties empty;
							 * It means that the messages ended, we exit the simulation for this sensor
							 * Else, we keep going
							 */
							if(propertiesList.isEmpty()) {
								sensorLogger.info("Messages ended for this sensor;\nExiting for this sensor");
								refreshHandle[sensorIndex].cancel(false);
								 
								threadState[sensorIndex] = true;
								simulationOn = false;
							} else {
								sensorLogger.info("The sensor is still on, we go to the next message");
								messageDuration = Integer
										.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getKey());
								messageValue = Integer.parseInt(
										((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getValue());
								realTimeSensors = 1;
							}

						} 
					}

					/*
					 * The sensor is between the warning levels, no alert
					 */
					while (!request && sensorFound[sensorIndex].getState())  {
						/*
						 * While the duration time didin't fully passed and that the sensor is between
						 * the warning level and that the sensor is on
						 */
						while (realTimeSensors < messageDuration && !request && sensorFound[sensorIndex].getState()) {
							/*
							 * We remove the last "no alert" entry on the cache (for this sensor) to put a
							 * new one 
							 * So we'll only have the last entries for the sensors in the sensors
							 * cache
							 */
							if (sensorsCache.containsKey("NOALERT" + sensorFound[sensorIndex].getId())) {
								sensorsCache.remove("NOALERT" + sensorFound[sensorIndex].getId());
							}

							sensorsCache.put("NOALERT" + sensorFound[sensorIndex].getId(), realTimeSensors);

							/*
							 * We log something every 5 seconds, or when we know that we will exit the
							 * "while" on the next loop
							 */
							if (realTimeSensors % 5 == 0 || realTimeSensors == messageDuration - 1) {
								sensorLogger.info("No alert for the sensor: " + sensorFound[sensorIndex].getId()
										+ " for " + realTimeSensors + " seconds with the value " + messageValue + "\n" + sensorFound[sensorIndex].getMin());
							}

							/*
							 * If the sensor is in a alert state, we put the sensor in an no alert state
							 */
							if (sensorFound[sensorIndex].getAlert()) {
								sensorFound[sensorIndex].setAlert(false);

								JSONObject switchToNoAlertJson = new JSONObject();
								switchToNoAlertJson.put("id", sensorFound[sensorIndex].getId());
								switchToNoAlertJson.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

								Request switchToNoAlertRequest = new Request();
								switchToNoAlertRequest.setType("UPDATE");
								switchToNoAlertRequest.setEntity("SENSOR");
								switchToNoAlertRequest.setFields(switchToNoAlertJson);

								ConnectionSimulation ccSwitchToNoAlert = new ConnectionSimulation(
										switchToNoAlertRequest.toJSON().toString());
								ccSwitchToNoAlert.run();

								sensorLogger.info("The sensor : "
										+ sensorFound[sensorIndex].getId() + "is now in a no alert state in the database");
							}

							realTimeSensors++;
							Thread.sleep(1000);

							request = refreshRequest(sensorType, messageValue, sensorIndex);
						}

						/*
						 * If the sensor messages fully passed, we go to
						 * another message
						 */
						if (!propertiesList.isEmpty() && realTimeSensors == messageDuration) {
							propertiesList.remove(propertiesList.size() - 1);
							alertCreated = false;

							/*
							 * If by removing the messages that passed
							 * makes the config.properties empty;
							 * It means that the messages ended, we exit the simulation for this sensor
							 * Else, we keep going
							 */
							if(propertiesList.isEmpty()) {
								sensorLogger.info("Messages ended for this sensor;\nExiting for this sensor");
								refreshHandle[sensorIndex].cancel(false);
								 
								threadState[sensorIndex] = true;
								simulationOn = false;
							} else {
								sensorLogger.info("The sensor is still on, we go to the next message");
								messageDuration = Integer
										.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getKey());
								messageValue = Integer.parseInt(
										((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getValue());
								realTimeSensors = 1;
							}

						} 

						/*
						 * If we still have messages and that the message didn't fully passed
						 * It means that the sensor warning levels changed and 
						 * that our message value is now considered to be an alert
						 * so we don't go to the next message but we erase the 
						 * seconds that already passed from our messages duration
						 */
						if (!propertiesList.isEmpty() && realTimeSensors != messageDuration) {
							messageDuration = messageDuration - realTimeSensors;
							realTimeSensors = 1;
							alertCreated = false;
						}



					}

				}

			}

			/*
			 * The sensor is disconnected (was on but now off)
			 */
			if (!sensorFound[sensorIndex].getState()) {
				if (sensorFound[sensorIndex].getConfigure() && !propertiesList.isEmpty()) {
					sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
							+ " got disconnected;\nExiting for this sensor");
					refreshHandle[sensorIndex].cancel(false);
					 
					threadState[sensorIndex] = true;
					simulationOn = false;
				}
			}

			/*
			 * The sensor is on but not configured (no warning levels), we exit
			 */
			if (!sensorFound[sensorIndex].getConfigure()) {
				sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
						+ " doesn't have any warning limits;\nExiting for this sensor");
				refreshHandle[sensorIndex].cancel(false);
				 
				threadState[sensorIndex] = true;
				simulationOn = false;
			}

		}

		/*
		 * The sensor is off at the beggining of the simulation (configured or not), we exit 
		 */
		if(simulationOn) {
			if (!sensorFound[sensorIndex].getState()) {			
				if (sensorFound[sensorIndex].getConfigure() && !propertiesList.isEmpty()) {
					sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
							+ " is off;\nExiting for this sensor");
					refreshHandle[sensorIndex].cancel(false);
					 
					threadState[sensorIndex] = true;
					simulationOn = false;
				}
			}
		}

		sensorLogger.info(simulationOn + "");

		/*
		 * We create a list with what contains threadState
		 */
		List<Boolean> threadStateList = Arrays.asList(threadState);

		/*
		 * If this list doesn't contain false, it means that every simulation ended
		 */
		if (!threadStateList.contains(false)) {
			mainLogger.info("Sensor cache at the end of the simulation :\n" + sensorsCache.toString());
			System.exit(0);
		}

	}

	public void refreshSensorInfos(int sensorId, int sensorIndex) {
		final Runnable refresh = new Runnable() {

			public void run() {
				JSONObject sensorJsonId = new JSONObject();
				sensorJsonId.put("id", sensorId);

				Request request = new Request();
				request.setType("FINDBYID");
				request.setEntity("SENSOR");
				request.setFields(sensorJsonId);

				ConnectionSimulation cc = new ConnectionSimulation(request.toJSON().toString());
				cc.run();

				String response = cc.getResponse();
				try {
					sensorFound[sensorIndex] = objectMapper.readValue(response.toString(), Sensor.class);

				} catch (IOException e) {

				}
			}
		};

		refreshHandle[sensorIndex] = scheduler.scheduleAtFixedRate(refresh, 1, 3, SECONDS);

	}

	public void refreshPeriodOfDay(int sensorIndex) {
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
				if (now.before(night) && now.after(day)) {
					periodOfDay = "DAYTIME";
				} else {
					periodOfDay = "NIGHTTIME";
				}
			}

		};
		
		ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresh, 0, 60, SECONDS);

	}

	public boolean refreshRequest(String sensorType, int messageValue, int sensorIndex) {
		boolean request;
		if(sensorType.equals("BOOLEAN") && periodOfDay.equals("DAYTIME")) {
			request = sensorFound[sensorIndex].getMin() <= messageValue;
		}

		else if(sensorType.equals("BOOLEAN") && periodOfDay.equals("NIGHTTIME")) {
			request = sensorFound[sensorIndex].getMax() <= messageValue;
		}

		else {
			request = sensorFound[sensorIndex].getMax() <= messageValue
					|| sensorFound[sensorIndex].getMin() >= messageValue;
		}

		return request;
	}

}
