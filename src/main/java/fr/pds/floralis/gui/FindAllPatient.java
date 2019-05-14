package fr.pds.floralis.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Alert;
import fr.pds.floralis.commons.bean.entity.Patient;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class FindAllPatient {
	private static ObjectMapper objectMapper;
	private static String host;
	private static int port;

	public FindAllPatient (String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public static List<Patient> findAll() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		
		objectMapper = new ObjectMapper();
		List<Patient> patientList;
		
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("PATIENTS");
		request.setFields(new JSONObject());
		
		ConnectionClient ccPatientFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccPatientFindAll.run();
		
		Patient[] patientFoundTab =  objectMapper.readValue(ccPatientFindAll.getResponse(), Patient[].class);
		patientList = Arrays.asList(patientFoundTab);
		
		return patientList;
	
	}
}
