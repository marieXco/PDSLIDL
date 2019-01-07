package configuration;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ReadXml {
	{
    /*
     * Etape 1 : récupération d'une instance de la classe "DocumentBuilderFactory"
     */
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	
    try {
        /*
         * Etape 2 : création d'un parseur
         */
        final DocumentBuilder builder = factory.newDocumentBuilder();
		
    /*
     * Etape 3 : création d'un Document
     */
    final Document document= builder.parse(new File("repertoire.xml"));
		
    //Affichage du prologue
    System.out.println("*************PROLOGUE************");
    System.out.println("version : " + document.getXmlVersion());
    System.out.println("encodage : " + document.getXmlEncoding());		
        System.out.println("standalone : " + document.getXmlStandalone());
				
    /*
     * Etape 4 : récupération de l'Element racine
     */
    final Element racine = document.getDocumentElement();
	
    //Affichage de l'élément racine
    System.out.println("\n*************RACINE************");
    System.out.println(racine.getNodeName());
	
    //TODO : boucle pour récupérer les noeuds Marie
    
	    }
   
    catch (final ParserConfigurationException e) {
        e.printStackTrace();
    }
    catch (final SAXException ex) {
        ex.printStackTrace();
    }
    catch (final IOException exx) {
        exx.printStackTrace();
    }		
}
}

   


