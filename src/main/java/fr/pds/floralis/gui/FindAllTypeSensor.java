package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.TypeSensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAllTypeSensor {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllTypeSensor(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<TypeSensor> findAll(Boolean refresh) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<TypeSensor> typeSensorsList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("TYPESENSOR");
		request.setFields(new JSONObject());
		
		ConnectionClient ccTypeSensorFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccTypeSensorFindAll.run();
		
		TypeSensor[] typeSensorsFoundTab =  objectMapper.readValue(ccTypeSensorFindAll.getResponse(), TypeSensor[].class);
		typeSensorsList = Arrays.asList(typeSensorsFoundTab);
		
		if(refresh) { 
			Thread.sleep(6000);
			findAll(true);
		}
		
		return typeSensorsList;
	}
}
