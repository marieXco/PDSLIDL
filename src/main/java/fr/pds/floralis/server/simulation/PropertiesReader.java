package fr.pds.floralis.server.simulation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
	List<Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>();
	
	// FIXME : deux valeurs ne peuvent avoir le même temps --> ajouter la value contenue dans
	// value.iter.duration puis faire un substring de l'autre côté

	public List<Entry<String, String>> getPropValues() throws IOException {

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
			
			String id = prop.getProperty("id");
			String type = prop.getProperty("type");
			
			prop.remove("id");
			prop.remove("type");
			
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
			int numberOfValues = map.size()/2;
			
			list.put("id", id);
			list.put("type", type);
			
			
			
			while (!(map.isEmpty())) {
				
				while(numberOfValues > index) {
					
					if(map.containsKey("iter." + numberOfValues + ".value")) {
						value = map.get("iter." + numberOfValues + ".value");
						map.remove("iter." + numberOfValues + ".value");		
					}

					if (map.containsKey("iter." + numberOfValues + ".duration")){ 
						duration = map.get("iter." + numberOfValues + ".duration");
						map.remove("iter." + numberOfValues + ".duration");							
					}
					
					list.put(duration, value);
					numberOfValues--;
					
				}

			}
			
			/**
			 * We put our key-value in a list, so we can get the last key-value pair
			 */
			 entryList = new ArrayList<Map.Entry<String, String>>(list.entrySet());

			System.out.println("Key-Values pairs : " + entryList.toString() + "\nSensorId : " + id + " \nType : " + type.toUpperCase() + 
					"\nLastKey (value.iter.1.duration) : " + entryList.get(entryList.size() - 1).getKey() + 
					"\nLastValue (value.iter.1.value) : " + entryList.get(entryList.size() - 1).getValue());


		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		
		return entryList;
	}
}