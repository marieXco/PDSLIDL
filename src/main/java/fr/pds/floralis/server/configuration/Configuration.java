package fr.pds.floralis.server.configuration;

/**
 * Configuration
 * 
 * This class is made to get the XML parameters 
 * (with our XML parser ReadXML) and 
 * to give them to our JDBCConnectionPool
 *
 */

public class Configuration extends ReadXml {
	String clogin;
	String curl, cpassword, cdriver;
	String cnbrConnexions;

	public Configuration() {
		super();
		this.clogin = (String) super.getLogin().getTextContent();
		this.curl = (String) super.getUrl().getTextContent();
		this.cdriver = (String) super.getDriver().getTextContent();
		this.cpassword = (String) super.getPassword().getTextContent();
		this.cnbrConnexions = (String) super.getNbrConnexions()
				.getTextContent();
	}


	public String getClogin() {
		return clogin;
	}

	public void setClogin(String clogin) {
		this.clogin = clogin;
	}

	public String getCurl() {
		return curl;
	}

	public void setCurl(String curl) {
		this.curl = curl;
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	public String getCdriver() {
		return cdriver;
	}

	public void setCdriver(String cdriver) {
		this.cdriver = cdriver;
	}

	public String getCnbrConnexions() {
		return cnbrConnexions;
	}

	public void setCnbrConnexions(String cnbrConnexions) {
		this.cnbrConnexions = cnbrConnexions;
	}

}
