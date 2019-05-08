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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
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
import fr.pds.floralis.commons.bean.entity.Breakdown;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.TypeSensor;

public class Simulation {

	static ObjectMapper objectMapper = new ObjectMapper();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Sensor sensorFound[];
	HashMap<String, Integer> sensorsCache;
	ArrayList<Entry<String, String>>[] propertiesTab;
	private String periodOfDay = "";
	static ScheduledFuture<?> refreshHandle[];
	private int propertiesLength;
	private static Boolean[] threadState;

	public Simulation(HashMap<String, Integer> sensorsCache, int propertiesLength) {
		this.sensorsCache = sensorsCache;
		this.propertiesLength = propertiesLength;
		this.sensorFound = new Sensor[propertiesLength];
		Simulation.refreshHandle = new ScheduledFuture<?>[propertiesLength];
		Simulation.threadState = new Boolean[propertiesLength];
	}

	@SuppressWarnings("deprecation")
	public void simulationTest(ArrayList<Entry<String, String>> propertiesList, Logger sensorLogger, Logger mainLogger, int sensorIndex)
			throws IOException, InterruptedException {

		threadState[sensorIndex] = false;
		/*
		 * Taking care of the warning levels depending of the period of day From
		 * 09:00:00 to 19:59:00 --> Daytime
		 */
		refreshPeriodOfDay();

		/*
		 * We catch the exception that would be raised if theconfig.properties wasn't
		 * fulled properly : String has id / No messages
		 */
		try {
			Integer.parseInt((propertiesList.get(0)).getValue());
		} catch (java.lang.NumberFormatException e1) {
			sensorLogger.warning("The sensor id is not of integer type;\n Exit");
			refreshHandle[sensorIndex].cancel(false);
			Thread.currentThread().stop();
		}

		int propertiesId = Integer.parseInt((propertiesList.get(0)).getValue());
		propertiesList.remove(0);

		/*
		 * We get the sensor infos every 3 seconds thanks to the id
		 */
		refreshSensorInfos(propertiesId, sensorIndex);

		/*
		 * This sleep is made to wait for the findById from all the sensor We wait 2
		 * seconds for each sensor
		 */
		Thread.sleep(propertiesLength * 3000);

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

		/*
		 * The sensor is on and configured (has warning levels)
		 */
		if (sensorFound[sensorIndex].getState() && sensorFound[sensorIndex].getConfigure()) {

			/*
			 * The sensor sensitivity depends on the period of the day
			 */
			int sensorSensitivity = 0;
			if (periodOfDay.equals("DAYTIME")) {
				sensorSensitivity = typeFound.getDaySensitivity();
				sensorLogger.info("We're in daytime : sensitivity of daytime --> " + sensorSensitivity + " seconds");
			} else {
				sensorSensitivity = typeFound.getNightSensitivity();
				sensorLogger
						.info("We're in nighttime : sensitivity of nighttime --> " + sensorSensitivity + " seconds");
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

				while (realTimeBreakdown <= breakdownTriggerTime && realTimeBreakdown % 5 == 0) {
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

					/*
					 * Then, we put this breakdown in history breakdown, once the breakdown the 120
					 * seconds passed We don't stock the end of the breakdown as we don't know when
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
				}
			}

			/*
			 * The sensor still has messages and is still on
			 */
			while (!propertiesList.isEmpty()) {
				if (sensorFound[sensorIndex].getState()) {

					sensorLogger.info(
							"Sensor with the id " + sensorFound[sensorIndex].getId() + " is on and we get messages");

					/*
					 * If the sensor is in a broken state, we put the sensor in no breakdown state
					 * As he's sending infos, it means that the sensor is fully functioning
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
					}

					String sensorType;

					/*
					 * If the sensor is a light sensor, a presence sensor or a fire then it'll send
					 * us only 0 or 1 has value for one duration (see the config.properties files)
					 * However, if it's a light or a presence sensor, the warning levels min/max
					 * that are contained for the sensor refers to the max levels dependings on the
					 * period of day Min --> Maximum for daytime Max --> Maximum for nighttime
					 * 
					 * It's made like this so the user can change the time before an alert when he
					 * configures a light or a presence sensor For fire sensors, he cannot as it's a
					 * sensor that is really important
					 */
					if (sensorFound[sensorIndex].getType().toUpperCase().equals("LIGHT")
							|| sensorFound[sensorIndex].getType().toUpperCase().equals("PRESENCE")) {
						sensorType = "BOOLEAN";
					} else if (sensorFound[sensorIndex].getType().toUpperCase().equals("FIRE")) {
						sensorType = "FIRE";
					} else {
						sensorType = "VALUE";
					}

					/*
					 * We retrieve the last messages from the sensor and we initialize an integer
					 * that will create a "fake" real time
					 */
					int messageDuration = Integer
							.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getKey());
					int messageValue = Integer.parseInt(
							((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getValue());
					int realTimeSensors = 1;

					Boolean alertCreated = false;

					/*
					 * This is the basic request : level Min < value < level Max
					 */
					boolean request = sensorFound[sensorIndex].getMax() <= messageValue
							|| sensorFound[sensorIndex].getMin() >= messageValue;

					/*
					 * This type of sensors only send 1 or 0 as values If 1 is sent, it means that
					 * the sensor is triggered As the level of these sensors are seconds, our
					 * duration become our message when the sensor is triggered
					 */
					if (sensorType.equals("BOOLEAN") || sensorType.equals("FIRE")) {
						if (messageValue == 1) {
							messageValue = messageDuration;
						} else {
							messageValue = sensorFound[sensorIndex].getMax() - 1;
						}
					}

					/*
					 * See lines from 227 to 237
					 */
					if (periodOfDay.equals("DAYTIME") && (sensorType.equals("BOOLEAN"))) {
						request = messageValue >= sensorFound[sensorIndex].getMin();
					}

					if (periodOfDay.equals("NIGHTTIME") && (sensorType.equals("BOOLEAN"))) {
						request = messageValue >= sensorFound[sensorIndex].getMax();
					}

					/*
					 * The sensor isn't between the warning levels, alert
					 */
					if (request) {

						/*
						 * This boolean is used to know if the alert has already been created in the
						 * history alert As we're on a while loop, it would create an history alert
						 * every second if this boolean wasn't there
						 */
						alertCreated = false;

						/*
						 * While the duration time didin't fully passed and that the request is true
						 * (the sensor is not between the warning levels) and that the sensor is on
						 */
						while (realTimeSensors <= messageDuration && (request) && sensorFound[sensorIndex].getState()) {

							/*
							 * We see if it's not a fake alert, thanks to the sensitivity (time before we
							 * get a real alert, changes for evry type of sensors)
							 */
							while (realTimeSensors <= sensorSensitivity) {

								/*
								 * We remove the last "possible alert" entry on the cache (for this sensor) to
								 * put a new one So we'll only have the last entries for the sensors in the
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
											+ " seconds with the value " + messageValue);
								}

								Thread.sleep(1000);
								realTimeSensors++;
							}

							// TODO : do we keep the possible alerts in the cache ?
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
							if (realTimeSensors % 5 == 0 || realTimeSensors == messageDuration) {
								sensorLogger.warning("Alert type : ALERT for the sensor : "
										+ sensorFound[sensorIndex].getId() + "\nFor " + realTimeSensors
										+ " seconds with the value " + messageValue);
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
							}

							realTimeSensors++;
							Thread.sleep(1000);

						}

						/*
						 * If the sensor alert is not already in the history alert
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
							 * and finishes at 16:01:02 A weird date would be put for beginning of alert
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
						}

					}

					/*
					 * The sensor is between the warning levels, no alert
					 */
					else {

						/*
						 * While the duration time didin't fully passed and that the sensor is between
						 * the warning level and that the sensor is on
						 */
						while (realTimeSensors <= messageDuration && (!request)
								&& sensorFound[sensorIndex].getState()) {
							/*
							 * We remove the last "no alert" entry on the cache (for this sensor) to put a
							 * new one So we'll only have the last entries for the sensors in the sensors
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
							if (realTimeSensors % 5 == 0 || realTimeSensors == messageDuration) {
								sensorLogger.info("No alert for the sensor: " + sensorFound[sensorIndex].getId()
										+ " for " + realTimeSensors + " seconds with the value " + messageValue);
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

							}

							realTimeSensors++;
							Thread.sleep(1000);

						}

					}

				}

			}
		}

		/*
		 * If the sensor is on and that we still have messsages, we go to the next
		 * message
		 */
		if (!propertiesList.isEmpty() && sensorFound[sensorIndex].getState()) {
			sensorLogger.info("The sensor is still on, we go to the next message");
			propertiesList.remove(propertiesList.size() - 1);
		}

		if (!sensorFound[sensorIndex].getState()) {
			if (sensorFound[sensorIndex].getConfigure() && !propertiesList.isEmpty()) {
				sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
						+ " is off but we get messages;\nWaiting");
				propertiesList.remove(propertiesList.size() - 1);
			}

			else if (!sensorFound[sensorIndex].getConfigure() && !propertiesList.isEmpty()) {
				sensorLogger.warning("Sensor with the id " + sensorFound[sensorIndex].getId()
						+ " is off and does't have any warning limits, but we get messages;\nExiting for this sensor");
				refreshHandle[sensorIndex].cancel(false);
				threadState[sensorIndex] = true;
			}
		}

		/*
		 * If the sensor is off and that we still have messsages, we go to the next
		 * messages and we wait for it to reconnect / connect However, we'll loose the
		 * last message sent
		 */
		if (!sensorFound[sensorIndex].getConfigure()) {
			sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
					+ " doesn't have any warning limits;\nExiting for this sensor");
			refreshHandle[sensorIndex].cancel(false);
			threadState[sensorIndex] = true;
		}

		else if (propertiesList.isEmpty()) {
			sensorLogger.info("Messages ended for this sensor;\nExiting for this sensor");
			refreshHandle[sensorIndex].cancel(false);
		}

		threadState[sensorIndex] = true;

		List<Boolean> threadStateList = Arrays.asList(threadState);
		
		if (!threadStateList.contains(false)) {
			mainLogger.info("Sensor cache at the end of the simulation :\n" + sensorsCache.toString());
			System.exit(0);
		}
		
	}

	public Sensor refreshSensorInfos(int sensorId, int sensorIndex) {
		Sensor sensorToChange = new Sensor();
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

		return sensorToChange;
	}

	// TODO : refactoring this
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
				if (now.before(night) && now.after(day)) {
					periodOfDay = "DAYTIME";
				} else {
					periodOfDay = "NIGHTTIME";
				}
			}

		};

		final ScheduledFuture<?> refreshHandle = scheduler.scheduleAtFixedRate(refresh, 0, 30, SECONDS);

		scheduler.schedule(new Runnable() {
			public void run() {
				refreshHandle.cancel(true);
			}
		}, 360, SECONDS);

		return periodOfDay;
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException,
			InterruptedException, BrokenBarrierException {
		/*
		 * We set a format on every logger
		 */
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");

		/*
		 * We read the .properties file
		 */
		PropertiesReader propertiesFile = new PropertiesReader();
		ArrayList<Entry<String, String>>[] propertiesData = propertiesFile.getPropValues();

		/*
		 * The cache and the cyclic barrier is created The Cyclic Barrier will make
		 * every thread start at the same time, the simulation will start at the same
		 * ime for every sensor that is into the config.properties file
		 */
		HashMap<String, Integer> cache = new HashMap<String, Integer>();
		final CyclicBarrier cyclicGate = new CyclicBarrier(propertiesData.length + 1);

		Simulation simulation = new Simulation(cache, propertiesData.length);

		/*
		 * We create a number of logger that is equal to the number of sensors that is
		 * stocked in the propertiesData One logger for one sensor
		 * Plus a main logger
		 */
		Logger loggers[] = new Logger[propertiesData.length + 1];
		FileHandler fileHandlers[] = new FileHandler[propertiesData.length];
		SimpleFormatter formatter = new SimpleFormatter();

		/*
		 * We initialize the main Logger
		 */
		loggers[0] = Logger.getLogger("Logger");
		fileHandlers[0] = new FileHandler("%hmainLogger.log");
		loggers[0].addHandler(fileHandlers[0]);
		fileHandlers[0].setFormatter(formatter);

		/*
		 * We catch an exception if the config.properties is empty
		 */
		try {
			propertiesData[0].toString();
		} catch (java.lang.NullPointerException e) {
			loggers[0].warning("The config.properties file is empty;\n Exiting the simulation");
			System.exit(0);
		}

		for (int i = 0; i <= propertiesData.length; i++) {

			/*
			 * We initialize every sensor Logger
			 */
			if ((i != 0) && (i != 5)) {
				loggers[i] = Logger.getLogger("Logger" + i + "");
				fileHandlers[i] = new FileHandler("%hsimulationLogger" + i + ".log");
				loggers[i].addHandler(fileHandlers[i]);
				fileHandlers[i].setFormatter(formatter);
			}

			/*
			 * We create a thread for every sensor stocked in the config.properties file
			 * Each thread will start at the same time thanks to the syclic barrier We put
			 * the sensors messages (propertiesData[index]), one logger (loggers[index]) and
			 * an int (index) that will be usefull for the refreshSensorInfos()
			 */
			int index = i;
			new Thread() {
				public void run() {
					try {
						cyclicGate.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
					try {
						simulation.simulationTest(propertiesData[index], loggers[index + 1], loggers[0], index);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					} catch (java.lang.NullPointerException e1) {
						loggers[index]
								.warning("This sensor doesn't exist;\nStoping the simulation for this sensor");
						loggers[0].warning("The sensor number " + (index + 1)
								+ " doesn't exist;\nStoping the simulation for this sensor");

						refreshHandle[index].cancel(false);
						threadState[index] = true;
					}
				}
			}.start();

		}

		/*
		 * This will make every thred wait until they are here to start
		 */
		cyclicGate.await();
		loggers[0].info("The simulation is started for all the sensors");
	}
}
