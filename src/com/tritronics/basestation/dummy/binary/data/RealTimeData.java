package com.tritronics.basestation.dummy.binary.data;

import com.tritronics.basestation.dummy.binary.DummyData;


public class RealTimeData extends BaseData{
	
	private byte type;
	private short unitid;
	private byte stroke;
	private short velocity;
	private short heartrate;
	private short lap;
	
	private Object[] structure = 
		{	
			type,
			unitid,
			stroke,
			velocity,
			heartrate,
			lap
		};
	
	
	@Override
	public Object[] getStructure() {
		return structure;
	}

	public byte getType() {
		return type;
	}

	public short getUnitid() {
		return unitid;
	}

	public byte getStroke() {
		return stroke;
	}

	public short getVelocity() {
		return velocity;
	}

	public short getHeartrate() {
		return heartrate;
	}

	public short getLap() {
		return lap;
	}
	
	public static void main(String args[]){
		RealTimeData rt = new RealTimeData();
//		rt.populate(DummyData.generateRealTimeData());
		System.out.println(rt.toString());
	}
}
