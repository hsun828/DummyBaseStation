package com.tritronics.basestation.dummy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DummyBaseStation {
	public static final int PORT = 8899;
	public static String DATA_FILE = "./data/rawdata.dat";
	public static String FILE_FORMAT = "UTF-8";
	
	private ExecutorService executorService; 
	private ServerSocket server;
	
	private DummyBaseStation(){
		executorService = Executors.newFixedThreadPool(3); 
	}
	
	private void startService(){
		try {
			server = new ServerSocket(PORT);
			System.out.println("Dummy Base Station started.");
			
			while(true){
				Socket s = server.accept();
				System.out.println("Dummy Base Station has a new client connected.");
				
				executorService.execute(new DummyBaseStationTask(s));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DummyBaseStationTask implements Runnable{

		private Socket client;
		
		private DummyBaseStationTask(Socket client){
			this.client = client;
		}
		
		@Override
		public void run() {
			InputStream    fis;
			BufferedReader br;
			String         line;
			System.out.println("Sending data to client: " + client.getInetAddress().getCanonicalHostName()
					+ "; thread: " + Thread.currentThread().getName());
			try {
				fis = new FileInputStream(DATA_FILE);
				br = new BufferedReader(new InputStreamReader(fis, Charset.forName(FILE_FORMAT)));
				int size = 0;
				while ((line = br.readLine()) != null) {
					byte[] data = (line+"\r").getBytes(FILE_FORMAT);
					size += data.length;
					client.getOutputStream().write(data);
				}
				System.out.println("Sending data complete size: " + size);
				client.close();
				br.close();
				br = null;
				fis = null;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	public static void main(String[] args){
		new DummyBaseStation().startService();
	}
}
