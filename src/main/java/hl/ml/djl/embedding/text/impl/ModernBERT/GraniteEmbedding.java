package hl.ml.djl.embedding.text.impl.ModernBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.embedding.text.common.TextEmbeddingCommon;

public class GraniteEmbedding extends TextEmbeddingCommon{
	
	private static GraniteEmbedding instance = null;
	
	private static String[] embeddings 	= new String[]{"english", "multilingual"};
    
	protected GraniteEmbedding()
	{	
		super(GraniteEmbedding.class, embeddings[1]);
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
		
		hl.ml.djl.embedding.text.test.UnitTest.testAll( GraniteEmbedding.getInstance() );
    }
}