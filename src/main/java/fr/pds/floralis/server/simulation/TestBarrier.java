package fr.pds.floralis.server.simulation;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestBarrier {





	public static void main (String[] args) throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException, BrokenBarrierException {

		final CyclicBarrier gate = new CyclicBarrier(3);

		Thread t1 = new Thread(){
			public void run(){
				try {
					gate.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());
				
				System.out.println("hele" + Calendar.getInstance().getTime());System.out.println("hele" + Calendar.getInstance().getTime());
				System.out.println("hele" + Calendar.getInstance().getTime());System.out.println("hele" + Calendar.getInstance().getTime());System.out.println("hele" + Calendar.getInstance().getTime());System.out.println("hele" + Calendar.getInstance().getTime());System.out.println("hele" + Calendar.getInstance().getTime());
			}}; 
			Thread t2 = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("OFOF"  + Calendar.getInstance().getTime());
				}};
				t1.start();
				t2.start();

				// At this point, t1 and t2 are blocking on the gate. 
				// Since we gave "3" as the argument, gate is not opened yet.
				// Now if we block on the gate from the main thread, it will open
				// and all threads will start to do stuff!

				gate.await();
				System.out.println("all threads started");


	}}