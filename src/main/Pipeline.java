package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import resource.Corpus;
import resource.Dictionary;
import resource.Stopwords;

public class Pipeline {	
	// Dictionary
	private static String DICTIONARY_PATH = "ziggurat/dico/elra_utf8.final";

	// XML translations
	private static String TRUTH_PATH = "ziggurat/data/corpus_breast_cancer/ts.xml";
	
	// Corpora
	private static String SOURCE_CORPUS_PATH = "ziggurat/data/corpus_breast_cancer/tmp_sc_fr/corpus.lem.utf8.tmp";
	private static String TARGET_CORPUS_PATH = "ziggurat/data/corpus_breast_cancer/tmp_sc_en/corpus.lem.utf8.tmp";
	
	// Stopwords
	private static String SOURCE_STOPWORDS_PATH = "src_stopwords.txt";
	private static String TARGET_STOPWORDS_PATH = "targ_stopwords.txt";

	// POS
	private static final String[] SOURCE_POS = {
        "SBC",
        "ADJ",
        "ADV",
        "VCJ",
//        "ADJ2PAR",
//        "ADJ1PAR",
        "VNCFF",
//        "SYM",
//        "VNCNT",
        "VPAR",
    };
        
        private static final String[] TARGET_POS = {
        "JJ",
//        "JJR",
//        "JJS",
        "NN",
//        "NNS",
//        "NNP",
//        "NNPS",
//        "RB",
//        "RBR",
//        "RBS",
//        "SYM",
        "VB",
        "VBD",
        "VBG",
        "VBN",
        "VBP",
        "VBZ",
    };
	
	public static void main(String[] args) {
		HashMap<Integer, HashMap<Integer, String>> matrix = new HashMap<Integer, HashMap<Integer, String>>();

		int[] windows = {1, 2, 4};
		
		for (int window : windows){
			matrix.put(window, new HashMap<Integer, String>());
			
			int[] tops = {1, 10, 50, 100};
			
			for (int top : tops){
				String rate = experiment(window, top);
				matrix.get(window).put(top, rate);
			}
		}
		
		echo(matrix);
	}
	
	public static String experiment(int window, int top) {
		Dictionary dictionary = new Dictionary(DICTIONARY_PATH);
		
		Stopwords sourceStopwords = new Stopwords(SOURCE_STOPWORDS_PATH);
		Stopwords targetStopwords = new Stopwords(TARGET_STOPWORDS_PATH);
		
		Corpus source = new Corpus(SOURCE_CORPUS_PATH, SOURCE_POS, sourceStopwords);
		Corpus target = new Corpus(TARGET_CORPUS_PATH, TARGET_POS, targetStopwords);
		
		Context sourceContext = new Context(source, window);
		Context targetContext = new Context(target, window);
		
		HashMap<String, HashMap<String, Integer>> sourceToTargetVectors = sourceContext.translateVectors(dictionary.getMap(), target);
		
		Evaluator evaluator = new Evaluator(TRUTH_PATH);
		
		HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();
		
		Integer i = 0;
		
		for (String word : evaluator.getWordsToTranslate()) {
			if (sourceToTargetVectors.containsKey(word)) {
				results.put(word, evaluator.findCandidates(sourceToTargetVectors.get(word), targetContext.getVectors(), top, word));
				i++;
			}
		}
		
		echo("Top: " + top + " , Window: " + window);
		
		echo("Translation candidates: " + i);
		
		String rate = evaluator.evaluate(results);
		
		Pipeline.echo("Successful alignment rate: " + rate + "%\n");
		
		return rate;
	}
	
	public static void echo(Object object){
		System.out.println(object);
	}
	
	public static void echo(ArrayList<String> list){
		for (String string : list){
			System.out.println(string);
		}
	}
	
	public static void echo(HashMap<?, ?> map){
		Iterator<?> it = map.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry<?, ?> entry = (Entry<?, ?>)it.next();
	        System.out.println(entry.getKey() + " = " + entry.getValue());
	    }
	}
}