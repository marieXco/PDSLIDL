package fr.pds.floralis.gui;

import java.io.IOException;
import java.sql.SQLException;

// TODO : comments on WindowUpdate, WindowAdd and the server package

/** 
 * PostgreSQLJDBC
 * Main for the client application
 * Contains the different GUI threads
 * 
 * @author alveslaura
 *
 */


public class PostgreSQLJDBC extends Thread {
	public static void main( String args[] )  throws ClassNotFoundException, SQLException, IOException, InterruptedException  {		
		String host = "127.0.0.1";
		int port = 2412;

		WindowOpening frame2 = new WindowOpening();
		synchronized(frame2.valueWaitEndGetConnection) {
			// We start the window
			frame2.start();

			// We wait for a .notify() form the window
			frame2.valueWaitEndGetConnection.wait();
		}

		MainWindow frame5 = new MainWindow(host, port);	
		synchronized(frame5.valueWait) {
			frame5.start();
			frame5.valueWait.wait();
		}

		// Disconnection Window with no thread because it's the last one and that it will exit the app
		new WindowDisconnect().init();
	}
}