package fr.pds.floralis.server.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class SimulationMain {

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
		 * stocked in the propertiesData One logger for one sensor Plus a main logger
		 */
		Logger loggers[] = new Logger[propertiesData.length + 1];
		FileHandler fileHandlers[] = new FileHandler[propertiesData.length + 1];
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
			if ((i != 0)) {
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
					} 
				}
			}.start();

		}

		/*
		 * This will make every thred wait until they are here to start
		 */
		cyclicGate.await();
		loggers[0].info("The simulation just started for each sensor");
		
		List<Boolean> threadStateList = Arrays.asList(Simulation.threadState);
		
		while (threadStateList.contains(false)) {
			
		}
		
		loggers[0].info("Sensor cache at the end of the simulation :\n" + cache.toString());
		System.exit(0);
		
	}
}
