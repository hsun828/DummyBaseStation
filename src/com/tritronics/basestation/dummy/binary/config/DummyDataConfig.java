package com.tritronics.basestation.dummy.binary.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DummyDataConfig {

	private long swimmerCount;
	private long poolLength;
	
	private ArrayList<DummySessionConfig> sessions = new ArrayList<DummySessionConfig>();
	
	public long getPoolLength() {
		return poolLength;
	}

	public long getSwimmerCount() {
		return swimmerCount;
	}

	public ArrayList<DummySessionConfig> getSessions() {
		return sessions;
	}

	public static DummyDataConfig load(String file){
		String jsonText = readFile(file);
		return parseJson(jsonText);
	}

	private static DummyDataConfig parseJson(String jsonText) {
		JSONParser parser = new JSONParser();
		DummyDataConfig config = null;
		try {
			config = new DummyDataConfig();
			JSONObject jo = (JSONObject)parser.parse(jsonText);
			config.swimmerCount = (Long)jo.get("SwimmerCount");
			config.poolLength = (Long)jo.get("PoolLength");
			JSONArray jArr = (JSONArray)jo.get("Sessions");
			
			for(Object o : jArr){
				DummySessionConfig dsd = new DummySessionConfig();
				JSONObject node = (JSONObject)o;
				dsd.setStroke((Long)node.get("Stroke"));
				dsd.setInterval((Long)node.get("Interval"));
				dsd.setDistance((Long)node.get("Distance"));
				dsd.setRest((Long)node.get("Rest"));
				
				config.sessions.add(dsd);
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return config;
	}

	private static String readFile(String file) {
		BufferedReader reader = null;
		String laststr = "";
		try{
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				if(tempString.startsWith("//")||tempString.startsWith("/*")
						||tempString.startsWith("*/")||tempString.startsWith("*")){
					continue;
				}
				laststr += tempString;
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try {
						reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return laststr;
	}
	
	public static void main(String[] args){
		DummyDataConfig config = DummyDataConfig.load("data/config.json");
		System.out.print(config.poolLength);
		
	}

}
