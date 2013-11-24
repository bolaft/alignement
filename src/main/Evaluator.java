package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import resource.Resource;

public class Evaluator extends Resource {

	protected HashMap<String, String> truth = new HashMap<String, String>();

	public Evaluator(String path) {
		super(path);

		load();
	}

	@Override
	public void load() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(path));

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("TRAD");

			for (int i = 0; i < nList.getLength(); i++) {
				Node n = nList.item(i);

				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;

					if (e.getAttribute("valid").equals("yes")) {
						NodeList children = n.getChildNodes();

						String key = null;
						
						for (int j = 0; j < children.getLength(); j++){
							Node child = children.item(j);
							
							if (child.getNodeType() == Node.ELEMENT_NODE) {
								Element echild = (Element) child;
								
								String type = echild.getAttribute("type");
								
								if (type.equals("source")){
									StringTokenizer tokenizer = new StringTokenizer(child.getTextContent());
									key = tokenizer.nextToken();
								} else if (type.equals("target") && key != null){
									StringTokenizer tokenizer = new StringTokenizer(child.getTextContent());
									truth.put(key, tokenizer.nextToken());
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getWordsToTranslate() {
		Iterator<Entry<String, String>> it = truth.entrySet().iterator();
	    
		ArrayList<String> wordsToTranslate = new ArrayList<String>();
		
	    while (it.hasNext()) {
	        Map.Entry<String, String> entry = (Entry<String, String>)it.next();
	        wordsToTranslate.add(entry.getKey());
	    }
	    
		return wordsToTranslate;
	}

	public HashMap<String, String> getTruth() {
		return truth;
	}

	public void setTruth(HashMap<String, String> truth) {
		this.truth = truth;
	}

	public ArrayList<String> findCandidates(HashMap<String, Integer> translatedSourceVector, HashMap<String, HashMap<String, Integer>> targetVectors, int top) {
		HashMap<String, Double> cosines = new HashMap<String, Double>();
		
		for (String targetWord : targetVectors.keySet()){
			double cosine = computeCosine(targetVectors.get(targetWord), translatedSourceVector);
			
			if (cosines.size() > top) {
				for (String cosineKey : cosines.keySet()) {
					if (cosines.get(cosineKey) < cosine) {
						cosines.remove(cosineKey);
						cosines.put(targetWord, cosine);
						break;
					}
				}
			} else {
				cosines.put(targetWord, cosine);
			}
		}
		
		return new ArrayList<String>(cosines.keySet());
	}
	
	public double evaluate(HashMap<String, ArrayList<String>> results) {
		int total = 0;
		
		for (String word : getWordsToTranslate()) {
			if (results.containsKey(word) && results.get(word).contains(truth.get(word))){
				total++;
			}
		}
		
		return total / truth.size();
	}
	
	public double computeCosine(HashMap<String, Integer> word1, HashMap<String, Integer> word2) {                

        ArrayList<String> words = new ArrayList<String>(word1.keySet());
        words.addAll(word2.keySet());
        
        Double total = 0.0;
        
        for (String word : words) {
        	Integer w1count = word1.get(word) == null ? 0 : word1.get(word);
        	Integer w2count = word2.get(word) == null ? 0 : word2.get(word);
        	
            total += w1count * w2count;
        }
        
        return total / (Math.sqrt(word1.size()) * Math.sqrt(word2.size()));
	}
	
	@Override
	public void updateMap(String line) {
		// Nothing here
	}
}