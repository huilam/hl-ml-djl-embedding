package hl.ml.djl.embedding.text.impl.MiniBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.DjlConstants;
import hl.ml.djl.DjlModelConfig;
import hl.ml.djl.embedding.text.common.TextEmbeddingCommon;

public class AllMiniLM extends TextEmbeddingCommon{
	
	private static AllMiniLM instance = null;
	private final static String model_name		= "all-MiniLM-L12-v2";
    
	protected AllMiniLM()
	{
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_name);
		config.setRuntime_engine(DjlConstants.RT_ENGINE_ONNX);
		//
		config.addMLArg("padding", "true");
		config.addMLArg("truncation", "true");
		config.addMLArg("includeTokenTypes", "true"); 
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
		
		hl.ml.djl.embedding.text.test.UnitTest.testAll( AllMiniLM.getInstance() );
		
    }
	
}