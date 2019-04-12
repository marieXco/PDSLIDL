package fr.pds.floralis.gui;

import java.io.IOException;
import java.sql.SQLException;

public class PostgreSQLJDBC extends Thread {

	public static void main( String args[] )  throws ClassNotFoundException, SQLException, IOException, InterruptedException  {		
		String host = "127.0.0.1";
		int port = 2412;
		
		//Fenêtre qui montre le logo
		WindowOpening frame2 = new WindowOpening();
		synchronized(frame2.valueWaitEndGetConnection) {
			System.out.println("WindowOpening waiting");
			//On la lance
			frame2.start();

			//On attend tant qu'un notify n'a pas été lancé du côté de la frame2
			frame2.valueWaitEndGetConnection.wait();
		}

		MainWindow frame5 = new MainWindow(host, port);	
		synchronized(frame5.valueWait) {
			//On la lance
			frame5.start();
			
			//On attend tant qu'un notify n'a pas été lancé du côté de la frame2
			frame5.valueWait.wait();
		}

		// Fenêtre de deconnection sans thread car 
		new WindowDisconnect(host, port).init();
	}
}