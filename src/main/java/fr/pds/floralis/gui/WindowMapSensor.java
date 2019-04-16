package fr.pds.floralis.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * @Author : Emilie
 * Vue pour visualise la map des capteurs 
 */
public class WindowMapSensor extends JFrame implements ActionListener{

	/**
	 * generated serial version
	 */
	private static final long serialVersionUID = 7522460106690315659L;


	/*
	 * Container
	 */
	JPanel container = new JPanel();

	/*
	 * JPanel pour afficher les sensors au clic d'une Location
	 */
	JPanel sensorsList = new JPanel();

	/*
	 * Pour mettre les capteurs
	 */
	JPanel sensorsnoconfig = new JPanel();

	/*
	 * Liste de tous les polygones qui seront la map
	 */
	ArrayList<PolygonLocation> polygonsList = new ArrayList<PolygonLocation>();

	/*
	 * polygon (location) cliqué (pour ensuite afficher les infos)
	 */
	private PolygonLocation clicked = null; // instancié à null ?

	/*
	 * tableaux des points pour tracer les lignes
	 */
	private ArrayList<Integer> intLines = new ArrayList<Integer>();

	public WindowMapSensor(String host, int port) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void initMap(){
		this.setTitle("Map");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	

	/*
	 * constructeur de la map
	 */
//	public WindowMapSensor() {
//		this.polygonsList = polygonsList;
//		// il faudrat ajouter les capteurs et la partie "popup" pour quand on
//		// cliquera sur une location
//		// peut être juste un autre jPanel qui refresh au moment du clic sur la
//		// location
//	}
	



	// TODO : methode pour tracer les points à partir des listes de points soit
	// intLines

}
