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
package org.jebtk.bioinformatics.dna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.Chromosome.Human;
import org.jebtk.bioinformatics.genomic.ChromosomeSizes;
import org.jebtk.bioinformatics.genomic.GenomeAssembly;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.SequenceRegion;
import org.jebtk.core.NameProperty;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.CharIterator;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a DNA sequence.
 * 
 * @author Antony Holmes Holmes
 *
 */
public class Sequence implements NameProperty, Iterable<Character> {
	
	/**
	 * The header pattern.
	 */
	public static Pattern HEADER_PATTERN = Pattern.compile(">(.+)");

	private static String DEFAULT_NAME = "DNA";
	
	/**
	 * The member sequence.
	 */
	protected String mSequence;

	private byte[] mIndexed;

	private char[] mArray;

	private String mName;
	
	public static final Pattern DNA_REGEX = Pattern.compile("[ACGTacgtNn]+");
	
	/**
	 * The constant LOG.
	 */
	private static final Logger LOG = 
			LoggerFactory.getLogger(Sequence.class);

	/**
	 * Instantiates a new sequence.
	 *
	 * @param name the name
	 * @param sequence the sequence
	 */
	private Sequence(String sequence) {
		this(DEFAULT_NAME, sequence);
	}
	
	private Sequence(String name, String sequence) {
		mName = name;
		mSequence = sequence;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	/**
	 * Gets the array.
	 *
	 * @return the array
	 */
	public char[] toArray() {
		if (mArray == null) {
			mArray = mSequence.toLowerCase().toCharArray();
		}
		
		return mArray;
	}
	
	public int length() {
		return mSequence.length();
	}
	
	/**
	 * Output a FASTA representation of the sequence.
	 * 
	 * @return
	 */
	public String toFasta() {
		return toFasta(mName);
	}
	
	/**
	 * Output a FASTA representation of the sequence.
	 * 
	 * @param name			Alternative name for sequence.
	 * @return
	 */
	public String toFasta(String name) {
		return toFasta(name, this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mSequence;
	}
	
	/**
	 * Reverse compliment this sequence.
	 * 
	 * @return		A copy of the sequence reverse complimented.
	 */
	public Sequence reverseComplement() {
		return reverseComplement(this);
	}
	
	/**
	 * Reverse complement.
	 *
	 * @param sequence the sequence
	 * @return the sequence
	 */
	public static Sequence reverseComplement(Sequence sequence) {
		return new Sequence(reverseComplement(sequence.mSequence));
	}
	
	/**
	 * Reverse compliment some DNA.
	 *
	 * @param sequence the sequence
	 * @return the string
	 */
	public static String reverseComplement(String sequence) {
		return complement(TextUtils.reverse(sequence));
	}
	
	/**
	 * Complement.
	 *
	 * @param sequence the sequence
	 * @return the string
	 */
	public static String complement(Sequence sequence) {
		return complement(sequence.mSequence);
	}
	
	/**
	 * Return the complement of a DNA sequence.
	 *
	 * @param sequence the sequence
	 * @return the string
	 */
	public static String complement(String sequence) {
		
		StringBuilder buffer = new StringBuilder();
		
		for (char c : sequence.toCharArray()) {
			switch(c) {
			case 'A':
				buffer.append('T');
				break;
			case 'a':
				buffer.append('t');
				break;
			case 'C':
				buffer.append('G');
				break;
			case 'c':
				buffer.append('g');
				break;
			case 'G':
				buffer.append('C');
				break;
			case 'g':
				buffer.append('c');
				break;
			case 'T':
				buffer.append('A');
				break;
			case 't':
				buffer.append('a');
				break;
			default:
				buffer.append('N');
				break;	
			}
		}
		
		return buffer.toString();
	}

	/**
	 * Return the character at a particular base
	 *
	 * @param i the i
	 * @return the char
	 */
	public char charAt(int i) {
		return mSequence.charAt(i);
	}
	
	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public int getLength() {
		return mSequence.length();
	}
	
	/**
	 * Gets the chars.
	 *
	 * @return the chars
	 */
	public char[] getChars() {
		return mSequence.toCharArray();
	}
	
	/**
	 * Return a numerical representation of the sequence where a = 0, c = 1,
	 * g = 2, t = 3.
	 *
	 * @return the byte[]
	 */
	public byte[] toIndex() {
		if (mIndexed == null) {
			char[] seq = mSequence.toLowerCase().toCharArray();

			mIndexed = seqToIndexSeq(seq);
		}

		return mIndexed;
	}
	
	@Override
	public Iterator<Character> iterator() {
		return new CharIterator(mSequence);
	}
	
	//
	// Static methods
	//
	
	/**
	 * Convert a sequence to an indexed sequence where
	 * a = 0
	 * c = 1
	 * g = 2
	 * t = 3.
	 *
	 * @param seq the seq
	 * @return the byte[]
	 */
	public static byte[] seqToIndexSeq(final char[] seq) {
		byte[] ret = new byte[seq.length];

		for (int i = 0; i < seq.length; ++i) {
			ret[i] = baseToIndex(seq[i]);
		}

		return ret;
	}
	

	/**
	 * Converts a dna base to a letter for indexing.
	 * a = 0
	 * c = 1
	 * g = 2
	 * t = 3
	 *
	 * @param c the c
	 * @return the byte
	 */
	public static byte baseToIndex(char c) {
		switch (c) {
		case 'C':
		case 'c':
			return 1;
		case 'G':
		case 'g':
			return 2;
		case 'T':
		case 't':
			return 3;
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the consensus sequence from a list of sequences
	 * i.e the most abundant base at each position.
	 *
	 * @param sequences the sequences
	 * @return the consensus
	 */
	public static Sequence getConsensus(List<Sequence> sequences) {
		StringBuilder buffer = new StringBuilder();
		
		int l = sequences.get(0).length();
		
		for (int i = 0; i < l; ++i) {
			Map<Character, Integer> counts = new HashMap<Character, Integer>();
			
			for (Sequence sequence : sequences) {
				char c = sequence.charAt(i);
				
				counts.put(c, (counts.containsKey(c) ? counts.get(c) : 0) + 1);
			}
			
			char c = '-';
			int max = -1;
			
			for (Entry<Character, Integer> e : counts.entrySet()) {
				if (e.getValue() > max) {
					c = e.getKey();
					max = e.getValue();
				}
			}
			
			buffer.append(c);
		}
		
		return new Sequence(buffer.toString());
	}
	
	/**
	 * Write fasta.
	 *
	 * @param <X> the generic type
	 * @param sequences the sequences
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static <X extends Sequence> void writeFasta(List<X> sequences,
			Path file) throws IOException {
		
		LOG.debug("Writing {}...", file);
		
		BufferedWriter writer = FileUtils.newBufferedWriter(file);
		
		try {
			for (Sequence s : sequences) {
				writer.write(">");
				writer.write(s.mName);
				writer.newLine();
				writer.write(s.mSequence);
				writer.newLine();
			}
		} finally {
			writer.close();
		}
	}
	
	/**
	 * Parses the fasta.
	 *
	 * @param file the file
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final List<Sequence> parseFasta(Path file) throws IOException {
		List<Sequence> sequences = new ArrayList<Sequence>();
		
		//System.out.println(file.toString());

		BufferedReader reader = FileUtils.newBufferedReader(file);

		String line;

		Matcher fastaHeaderMatcher;

		String name = null;
		StringBuilder buffer = null;

		try {
			while ((line = reader.readLine()) != null) {
				if (Io.isEmptyLine(line)) {
					continue;
				}
				
				fastaHeaderMatcher = HEADER_PATTERN.matcher(line);

				if (fastaHeaderMatcher.find()) {
					if (buffer != null) {
						sequences.add(new Sequence(name, buffer.toString()));
					}
					
					name = fastaHeaderMatcher.group(1);

					buffer = new StringBuilder();
				} else {
					buffer.append(line);
				}
			}
		} finally {
			reader.close();
		}

		// Add the last sequence read
		sequences.add(new Sequence(name, buffer.toString()));
		
		return sequences;
	}

	/**
	 * Reverse complement a list of sequences.
	 *
	 * @param sequences the sequences
	 * @return the list
	 */
	public static List<Sequence> reverseComplement(List<Sequence> sequences) {
		List<Sequence> ret = new ArrayList<Sequence>(sequences.size());
		
		for (Sequence sequence : sequences) {
			ret.add(reverseComplement(sequence));
		}
		
		return ret;
	}

	/**
	 * To upper.
	 *
	 * @param seq the seq
	 * @param offset the offset
	 * @param length the length
	 * @return the sequence
	 */
	public static Sequence toUpper(Sequence seq, int offset, int length) {
		char[] bases = seq.mSequence.toCharArray();
		
		int c;
		for (int i = 0; i < length; ++i) {
			c = offset + i;
			
			bases[c] = Character.toUpperCase(bases[c]);
		}
		
		return new Sequence(seq.mName, new String(bases));
	}

	/**
	 * Return the percentage of GC in the sequence.
	 *
	 * @param sequence the sequence
	 * @return the double
	 */
	public static double gcContent(Sequence sequence) {
		return gcContent(sequence.toArray());
	}

	/**
	 * Return the percentage of GC in the sequence.
	 *
	 * @param sequence the sequence
	 * @return the double
	 */
	public static double gcContent(String sequence) {
		return gcContent(sequence.toCharArray());
	}

	/**
	 * Return the percentage of GC in the sequence.
	 *
	 * @param sequence the sequence
	 * @return the double
	 */
	public static double gcContent(char[] sequence) {
		double ret = 0;
		
		for (char c : sequence) {
			if (c == 'c' || c == 'C' || c == 'g' || c == 'G') {
				++ret;
			}
		}
		
		return ret / sequence.length;
	}

	/**
	 * Extract a random sequence of a given length from the genome.
	 *
	 * @param genome the genome
	 * @param mAssembly the m assembly
	 * @param mChrSizes the m chr sizes
	 * @param length the length
	 * @return the random sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static SequenceRegion getRandomSequence(String genome,
			GenomeAssembly mAssembly,
			ChromosomeSizes mChrSizes,
			int length) throws IOException {
		Random rand = new Random();
		
		Chromosome chr = Human.randChr();
		
		int start = rand.nextInt(mChrSizes.getSize(chr) - length);
		int end = start + length - 1;
		
		GenomicRegion region = new GenomicRegion(chr, start, end);
		
		return mAssembly.getSequence(genome, region);
	}
	
	/**
	 * To index.
	 *
	 * @param <X> the generic type
	 * @param seqs the seqs
	 * @return the byte[][]
	 */
	public static <X extends Sequence> byte[][] toIndex(List<X> seqs) {
		byte[][] ret = new byte[seqs.size()][];
		
		for (int i = 0; i < seqs.size(); ++i) {
			ret[i] = seqs.get(i).toIndex();
		}
		
		return ret;
	}
	
	public static String toFasta(String name, Sequence sequence) {
		return new StringBuilder(">").append(name).append(TextUtils.NEW_LINE).append(sequence).toString();
	}
	
	public static Sequence create(String dna) {
		return create(DEFAULT_NAME, dna);
	}
	
	/**
	 * Create a Sequence. If the DNA string does not appear to be valid
	 * DNA, null will be returned. Thus a legitimate sequence will only
	 * contain valid DNA.
	 * 
	 * @param name
	 * @param dna
	 * @return
	 */
	public static Sequence create(String name, String dna) {
		if (!DNA_REGEX.matcher(dna).matches()) {
			return null;
		}
		
		return new Sequence(name, dna); //.toUpperCase());
	}

}
