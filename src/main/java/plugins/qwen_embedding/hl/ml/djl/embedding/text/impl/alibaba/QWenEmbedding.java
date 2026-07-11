package hl.ml.djl.embedding.text.impl.alibaba;

import hl.ml.djl.embedding.text.common.TextEmbeddingCommon;

public class QWenEmbedding extends TextEmbeddingCommon{
	
	private static QWenEmbedding instance = null;
    
	protected QWenEmbedding()
	{	
		super(QWenEmbedding.class, null);
	}
	
	public static QWenEmbedding getInstance()
	{
		if(instance==null)
		{
			instance = new QWenEmbedding();
		}
		return instance;
	}
	
}