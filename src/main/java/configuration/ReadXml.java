package configuration;

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

public class ReadXml {
	
	Element login, url, password, driver, nbrConnexions;
	
	{
		
		
		/*
		 * Etape 1 : récupération d'une instance de la classe
		 * "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		
		

		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : création d'un Document
			 */
			final Document document = builder.parse(new File("src/main/resources/configuration/Configuration.xml"));

			// Affichage du prologue
			System.out.println("*************PROLOGUE************");
			System.out.println("version : " + document.getXmlVersion());
			System.out.println("encodage : " + document.getXmlEncoding());
			

			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();

			// Affichage de l'élément racine
			System.out.println("\n*************RACINE************");
			System.out.println(racine.getNodeName());
		
			

			// TODO : boucle pour récupérer les noeuds Marie
			System.out.println("\n*************NOEUDS************");
		
			
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();
			
			for (int i = 0; i<2; i++) {

	            if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {

	            	final Element serveur = (Element) racineNoeuds.item(i);
	            	
	            	login = (Element) racine.getElementsByTagName("login").item(0);

	                password = (Element) racine.getElementsByTagName("password").item(0);
	                
	                driver = (Element) racine.getElementsByTagName("driver").item(0);
	                
	                nbrConnexions = (Element) racine.getElementsByTagName("nbrconnexion").item(0);
	                
	                url = (Element) racine.getElementsByTagName("url").item(0);
	                
	                
	                

	             
	                


	            }}
			
			  

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
