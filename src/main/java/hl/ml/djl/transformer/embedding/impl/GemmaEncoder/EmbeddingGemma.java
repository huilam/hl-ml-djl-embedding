package hl.ml.djl.transformer.embedding.impl.GemmaEncoder;

import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.DJLConstants;
import hl.ml.djl.transformer.embedding.common.DjlModelConfig;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;

public class EmbeddingGemma extends EmbeddingCommon{
	
	private static EmbeddingGemma instant = null;
	
	private final static String model_name = "embeddinggemma-300m";
    
	protected EmbeddingGemma()
	{			
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_name);
		config.setRuntime_engine(DJLConstants.RT_ENGINE_ONNX);
		//
		config.addOptArg("padding", "true");
		config.addOptArg("truncation", "true");
		config.addOptArg("pooling", "mean"); 
		config.addOptArg("includeTokenTypes", "false"); // Gemma is decoder-only
		//
		super(EmbeddingGemma.class, config);
	}
	
	public static EmbeddingGemma getInstance()
	{
		if(instant==null)
		{
			instant = new EmbeddingGemma();
		}
		return instant;
	}
	
	public static void main(String[] args) throws TranslateException {
		EmbeddingCommon.unit_test_1( EmbeddingGemma.getInstance() );
    }
}