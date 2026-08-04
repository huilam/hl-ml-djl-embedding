package hl.ml.djl.embedding.text.common;

import java.io.File;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hl.common.FileUtil;

public class SentenceSplitter {
	
	private List<String> listAbbreviations = new ArrayList<>();
	private Locale locale = Locale.ENGLISH;
	private boolean includeParagraphIndex = false;
	
	public SentenceSplitter(Locale aLocale)
	{
		this.locale = aLocale;
	}
	
	public SentenceSplitter()
	{
		this.locale = Locale.ENGLISH;
	}
	
	public boolean initAbbreviations(List<String> aAbbreviationList) throws Exception
	{
		listAbbreviations.clear();
		if(aAbbreviationList!=null && aAbbreviationList.size()>0)
		{
			for(String sAbbr : aAbbreviationList)
			{
				if(sAbbr.trim().length()>0)
				{
					listAbbreviations.add(sAbbr.toLowerCase());
				}
			}
		}
		else
		{
			throw new Exception("Failed to load abbreviations from list: "+aAbbreviationList);
		}
		
		return listAbbreviations.size()>0;
		
	}
	public boolean initAbbreviations(String aDictFileName) throws Exception
	{
		String sData = FileUtil.loadContent(aDictFileName);
		if(sData!=null)
		{
			return initAbbreviations(sData.lines().toList());
		}
		else
		{
			throw new Exception("Failed to load abbreviations from file: "+aDictFileName);
		}
	}
	
	private boolean isEndWithAbbreviations(String aSentence) {
	    boolean isFound = false;
	    String sTrimmedSentence = aSentence.trim();
	    
	    String sEndText = sTrimmedSentence.toLowerCase(); 
	    int iPos = sTrimmedSentence.lastIndexOf(" ");
	    if(iPos>-1)
	    {
	    	sEndText = sEndText.substring(iPos+1).trim();
	    }
	    //System.out.println("sEndText = "+sEndText);
	    isFound = (listAbbreviations.contains(sEndText));
	    return isFound;
	}
	
	public boolean isIncludeParagraphIndex() {
		return includeParagraphIndex;
	}

	public void setIncludeParagraphIndex(boolean aIncludeParagraphIndex) {
		this.includeParagraphIndex = aIncludeParagraphIndex;
	}

	public String[] split(final String aParagraphs) {
		
		if(aParagraphs==null || aParagraphs.trim().length()==0)
		{
			return new String[0];
		}
		
		List<String> listSentences = new ArrayList<>();
		BreakIterator iterSentence = BreakIterator.getSentenceInstance(this.locale);
		String[] paragraphs = aParagraphs.split("\n\n");
		
		int iParaCount = 0;
		String sParagraphPrefix = "";
		for(String sParagraph : paragraphs)
		{
			if(includeParagraphIndex)
			{
				sParagraphPrefix = String.format("[%02d]",(++iParaCount));
			}
			
			iterSentence.setText(sParagraph);
			
			int start = iterSentence.first();
	        for (int end = iterSentence.next(); end != BreakIterator.DONE; end = iterSentence.next()) {
	        	String sentence = sParagraph.substring(start, end).trim();
	        	if(isEndWithAbbreviations(sentence))
	        	{
	        		continue;
	        	}
	        	else
	        	{
	        		start = end;
	        		listSentences.add(sParagraphPrefix+sentence.replaceAll("\n", " "));
	        	}
	        }
	        
	        if (start < sParagraph.length()) {
	        	String remaining = sParagraph.substring(start).trim();
	        	if (remaining.length() > 0) {
	        		listSentences.add(sParagraphPrefix + remaining.replaceAll("\n", " "));
	        	}
	        }
		}
		return listSentences.toArray(new String[0]);
	}
	
	public static void main(String[] args) throws Exception {
		SentenceSplitter splitter = new SentenceSplitter();
		splitter.initAbbreviations("abbreviations.dict");
		
		String[] sTestDataFileNames = new String[] {
				"/test-01-sentences.txt"
				//,
				//"/test-02-paragraph-multi.txt"
				};
		
		for(String sFileName : sTestDataFileNames)
		{
			URL url =  SentenceSplitter.class.getResource(sFileName);
			if(url!=null || new File(sFileName).exists())
			{
				
				System.out.println("==============================================");
				System.out.println("== Processing file: "+sFileName);
				System.out.println("==============================================");
				String sContent = FileUtil.loadContent(sFileName);
				String[] sentences = splitter.split(sContent);
				for (String sentence : sentences) {
					System.out.println(sentence);
				}
				System.out.println();
			}
			else
			{
				System.err.println("File not found: "+sFileName);
				continue;
			}
		}
	}
	
}