package hl.ml.djl.transformer.embedding.ModernBERT;

import java.util.HashMap;
import java.util.Map;

import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.DJLConstants;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;

public class GraniteEmbedding extends EmbeddingCommon{
	
	private static GraniteEmbedding instant = null;
	private final static String model_name = "granite-embedding-english-r2";
    
	protected GraniteEmbedding()
	{
		Map<String, Object> mapArgs = new HashMap<>();
		mapArgs.put("padding", "true");
		mapArgs.put("truncation", "true");
		mapArgs.put("pooling", "mean"); 
		mapArgs.put("includeTokenTypes", "false"); // Gemma is decoder-only
		
		super(GraniteEmbedding.class, DJLConstants.RT_ENGINE_ONNX, model_name, mapArgs);
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
		EmbeddingCommon.unit_test_1( GraniteEmbedding.getInstance() );
    }
}