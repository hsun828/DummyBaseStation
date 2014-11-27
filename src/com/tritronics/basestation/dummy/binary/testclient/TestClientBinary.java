package com.tritronics.basestation.dummy.binary.testclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.tritronics.basestation.dummy.binary.data.BaseData;
import com.tritronics.basestation.dummy.binary.data.FinalLapData;
import com.tritronics.basestation.dummy.binary.data.RealTimeData;
import com.tritronics.basestation.dummy.utils.ByteUtils;

public class TestClientBinary {

	private static final String SERVER_ADDRESS = "127.0.0.1";
	private static final int PORT = 8898;
	
	private TestClientBinary(){
		
	}
	
	private void startNewClient(){
		try {
			Socket client = new Socket(SERVER_ADDRESS, PORT);
			
			new ClientThread(client).start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
			try {
				InputStream in = client.getInputStream();
				int header = -1;
				while((header = in.read()) != -1){
					byte dataType = (byte) (header & 0xff);
					if(dataType == BaseData.DATA_TYPE_REAL_TIME){
						RealTimeData rt = new RealTimeData();
						byte[] b = new byte[rt.getByteLength() - 1];
						in.read(b);
						byte[] data = ByteUtils.combineBytes(ByteUtils.getBytes(dataType), b);
						
						System.out.println("Received [RT] data:" +Arrays.toString(data));
						
						rt.populate(data);
						
						System.out.println("Converted [RT] data:" + rt.toString());
					}else if(dataType == BaseData.DATA_TYPE_FINAL_LAP){
						FinalLapData fl = new FinalLapData();
						byte[] b = new byte[fl.getByteLength() - 1];
						in.read(b);
						byte[] data = ByteUtils.combineBytes(ByteUtils.getBytes(dataType), b);
						
						System.out.println("Received [FL] data:" +Arrays.toString(data));
						
						fl.populate(data);
						
						System.out.println("Converted [FL] data:" + fl.toString());
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		new TestClientBinary().startNewClient();
	}
}
