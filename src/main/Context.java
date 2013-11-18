package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import resource.Corpus;

public class Context {
	
	protected HashMap<String, HashMap<String, Integer>> vectors = new HashMap<String, HashMap<String, Integer>>();
	protected HashMap<String, HashMap<String, Integer>> translatedVectors = new HashMap<String, HashMap<String, Integer>>();
	
	protected int window;
	
	public Context(Corpus corpus, int window) {
		this.window = window;
		
		this.vectors = vectorize(corpus);
	}
	
	public HashMap<String, HashMap<String, Integer>> vectorize(Corpus corpus){
		ArrayList<String> tokens = corpus.getTokens();
		
		ArrayList<String> heads = new ArrayList<String>();
		
		for (String token : tokens){
			for (String head : heads){
				if (!vectors.containsKey(head)){
					vectors.put(head, new HashMap<String, Integer>());
				}
				
				if (!vectors.get(head).containsKey(token)){
					vectors.get(head).put(token, 1);
				} else {
					vectors.get(head).put(token, vectors.get(head).get(token) + 1);
				}
			}
		
			if (!heads.contains(token)){
				heads.add(token);
			}
			
			if (heads.size() > window){
				heads.remove(heads.get(0));
			}
		}
		
		return vectors;
	}
	
	public HashMap<String, HashMap<String, Integer>> translateVectors(HashMap<String, ArrayList<String>> translations){
		Iterator<Entry<String, HashMap<String, Integer>>> it = vectors.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry<String, HashMap<String, Integer>> entry = (Entry<String, HashMap<String, Integer>>)it.next();
	        
	        String head = entry.getKey();
	        
	        if (!translatedVectors.containsKey(head)){
	        	translatedVectors.put(head, new HashMap<String, Integer>());
	        }
	        
	        Iterator<Entry<String, Integer>> subit = entry.getValue().entrySet().iterator();
	        
	        while (subit.hasNext()) {
	        	Entry<String, Integer> subentry = subit.next();
	        	String token = subentry.getKey();
	        	Integer count = subentry.getValue();

	    		ArrayList<String> translatedTokens = new ArrayList<String>();
	    		
	        	if (translations.containsKey(token)) {
	        		translatedTokens = translations.get(token);
	        	}
	        	
	        	for (String translatedToken : translatedTokens){
	        		HashMap<String, Integer> headTranslations = translatedVectors.get(head);
	        		
	        		if (!headTranslations.containsKey(translatedToken)){
	        			headTranslations.put(translatedToken, count);
	        		} else {
	        			headTranslations.put(translatedToken, count + headTranslations.get(translatedToken));
	        		}
	        	}
	        }
	    }
	    
		return translatedVectors;
	}

	protected HashMap<String, HashMap<String, Integer>> getVectors() {
		return vectors;
	}

	public void setVectors(HashMap<String, HashMap<String, Integer>> vectors) {
		this.vectors = vectors;
	}

	public int getWindow() {
		return window;
	}

	public void setWindow(int window) {
		this.window = window;
	}
}
