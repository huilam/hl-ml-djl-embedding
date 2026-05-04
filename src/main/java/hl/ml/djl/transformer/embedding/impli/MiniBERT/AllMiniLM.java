package hl.ml.djl.transformer.embedding.impli.MiniBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.DJLConstants;
import hl.ml.djl.transformer.embedding.common.DjlModelConfig;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;

public class AllMiniLM extends EmbeddingCommon{
	
	private static AllMiniLM instant = null;
	private final static String model_name		= "all-MiniLM-L12-v2";
    
	protected AllMiniLM()
	{
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_name);
		config.setRuntime_engine(DJLConstants.RT_ENGINE_ONNX);
		//
		config.addOptArg("padding", "true");
		config.addOptArg("truncation", "true");
		config.addOptArg("includeTokenTypes", "true"); 
		//
		super(AllMiniLM.class, config);
	}
	
	public static AllMiniLM getInstance()
	{
		if(instant==null)
		{
			instant = new AllMiniLM();
		}
		return instant;
	}
	
	
	
	public static void main(String[] args) throws TranslateException {
		EmbeddingCommon.unit_test_1( AllMiniLM.getInstance() );
    }
	
}