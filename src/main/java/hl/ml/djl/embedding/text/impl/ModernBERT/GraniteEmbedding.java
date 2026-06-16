package hl.ml.djl.embedding.text.impl.ModernBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.DJLConstants;
import hl.ml.djl.DjlModelConfig;
import hl.ml.djl.embedding.text.common.EmbeddingCommon;
import hl.ml.djl.embedding.text.common.test.UnitTest;

public class GraniteEmbedding extends EmbeddingCommon{
	
	private static GraniteEmbedding instance = null;
	private final static String[] model_names = new String[]{
			"granite-embedding-english-r2",
			"granite-embedding-97m-multilingual-r2"};
    
	protected GraniteEmbedding()
	{	
		DjlModelConfig config = new DjlModelConfig();
		//
		config.setModel_name(model_names[0]);
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
		if(instance==null)
		{
			instance = new GraniteEmbedding();
		}
		return instance;
	}
	
	public static void main(String[] args) throws TranslateException {
		
		UnitTest.testAll( GraniteEmbedding.getInstance() );
    }
}