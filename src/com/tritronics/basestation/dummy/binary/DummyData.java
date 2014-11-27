package com.tritronics.basestation.dummy.binary;

import java.util.Arrays;
import java.util.Random;

import com.tritronics.basestation.dummy.binary.data.BaseData;
import com.tritronics.basestation.dummy.utils.ByteUtils;

public class DummyData {
	

	
	private static final float DATA_TYPE_RATE_REAL_TIME = 0.3f;

	private static Random RDM = new Random();
	private static final short MIN_V = 20;
	private static final short MAX_V = 30;
	private static final short MIN_HR = 100;
	private static final short MAX_HR = 160;
	private static final short MIN_LAP = 1;
	private static final short MAX_LAP = 10;
	private static final short MIN_LT = 40;
	private static final short MAX_LT = 60;
	private static final short V_HR_PROFILE_LENGTH = 32;
	
	public static byte[] generateRealTimeData(short unitId, byte stroke){
		
		byte type;
		short unitid;
		//byte stroke;
		short velocity;
		short heartrate;
		short lap;
		
		type 		= BaseData.DATA_TYPE_REAL_TIME;
		unitid 		= unitId;
		//stroke 		= stroke;
		velocity 	= generateRdmShort(MIN_V, MAX_V);
		heartrate 	= generateRdmShort(MIN_HR, MAX_HR);
		lap 		= generateRdmShort(MIN_LAP, MAX_LAP);
		
		System.out.println(type+","+unitid+","+stroke+","+velocity+","+heartrate+","+lap);
		
		return ByteUtils.combineBytes(
				ByteUtils.getBytes(type),
				ByteUtils.getBytes(unitid),
				ByteUtils.getBytes(stroke),
				ByteUtils.getBytes(velocity),
				ByteUtils.getBytes(heartrate),
				ByteUtils.getBytes(lap)
				);
		
		
	}
	
	public static byte[] generateFinalLapData(long ts, byte stk, short uid){
		
		byte type;
		short unitid;
		byte stroke;
		long time;
		short laptime;
		short[] velocity;
		short[] heartrate;
		short lap;
		
		type 		= BaseData.DATA_TYPE_FINAL_LAP;
		unitid 		= uid;
		stroke 		= stk;
		time		= ts;//System.currentTimeMillis();//generateRdmLong(System.currentTimeMillis() - 10000, System.currentTimeMillis());
		laptime		= generateRdmShort(MIN_LT, MAX_LT);
		velocity 	= generateRdmShortArray(MIN_V, MAX_V, V_HR_PROFILE_LENGTH);
		heartrate 	= generateRdmShortArray(MIN_HR, MAX_HR, V_HR_PROFILE_LENGTH);
		lap 		= generateRdmShort(MIN_LAP, MAX_LAP);
		
		System.out.println(type+","+unitid+","+stroke+","+time+","+laptime+","+Arrays.toString(velocity)+","+Arrays.toString(heartrate)+","+lap);
		
		return ByteUtils.combineBytes(
				ByteUtils.getBytes(type),
				ByteUtils.getBytes(unitid),
				ByteUtils.getBytes(stroke),
				ByteUtils.getBytes(time),
				ByteUtils.getBytes(laptime),
				ByteUtils.getBytes(velocity),
				ByteUtils.getBytes(heartrate),
				ByteUtils.getBytes(lap)
				);
		
	}
	
	public static byte randomDataType(){
		float rdm = RDM.nextFloat();
		
		return rdm > DATA_TYPE_RATE_REAL_TIME ? BaseData.DATA_TYPE_FINAL_LAP : BaseData.DATA_TYPE_REAL_TIME;
	}

	private static short generateRdmShort(short min, short max){
		int value = RDM.nextInt(max-min);
		return (short) ((short)value + min);
		
	}
	
	private static short[] generateRdmShortArray(short min, short max, int length){
		short[] result = new short[length];
		
		for(int i = 0; i < length; i++){
			result[i] = generateRdmShort(min, max);
		}
		
		return result;
	}
	
	private static long generateRdmLong(long min, long max){
		int value = RDM.nextInt((int) (max-min));
		return value + min;
		
	}

	public static void main(String[] args){
//		for(int i = 0; i < 10; i++){
//			byte[] b1 = generateRealTimeData();
//			System.out.println(b1.length);
//			System.out.println(Arrays.toString(b1));
//			
//			byte[] b2 = generateFinalLapData();
//			System.out.println(b2.length);
//			System.out.println(Arrays.toString(b2));
//		}
		byte[] b = {0};
		
		System.out.println(b.getClass().getComponentType());
	}
}
