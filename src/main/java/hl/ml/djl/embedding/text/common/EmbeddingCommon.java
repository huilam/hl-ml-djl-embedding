package hl.ml.djl.embedding.text.common;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.translate.TranslateException;
import hl.ml.djl.DjlBaseImpl;
import hl.ml.djl.DjlModelConfig;

public class EmbeddingCommon extends DjlBaseImpl{
	
	protected int embedding_output_size = 0;
	
	@SuppressWarnings("rawtypes")
	protected EmbeddingCommon(Class aImplClass, DjlModelConfig aDjlModelConfig)
	{
		super(aImplClass, aDjlModelConfig, new TextEmbeddingTranslatorFactory());
		
		if(this.model_init_ok && this.predictor!=null)
		{
			try {
				float[] test = this.predictor.predict("test");
				this.embedding_output_size = test.length;
			} catch (TranslateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    public int getInputContentLength() {
		return Integer.parseInt(this.model_prop.getOrDefault("max_seq_length","-1"));
	}
    
    public int getOutputEmbeddingSize() {
		return embedding_output_size;
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
	public Map<String, Double> getTopKSimilarity(String aText, String[] aTargetTexts, 
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
    
}