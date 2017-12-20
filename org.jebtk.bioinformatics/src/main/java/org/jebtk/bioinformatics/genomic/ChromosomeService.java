/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.bioinformatics.genomic;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.HashMapCreator;
import org.jebtk.core.collections.IterHashMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.text.TextUtils;


// TODO: Auto-generated Javadoc
/**
 * Deals with functions related to chromosomes.
 *
 * @author Antony Holmes Holmes
 *
 */
public class ChromosomeService {
	
	
	
	/**
	 * The Class ChromosomesLoader.
	 */
	private static class ChromosomesLoader {
		
		/** The Constant INSTANCE. */
		private static final ChromosomeService INSTANCE = new ChromosomeService();
	}

	/**
	 * Gets the single instance of SettingsService.
	 *
	 * @return single instance of SettingsService
	 */
	public static ChromosomeService getInstance() {
		return ChromosomesLoader.INSTANCE;
	}

	/** The Constant HUMAN_PARSER. */
	private static final ChromosomeParser HUMAN_PARSER = new ChrParserHuman();
	
	/** The Constant MOUSE_PARSER. */
	private static final ChromosomeParser MOUSE_PARSER = new ChrParserMouse();

	
	private IterMap<String, ChromosomeParser> mParserMap =
			new IterHashMap<String, ChromosomeParser>();
	
	/**
	 * Cache parsed chromosomes.
	 */
	private IterMap<String, IterMap<String, Chromosome>> mChrMap = 
			DefaultHashMap.create(new HashMapCreator<String, Chromosome>());
	
	private ChrSpeciesGuess mChrGuess = new ChrSpeciesGuess();
	
	
	/**
	 * Instantiates a new chromosomes.
	 */
	private ChromosomeService() {
		register(HUMAN_PARSER);
		register(MOUSE_PARSER);
	}
	
	public void register(ChromosomeParser parser) {
		mParserMap.put(parser.getSpecies(), parser);
	}
	
	/*
	public ChromosomeParser human() {
		return parser("human");
	}
	
	public ChromosomeParser mouse() {
		return parser("mouse");
	}
	
	public ChromosomeParser parser(String species) {
		return mParserMap.get(species);
	}
	*/
	
	public void setChrGuess(ChrSpeciesGuess chrGuess) {
		mChrGuess = chrGuess;
	}
	
	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @return the chromosome
	 */
	public Chromosome parse(String chr) {
		return human(chr);
	}
	
	public Chromosome human(String chr) {
		return parse("human", chr);
	}
	
	public Chromosome mouse(String chr) {
		return parse("mouse", chr);
	}
	
	/**
	 * Guess the species from file name.
	 * 
	 * @param file
	 * @param chr
	 * @return
	 */
	public Chromosome guess(Path file, String chr) {
		return guess(PathUtils.getName(file), chr);
	}
	
	/**
	 * Guess species from name.
	 * 
	 * @param name
	 * @param chr
	 * @return
	 */
	public Chromosome guess(String name, String chr) {
		return parse(guess(name), chr);
	}
	
	public String guess(Path file) {
		return guess(PathUtils.getName(file));
	}
	
	public String guess(String name) {
		return mChrGuess.guess(name);
	}

	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @param parser the parser
	 * @return the chromosome
	 */
	public Chromosome parse(String species, String chr) {
		species = getSpecies(species);
		
		if (!mChrMap.get(species).containsKey(chr)) {
			mChrMap.get(species).put(chr, mParserMap.get(species).parse(chr));
		}
		
		return mChrMap.get(species).get(chr);
	}
	
	

	public static String getSpecies(String species) {
		if (TextUtils.find(species, "hg17", "hg18", "hg19", "grch38", "human")) {
			return "human";
		} else if (TextUtils.find(species, "mm9", "mm10", "grcm38", "mouse")) {
			return "mouse";
		} else {
			return species;
		}
	}

	/**
	 * Parses a range of numbers containing comma separated lists and ranges of the form x-y.
	 *
	 * @param text the text
	 * @return the list
	 */
	public static final List<Short> parseNumberRanges(String text) {
		List<Short> numbers = new ArrayList<Short>();

		List<String> commas = TextUtils.fastSplitRemoveQuotes(text, TextUtils.COMMA_DELIMITER_CHAR);

		for (String term : commas) {
			List<String> range = TextUtils.fastSplitRemoveQuotes(term, TextUtils.DASH_DELIMITER_CHAR);

			int start = Integer.parseInt(range.get(0));

			int end = start;

			//System.out.println(range);

			if (range.size() == 2) {
				end = Integer.parseInt(range.get(1));
			}

			for (int i = start; i <= end; ++i) {
				numbers.add((short)i);
			}
		}

		return numbers;
	}

	/**
	 * Parses a number range specifically of the form x-y.
	 *
	 * @param text the text
	 * @return the list
	 */
	public static final List<Integer> parseNumberRange(String text) {
		List<String> range = TextUtils.fastSplitRemoveQuotes(text, TextUtils.DASH_DELIMITER_CHAR);

		int start = Integer.parseInt(range.get(0));

		List<Integer> numbers = new ArrayList<Integer>();

		numbers.add(start);

		int end = start;

		if (range.size() == 2) {
			end = Integer.parseInt(range.get(1));
		}

		if (end > start) {
			numbers.add(end);
		}

		return numbers;
	}

	public Chromosome randChr(String species) {
		return mParserMap.get(species).randChr();
	}
}
