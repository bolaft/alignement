package resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import main.Token;

public class Corpus extends Resource {
	protected HashMap<String, String> map = new HashMap<String, String>();
	
	public String[] pos;
	public Stopwords stopwords;
	
	public ArrayList<Token> tokens = new ArrayList<Token>();
	
	protected HashMap<String, String> documents = new HashMap<String, String>();
	protected HashMap<String, Integer> occurrences = new HashMap<String, Integer>();
	
	protected String documentName = "";
	protected String documentContents = "";
	
	public Corpus(String path, String[] pos, Stopwords stopwords) {
		super(path);
		
		this.pos = pos;
		this.stopwords = stopwords;
		
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

			if (metadata.length > 2) {
				String POS = null;
				
				if (metadata[1].indexOf(":") > 0){
					POS = metadata[1].substring(0, metadata[1].indexOf(":"));
				} else {
					POS = metadata[1];
				}
				
				String word = metadata[0];
				String base = metadata[2];

				if (!word.startsWith("__") 
						&& !word.equals("") 
						&& Arrays.asList(pos).contains(POS)
						&& !stopwords.getList().contains(word)
						&& !stopwords.getList().contains(base)){
				    tokens.add(new Token(word, base, POS));
				    
				    if (!occurrences.containsKey(base)){
				    	occurrences.put(base, 1);
				    } else {
				    	occurrences.put(base, occurrences.get(base) + 1);
				    }
				}
			}
		}
		
//		tokens.add(new Token("ENDLINE", "ENDLINE", "ENDLINE"));
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

	public Stopwords getStopwords() {
		return stopwords;
	}

	public void setStopwords(Stopwords stopwords) {
		this.stopwords = stopwords;
	}

	public HashMap<String, Integer> getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(HashMap<String, Integer> occurrences) {
		this.occurrences = occurrences;
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

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<Token> tokens) {
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