package com.tritronics.basestation.dummy.binary;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tritronics.basestation.dummy.binary.config.DummyDataConfig;
import com.tritronics.basestation.dummy.binary.config.DummySessionConfig;
import com.tritronics.basestation.dummy.binary.data.BaseData;

public class DummySwimmer{
	interface SwimmerObserver{
		public void onFinalLap(int unitID, byte[] data);
		public void onRealTime(int unitID, byte[] data);
	}
	
	
	private int unitID;
	private DummyDataConfig config;
	private float currentLap = 0;
	private int currentSession = 0;
	private ScheduledExecutorService executor;
	private SwimmerObserver observer;
	private boolean isStop = false;
	private RealTimeWatch rtw;
	
	
	DummySwimmer(DummyDataConfig config, int unitID, SwimmerObserver observer){
		this.config = config;
		this.unitID = unitID;
		this.observer = observer;
		executor =  Executors.newScheduledThreadPool(1);
		rtw = new RealTimeWatch();
	}
	
		
	void begin(){
		executor.execute(new Swim());
	}
	
	void stop(){
		isStop = true;
	}
	
	private static final int REAL_TIME_DATA_INTERVAL = 5;
	private class Swim implements Runnable{
		private static final int STATUS_NOT_STARTED = 0;
		private static final int STATUS_STARTED 	= 1;
		private static final int STATUS_REST 		= 2;
		private static final int STATUS_LAST_HALF 	= 3;
		private static final int STATUS_FINISHED 	= 4;
		
		
		private int status = STATUS_NOT_STARTED;
		private int sessionWholeLap = 0;
		private boolean sessionExtraHalf = false;
		private DummySessionConfig session;
		
		private void initSession(){
			session = config.getSessions().get(currentSession);
			int halfLap = (int)(session.getDistance() / config.getPoolLength()); 
			
			sessionWholeLap = halfLap / 2;
			sessionExtraHalf = halfLap % 2 == 1;
		}
		
		@Override
		public void run() {
			if(isStop){
				System.out.println("Swimmer ["+unitID+"] has stopped.");
				return;
			}
			
			switch(status){
			case STATUS_NOT_STARTED:
				initSession();
				currentLap = 0;
				System.out.println("Swimmer ["+unitID+"] prepare session Stroke: " + BaseData.stroke2String((byte)session.getStroke()));
				if(sessionWholeLap <= 0){
					status = sessionExtraHalf? STATUS_LAST_HALF : STATUS_FINISHED;
				}else{
					status = STATUS_STARTED;
				}
				executor.execute(this);
				break;
			case STATUS_STARTED:
				executor.schedule(this, session.getInterval(), TimeUnit.SECONDS);
				rtw.session = session;
				executor.schedule(rtw, REAL_TIME_DATA_INTERVAL, TimeUnit.SECONDS);
				System.out.println("Swimmer ["+unitID+"] started Stroke: " + BaseData.stroke2String((byte)session.getStroke()) +" lap: " + (currentLap + 1));
				status = STATUS_REST;
				break;
			case STATUS_REST:
				executor.schedule(this, session.getRest(), TimeUnit.SECONDS);
				System.out.println("Swimmer ["+unitID+"] rested Stroke: " + BaseData.stroke2String((byte)session.getStroke()) 
						+" lap: " + (currentLap + (sessionWholeLap > 0 ? 1 :0)));
				byte[] b = DummyData.generateFinalLapData(System.currentTimeMillis(), (byte)session.getStroke(), (short)unitID);
				
				if(observer != null){
					observer.onFinalLap(unitID, b);
				}
				status = STATUS_FINISHED;
				break;
			case STATUS_LAST_HALF:
				System.out.println("Swimmer ["+unitID+"] started last half Stroke: " + BaseData.stroke2String((byte)session.getStroke()) 
						+" lap: " + (currentLap + 0.5 + (sessionWholeLap > 0 ? 1 :0)));
				currentLap += 0.5;
				executor.schedule(this, session.getInterval()/2, TimeUnit.SECONDS);
				sessionExtraHalf = false;
				status = STATUS_REST;
				break;
			case STATUS_FINISHED:
				
				if(currentLap < sessionWholeLap - 1){
					currentLap ++;
					status = STATUS_STARTED;
					executor.execute(this);
					System.out.println("Swimmer ["+unitID+"] finished Stroke: " + BaseData.stroke2String((byte)session.getStroke()) +" lap: " + (currentLap + 1));
				}else if(sessionExtraHalf){
					status = STATUS_LAST_HALF;
					executor.execute(this);
				}else if(currentSession < config.getSessions().size() - 1){
					System.out.println("Swimmer ["+unitID+"] finished Stroke: "+ BaseData.stroke2String((byte)session.getStroke()));
					currentSession ++;
					status = STATUS_NOT_STARTED;
					executor.execute(this);
				}else{
					System.out.println("Swimmer ["+unitID+"] finished all assigned sessions.");
					//Reset and restart
					currentSession = 0;
					currentLap = 0;
					status = STATUS_NOT_STARTED;
					executor.execute(this);
				}
				break;
			}
		}
	} 
	
	private class RealTimeWatch implements Runnable{
		private DummySessionConfig session;
		
		@Override
		public void run() {
			if(isStop){
				return;
			}
			byte[] rtd =  DummyData.generateRealTimeData((short)unitID, (byte)session.getStroke());
			System.out.println("Swimmer ["+unitID+"] real time data:" + Arrays.toString(rtd));
			if(observer != null){
				observer.onFinalLap(unitID, rtd);
			}
			executor.schedule(this, REAL_TIME_DATA_INTERVAL, TimeUnit.SECONDS);
		}
	}
	
	public static void main(String[] args){
		DummyDataConfig config = DummyDataConfig.load("data/config.json");
		DummySwimmer swimmer = new DummySwimmer(config, 0, null);
		DummySwimmer swimmer2 = new DummySwimmer(config, 1, null);
		DummySwimmer swimmer3 = new DummySwimmer(config, 2, null);
		DummySwimmer swimmer4 = new DummySwimmer(config, 3, null);
		DummySwimmer swimmer5 = new DummySwimmer(config, 4, null);
		swimmer.begin();
//		swimmer2.begin();
//		swimmer3.begin();
//		swimmer4.begin();
//		swimmer5.begin();
	}
	
}
