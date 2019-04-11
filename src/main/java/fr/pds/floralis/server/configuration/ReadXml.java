package fr.pds.floralis.server.configuration;

import java.io.File;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ReadXml
 * 
 * This class is made to fill up the Configuration.java file
 * It parses our XML configuration file to give 
 * our XML parameters to the configuration class 
 *
 */

public class ReadXml {
	Element login, url, password, driver, nbrConnexions;

	{
		/*
		 * Retrieval of on instance of the class
		 * "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();

		try {
			/*
			 * Parser creation
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();


			/*
			 * Getting the document that will be parsed
			 */
			// FIXME BEFORE EXPORTING THE JARS, uncomment the next line and comment the one after
			// final Document document = builder.parse(new File("./configuration/configuration.xml"));
			final Document document = builder.parse(new File("src/main/resources/configuration/configuration.xml"));


			/*
			 * Retrieval of the root element
			 */
			final Element racine = document.getDocumentElement();


			/*
			 * Retrieval of the children of the root element
			 */
			final NodeList racineNoeuds = racine.getChildNodes();

			for (int i = 0; i < 2; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {

					login = (Element) racine.getElementsByTagName("login").item(0);

					password = (Element) racine.getElementsByTagName("password").item(0);

					driver = (Element) racine.getElementsByTagName("driver").item(0);

					nbrConnexions = (Element) racine.getElementsByTagName("nbrconnexion").item(0);

					url = (Element) racine.getElementsByTagName("url").item(0);
				}
			}
		}

		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException ex) {
			ex.printStackTrace();
		} catch (final IOException exx) {
			exx.printStackTrace();
		}

	}

	public Element getLogin() {
		return login;
	}

	public void setLogin(Element login) {
		this.login = login;
	}

	public Element getUrl() {
		return url;
	}

	public void setUrl(Element url) {
		this.url = url;
	}

	public Element getPassword() {
		return password;
	}

	public void setPassword(Element password) {
		this.password = password;
	}

	public Element getDriver() {
		return driver;
	}

	public void setDriver(Element driver) {
		this.driver = driver;
	}

	public Element getNbrConnexions() {
		return nbrConnexions;
	}

	public void setNbrConnexions(Element nbrConnexions) {
		this.nbrConnexions = nbrConnexions;
	}
}
