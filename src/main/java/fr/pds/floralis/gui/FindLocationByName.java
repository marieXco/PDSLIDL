package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindLocationByName {

	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindLocationByName(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Location> findByName(Boolean refresh, Boolean name)
			throws JsonParseException, JsonMappingException, JSONException,
			IOException, InterruptedException {

		objectMapper = new ObjectMapper();
		List<Location> locationNameList;

		JSONObject locationName = new JSONObject();
		locationName.put("name", name);

		Request request = new Request();
		request.setType("FINDBYNAME");
		request.setEntity("LOCATION");
		request.setFields(locationName);

		ConnectionClient ccLocationFindByName = new ConnectionClient(host,
				port, request.toJSON().toString());
		ccLocationFindByName.run();

		Location[] locationNameTab = objectMapper.readValue(
				ccLocationFindByName.getResponse(), Location[].class);
		locationNameList = Arrays.asList(locationNameTab);

		if (refresh) {
			Thread.sleep(6000);
			findByName(true, name);
		}

		return locationNameList;
	}
}
