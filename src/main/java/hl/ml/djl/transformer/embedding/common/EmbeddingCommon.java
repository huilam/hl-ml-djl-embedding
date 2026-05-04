package hl.ml.djl.transformer.embedding.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    
    public double calcSimilarityScore(String aSentence1, String aSentence2) throws TranslateException
    {
    	float[] v1 = getEmbedding(aSentence1);
        return calcSimilarityScore(v1, aSentence2);
    }
    
    public double calcSimilarityScore(float[] aEmbedding1, String aSentence2) throws TranslateException
    {
		double lSimilarityScore = -1;
        float[] v2 = getEmbedding(aSentence2);
        lSimilarityScore = calcSimilarityScore(aEmbedding1, v2);
        return lSimilarityScore;
    }
    
    public double calcSimilarityScore(float[] aEmbedding1, float[] aEmbedding2) throws TranslateException
    {
        return cosineSimilarity(aEmbedding1, aEmbedding2);
    }
    
    public float[] getEmbedding(String aSentence) throws TranslateException
    {
    	return predictor.predict(aSentence);
    }
    
    public Map<String, float[]> getEmbeddings(final String aSentences[]) throws TranslateException
    {
    	Map<String, float[]>  mapEmbeddings = new HashMap<String, float[]>();
    	
    	for(int i=0; i<aSentences.length; i++)
    	{
    		mapEmbeddings.put(aSentences[i], getEmbedding(aSentences[i]));
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
}