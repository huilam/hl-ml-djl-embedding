package hl.ml.djl.transformer.embedding.common.test;

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
        System.out.println("Similarity Score: " + embedding.calcSimilarityScore(s1, s2));
        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        
        System.out.println();
    }
	
	public static void testTopKSimilarWords(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		
		String sText = "Artificial Intelligence";
		
		String[] sSentences = 
				new String[]{"Machine Learning", "Deep Learning", "Neural Networks", "Robotics", "Data Science", "Marshmallow", "Running Dog"};
        
        long lInferenceStart = System.currentTimeMillis();
        Map<String, Double> mapMatches = embedding.getTopKSimilarity(sText, sSentences, 0.1, 5);
        
        System.out.println("====== testTopKSimilarWords ======");
        System.out.println("Model Name: " + embedding.getModel_name());
        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");

        for(String sString : mapMatches.keySet())
        {
        	 System.out.println("  " + sString+" = "+mapMatches.get(sString));
        }
        
       
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
        System.out.println();
    }
}