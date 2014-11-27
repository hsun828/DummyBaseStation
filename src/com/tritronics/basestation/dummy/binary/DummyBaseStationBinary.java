package com.tritronics.basestation.dummy.binary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tritronics.basestation.dummy.binary.DummySwimmer.SwimmerObserver;
import com.tritronics.basestation.dummy.binary.config.DummyDataConfig;

public class DummyBaseStationBinary {
	public static final int PORT = 8898;
	
	private ExecutorService executorService; 
	private ServerSocket server;
	
	private DummyBaseStationBinary(){
		executorService = Executors.newFixedThreadPool(3); 
	}
	
	private void startService(){
		try {
			server = new ServerSocket(PORT);
			System.out.println("Dummy Base Station started -> " + InetAddress.getLocalHost().getHostAddress() +":"+server.getLocalPort());
			
			while(true){
				Socket s = server.accept();
				System.out.println("Dummy Base Station has a new client connected -> " + s.getInetAddress().getCanonicalHostName()+":" +s.getLocalPort());
				
				File f = new File("data/config.json");
				File abf = new File(f.getAbsolutePath());
				
				DummyDataConfig config;
				
				if(f.exists()){
					config = DummyDataConfig.load("data/config.json");
				}else{
					config = DummyDataConfig.load(abf.getParentFile().getParentFile().getParent()+"/data/config.json");
				}
				
				if(config == null || config.getSwimmerCount() <= 0){
					executorService.execute(new DummyBaseStationTask(s));
				}else{
					new MySwimmerObserver(s, config).begin();
				}
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
		private GregorianCalendar cal;
		private int index = 0;
		
		private DummyBaseStationTask(Socket client){
			this.client = client;
		}
		
		private byte getStroke() {
			return (byte)((index / 10 % 5) + 1);
		}
		
		private long getTs() {
			if(cal == null) {
				cal = new GregorianCalendar();
				cal.set(2014, 8, 1, 5, 30, 0);
				cal.set(Calendar.MILLISECOND, 0);
			}
			
			if(index==40) {
				cal.add(Calendar.MINUTE, 15);
				index++;
			} else if(index == 80) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR, 5);
				index = 0;
			} else {
				cal.add(Calendar.SECOND, 80);
				index++;
			}
			cal.get(Calendar.YEAR);
				
			return cal.getTimeInMillis();
		}
		
		@Override
		public void run() {
			System.out.println("Sending data to client: " + client.getInetAddress().getCanonicalHostName()
					+ "; thread: " + Thread.currentThread().getName());
			int size = 0;
			try {
				while (true) {
					byte dataType = DummyData.randomDataType();
					byte[] data =  DummyData.generateFinalLapData(getTs(), getStroke(), (short)1);
//					byte[] data = dataType == BaseData.DATA_TYPE_REAL_TIME ? 
//							DummyData.generateRealTimeData() : DummyData.generateFinalLapData();
//							
					size += data.length;
//					System.out.println("Sending data to client: " + Arrays.toString(data));
					client.getOutputStream().write(data);
					
					Thread.sleep(200);
					System.out.println("Sending data complete size: " + size);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Client disconnected -> "+ client.getInetAddress().getCanonicalHostName()+":"+client.getLocalPort());
				//e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class MySwimmerObserver implements SwimmerObserver {
		private Socket client;
		private DummyDataConfig config;
		private ArrayList<DummySwimmer> swimmers = new ArrayList<DummySwimmer>();
		
		private MySwimmerObserver(Socket client, DummyDataConfig config){
			this.client = client;
			this.config = config;
		}
		
		private void begin(){
			for(int i = 0; i < config.getSwimmerCount(); i++){
				DummySwimmer ds = new DummySwimmer(config, i+1 , this);
				swimmers.add(ds);
				ds.begin();
			}
		}
		
		@Override
		public void onFinalLap(int unitID, byte[] data) {
			try {
				client.getOutputStream().write(data);
			} catch (IOException e) {
				System.out.println("Client disconnected -> "+ client.getInetAddress().getCanonicalHostName()+":"+client.getLocalPort());
				for(DummySwimmer ds : swimmers){
					ds.stop();
				}
				//e.printStackTrace();
			}
		}

		@Override
		public void onRealTime(int unitID, byte[] data) {
			
		}
		
	}
	
	public static void main(String[] args){
		new DummyBaseStationBinary().startService();
		
	}
}
