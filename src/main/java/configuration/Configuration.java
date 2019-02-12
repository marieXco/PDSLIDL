package configuration;

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

		System.out
				.println(clogin + curl + cdriver + cpassword + cnbrConnexions);

	}

	// surement une liste de parametre
	// this.driver = nomdemaliste.get(index);

	// TODO : constructeur
	// instancier dans un att
	// refractor configuration
	// methode pour close sql

	public static void main(final String[] args) {
		Configuration configuration = new Configuration();

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
