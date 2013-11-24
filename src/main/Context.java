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
				

				if (!vectors.containsKey(token)){
					vectors.put(token, new HashMap<String, Integer>());
				}
				
				if (!vectors.get(token).containsKey(head)){
					vectors.get(token).put(head, 1);
				} else {
					vectors.get(token).put(head, vectors.get(token).get(head) + 1);
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
		for (String head : vectors.keySet()) {
			translatedVectors.put(head, new HashMap<String, Integer>());
			
			for (String word : vectors.get(head).keySet()){
				String translation;
				
				if (translations.containsKey(word)) {
					translation = translations.get(word).get(0);
				} else {
					translation = word;
				}

				translatedVectors.get(head).put(translation, vectors.get(head).get(word));
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
