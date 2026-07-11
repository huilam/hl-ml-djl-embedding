package hl.ml.djl.embedding.text.impl.MiniBERT;

import ai.djl.translate.TranslateException;
import hl.ml.djl.embedding.text.common.TextEmbeddingCommon;

public class AllMiniLM extends TextEmbeddingCommon{
	
	private static AllMiniLM instance = null;
    
	protected AllMiniLM()
	{
		super(AllMiniLM.class, "all-MiniLM-L12-v2");
	}
	
	public static AllMiniLM getInstance()
	{
		if(instance==null)
		{
			instance = new AllMiniLM();
		}
		return instance;
	}
	
	
	
	public static void main(String[] args) throws TranslateException {
		
		hl.ml.djl.embedding.text.test.UnitTest.testAll( AllMiniLM.getInstance() );
		
    }
	
}