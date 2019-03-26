package fr.pds.floralis.gui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class PostgreSQLJDBC extends Thread {

	//TODO : enlever les appels BDD inutiles et enlever les classes avec

	public static void main( String args[] )  throws ClassNotFoundException, SQLException, IOException, InterruptedException  {
		//On ouvre la BDD
		JDBCConnectionPool jdbc = OpenDatabase.database();

		//On récupère une connection
		Connection connect = OpenDatabase.databaseConnection(jdbc);

		//Fenêtre qui montre le logo
		WindowOpening frame2 = new WindowOpening(jdbc, connect);
		synchronized(frame2.valueWaitEndGetConnection) {
			System.out.println("WindowOpening waiting");
			//On la lance
			frame2.start();

			//On attend tant qu'un notify n'a pas été lancé du côté de la frame2
			frame2.valueWaitEndGetConnection.wait();
		}


		WindowConnection frame3 = new WindowConnection(jdbc, connect);
		synchronized(frame3.valueWaitConnection) {
			//On la lance
			frame3.start();		

			//On attend tant qu'un notify n'a pas été lancé du côté de la frame2
			frame3.valueWaitConnection.wait();
		}


		WindowWorker frame5 = new WindowWorker(jdbc, connect);	
		synchronized(frame5.valueWait) {
			//On la lance
			frame5.start();
			
			//On attend tant qu'un notify n'a pas été lancé du côté de la frame2
			frame5.valueWait.wait();
		}

		// Fenêtre de deconnection sans thread car 
		new WindowDisconnect(jdbc, connect).init();
	}
}