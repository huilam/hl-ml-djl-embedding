package hl.ml.djl.transformer.embedding.common;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DjlModelLoader {
	
	public static Predictor<String, float[]> loadModel(final DjlModelConfig aConfig)
	{
		return loadModel(aConfig.getRuntime_engine(), aConfig.getModel_uri(), aConfig.getOptArgs());
	}
	
	private static Predictor<String, float[]> loadModel(final String aRTEngine, String aModelPath, final Map<String,Object> aMapArgs)
	{
		long lStartMs = System.currentTimeMillis();
		
		int iPos = aModelPath.indexOf(":");
		if(iPos>-1)
		{
			aModelPath = aModelPath.substring(iPos+1);
		}
		
		Predictor<String, float[]> predictor = null;
		File folderModel = new File(aModelPath);
		if(!folderModel.exists())
		{
			System.err.println("folder not exist ! - "+folderModel.getAbsolutePath());
		}
		else
		{
			System.out.println("Loading model from "+folderModel.getAbsolutePath());
		}
		
        // In 0.36.0, we use optArgument to pass configuration 
        // and let the ServiceLoader find the translator automatically.
		Criteria<String, float[]> criteria = Criteria.builder()
        	    .setTypes(String.class, float[].class)
        	    .optEngine(aRTEngine)
        	    .optModelUrls(folderModel.getAbsolutePath()) // DJL looks here first
        	    
        	    // FORCE CPU HERE
        	    .optDevice(Device.cpu())
        	
        	    //Optional args
        	    .optArguments(aMapArgs)
        	    
        	    //HuggingFace extension for DJL
        	    .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
        	    .build();
		
		ZooModel<String, float[]> model = null;
		try {
			model = criteria.loadModel();
		} catch (ModelNotFoundException | MalformedModelException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Model loading time = "+(System.currentTimeMillis()-lStartMs)+" ms");
		
		if(model!=null)
		{
			predictor = model.newPredictor();
		}
		
		return predictor;
	}

}