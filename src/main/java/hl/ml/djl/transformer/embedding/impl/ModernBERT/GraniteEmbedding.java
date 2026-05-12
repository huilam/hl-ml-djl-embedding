package hl.ml.djl.transformer.embedding.impl.ModernBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.DJLConstants;
import hl.ml.djl.transformer.embedding.common.DjlModelConfig;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;
import hl.ml.djl.transformer.embedding.common.test.UnitTest;

public class GraniteEmbedding extends EmbeddingCommon{
	
	private static GraniteEmbedding instant = null;
	private final static String model_name = "granite-embedding-english-r2";
    
	protected GraniteEmbedding()
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
		super(GraniteEmbedding.class, config);
	}
	
	public static GraniteEmbedding getInstance()
	{
		if(instant==null)
		{
			instant = new GraniteEmbedding();
		}
		return instant;
	}
	
	public static void main(String[] args) throws TranslateException {
		
		UnitTest.testAll( GraniteEmbedding.getInstance() );
    }
}