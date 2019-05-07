package fr.pds.floralis.server.simulation;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class Simulation {

	static ObjectMapper objectMapper = new ObjectMapper();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Sensor sensorFound[];
	HashMap<String, Integer> sensorsCache;
	ArrayList<Entry<String, String>>[] propertiesTab;
	private String periodOfDay = "";
	static ScheduledFuture<?> refreshHandle[];
	private int propertiesLength;

	public Simulation(HashMap<String, Integer> sensorsCache, int propertiesLength) {
		this.sensorsCache = sensorsCache;
		this.propertiesLength = propertiesLength;
		this.sensorFound = new Sensor[propertiesLength];
		this.refreshHandle = new ScheduledFuture<?>[propertiesLength];
	}

	@SuppressWarnings("deprecation")
	public void simulationTest(ArrayList<Entry<String, String>> propertiesList, Logger sensorLogger, int sensorIndex)
			throws IOException, InterruptedException {

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

		ConnectionClient ccRequestSensitivity = new ConnectionClient(requestSensitivityByType.toJSON().toString());
		ccRequestSensitivity.run();

		String responseRequestSensitivity = ccRequestSensitivity.getResponse();
		TypeSensor typeFound = objectMapper.readValue(responseRequestSensitivity.toString(), TypeSensor.class);
		// End of requestSensitivities

		/*
		 * The sensor sensitivity depends on the period of the day
		 */
		int sensorSensitivity = 0;
		if (periodOfDay.equals("DAYTIME")) {
			sensorSensitivity = typeFound.getDaySensitivity();
			sensorLogger.info("We're in daytime : sensitivity of daytime --> " + sensorSensitivity + " seconds");
		} else {
			sensorSensitivity = typeFound.getNightSensitivity();
			sensorLogger.info("We're in nighttime : sensitivity of nighttime --> " + sensorSensitivity + " seconds");
		}

		/*
		 * The sensor is on and configured (has warning levels)
		 */
		if (sensorFound[sensorIndex].getState() && sensorFound[sensorIndex].getConfigure()) {

			/*
			 * If the sensor doesn't has any messages, then it'll be put in breakdown after
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

					ConnectionClient ccSwitchToBreakdown = new ConnectionClient(switchToBreakdown.toJSON().toString());
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

					ConnectionClient ccBreakdownForHistory = new ConnectionClient(
							breakdownForHistoryRequest.toJSON().toString());
					ccBreakdownForHistory.run();
				}
			}

			/*
			 * The sensor still has messages and is still on
			 */
			while (!propertiesList.isEmpty() && sensorFound[sensorIndex].getState()) {
				sensorLogger
						.info("Sensor with the id " + sensorFound[sensorIndex].getId() + " is on and we get messages");

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

					ConnectionClient ccSwitchToNoBreakdown = new ConnectionClient(
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
				int messageValue = Integer
						.parseInt(((Entry<String, String>) propertiesList.get(propertiesList.size() - 1)).getValue());
				int realTimeValue = 1;

				Boolean alertCreated = false;

				/*
				 * This is the basic request : level Min < value < level Max
				 */
				boolean request = Integer.parseInt(sensorFound[sensorIndex].getMax()) <= messageValue
						|| Integer.parseInt(sensorFound[sensorIndex].getMin()) >= messageValue;

				/*
				 * This type of sensors only send 1 or 0 as values
				 * If 1 is sent, it means that the sensor is triggered
				 * As the level of these sensors are seconds, our duration become our message when the sensor is triggered
				 */
				if (sensorType.equals("BOOLEAN") || sensorType.equals("FIRE")) {
					if (messageValue == 1) {
						messageValue = messageDuration;
					} else {
						messageValue = Integer.parseInt(sensorFound[sensorIndex].getMax()) - 1;
					}
				}
				
				/*
				 * See lines from 227 to 237
				 */
				if (periodOfDay.equals("DAYTIME") && (sensorType.equals("BOOLEAN"))) {
					request = messageValue <= Integer.parseInt(sensorFound[sensorIndex].getMin());
				}

				if (periodOfDay.equals("NIGHTTIME") && (sensorType.equals("BOOLEAN"))) {
					request = messageValue <= Integer.parseInt(sensorFound[sensorIndex].getMax());
				}

				if (request) {
					alertCreated = false;
					while (realTimeValue <= messageDuration && (request) && sensorFound[sensorIndex].getState()) {

						while (realTimeValue < sensorSensitivity) {
							if (sensorsCache.containsKey("POSSIBLEALERT" + sensorFound[sensorIndex].getId())) {
								sensorsCache.remove("POSSIBLEALERT" + sensorFound[sensorIndex].getId());
							}

							sensorsCache.put("POSSIBLEALERT" + sensorFound[sensorIndex].getId(), realTimeValue);

							if (realTimeValue % 5 == 0 || realTimeValue == 1) {
								sensorLogger.warning("Type alert : POSSIBLEALERT for the sensor : "
										+ sensorFound[sensorIndex].getId() + " for " + realTimeValue
										+ " seconds with the value " + messageValue);
							}

							Thread.sleep(1000);
							realTimeValue++;
						}

						sensorsCache.remove("POSSIBLEALERT" + sensorFound[sensorIndex].getId());

						if (sensorsCache.containsKey("ALERT" + sensorFound[sensorIndex].getId())) {
							sensorsCache.remove("ALERT" + sensorFound[sensorIndex].getId());
						}

						sensorsCache.put("ALERT" + sensorFound[sensorIndex].getId(), realTimeValue);

						if (realTimeValue % 5 == 0 || realTimeValue == messageDuration || realTimeValue == 1) {
							sensorLogger.warning(
									"Type alert : HIGHERMAX for the sensor : " + sensorFound[sensorIndex].getId()
											+ " for " + realTimeValue + " seconds with the value " + messageValue);
						}

						if (sensorFound[sensorIndex].getAlert() == false) {
							JSONObject newStateOnAlert = new JSONObject();
							sensorFound[sensorIndex].setAlert(true);
							newStateOnAlert.put("id", sensorFound[sensorIndex].getId());
							newStateOnAlert.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

							Request secondRequest = new Request();
							secondRequest.setType("UPDATE");
							secondRequest.setEntity("SENSOR");
							secondRequest.setFields(newStateOnAlert);

							ConnectionClient ccr = new ConnectionClient(secondRequest.toJSON().toString());
							ccr.run();
						}

						realTimeValue++;
						Thread.sleep(1000);

					}

					if (alertCreated == false) {
						Calendar dateNow = Calendar.getInstance();
						Date today = new Date(dateNow.get(Calendar.YEAR) - 1900, dateNow.get(Calendar.MONTH),
								dateNow.get(Calendar.DAY_OF_MONTH));
						Time endOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY), dateNow.get(Calendar.MINUTE),
								dateNow.get(Calendar.SECOND) - 1);

						Time beginningOfAlert = new Time(dateNow.get(Calendar.HOUR_OF_DAY),
								dateNow.get(Calendar.MINUTE), dateNow.get(Calendar.SECOND));

						if (messageDuration > 60) {
							beginningOfAlert.setMinutes(dateNow.get(Calendar.MINUTE) - messageDuration / 60);
							beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration % 60);
						} else {
							beginningOfAlert.setSeconds(dateNow.get(Calendar.SECOND) - messageDuration);
						}

						Alert alerte = new Alert(2, sensorFound[sensorIndex].getId(), beginningOfAlert, endOfAlert,
								today);

						Request fifthRequest = new Request();
						fifthRequest.setType("CREATE");
						fifthRequest.setEntity("HISTORY_ALERTS");
						fifthRequest.setFields(alerte.toJSON());

						ConnectionClient ccSensorInAlert = new ConnectionClient(fifthRequest.toJSON().toString());
						ccSensorInAlert.run();
						alertCreated = true;
					}

				}

				else {

					if (sensorsCache.containsKey("NOALERT" + sensorFound[sensorIndex].getId())) {
						sensorsCache.remove("NOALERT" + sensorFound[sensorIndex].getId());
					}

					while (realTimeValue <= messageDuration && sensorFound[sensorIndex].getState()) {

						sensorsCache.put("NOALERT" + sensorFound[sensorIndex].getId(), realTimeValue);
						if (realTimeValue % 5 == 0 || realTimeValue == messageDuration - 1 || realTimeValue == 1) {
							sensorLogger.info("No alert for the sensor: " + sensorFound[sensorIndex].getId() + " for "
									+ realTimeValue + " seconds with the value " + messageValue);
						}

						if (sensorFound[sensorIndex].getAlert()) {
							JSONObject newStateNoAlert = new JSONObject();
							sensorFound[sensorIndex].setAlert(false);
							newStateNoAlert.put("id", sensorFound[sensorIndex].getId());
							newStateNoAlert.put("sensorToUpdate", sensorFound[sensorIndex].toJSON());

							Request thridRequest = new Request();
							thridRequest.setType("UPDATE");
							thridRequest.setEntity("SENSOR");
							thridRequest.setFields(newStateNoAlert);

							ConnectionClient ccUpdateSensor = new ConnectionClient(thridRequest.toJSON().toString());
							ccUpdateSensor.run();

						}

						realTimeValue++;
						Thread.sleep(1000);

					}

				}

				if (!propertiesList.isEmpty() && sensorFound[sensorIndex].getState()) {
					propertiesList.remove(propertiesList.size() - 1);
				}

				if (!propertiesList.isEmpty() && !sensorFound[sensorIndex].getState()) {
					sensorLogger.warning("The sensor is now off but we get messages, something went wrong");
					refreshHandle[sensorIndex].cancel(false);
				}

			}

		} else {
			if (!sensorFound[sensorIndex].getState() && !sensorFound[sensorIndex].getConfigure()) {
				sensorLogger.warning("Sensor with the id " + sensorFound[sensorIndex].getId()
						+ " is off and does't have any warning limits, but we get messages;\nExit");
				refreshHandle[sensorIndex].cancel(false);
			}

			else if (!sensorFound[sensorIndex].getState()) {
				sensorLogger.warning("Sensor with the id " + sensorFound[sensorIndex].getId()
						+ " is off, but we get messages;\nExit");
				refreshHandle[sensorIndex].cancel(false);
			}

			else if (!sensorFound[sensorIndex].getConfigure()) {
				sensorLogger.warning("The sensor with the id " + sensorFound[sensorIndex].getId()
						+ " does't have any warning limits;\nExit");
				refreshHandle[sensorIndex].cancel(false);
			}
		}

		sensorLogger.info("Messages ended for this sensor");

		refreshHandle[sensorIndex].cancel(false);
		Thread.currentThread().stop();
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

				ConnectionClient cc = new ConnectionClient(request.toJSON().toString());
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
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %5$s%6$s%n");

		PropertiesReader properties = new PropertiesReader();
		ArrayList<Entry<String, String>>[] propertiesList = properties.getPropValues();
		HashMap<String, Integer> sensorsCache = new HashMap<String, Integer>();
		final CyclicBarrier gate = new CyclicBarrier(propertiesList.length + 1);

		Simulation simu = new Simulation(sensorsCache, propertiesList.length);

		Logger loggers[] = new Logger[propertiesList.length];
		FileHandler fileHandlers[] = new FileHandler[propertiesList.length];
		SimpleFormatter formatter = new SimpleFormatter();

		loggers[0] = Logger.getLogger("Logger");
		fileHandlers[0] = new FileHandler("%mainLogger.log");
		loggers[0].addHandler(fileHandlers[0]);
		fileHandlers[0].setFormatter(formatter);

		try {
			propertiesList[0].toString();
		} catch (java.lang.NullPointerException e) {
			loggers[0].warning("The config.properties file is empty;\n Exiting the simulation");
			System.exit(0);
		}

		for (int i = 1; i < propertiesList.length; i++) {
			loggers[i] = Logger.getLogger("Logger" + i + "");
			fileHandlers[i] = new FileHandler("%hsimulationLogger" + i + ".log");
			loggers[i].addHandler(fileHandlers[i]);
			fileHandlers[i].setFormatter(formatter);
		}

		for (int i = 0; i < propertiesList.length; i++) {
			int index = i;
			new Thread() {
				public void run() {
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						e.printStackTrace();
					}
					try {
						simu.simulationTest(propertiesList[index], loggers[index], index);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					} catch (java.lang.NullPointerException e1) {
						loggers[index]
								.warning("This sensor dosn't send messages;\n Stoping the simulation for this sensor");
					}
				}
			}.start();
		}

		gate.await();

		loggers[0].info("The simulation is started for all the sensors");
	}
}
