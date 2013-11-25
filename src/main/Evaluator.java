package main;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

	public ArrayList<String> findCandidates(HashMap<String, Integer> translatedSourceVector, HashMap<String, HashMap<String, Integer>> targetVectors, int top, String sourceWord) {
		HashMap<String, Double> cosines = new HashMap<String, Double>();
		
		for (String targetWord : targetVectors.keySet()){
			double cosine = computeCosine(targetVectors.get(targetWord), translatedSourceVector) * computeCosine(targetWord, sourceWord);
			
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
	
	public String evaluate(HashMap<String, ArrayList<String>> results) {
		int found = 0;
		
		for (String q : results.keySet()) {			
			if (results.get(q).contains(truth.get(q))) {
				found++;
			}
		}
		
		int tests = truth.size();
		
		double fraction = Double.valueOf(found) / Double.valueOf(tests) * 100;
		
		DecimalFormat df = new DecimalFormat("0.00"); 
		String rate = df.format(fraction); 
		
		return rate;
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
	
	public double computeCosine(String word1, String word2) {
		char [] chars1 = word1.toCharArray (); 
		char [] chars2 = word2.toCharArray (); 
		
		ArrayList <Character> charList = new ArrayList <Character> (); 
		
		for (char c : chars1) charList.add (c);
		for (char c : chars2) charList.add (c);
		        
        Double total = 0.0;
        
        for (char ch : charList) {
        	int w1count = 0;
        	
        	for (char c : chars1) if (c == ch) w1count++;
        	
        	int w2count = 0;
        	
        	for (char c : chars2) if (c == ch) w2count++;
        	
            total += w1count * w2count;
        }
        
        return total / (Math.sqrt(word1.length()) * Math.sqrt(word2.length()));
	}
	
	@Override
	public void updateMap(String line) {
		// Nothing here
	}
}