package hl.ml.djl.embedding.text.impl.GemmaEncoder;

import ai.djl.translate.TranslateException;
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
	
	public static void main(String[] args) throws TranslateException {

		hl.ml.djl.embedding.text.test.UnitTest.testAll( EmbeddingGemma.getInstance() );
		
    }
}