package fr.pds.floralis.server.simulation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Crunchify.com
 * 
 */

public class PropertiesReader {
	String result = "";
	InputStream inputStream;
	ArrayList<Entry<String, String>>[] entryList;

	// FIXME : deux valeurs ne peuvent avoir le même temps --> ajouter la value contenue dans
	// value.iter.duration puis faire un substring de l'autre côté

	public ArrayList<Entry<String, String>>[] getPropValues() throws IOException {

		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}


			/**
			 * We get the id property and the type property
			 */

			String id = "";
			String type = "";

			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> list = new LinkedHashMap<String, String>();

			/**
			 * We put all our key-values pairs in a Map
			 */
			for (String key : prop.stringPropertyNames()) {	
				map.put(key, prop.getProperty(key));
			}

			String value = "";
			String duration = "";
			int index = 0;
			int numberOfValues = 2;
			int numberOfSensors = Integer.parseInt(map.get("numberOfSensors"));
			entryList = (ArrayList<Entry<String, String>>[])new ArrayList[numberOfSensors];
			ArrayList<Map.Entry<String, String>> testList = null;

			while (!(map.isEmpty())) {
				while(numberOfSensors > 0) {

					if (map.containsKey(numberOfSensors + ".id")){ 
						id = map.get(numberOfSensors + ".id");
						map.remove(numberOfSensors + ".id");	
						list.put("id", id);
						
						numberOfValues = Integer.parseInt(map.get(numberOfSensors + ".numberOfValues"));

						while(numberOfValues > 0) {
							if(map.containsKey(numberOfSensors + ".iter." + numberOfValues + ".value") && map.containsKey(numberOfSensors + ".iter." + numberOfValues + ".duration")) {
								value = map.get(numberOfSensors + ".iter." + numberOfValues + ".value");
								duration = map.get(numberOfSensors + ".iter." + numberOfValues + ".duration");
								map.remove(numberOfSensors + ".iter." + numberOfValues + ".value");	
								map.remove(numberOfSensors + ".iter." + numberOfValues + ".duration");
								list.put(duration, value);

							}
							numberOfValues--;
						}
						
						testList = new ArrayList<Map.Entry<String, String>>(list.entrySet());
						entryList[numberOfSensors - 1] = testList;
						numberOfSensors--;
						System.out.println(list.toString());
						list.clear();
					}
				
					else {
						numberOfSensors--;
					}
				}

				map.clear();

			}

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return entryList;
	}
}