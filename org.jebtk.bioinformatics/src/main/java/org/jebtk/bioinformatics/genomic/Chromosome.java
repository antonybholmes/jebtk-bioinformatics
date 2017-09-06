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

import java.text.ParseException;
import java.util.Random;

import org.jebtk.core.IdProperty;
import org.jebtk.core.NameProperty;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * Represents a chromosome that can be sorted.
 *
 * @author Antony Holmes Holmes
 */
public class Chromosome implements Comparable<Chromosome>, IdProperty, NameProperty {

	/** The Constant HUMAN_PARSER. */
	public static final ChromosomeParser HUMAN_PARSER = new ChrParserHuman();
	
	/** The Constant MOUSE_PARSER. */
	public static final ChromosomeParser MOUSE_PARSER = new ChrParserMouse();

	/**
	 * Represents an invalid chromosome.
	 */
	public static final Chromosome NO_CHR = new Chromosome(-1, "U");
	
	/**
	 * The Class Human.
	 */
	public static class Human {
		/**
		 * The constant CHR1.
		 */
		public static final Chromosome CHR1 = parseHuman("chr1");

		/**
		 * The constant CHR2.
		 */
		public static final Chromosome CHR2 = parseHuman("chr2");

		/**
		 * The constant CHR3.
		 */
		public static final Chromosome CHR3 = parseHuman("chr3");

		/**
		 * The constant CHR4.
		 */
		public static final Chromosome CHR4 = parseHuman("chr4");

		/**
		 * The constant CHR5.
		 */
		public static final Chromosome CHR5 = parseHuman("chr5");

		/**
		 * The constant CHR6.
		 */
		public static final Chromosome CHR6 = parseHuman("chr6");

		/**
		 * The constant CHR7.
		 */
		public static final Chromosome CHR7 = parseHuman("chr7");

		/**
		 * The constant CHR8.
		 */
		public static final Chromosome CHR8 = parseHuman("chr8");

		/**
		 * The constant CHR9.
		 */
		public static final Chromosome CHR9 = parseHuman("chr9");

		/**
		 * The constant CHR10.
		 */
		public static final Chromosome CHR10 = parseHuman("chr10");

		/**
		 * The constant CHR11.
		 */
		public static final Chromosome CHR11 = parseHuman("chr11");

		/**
		 * The constant CHR12.
		 */
		public static final Chromosome CHR12 = parseHuman("chr12");

		/**
		 * The constant CHR13.
		 */
		public static final Chromosome CHR13 = parseHuman("chr13");

		/**
		 * The constant CHR14.
		 */
		public static final Chromosome CHR14 = parseHuman("chr14");

		/**
		 * The constant CHR15.
		 */
		public static final Chromosome CHR15 = parseHuman("chr15");

		/**
		 * The constant CHR16.
		 */
		public static final Chromosome CHR16 = parseHuman("chr16");

		/**
		 * The constant CHR17.
		 */
		public static final Chromosome CHR17 = parseHuman("chr17");

		/**
		 * The constant CHR18.
		 */
		public static final Chromosome CHR18 = parseHuman("chr18");

		/**
		 * The constant CHR19.
		 */
		public static final Chromosome CHR19 = parseHuman("chr19");

		/**
		 * The constant CHR20.
		 */
		public static final Chromosome CHR20 = parseHuman("chr20");

		/**
		 * The constant CHR21.
		 */
		public static final Chromosome CHR21 = parseHuman("chr21");

		/**
		 * The constant CHR22.
		 */
		public static final Chromosome CHR22 = parseHuman("chr22");

		/**
		 * The constant CHRX.
		 */
		public static final Chromosome CHRX = parseHuman("chrX");

		/**
		 * The constant CHRY.
		 */
		public static final Chromosome CHRY = parseHuman("chrY");

		/**
		 * The constant CHRM.
		 */
		public static final Chromosome CHRM = parseHuman("chrM");

		/**
		 * Chromosomes 1-Y.
		 */
		public static final Chromosome[] CHROMOSOMES = {CHR1, 
				CHR2, 
				CHR3, 
				CHR4, 
				CHR5, 
				CHR6, 
				CHR7, 
				CHR8, 
				CHR9, 
				CHR10, 
				CHR11, 
				CHR12, 
				CHR13, 
				CHR14, 
				CHR15, 
				CHR16, 
				CHR17, 
				CHR18, 
				CHR19, 
				CHR20, 
				CHR21, 
				CHR22, 
				CHRX, 
				CHRY};

		/**
		 * The constant CHROMOSOMES_M.
		 */
		public static final Chromosome[] CHROMOSOMES_M = {CHR1, 
				CHR2, 
				CHR3, 
				CHR4, 
				CHR5, 
				CHR6, 
				CHR7, 
				CHR8, 
				CHR9, 
				CHR10, 
				CHR11, 
				CHR12, 
				CHR13, 
				CHR14, 
				CHR15, 
				CHR16, 
				CHR17, 
				CHR18, 
				CHR19, 
				CHR20, 
				CHR21, 
				CHR22, 
				CHRX, 
				CHRY, 
				CHRM};
		
		
		/**
		 * Instantiates a new human.
		 */
		private Human() {
			// Do nothing
		}
		
		/**
		 * Parse a chromosome from an expression.
		 *
		 * @param chr the chr
		 * @return the int
		 * @throws ParseException the parse exception
		 * @throws NumberFormatException {
		 */
		/*
		public int valueOf(String chr) throws NumberFormatException {
			String cleaned = 
					chr.toLowerCase().replaceFirst("^chr", "").replaceAll("[^0-9a-z]", "");

			if (cleaned.equals("x")) {
				return 23;
			} else if (cleaned.equals("y")) {
				return 24;
			} else if (cleaned.equals("m")) {
				return 25;
			} else {
				return Integer.parseInt(cleaned);
			}
		}
		*/
		
		public static int valueOf(Chromosome chr) {
			if (chr.toString().endsWith("X")) {
				return 23;
			} else if (chr.toString().endsWith("Y")) {
				return 24;
			} else if (chr.toString().endsWith("M")) {
				return 25;
			} else {
				return Integer.parseInt(chr.getShortName());
			}
		}

		/**
		 * Gets the chromosome.
		 *
		 * @param chr the chr
		 * @return the chromosome
		 * @throws NumberFormatException the number format exception
		 */
		public static Chromosome getChromosome(int chr) throws NumberFormatException {
			return CHROMOSOMES[chr - 1];
		}

		/**
		 * Returns a shared instance of a chromosome
		 * to reduce object creation.
		 *
		 * @param chr the chr
		 * @return the chromosome
		 */
		public static Chromosome parse(String chr) {
			if (chr.contains("_")) {
				return null;
			}

			String lchr = chr.toLowerCase()
					.replace("chromosome", "")
					.replace("chr", "");

			if (lchr.startsWith("10")) {
				return CHR10;
			} else if (lchr.startsWith("11")) {
				return CHR11;
			} else if (lchr.startsWith("12")) {
				return CHR12;
			} else if (lchr.startsWith("13")) {
				return CHR13;
			} else if (lchr.startsWith("14")) {
				return CHR14;
			} else if (lchr.startsWith("15")) {
				return CHR15;
			} else if (lchr.startsWith("16")) {
				return CHR16;
			} else if (lchr.startsWith("17")) {
				return CHR17;
			} else if (lchr.startsWith("18")) {
				return CHR18;
			} else if (lchr.startsWith("19")) {
				return CHR19;
			} else if (lchr.startsWith("20")) {
				return CHR20;
			} else if (lchr.startsWith("21")) {
				return CHR21;
			} else if (lchr.startsWith("22")) {
				return CHR22;
			} else if (lchr.startsWith("23")) {
				return CHRX;
			} else if (lchr.startsWith("x")) {
				return CHRX;
			} else if (lchr.startsWith("24")) {
				return CHRY;
			} else if (lchr.startsWith("y")) {
				return CHRY;
			} else if (lchr.startsWith("25")) {
				return CHRY;
			} else if (lchr.startsWith("m")) {
				return CHRM;
			} else if (lchr.startsWith("1")) {
				return CHR1;
			} else if (lchr.startsWith("2")) {
				return CHR2;
			} else if (lchr.startsWith("3")) {
				return CHR3;
			} else if (lchr.startsWith("4")) {
				return CHR4;
			} else if (lchr.startsWith("5")) {
				return CHR5;
			} else if (lchr.startsWith("6")) {
				return CHR6;
			} else if (lchr.startsWith("7")) {
				return CHR7;
			} else if (lchr.startsWith("8")) {
				return CHR8;
			} else if (lchr.startsWith("9")) {
				return CHR9;
			} else {
				// return a new chromosome without id.
				return null;
			}
		}

		/**
		 * Rand chr.
		 *
		 * @return the chromosome
		 */
		public static Chromosome randChr() {
			return CHROMOSOMES[new Random().nextInt(CHROMOSOMES.length)];
		}
	}

	
	
	/**
	 * The member chr.
	 */
	private String mChr = null;
	
	/** The m short name. */
	private String mShortName;
	//private String mSpecies;

	/** The m id. */
	private int mId;
	
	/**
	 * Instantiates a new chromosome.
	 *
	 * @param chr the chr
	 * @param parser the parser
	 */
	private Chromosome(int id, String shortName) {
		//mSpecies = parser.getSpecies();
		
		// Ensures chromosome always begins with chr prefix and not cHr or
		// some other variant
		
		// The suffix of the chromosome without the chr prefix. 
		mShortName = shortName;
		
		mId = id;
		
		mChr = "chr" + mShortName.toUpperCase().replaceAll("CHR", TextUtils.EMPTY_STRING);
	}
	
	/* (non-Javadoc)
	 * @see org.abh.common.IdProperty#getId()
	 */
	@Override
	public int getId() {
		return mId;
	}
	
	//public String getSpecies() {
	//	return mSpecies;
	//}
	
	/* (non-Javadoc)
	 * @see org.abh.common.NameProperty#getName()
	 */
	@Override
	public String getName() {
		return mChr;
	}

	/**
	 * Gets the short name.
	 *
	 * @return the short name
	 */
	public String getShortName() {
		return mShortName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mChr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Chromosome c) {
		//System.err.println("compare chr " + mId + " " + c.mId + " " + mChr + " " + c.mChr);
		
		if (mId != -1 && c.mId != -1) {
			if (mId > c.mId) {
				return 1;
			} else if (mId < c.mId) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return mChr.compareTo(c.mChr);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Chromosome) {
			return compareTo((Chromosome)o) == 0;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mChr.hashCode();
	}
	
	/**
	 * Parses the human.
	 *
	 * @param chr the chr
	 * @return the chromosome
	 */
	public static Chromosome parseHuman(String chr) {
		return parse(chr, HUMAN_PARSER);
	}
	
	/**
	 * Parses the mouse.
	 *
	 * @param chr the chr
	 * @return the chromosome
	 */
	public static Chromosome parseMouse(String chr) {
		return parse(chr, MOUSE_PARSER);
	}
	
	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @return the chromosome
	 */
	public static Chromosome parse(String chr) {
		return parseHuman(chr);
	}
	
	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @param parser the parser
	 * @return the chromosome
	 */
	public static Chromosome parse(String chr, ChromosomeParser parser) {
		int id = parser.getId(chr);
		String shortName = parser.getShortName(chr);
		
		return new Chromosome(id, shortName);
	}
	
	/**
	 * Returns true if a string starts with chr (case insensitive).
	 *
	 * @param value the value
	 * @return true, if is chr
	 */
	public static boolean isChr(String value) {
		if (value == null) {
			return false;
		}
		
		return value.toLowerCase().startsWith("chr");
	}
}
