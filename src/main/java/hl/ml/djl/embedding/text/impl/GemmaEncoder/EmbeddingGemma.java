package hl.ml.djl.embedding.text.impl.GemmaEncoder;

import ai.djl.translate.TranslateException;
import hl.ml.djl.DjlConstants;
import hl.ml.djl.DjlModelConfig;
import hl.ml.djl.embedding.text.common.EmbeddingCommon;
import hl.ml.djl.embedding.text.common.test.UnitTest;

public class EmbeddingGemma extends EmbeddingCommon{
	
	private static EmbeddingGemma instance = null;
	
	private final static String model_name = "embeddinggemma-300m";
    
	protected EmbeddingGemma()
	{			
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_name);
		config.setRuntime_engine(DjlConstants.RT_ENGINE_ONNX);
		//
		config.addMLArg("padding", "true");
		config.addMLArg("truncation", "true");
		config.addMLArg("pooling", "mean"); 
		config.addMLArg("includeTokenTypes", "false"); // Gemma is decoder-only
		//
		super(EmbeddingGemma.class, config);
	}
	
	public static EmbeddingGemma getInstance()
	{
		if(instance==null)
		{
			instance = new EmbeddingGemma();
		}
		return instance;
	}
	
	public static void main(String[] args) throws TranslateException {

		UnitTest.testAll( EmbeddingGemma.getInstance() );
		
    }
}