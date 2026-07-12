package hl.ml.djl.embedding.text.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;

import ai.djl.Device;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.translate.TranslateException;
import ai.djl.translate.TranslatorFactory;
import hl.common.PropUtil;
import hl.ml.djl.AbtractDjlBaseImpl;
import hl.ml.djl.DjlModelConfig;

public class TextEmbeddingCommon extends AbtractDjlBaseImpl<String, float[]>{
	
	protected static String embedding_prop_filename 	= "hl-ml-djl.properties";
	protected static String embedding_prop_key 			= "embedding-model";
	protected int embedding_output_size 				= 0;
	
	@SuppressWarnings("rawtypes")
	protected TextEmbeddingCommon(Class aImplClass, String aSubCfgPrefix)
	{
		DjlModelConfig aDjlModelConfig = initTextEmbeddingConfig(aImplClass, aSubCfgPrefix);
		
		super(aImplClass, 
				aDjlModelConfig, 
				Criteria.builder().setTypes(String.class, float[].class));
			
		initEmbeddingModel();
		
	}
	
	protected void initEmbeddingModel()
	{
		super.loadModel();
	}
	
	protected static DjlModelConfig initTextEmbeddingConfig(Class<?> aImplClass, String aSubCfgPrefix)
	{
		DjlModelConfig aDjlModelConfig = null;
		Properties propEmbedding = null;
		
		try {
			propEmbedding = PropUtil.loadProperties(aImplClass, embedding_prop_filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("propEmbedding.size()="+propEmbedding.size());
		
		if(propEmbedding!=null)
		{
			aDjlModelConfig = new DjlModelConfig();
			String[] sEmbeddingPrefix = new String[]{embedding_prop_key};
			

			if(aSubCfgPrefix!=null && aSubCfgPrefix.trim().length()>0)
			{
				sEmbeddingPrefix = new String[]{
						embedding_prop_key, 
						embedding_prop_key+"."+aSubCfgPrefix };
			}
			
			for(String sPrefix: sEmbeddingPrefix)
			{
				aDjlModelConfig.setModel_name(propEmbedding.getProperty(sPrefix+".name", aDjlModelConfig.getModel_name()));
				aDjlModelConfig.setModel_download_url(propEmbedding.getProperty(sPrefix+".url", aDjlModelConfig.getModel_download_url()));
				aDjlModelConfig.setModel_folder(propEmbedding.getProperty(sPrefix+".folder", aDjlModelConfig.getModel_folder()));
				aDjlModelConfig.setModel_filename(propEmbedding.getProperty(sPrefix+".filename", aDjlModelConfig.getModel_filename()));
				aDjlModelConfig.setModel_license(propEmbedding.getProperty(sPrefix+".license", aDjlModelConfig.getModel_license()));
				aDjlModelConfig.setRuntime_engine(propEmbedding.getProperty(sPrefix+".runtime_engine", aDjlModelConfig.getRuntime_engine()));
				
				String sTranslatorFactoryClassName = propEmbedding.getProperty(sPrefix+".translator_factory.classname", null);
				if(sTranslatorFactoryClassName!=null && sTranslatorFactoryClassName.trim().length()>0)
				{
					try {
						Class<?> aTranslatorFactoryClass = Class.forName(sTranslatorFactoryClassName);
						Object aTranslatorFactoryObj = aTranslatorFactoryClass.getDeclaredConstructor().newInstance();
						if(aTranslatorFactoryObj instanceof TranslatorFactory)
						{
							aDjlModelConfig.setTranslator_factory((TranslatorFactory)aTranslatorFactoryObj);
						}
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				
				if(aDjlModelConfig.getModel_name()!=null)
				{
					for(String sKey: propEmbedding.stringPropertyNames())
					{
						if(sKey.startsWith(sPrefix+".optMLArg."))
						{
							String sArgName = sKey.substring((sPrefix+".optMLArg.").length());
							String sArgValue = propEmbedding.getProperty(sKey);
							aDjlModelConfig.addMLArg(sArgName, sArgValue);
						}
						else if(sKey.startsWith(sPrefix+".options."))
						{
							String sOptKey = sKey.substring((sPrefix+".options.").length());
							String sOptValue = propEmbedding.getProperty(sKey);
							aDjlModelConfig.addOption(sOptKey, sOptValue);
						}
							
					}
				}
			}

			//common embedding translator
			if(aDjlModelConfig.getTranslator_factory()==null)
				aDjlModelConfig.setTranslator_factory(new TextEmbeddingTranslatorFactory());
			
			if(aDjlModelConfig.getDevice_type()==null)
				aDjlModelConfig.setDevice_type(Device.cpu());
			
			System.out.println("aDjlModelConfig="+aDjlModelConfig);
		}
		return aDjlModelConfig;
		
	}
	
	
	
    public int getInputContentLength() {
		return Integer.parseInt((String)
				this.djl_model_config.getMLArgs().getOrDefault("max_seq_length","-1"));
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
    
    public static TextEmbeddingCommon getInstance(String aTextEmbeddingClassName)
    {
    	TextEmbeddingCommon textEmbeddingCommon = null;
    	try {
			Class<?> classTextEmbedding = Class.forName(aTextEmbeddingClassName);
			Method getInstanceMethod = classTextEmbedding.getMethod("getInstance");
			textEmbeddingCommon = 
					(TextEmbeddingCommon) getInstanceMethod.invoke(null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return textEmbeddingCommon;
    }
    
}