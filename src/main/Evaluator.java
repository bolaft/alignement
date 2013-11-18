package main;

import java.io.File;
import java.util.ArrayList;
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

	public HashMap<String, Double> test(HashMap<String, Integer> sourceToTargetVector, HashMap<String, HashMap<String, Integer>> targetVector, int top) {
		HashMap<String, Double> results = new HashMap<String, Double>();

		Iterator<Entry<String, HashMap<String, Integer>>> it = targetVector.entrySet().iterator();
		
		while (it.hasNext()){
	        Map.Entry<String, HashMap<String, Integer>> entry = (Entry<String, HashMap<String, Integer>>) it.next();
	        double score = computeCosine(sourceToTargetVector, entry.getValue());
	        
	        Iterator<Entry<String, Integer>> trgit = entry.getValue().entrySet().iterator();
	        
	        while (trgit.hasNext()) {
		        Map.Entry<String, Integer> trgEntry = (Entry<String, Integer>) trgit.next();
		        
		        if (results.size() <= top) {
		        	results.put(trgEntry.getKey(), score);
		        } else {
			        Iterator<Entry<String, Double>> rit = results.entrySet().iterator();
			        
			        while (rit.hasNext()) {
				        Map.Entry<String, Double> resultEntry = (Entry<String, Double>) rit.next();
			        	
				        if (resultEntry.getValue() < score) {
				        	results.remove(resultEntry.getKey());
				        	results.put(trgEntry.getKey(), score);
				        	break;
				        }
			        }
		        }
	        }
		}
		
		return results;
	}
	
	public double evaluate(HashMap<String, HashMap<String, Double>> results) {
		int total = 0;
		
		for (String word : getWordsToTranslate()) {
			if (results.containsKey(word) && results.get(word).containsKey(truth.get(word))){
				total++;
			}
		}
		
		return total / truth.size();
	}
	
	public double computeCosine(HashMap<String, Integer> v1, HashMap<String, Integer> v2) {		
		Iterator<Entry<String, HashMap<String, Integer>>> rit = matrix(v1, v2).entrySet().iterator();
        
		int total = 0;
		
		while (rit.hasNext()) {
		    Map.Entry<String, HashMap<String, Integer>> rEntry = (Entry<String, HashMap<String, Integer>>) rit.next();
		    
		    int v1count = rEntry.getValue().get("vector1");
		    int v2count = rEntry.getValue().get("vector2");
		    
		    total = total + v1count * v2count;
		}
		
		return total / (Math.sqrt(v1.size()) * Math.sqrt(v2.size()));
	}
	
	public HashMap<String, HashMap<String, Integer>> matrix(HashMap<String, Integer> v1, HashMap<String, Integer> v2) {
		HashMap<String, HashMap<String, Integer>> matrix = new HashMap<String, HashMap<String, Integer>>();
		
		Iterator<Entry<String, Integer>> it1 = v1.entrySet().iterator();
	        
		while (it1.hasNext()) {
		    Map.Entry<String, Integer> entry1 = (Entry<String, Integer>)it1.next();
		    
		    String word1 = entry1.getKey();
		    Integer count1 = entry1.getValue();
		    
		    if (!matrix.containsKey(word1)){
		    	HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		    	matrix.put(word1, map1);
		    	matrix.get(word1).put("vector2", 0);
		    }
		    
	    	matrix.get(word1).put("vector1", count1);
		    
		    Iterator<Entry<String, Integer>> it2 = v2.entrySet().iterator();
	        
			while (it2.hasNext()) {
			    Map.Entry<String, Integer> entry2 = (Entry<String, Integer>)it2.next();
			    String word2 = entry2.getKey();
			    Integer count2 = entry2.getValue();
			    
			    if (!matrix.containsKey(word2)){
			    	HashMap<String, Integer> map2 = new HashMap<String, Integer>();
			    	matrix.put(word2, map2);
			    	matrix.get(word2).put("vector1", 0);
			    }
			    
		    	matrix.get(word2).put("vector2", count2);
			}
		}
		
		return matrix;
	}
	
	@Override
	public void updateMap(String line) {
		// Nothing here
	}
}