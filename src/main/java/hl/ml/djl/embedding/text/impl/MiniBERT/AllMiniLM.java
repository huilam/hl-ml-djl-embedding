package hl.ml.djl.embedding.text.impl.MiniBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.DjlConstants;
import hl.ml.djl.DjlModelConfig;
import hl.ml.djl.embedding.text.common.EmbeddingCommon;
import hl.ml.djl.embedding.text.common.test.UnitTest;

public class AllMiniLM extends EmbeddingCommon{
	
	private static AllMiniLM instance = null;
	private final static String model_name		= "all-MiniLM-L12-v2";
    
	protected AllMiniLM()
	{
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_name);
		config.setRuntime_engine(DjlConstants.RT_ENGINE_ONNX);
		//
		config.addOptArg("padding", "true");
		config.addOptArg("truncation", "true");
		config.addOptArg("includeTokenTypes", "true"); 
		//
		super(AllMiniLM.class, config);
	}
	
	public static AllMiniLM getInstance()
	{
		if(instance==null)
		{
			instance = new AllMiniLM();
		}
		return instance;
	}
	
	
	
	public static void main(String[] args) throws TranslateException {
		
		UnitTest.testAll( AllMiniLM.getInstance() );
		
    }
	
}