package fr.pds.floralis.gui;


import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Passage;

/*
 * La vue pour dessiner les polygones soit les parties de la map qui seront dans WindowMapSensor
 *TODO : un script avec toutes les données et points des bâtiments etc 
 */
public class PolygonLocation extends Polygon {

	/**
	 * generated serial version
	 */
	private static final long serialVersionUID = -6623451885040662789L;

	/*
	 * localisation de l'emplacement pour pouvoir tracer les polygones TODO :
	 * reprendre la classe location pour vérifier que les étages etc sont bien
	 * représentés
	 */
	private Location location;
	private int locationId;

	/*
	 * localisation du passage pour pouvoir tracer les polygones TODO :
	 * reprendre la classe Passagr pour vérifier que les étages etc sont bien
	 * représentés
	 */
	private Passage passage;
	private int passageId;

	/*
	 * tableaux des coordonnées
	 */
	private int[] new_x;
	private int[] new_y;
	private int total;

	// //minimum à 4 points mais peut être pas nécessaire de le laisser pcq
	// étend polygone
	// private static final int MIN_LENGTH = 4;

	// /*
	// * Rectangle : peut être NULL
	// */
	// //TODO : le laisser ?
	// protected Rectangle bounds;

	/*
	 * calcul de l'air pour voir si les capteurs peuvent y être ou non
	 */
	private float surface; // initialisé ?

	/*
	 * Boolean pour savoir si la piece ou le passage est selectionné initialisé
	 * à false
	 */
	private Boolean selected = false;

	/*
	 * Boolean pour savoir si un capteur est recherché initialisé à false
	 */
	private Boolean research = false;

	// /**
	// * Creates an empty polygon.
	// *
	// * @since 1.0
	// */
	// public PolygonLocation() {
	// xpoints = new int[MIN_LENGTH];
	// ypoints = new int[MIN_LENGTH];
	// }

	// les constructeurs soit à partir de coordonnées soit à partir de
	// liste de points
	// On rempli la liste avec les coordonnées
	//TODO : récupérer les points des location et passage 
	public PolygonLocation(ArrayList<Point> points) {
		for (Point point : points) {
			// utilisation de la classe Point pour ajouter les points
			// la classe Point a deux arguments x et y
			this.addPoint(point.x, point.y);
		}
	}

	/*
	 * Constructeur : tableau de coordonnées x tableau de coordonnées y nombre
	 * total de points
	 */
	public PolygonLocation(int[] xpoints, int[] ypoints, int total) {
		// TODO : vérifier si doit rester un constructeur ou une méthode
		// // total = xpoints.length + ypoints.length;
		// // Fix 4489009: should throw IndexOutofBoundsException instead
		// // of OutofMemoryException if npoints is huge and >
		// {x,y}points.length
		// if (total > xpoints.length || total > ypoints.length) {
		// throw new IndexOutOfBoundsException("total > xpoints.length || "
		// + "total > ypoints.length");
		// }
		// // Fix 6191114: should throw NegativeArraySizeException with
		// // negative npoints
		// if (total < 0) {
		// throw new NegativeArraySizeException("total < 0");
		// }
		// // Fix 6343431: Applet compatibility problems if arrays are not
		// // exactly npoints in length
		// this.total = total;
		// this.xpoints = Arrays.copyOf(xpoints, total);
		// this.ypoints = Arrays.copyOf(ypoints, total);

		/*
		 * 
		 * En fait pas besoin : là copie du constructeur de Polygone donc si on
		 * l'étend on peut utiliser super()
		 */
		super(xpoints, ypoints, total);

	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public Passage getPassage() {
		return passage;
	}

	public void setPassage(Passage passage) {
		this.passage = passage;
	}

	public int getPassageId() {
		return passageId;
	}

	public void setPassageId(int passageId) {
		this.passageId = passageId;
	}

	public int[] getNew_x() {
		return new_x;
	}

	public void setNew_x(int[] new_x) {
		this.new_x = new_x;
	}

	public int[] getNew_y() {
		return new_y;
	}

	public void setNew_y(int[] new_y) {
		this.new_y = new_y;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public float getSurface() {
		return surface;
	}

	public void setSurface(float surface) {
		this.surface = surface;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getResearch() {
		return research;
	}

	public void setResearch(Boolean research) {
		this.research = research;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// TODO methode pour dessiner le polygone
	// TODO methode pour redessiner le polygone --> repaint

}
