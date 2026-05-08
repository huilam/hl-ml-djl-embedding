package hl.ml.djl.transformer.embedding.common;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;

public class EmbeddingCommon {
	
	protected DjlModelConfig djl_model_config = null;
	
	protected Predictor<String, float[]> predictor = null;
	private boolean model_init_ok = false;

	@SuppressWarnings("rawtypes")
	protected EmbeddingCommon(Class aImplClass, DjlModelConfig aDjlModelConfig)
	{
		if(aDjlModelConfig.getModel_uri()==null)
		{
			URL url = aImplClass.getProtectionDomain().getCodeSource().getLocation();
			String sModelFolder = url.toString()+aImplClass.getPackageName().replace(".","/")+"/model/";
			aDjlModelConfig.setModel_uri(sModelFolder);
		}
		
		this.predictor = DjlModelLoader.loadModel(aDjlModelConfig);
		this.model_init_ok = true;
		this.djl_model_config = aDjlModelConfig;
	}
	
    public boolean isModelInitOk() {
		return this.model_init_ok;
	}
    
    public String getRt_engine() {
		return djl_model_config.getRuntime_engine();
	}

    public String getModel_name() {
		return djl_model_config.getModel_name();
	}

	protected double cosineSimilarity(float[] v1, float[] v2) {
        double dot = 0, n1 = 0, n2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            n1 += v1[i] * v1[i];
            n2 += v2[i] * v2[i];
        }
        return dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }
    ///////////////////////////
	protected Map<String, Double> getTopKSimilarity(String aText, String[] aTargetTexts, 
			double aMinThreshold, int aTopK) throws TranslateException 
	{
		Map<float[], String> mapTextEmbedding = new HashMap<float[], String>();
		
		for(int i=0; i<aTargetTexts.length; i++)
		{
			String aTarText = aTargetTexts[i];
			float[] fEmbedding = getEmbedding(aTarText);
			//
			mapTextEmbedding.put(fEmbedding, aTarText);
		}
		return getTopKSimilarity(aText, mapTextEmbedding, aMinThreshold, aTopK);
	}
	
	protected Map<String, Double> getTopKSimilarity(String aText, Map<float[], String> aMapTextEmbedding, 
			double aMinThreshold, int aTopK) throws TranslateException 
	{
		int iCount = aTopK;
		
		if(iCount<=0)
			iCount = 9999999;
		
	    // 1. CHANGE THIS LINE: The queue must hold the Entry, not just a Double
		Queue<Map.Entry<String, Double>> q = new PriorityQueue<>(
	        (e1, e2) -> Double.compare(e2.getValue(), e1.getValue())
	    );

	    float[] emText1 = getEmbedding(aText);

	    for (Map.Entry<float[], String> entry : aMapTextEmbedding.entrySet()) 
	    {
	        double score = cosineSimilarity(emText1, entry.getKey());
	        
	        if(score>=aMinThreshold)
	        {
		        // 2. Now the types match: adding an Entry to a Queue of Entries
		        q.offer(new AbstractMap.SimpleEntry<String, Double>(entry.getValue(), score));
	        }
	    }

	    // 3. Convert the sorted queue into the return Map
	    Map<String, Double> results = new LinkedHashMap<>();
	    while (!q.isEmpty() && (iCount>0)) {
	        Map.Entry<String, Double> sortedEntry = q.poll();
	        results.put(sortedEntry.getKey(), sortedEntry.getValue());
	        iCount--;
	    }

	    return results;
	}
    

    ///////////////////////////

    public double calcSimilarityScore(String aSentence1, String aSentence2) throws TranslateException
    {
    	float[] v1 = getEmbedding(aSentence1);
        return calcSimilarityScore(v1, aSentence2);
    }
    
    protected double calcSimilarityScore(float[] aEmbedding1, String aSentence2) throws TranslateException
    {
		double lSimilarityScore = -1;
        float[] v2 = getEmbedding(aSentence2);
        lSimilarityScore = calcSimilarityScore(aEmbedding1, v2);
        return lSimilarityScore;
    }
    
    protected double calcSimilarityScore(float[] aEmbedding1, float[] aEmbedding2) throws TranslateException
    {
        return cosineSimilarity(aEmbedding1, aEmbedding2);
    }
    
    ///////////////////////////
    
    public float[] getEmbedding(String aSentence) throws TranslateException
    {
    	return predictor.predict(aSentence);
    }
    
    public Map<float[], String> getEmbeddings(final String aSentences[]) throws TranslateException
    {
    	Map<float[], String>  mapEmbeddings = new HashMap<float[], String>();
    	
    	for(int i=0; i<aSentences.length; i++)
    	{
    		mapEmbeddings.put(getEmbedding(aSentences[i]), aSentences[i]);
    	}
    	
    	return mapEmbeddings;
    }
    
	protected static void unit_test_1(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		
        String s1 = "The weather is very sunny today.";
        String s2 = "It is a bright and sun-filled day.";
        
        long lInferenceStart = System.currentTimeMillis();
        System.out.println("Model Name: " + embedding.getModel_name());
        System.out.println("Similarity Score: " + embedding.calcSimilarityScore(s1, s2));
        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
    }
	
	protected static void unit_test_topK(EmbeddingCommon embedding) throws TranslateException {
		
		long lAppStart = System.currentTimeMillis();
		
		String sText = "Artificial Intelligence";
		
		String[] sSentences = 
				new String[]{"Machine Learning", "Deep Learning", "Neural Networks", "Robotics", "Data Science"};
        
        long lInferenceStart = System.currentTimeMillis();
        Map<String, Double> mapMatches = embedding.getTopKSimilarity(sText, sSentences, 0.1, -1);
        System.out.println("Model Name: " + embedding.getModel_name());
        System.out.println("Inference Time = "+(System.currentTimeMillis()-lInferenceStart)+" ms");

        for(String sString : mapMatches.keySet())
        {
        	 System.out.println("  " + sString+" = "+mapMatches.get(sString));
        }
        
       
        
        System.out.println("App Elapsed Time = "+(System.currentTimeMillis()-lAppStart)+" ms");
    }
}