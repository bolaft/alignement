package resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Corpus extends Resource {
	protected HashMap<String, String> map = new HashMap<String, String>();
	
	public String[] pos;
	
	public ArrayList<String> tokens = new ArrayList<String>();
	
	protected HashMap<String, String> documents = new HashMap<String, String>();
	
	protected String documentName = "";
	protected String documentContents = "";
	
	public Corpus(String path, String[] pos) {
		super(path);
		
		this.pos = pos;
		
		load();
	}

	@Override
	public void updateMap(String line) {

		if (lineStartsDocument(line)) {
			documentName = extractDocumentName(line);
		} else if (lineContinuesDocument(line)) {
			tokenize(line);
			
			documentContents = documentContents + " " + line;
		} else if (lineEndsDocument(line)) {
			this.map.put(documentName, documentContents);
			
			documentName = documentContents = "";
		} else {
			System.out.println("ERROR: PROBLEM CREATING CORPUS");
		}
	}
	
	public void tokenize(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			String[] metadata = token.split("/");

			if (metadata.length > 2 && !metadata[2].startsWith("__") && !metadata[2].equals("") && Arrays.asList(pos).contains(metadata[1])){
			    tokens.add(metadata[2]);
			}
		};
	}
	
	protected String extractDocumentName(String line){
		return line.substring(7, line.indexOf("/"));
	}

	protected boolean lineContinuesDocument(String line){
		return (!lineStartsDocument(line) && !lineEndsDocument(line));
	}
	
	protected boolean lineStartsDocument(String line){
		return line.startsWith("__FILE=");
	}
	
	protected boolean lineEndsDocument(String line){
		return line.startsWith("__ENDFILE");
	}

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	public String[] getPos() {
		return pos;
	}

	public void setPos(String[] pos) {
		this.pos = pos;
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<String> tokens) {
		this.tokens = tokens;
	}

	public HashMap<String, String> getDocuments() {
		return documents;
	}

	public void setDocuments(HashMap<String, String> documents) {
		this.documents = documents;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentContents() {
		return documentContents;
	}

	public void setDocumentContents(String documentContents) {
		this.documentContents = documentContents;
	}
}