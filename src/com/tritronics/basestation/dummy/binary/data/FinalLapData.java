package com.tritronics.basestation.dummy.binary.data;

import com.tritronics.basestation.dummy.binary.DummyData;

public class FinalLapData extends BaseData{

	private byte type;
	private short unitid;
	private byte stroke;
	private long time;
	private short laptime;
	private Short[] velocities = new Short[32];
	private Short[] heartrates = new Short[32];
	private short lap;
	
	private Object[] structure = 
		{	
			type,
			unitid,
			stroke,
			time,
			laptime,
			velocities,
			heartrates,
			lap
		};
	
	@Override
	protected Object[] getStructure() {
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

	public long getTime() {
		return time;
	}

	public short getLaptime() {
		return laptime;
	}

	public Short[] getVelocities() {
		return velocities;
	}

	public Short[] getHeartrates() {
		return heartrates;
	}

	public short getLap() {
		return lap;
	}
	
	public static void main(String args[]){
//		FinalLapData fl = new FinalLapData();
//		fl.populate(DummyData.generateFinalLapData(System.currentTimeMillis());
//		System.out.println(fl.toString());
	}
}
