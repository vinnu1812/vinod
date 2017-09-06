package com.vinod.nlp.ner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
	
public class Test {
	
	static NameFinderME[] finders = new NameFinderME[3];
	static String[] names = { "person", "location", "organization" };
	static Map<String , Set<String>> nerMap = new HashMap<String, Set<String>>();
	
	public static void getNER(String[] tokens) {
		for (int fi = 0; fi < finders.length; fi++) {
			Set<String> entitySet = new HashSet<String>();
			Span[] spans = finders[fi].find(tokens);
			StringBuilder sb; 
			for (Span s : spans) {
				sb = new StringBuilder();
				for (int x = s.getStart(); x < s.getEnd(); x++) {
					sb.append(tokens[x]);
					sb.append(" ");
				}
				if(nerMap.containsKey(names[fi])){
					entitySet = nerMap.get(names[fi]);
				    entitySet.add(sb.toString().trim());
				} else {
					entitySet.add(sb.toString().trim());
				}
				nerMap.put(names[fi], entitySet);
			}
		}
	}
	public static void main(String[] args) throws InvalidFormatException, FileNotFoundException, IOException {

		/*String[] sentences = {
				"Former first lady Nancy Reagan, Williams was taken to a " + "suburban Los Angeles "
						+ "hospital as a precaution Sunday after a fall at " + "her home, an " + "aide said. ",
				"The 86-year-old Reagan will remain overnight for "
						+ "observation at a hospital in Santa Monica, California, " + "said Joanne "
						+ "Drake, chief of staff for the Reagan Foundation." };*/
		
		//String reqStr = "Starbucks is not doing very well lately, It's main branch is in New York. Nancy Reagan, Williams are founders";
		String reqStr = "Former first lady Nancy Reagan, Williams was taken to a suburban Los Angeles hospital as a precaution Sunday after a fall at her home, an aide said.";
		InputStream modelIn = new FileInputStream("/home/yotta/nlp/ner/models/en-sent.bin");
		SentenceModel model = new SentenceModel(modelIn);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String[] sentences = sentenceDetector.sentDetect(reqStr);
		for(String str : sentences){
			System.out.println("sentence : "+ str);
		}
		
		for (int mi = 0; mi < names.length; mi++) {
			String modelDir = "/home/yotta/nlp/ner/models";
			finders[mi] = new NameFinderME(
					new TokenNameFinderModel(new FileInputStream(new File(modelDir, "en-ner-" + names[mi] + ".bin"))));
		}
		try {		
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
			for (int si = 0; si < sentences.length; si++) {
				String[] tokens = tokenizer.tokenize(sentences[si]);
				getNER(tokens);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		System.out.println("NER-Map :" + nerMap);
	}
}
