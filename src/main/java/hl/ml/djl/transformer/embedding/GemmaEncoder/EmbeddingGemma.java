package hl.ml.djl.transformer.embedding.GemmaEncoder;

import java.util.HashMap;
import java.util.Map;

import ai.djl.translate.TranslateException;
import hl.ml.djl.transformer.embedding.common.DJLConstants;
import hl.ml.djl.transformer.embedding.common.EmbeddingCommon;

public class EmbeddingGemma extends EmbeddingCommon{
	
	private static EmbeddingGemma instant = null;
	
	private final static String model_name = "embeddinggemma-300m";
    
	protected EmbeddingGemma()
	{	
		Map<String, Object> mapArgs = new HashMap<>();
		mapArgs.put("padding", "true");
		mapArgs.put("truncation", "true");
		mapArgs.put("pooling", "mean"); 
		mapArgs.put("includeTokenTypes", "false"); // Gemma is decoder-only
		
		super(EmbeddingGemma.class, DJLConstants.RT_ENGINE_ONNX, model_name, mapArgs);
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