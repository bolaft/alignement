package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import resource.Corpus;
import resource.Dictionary;

public class Pipeline {

	// Parameters
	private static final int WINDOW = 4;
	private static final int TOP = 100;
	
	// Dictionary
	private static final String DICTIONARY_PATH = "ziggurat/dico/elra_utf8.final";

	// XML translations
	private static final String TRUTH_PATH = "ziggurat/data/corpus_breast_cancer/ts.xml";
	
	// Corpora
	private static final String SOURCE_CORPUS_PATH = "ziggurat/data/corpus_breast_cancer/tmp_sc_fr/corpus.lem.utf8.tmp";
	private static final String TARGET_CORPUS_PATH = "ziggurat/data/corpus_breast_cancer/tmp_sc_en/corpus.lem.utf8.tmp";

	// POS
	private static final String[] SOURCE_POS = {
        "SBC",
        "ADJ",
        "ADV",
        "VCJ",
        "ADJ2PAR",
        "ADJ1PAR",
        "VNCFF",
        "SYM",
        "VNCNT",
        "VPAR",
    };
	
	private static final String[] TARGET_POS = {
        "JJ",
        "JJR",
        "JJS",
        "NN",
        "NNS",
        "NNP",
        "NNPS",
        "RB",
        "RBR",
        "RBS",
        "SYM",
        "VB",
//        "VBD",
        "VBG",
//        "VBN",
//        "VBP",
//        "VBZ",
    };
	
	public static void main(String[] args) {
		
		echo("Loading dictionary...");
		Dictionary dictionary = new Dictionary(DICTIONARY_PATH);
		echo("Dictionary loaded.");
		
		echo("Loading source corpus...");
		Corpus source = new Corpus(SOURCE_CORPUS_PATH, SOURCE_POS);
		echo("Source corpus loaded.");
		
		echo("Loading target corpus...");
		Corpus target = new Corpus(TARGET_CORPUS_PATH, TARGET_POS);
		echo("Target corpus loaded.");
		
		echo("Vectorizing source tokens...");
		Context sourceContext = new Context(source, WINDOW);
		echo("Source tokens vectorized.");

		echo("Vectorizing target tokens...");
		Context targetContext = new Context(target, WINDOW);
		echo("Target tokens vectorized.");

		echo("Translating source to target vectors...");
		HashMap<String, HashMap<String, Integer>> sourceToTargetVectors = sourceContext.translateVectors(dictionary.getMap());
		echo("Source to target vectors translated.");
		
		echo("Translating target to source vectors...");
		HashMap<String, HashMap<String, Integer>> targetToSourceVectors = targetContext.translateVectors(dictionary.getReverseMap());
		echo("Target to source vectors translated.");

		echo("Loading the truth...");
		Evaluator evaluator = new Evaluator(TRUTH_PATH);
		echo("Truth loaded.");

		ArrayList<String> wordsToTranslate = evaluator.getWordsToTranslate();

		echo("Words to translate: " + wordsToTranslate.size());
		
		Integer i = 0;
		HashMap<String, HashMap<String, Double>> results = new HashMap<String, HashMap<String, Double>>();
		
		for (String word : wordsToTranslate) {
			if (sourceToTargetVectors.containsKey(word)) {
				echo("Finding candidates for word \"" + word + "\"...");
				results.put(word, evaluator.test(sourceToTargetVectors.get(word), targetContext.getVectors(), TOP));
				echo("Candidates found.");
				i++;
			}
		}
		
		echo("Translation candidates: " + i);
		
		echo(results);
		
		echo("Successful alignment rate (Top " + TOP + ": " + evaluator.evaluate(results) + "%");
	}
	
	public static void echo(String string){
		System.out.println(string);
	}
	
	public static void echo(HashMap<?, ?> map){
		Iterator<?> it = map.entrySet().iterator();
	    
	    while (it.hasNext()) {
	        Map.Entry<?, ?> entry = (Entry<?, ?>)it.next();
	        System.out.println(entry.getKey() + " = " + entry.getValue());
	        it.remove();
	    }
	}
}