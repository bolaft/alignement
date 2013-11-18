package resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public abstract class Resource {

	protected Map<?, ?> map;
	
	protected String path;
	
	public Resource(String path) {
		this.path = path;
	}
	
	public void load() {
		try {
			FileReader file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);

			String line;
			
			while ((line = br.readLine()) != null) {
				updateMap(line);
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void updateMap(String line);
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
