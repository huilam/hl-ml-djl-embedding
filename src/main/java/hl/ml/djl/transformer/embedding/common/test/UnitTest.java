package hl.ml.djl.transformer.embedding.common.test;

import java.util.HashMap;
import java.util.Map;
import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;

public class UnitTest {
	
	public static void testSimilarSentence(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		
        String s1 = "The weather is very sunny today.";
        String s2 = "It is a bright and sun-filled day.";
        
        long lInferenceStart = System.currentTimeMillis();
        
        System.out.println("====== testSimilarSentence ======");
        
        System.out.println("Model Name: " + embedding.getModel_name());
        
        System.out.println(String.format("    %.4f = %s -vs- %s", embedding.calcSimilarityScore(s1, s2), s1, s2));

        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        
        System.out.println();
    }
	
	public static void testSentencePair_Similar(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		Map<String,String> mapSentences = new HashMap<>();
		
	    System.out.println("====== testSentencePair_Unrelated ======");
	    System.out.println("Model Name: " + embedding.getModel_name());
	 
	    // --- 10 Similar Sentence Pairs (Paraphrases) ---
	    mapSentences.put("How do I reset my password?", "I forgot my login credentials and need a new one.");
	    mapSentences.put("The weather is beautiful today.", "It is a very sunny and pleasant day outside.");
	    mapSentences.put("Can I use this credit card abroad?", "Will this card work for international transactions?");
	    mapSentences.put("The movie was incredibly boring.", "I found the film to be quite dull and uninteresting.");
	    mapSentences.put("Start the engine of the car.", "Turn the ignition to get the vehicle running.");
	    mapSentences.put("Where is the nearest grocery store?", "How do I find a supermarket close to here?");
	    mapSentences.put("The chef prepared a delicious meal.", "A tasty dinner was cooked by the cook.");
	    mapSentences.put("I need to schedule a doctor appointment.", "I want to book a visit with a physician.");
	    mapSentences.put("The team won the championship.", "The players secured a victory in the final game.");
	    mapSentences.put("Please keep the door closed.", "Ensure that the entrance remains shut.");
	    
 	    testWordPairs(embedding, mapSentences);
	    
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
 	}
	
	public static void testSentencePair_Unrelated(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		Map<String,String> mapSentences = new HashMap<>();
		
	    System.out.println("====== testSentencePair_Unrelated ======");
	    System.out.println("Model Name: " + embedding.getModel_name());
	 
	    // --- 10 Unrelated Sentence Pairs (Random Topics) ---
	    mapSentences.put("The stock market fluctuated today.", "I prefer my coffee with a bit of milk.");
	    mapSentences.put("Quantum physics is a complex subject.", "My cat likes to sleep on the sofa.");
	    mapSentences.put("The airplane landed safely at noon.", "The history of the Roman Empire is long.");
	    mapSentences.put("I am learning how to play the guitar.", "The chemical formula for water is H2O.");
	    mapSentences.put("Recycling helps protect the environment.", "Please pass the salt and pepper.");
	    mapSentences.put("The smartphone has a high-resolution screen.", "Elephants are the largest land mammals.");
	    mapSentences.put("Basketball is a popular sport in America.", "The software update will take ten minutes.");
	    mapSentences.put("I enjoy hiking in the mountains.", "The library is closed on public holidays.");
	    mapSentences.put("Artificial intelligence is changing the world.", "The recipe calls for two cups of flour.");
	    mapSentences.put("The concert starts at eight o'clock.", "The Great Wall of China is a famous landmark.");
	    
 	    testWordPairs(embedding, mapSentences);
	    
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
 	}
	
	public static void testWordPairs_Similar(EmbeddingCommon embedding) throws TranslateException {
	
		long lAppStart = System.currentTimeMillis();
		Map<String,String> mapWords = new HashMap<>();
		
	    System.out.println("====== testWordPairs_Similar ======");
	    System.out.println("Model Name: " + embedding.getModel_name());
	    
		// --- Similar Pairs (Action & Items) ---
	    mapWords.put("sprint", "jog");
	    mapWords.put("purchase", "buy");
	    mapWords.put("construct", "build");
	    mapWords.put("verify", "confirm");
	    mapWords.put("shout", "whisper");
	    mapWords.put("automobile", "vehicle");
	    mapWords.put("laptop", "computer");
	    mapWords.put("sofa", "couch");
	    mapWords.put("ocean", "sea");
	    mapWords.put("physician", "doctor");
	    
	    testWordPairs(embedding, mapWords);
	    
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
	}
	
	public static void testWordPairs_Unrelated(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		Map<String,String> mapWords = new HashMap<>();
		
	    System.out.println("====== testWordPairs_Unrelated ======");
	    System.out.println("Model Name: " + embedding.getModel_name());
	 
	        
	    // --- Unrelated Pairs ---
	    mapWords.put("calculate", "banana");
	    mapWords.put("keyboard", "ocean");
	    mapWords.put("gravity", "soup");
	    mapWords.put("sleep", "concrete");
	    mapWords.put("philosophy", "toaster");
	    mapWords.put("whisper", "iron");
	    mapWords.put("mountain", "delete");
	    mapWords.put("bicycle", "democracy");
	    mapWords.put("painting", "nitrogen");
	    mapWords.put("climb", "yellow");
	    
 	    testWordPairs(embedding, mapWords);
	    
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
 	}
	
	private static void testWordPairs(EmbeddingCommon embedding, Map<String,String> mapWordPairs) throws TranslateException {
		
        long lInferenceStart = System.currentTimeMillis();
        StringBuffer sbResult = new StringBuffer();
        for(String aText1 : mapWordPairs.keySet())
        {
        	String aText2 = mapWordPairs.get(aText1);
        	double dScore = embedding.calcSimilarityScore(aText1, aText2);
        	if(sbResult.length()>0)
        		sbResult.append("\n");
        	sbResult.append("    ").append(String.format("%.4f", dScore)).append(" = ").append(aText1).append(" -vs- ").append(aText2);
        }
        long lInferenceMs = System.currentTimeMillis()-lInferenceStart;
        
        System.out.println("Inference Time = "+(lInferenceMs)+" ms");
        System.out.println(sbResult.toString());
    }
	
	public static void testTopKSimilarWords(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		
		String sText = "Artificial Intelligence";
		
		String[] sSentences = 
				new String[]{"Machine Learning", "Deep Learning", "Neural Networks", "Robotics", "Data Science", "Marshmallow", "Running Dog"};
        
        long lInferenceStart = System.currentTimeMillis();
        Map<String, Double> mapMatches = embedding.getTopKSimilarity(sText, sSentences, 0.5, 6);
        
        System.out.println("====== testTopKSimilarWords ======");
        System.out.println("Model Name: " + embedding.getModel_name());
        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");

        for(String s2 : mapMatches.keySet())
        {
        	double dScore = mapMatches.get(s2);
        	System.out.println(String.format("    %.4f = %s -vs- %s", dScore, sText, s2));
        }
        
       
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
    }
	
	public static void testAll(EmbeddingCommon embedding) throws TranslateException 
	{
		testSimilarSentence(embedding);
		testTopKSimilarWords(embedding);
		//
		testWordPairs_Similar(embedding);
		testWordPairs_Unrelated(embedding);
		//
		testSentencePair_Similar(embedding);
		testSentencePair_Unrelated(embedding);
	}
}