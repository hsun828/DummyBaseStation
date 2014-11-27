package com.tritronics.basestation.dummy.binary.data;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.tritronics.basestation.dummy.utils.ByteUtils;

public abstract class BaseData {
	
	public static final byte DATA_TYPE_REAL_TIME = 0;
	public static final byte DATA_TYPE_FINAL_LAP = 1;
	
	private int length = 0;
	
	@Override
	public String toString() {
		return Arrays.deepToString(getStructure());
	}
	
	public int getByteLength(){
		if(length == 0){
		
			for(int i = 0; i < getStructure().length; i++){
				Object field = getStructure()[i];
				
				if(field instanceof Object[]){
					length += (getTypeLength(field.getClass().getComponentType())) * ((Object[])field).length;
				}else{
					Class T = null;
					if(field instanceof Byte){
						T = Byte.class;
					}else if(field instanceof Short){
						T = Short.class;
					}else if(field instanceof Integer){
						T = Integer.class;
					}else if(field instanceof Character){
						T = Character.class;
					}else if(field instanceof Long){
						T = Long.class;
					}else if(field instanceof Double){
						T = Double.class;
					}else if(field instanceof Float){
						T = Float.class;
					}
					
					length += getTypeLength(T);
				}
			}
			
		}
		
		return length;
	}
	
	public void populate(byte[] sourceData){
		if(sourceData.length != getByteLength()){
			throw new IllegalArgumentException("Source Data Length is not "+ getByteLength());
		}
		Object[] structure = getStructure();
		int byteIndex = 0;
		
		for(int i =0; i < structure.length; i++){
			Object field = structure[i];
			
			if(field instanceof Byte){
				Byte[] result = {0};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Byte[]){
				byteIndex += parseData(sourceData, byteIndex, (Byte[])field);
			}else if(field instanceof Short){
				Short[] result = {0};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Short[]){
				byteIndex += parseData(sourceData, byteIndex, (Short[])field);
			}else if(field instanceof Integer){
				Integer[] result = {0};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Integer[]){
				byteIndex += parseData(sourceData, byteIndex, (Integer[])field);
			}else if(field instanceof Character){
				Character[] result = {0};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Character[]){
				byteIndex += parseData(sourceData, byteIndex, (Character[])field);
			}else if(field instanceof Long){
				Long[] result = {0l};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Long[]){
				byteIndex += parseData(sourceData, byteIndex, (Long[])field);
			}else if(field instanceof Double){
				Double[] result = {0.0};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Double[]){
				byteIndex += parseData(sourceData, byteIndex, (Double[])field);
			}else if(field instanceof Float){
				Float[] result = {0.0f};
				byteIndex += parseData(sourceData, byteIndex, result);
				structure[i] = result[0];
			}else if(field instanceof Float[]){
				byteIndex += parseData(sourceData, byteIndex, (Float[])field);
			}
		}
	}
	
	private int parseData(byte[] sourceData, int byteIndex, Object[] target){
		
		int consumed = 0;
		
		try {
			Field f = target.getClass().getComponentType().getDeclaredField("SIZE");
			int typeLength = f.getInt(null) / Byte.SIZE;
						
			for(int i = 0; i < target.length; i ++){
				byte[] targetBytes = new byte[typeLength];
				System.arraycopy(sourceData, byteIndex + consumed, targetBytes, 0, typeLength);
				consumed += typeLength;
				
				if(target.getClass().getComponentType().equals(Byte.class)){
					target[i] = targetBytes[0];
				}else if(target.getClass().getComponentType().equals(Short.class)){
					target[i] = ByteUtils.getShort(targetBytes);
				}else if(target.getClass().getComponentType().equals(Integer.class)){
					target[i] = ByteUtils.getInt(targetBytes);
				}else if(target.getClass().getComponentType().equals(Float.class)){
					target[i] = ByteUtils.getFloat(targetBytes);
				}else if(target.getClass().getComponentType().equals(Double.class)){
					target[i] = ByteUtils.getDouble(targetBytes);
				}else if(target.getClass().getComponentType().equals(Long.class)){
					target[i] = ByteUtils.getLong(targetBytes);
				}else if(target.getClass().getComponentType().equals(Character.class)){
					target[i] = ByteUtils.getShort(targetBytes);
				}
				
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return consumed;
	}
	
	private int getTypeLength(Class T){
		int typeLength = 0;
		try {
			Field f = T.getDeclaredField("SIZE");
			typeLength = f.getInt(null) / Byte.SIZE;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return typeLength;
	}
	
	protected abstract Object[] getStructure();
	
	
	//1-Free, 2-Breast, 3-Fly, 4-Back, 5-Drill, 6-Kick;
	public static String stroke2String(byte stroke){
		switch(stroke){
		case 1:
			return "Free";
		case 2:
			return "Breast";
		case 3: 
			return "Fly";
		case 4:
			return "Back";
		case 5:
			return "Drill";
		case 6:
			return "Kick";
		default:
			return "Unknown";
		}
	}
}
