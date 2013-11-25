package resource;

import java.util.ArrayList;

public class Stopwords extends Resource {
	ArrayList<String> list = new ArrayList<String>();
	
	public Stopwords(String path) {
		super(path);
		
		load();
	}

	@Override
	public void updateMap(String line) {
		list.add(line);
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}
}
