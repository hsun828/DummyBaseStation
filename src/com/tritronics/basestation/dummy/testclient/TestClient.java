package com.tritronics.basestation.dummy.testclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {

	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int PORT = 8899;
	
	private TestClient(){
		
	}
	
	private void startNewClient(){
		try {
			Socket client = new Socket(SERVER_ADDRESS, PORT);
			
			new ClientThread(client).start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	private class ClientThread extends Thread{
		
		private Socket client;
		
		private ClientThread (Socket client){
			this.client = client;
		}
		
		@Override
		public void run() {
			BufferedReader in;
			String line;
			try {
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				while((line = in.readLine()) != null){
					System.out.println(line);
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		new TestClient().startNewClient();
	}
}
