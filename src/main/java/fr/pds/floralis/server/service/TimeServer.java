package fr.pds.floralis.server.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class TimeServer {

	private int port = 2412;
	private String host = "127.0.0.1";
	private ServerSocket server = null;
	private boolean isRunning = true;


	public TimeServer(){
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TimeServer(String pHost, int pPort){
		host = pHost;
		port = pPort;
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * open
	 * Class made to open our server
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void open() throws ClassNotFoundException, SQLException{
		JDBCConnectionPool jdbc = DataSource.createPool();
		
		Thread t = new Thread(new Runnable(){
			public void run(){
				while(isRunning == true){
					try {
						
						/**
						 * Listening for a connection and accepting it
						 */
						Socket client = server.accept();
						System.out.println("Connexion cliente reçue.");   
						
						/**
						 * Creating a new Thread where we launch a RequestHandler and launching it
						 */
						Thread t = new Thread(new RequestHandler(client, jdbc, DataSource.getConnectionFromPool(jdbc)));
						t.start();

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						System.out.println("Aucune connexon est disponible, veuillez patienter");
						// TODO : adding this messsage on a window
						e.printStackTrace();
					}
				}

				/**
				 * We close the socket once all the thread are closed
				 */
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
					server = null;
				}
			}
		});
		
		/**
		 * We run the run method that is contained in our thread
		 */
		t.start();
	}
	
	/**
	 * FIXME : Here but never used ? 
	 */
	public void close(){
		isRunning = false;
	}   
}
