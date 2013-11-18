package resource;

import java.util.ArrayList;
import java.util.HashMap;

public class Dictionary extends Resource {
	protected HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	protected HashMap<String, ArrayList<String>> reverseMap = new HashMap<String, ArrayList<String>>();
	
	public Dictionary(String path) {
		super(path);
		
		load();
	}

	@Override
	public void updateMap(String line) {
		String key = extractKey(line);
		String value = extractValue(line);
		
		// map
		
		if (!this.map.containsKey(key)) {
			this.map.put(key, new ArrayList<String>());
		}
		
		this.map.get(key).add(value);
		
		// reverse map
		
		if (!this.reverseMap.containsKey(value)) {
			this.reverseMap.put(value, new ArrayList<String>());
		}
		
		this.reverseMap.get(value).add(key);
	}

	protected String extractKey(String line){
		return line.substring(0, line.indexOf("::") - 1);
	}

	protected String extractValue(String line){
		return line.substring(line.indexOf("::") + 2, line.length() - 2);
	}

	public HashMap<String, ArrayList<String>> getReverseMap() {
		return reverseMap;
	}

	public void setReverseMap(HashMap<String, ArrayList<String>> reverseMap) {
		this.reverseMap = reverseMap;
	}

	public HashMap<String, ArrayList<String>> getMap() {
		return map;
	}

	public void setMap(HashMap<String, ArrayList<String>> map) {
		this.map = map;
	}

}