package fr.pds.floralis.gui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class PostgreSQLJDBC extends Thread {
	

	public static void main( String args[] )  throws ClassNotFoundException, SQLException, IOException, InterruptedException  {
		JDBCConnectionPool jdbc = OpenDatabase.database();
		Connection connect = OpenDatabase.databaseConnection(jdbc);
		
		//Window that waits for the connection 
		WindowOpening frame2 = new WindowOpening(jdbc, connect);
		try {
			synchronized(frame2.valueWaitEndGetConnection) {
				System.out.println("WindowOpening waiting");
				frame2.start();
				frame2.valueWaitEndGetConnection.wait();
			}
			} catch (InterruptedException e) {
				System.out.println("Pas fini");
			}
		

		WindowConnection frame3 = new WindowConnection(jdbc, connect);
		synchronized(frame3.valueWaitConnection) {
			System.out.println("WindowWorker waiting");
			frame3.start();		
			frame3.valueWaitConnection.wait();
		}
		
//		new WindowAdd(jdbc, connect).initAddPersonnel();

//		WindowConfirm frame4 = new WindowConfirm(jdbc, connect);
//		frame4.init("supprimer définitivement ces patients");
	
		WindowWorker frame5 = new WindowWorker(jdbc, connect);	
		synchronized(frame5.valueWait) {
			System.out.println("WindowWorker waiting");
			frame5.start();
			frame5.valueWait.wait();
		}
		
		// Window disconnect
//		new WindowDisconnect(jdbc, connect).init();
	}
}