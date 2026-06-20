package hl.ml.djl;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslatorFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DjlModelLoader {
	
	public static ZooModel<String, float[]> loadModel(
			final DjlModelConfig aConfig,
			final TranslatorFactory aTranslatorFactory)
	{
		return loadModel(
				aConfig.getRuntime_engine(), 
				aConfig.getModel_uri(), 
				aConfig.getOptArgs(),
				aConfig.getDevice_type(),
				aConfig.getModel_input_class(),
				aConfig.getModel_output_class(),
				aTranslatorFactory);
	}
	
	private static ZooModel<String, float[]> loadModel(
			final String aRTEngine, 
			String aModelPath, 
			final Map<String,Object> aMapArgs,
			final Device aDevice,
			final Class aInputClass,
			final Class aOutputClass,
			final TranslatorFactory aTranslatorFactory)
	{
		long lStartMs = System.currentTimeMillis();
		
		int iPos = aModelPath.indexOf(":");
		if(iPos>-1)
		{
			aModelPath = aModelPath.substring(iPos+1);
		}
		
		
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
		Criteria.Builder<String, float[]> builder = Criteria.builder()
        	    .setTypes(aInputClass, aOutputClass)
        	    .optEngine(aRTEngine)
        	    .optModelUrls(folderModel.getAbsolutePath()); // DJL looks here first
        	    
        if(aDevice!=null)
        	builder.optDevice(aDevice);
        	
        if(aMapArgs!=null && aMapArgs.size()>0)
        	builder.optArguments(aMapArgs);
		
		if(aTranslatorFactory!=null)
			builder.optTranslatorFactory(aTranslatorFactory);

        Criteria<String, float[]> criteria = builder.build();
		
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
			return model;
		}
		
		return null;
	}

}