package com.vinod.nlp.ner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

@Service
public class NERService {

	public NERService() {
		//super();
	}

	static NameFinderME[] finders = new NameFinderME[3];
	static String[] names = { "person", "location", "organization" };
	static Map<String, Set<String>> nerMap = new HashMap<String, Set<String>>();

	public Map<String, Set<String>> findNER(String text) {
		try {
			InputStream modelIn = new FileInputStream("/home/yotta/nlp/ner/models/en-sent.bin");
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			String[] sentences = sentenceDetector.sentDetect(text);
			for (String str : sentences) {
				System.out.println("sentence : " + str);
			}
			for (int mi = 0; mi < names.length; mi++) {
				String modelDir = "/home/yotta/nlp/ner/models";
				finders[mi] = new NameFinderME(new TokenNameFinderModel(
						new FileInputStream(new File(modelDir, "en-ner-" + names[mi] + ".bin"))));
			}
			Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
			for (int si = 0; si < sentences.length; si++) {
				String[] tokens = tokenizer.tokenize(sentences[si]);
				getNER(tokens);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("NER-Map :" + nerMap);
		return nerMap;
	}

	private void getNER(String[] tokens) {
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
				if (nerMap.containsKey(names[fi])) {
					entitySet = nerMap.get(names[fi]);
					entitySet.add(sb.toString().trim());
				} else {
					entitySet.add(sb.toString().trim());
				}
				nerMap.put(names[fi], entitySet);
			}
		}
	}

}
