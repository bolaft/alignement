package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
		ArrayList<Token> tokens = corpus.getTokens();
		
		for (int i = 0; i < tokens.size(); i++){
			String head = tokens.get(i).getBase();
		
			if (!vectors.containsKey(head)){
				vectors.put(head, new HashMap<String, Integer>());
			}
			
			for (int j = -window; j < window; j++){	
				int pos = i + j;
				
				if (pos >= tokens.size() || pos < 0) continue;
				
				String contextToken = tokens.get(pos).getBase();
				
//				if (contextToken.equals("ENDLINE")) break;
				
				if (!vectors.get(head).containsKey(contextToken)){
					vectors.get(head).put(contextToken, 1);
				} else {
					vectors.get(head).put(contextToken, vectors.get(head).get(contextToken) + 1);
				}
			}
		}
		
//		for (String head : vectors.keySet()){
//			ArrayList<String> toBeRemoved = new ArrayList<String>();
//			
//			for (String word : vectors.get(head).keySet()){
//				if (vectors.get(head).get(word) < 2){
//					toBeRemoved.add(word);
//				}
//			}
//			
//			for (String word : toBeRemoved){
//				vectors.get(head).remove(word);
//			}
//		}
		
		return vectors;
	}
	
	public HashMap<String, HashMap<String, Integer>> translateVectors(HashMap<String, ArrayList<String>> translations, Corpus targetCorpus){
		for (String head : vectors.keySet()) {
			translatedVectors.put(head, new HashMap<String, Integer>());
			
			for (String word : vectors.get(head).keySet()){
				String translation;
				
				if (translations.containsKey(word)) {
					translation = translations.get(word).get(0);
					
					int occ = 0;
					
					for (String t : translations.get(word)){
						if (!targetCorpus.getOccurrences().containsKey(t)) break;
						
						int targetOcc = targetCorpus.getOccurrences().get(t);
						
						if (targetOcc > occ) {
							translation = t;
							occ = targetOcc;
						}
					}
					
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
