package hl.ml.djl.embedding.text.impl.google;

import hl.ml.djl.embedding.text.common.TextEmbeddingCommon;

public class EmbeddingGemma extends TextEmbeddingCommon{
	
	private static EmbeddingGemma instance = null;
    
	protected EmbeddingGemma()
	{
		super(EmbeddingGemma.class, null);
	}
	
	public static EmbeddingGemma getInstance()
	{
		if(instance==null)
		{
			instance = new EmbeddingGemma();
		}
		return instance;
	}
	
}