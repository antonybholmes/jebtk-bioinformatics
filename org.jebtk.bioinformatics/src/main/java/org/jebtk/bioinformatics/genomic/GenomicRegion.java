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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jebtk.core.io.Io;
import org.jebtk.core.text.Formatter;
import org.jebtk.core.text.Formatter.NumberFormatter;
import org.jebtk.core.text.Parser;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;



// TODO: Auto-generated Javadoc
/**
 * Describes a region of a genome.
 *
 * @author Antony Holmes Holmes
 */
public class GenomicRegion extends Region implements Comparable<GenomicRegion> {

	/** The Constant GENOMIC_REGEX. */
	public static final Pattern GENOMIC_REGEX =
			Pattern.compile("chr.+?:(?:\\d+(?:,\\d+)*)(?:-\\d+(?:,\\d+)*)?");

	/**
	 * The member chr.
	 */
	protected Chromosome mChr;

	/**
	 * The member strand.
	 */
	protected Strand mStrand = Strand.NONE;

	/**
	 * The member location.
	 *
	 * @param region the region
	 */
	//private String mLocation;

	/**
	 * Instantiates a new genomic region.
	 *
	 * @param region the region
	 */
	public GenomicRegion(GenomicRegion region) {
		this(region.mChr, 
				region.mStart, 
				region.mEnd);
	}


	/**
	 * Instantiates a new genomic region.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 */
	public GenomicRegion(Chromosome chr, 
			int start, 
			int end) {
		this(chr, start, end, Strand.NONE);
	}

	/**
	 * Instantiates a new genomic region.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 */
	public GenomicRegion(Chromosome chr, 
			int start, 
			int end,
			Strand strand) {
		super(Math.max(1, start), Math.max(1, end));

		mChr = chr;

		mStrand = strand;
	}

	/**
	 * Gets the strand.
	 *
	 * @return the strand
	 */
	public Strand getStrand() {
		return mStrand;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return toLocation(mChr, mStart, mEnd);
	}
	
	/**
	 * Returns the numerical range of the location in form "<start>-<end>"
	 * @return
	 */
	public String getRange() {
		return toRange(mStart, mEnd);
	}

	/**
	 * Gets the formatted location.
	 *
	 * @return the formatted location
	 */
	public String getFormattedLocation() {
		return formattedLocation(mChr, mStart, mEnd);
	}

	/**
	 * Gets the chr.
	 *
	 * @return the chr
	 */
	public Chromosome getChr() {
		return mChr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getLocation();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GenomicRegion r) {
		if (mChr.equals(r.mChr)) {
			if (mStart > r.mStart) {
				return 1;
			} else if (mStart < r.mStart) {
				return -1;
			} else {
				// If the starts are the same, rank by end coordinate
				if (mEnd > r.mEnd) {
					return 1;
				} else if (mEnd < r.mEnd) {
					return -1;
				} else {
					// both start and end equal each other
					return 0;
				}
			}
		} else {
			return mChr.compareTo(r.mChr);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof GenomicRegion) {
			return compareTo((GenomicRegion)o) == 0;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.genome.Region#formattedTxt(java.lang.Appendable)
	 */
	@Override
	public void formattedTxt(Appendable buffer) throws IOException {
		buffer.append(mChr.toString());
		buffer.append(TextUtils.TAB_DELIMITER);
		buffer.append(Integer.toString(mStart));
		buffer.append(TextUtils.TAB_DELIMITER);
		buffer.append(Integer.toString(mEnd));
		buffer.append(TextUtils.NEW_LINE);
	}

	/**
	 * Parses the.
	 *
	 * @param locations the locations
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	public static List<GenomicRegion> parse(List<String> locations) throws ParseException {
		List<GenomicRegion> ret = new ArrayList<GenomicRegion>();

		for (String location : locations) {
			GenomicRegion region = parse(location);

			if (region != null) {
				ret.add(region);
			}
		}

		return ret;
	}

	/**
	 * Parse a position annotation in the form (chr)n:x-(y) {.
	 *
	 * @param location the location
	 * @return the genomic region
	 */
	public static GenomicRegion parse(String location) {
		if (Io.isEmptyLine(location)) {
			return null;
		}

		if (location.contains(TextUtils.NA)) {
			return null;
		}

		location = location.trim().replaceAll(",", "");
		
		if (isGenomicRegion(location)) {

			List<String> tokens = TextUtils.fastSplit(location, 
					TextUtils.COLON_DELIMITER_CHAR);

			// convert first part to chromosome (replacing x,y and m) {
			Chromosome chromosome = 
					ChromosomeService.getInstance().parse(tokens.get(0));

			if (chromosome == null) {
				return null;
			}

			int start;
			int end;

			if (tokens.get(1).indexOf("-") != -1) {
				tokens = Splitter.on('-').text(tokens.get(1)); //)    .(tokens.get(1), '-');

				start = Integer.parseInt(tokens.get(0));
				end = Integer.parseInt(tokens.get(1));
			} else {
				// single position

				start = Integer.parseInt(tokens.get(1));
				end = start;
			}

			return new GenomicRegion(chromosome, start, end);
		} else if (isRegion(location)) {
			int start;
			int end;

			if (location.indexOf("-") != -1) {
				List<String> tokens = Splitter.on('-').text(location); //)    .(tokens.get(1), '-');

				start = Integer.parseInt(tokens.get(0));
				end = Integer.parseInt(tokens.get(1));
			} else {
				// single position

				start = Integer.parseInt(location);
				end = start;
			}

			return GenomicRegion.create(start, end);
		} else {
			return null;
		}
	}

	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the genomic region
	 * @throws ParseException the parse exception
	 */
	public static GenomicRegion parse(String chr, String start, String end) throws ParseException {
		return new GenomicRegion(ChromosomeService.getInstance().parse(chr), 
				Parser.toInt(start), 
				Parser.toInt(end));
	}

	/**
	 * Parses the.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @return the genomic region
	 * @throws ParseException the parse exception
	 */
	public static GenomicRegion parse(String chr, String start) throws ParseException {
		return parse(chr, start, start);
	}

	/**
	 * Return the overlapping region between two regions or null if they
	 * are not overlapping.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the genomic region
	 */
	public static GenomicRegion overlap(GenomicRegion region1, 
			GenomicRegion region2) {
		if (!region1.mChr.equals(region2.mChr)) {
			return null;
		}

		return overlap(region1.mChr, 
				region1.mStart, 
				region1.mEnd, 
				region2.mStart, 
				region2.mEnd);
	}

	/**
	 * Overlap.
	 *
	 * @param chr the chr
	 * @param start1 the start 1
	 * @param end1 the end 1
	 * @param region2 the region 2
	 * @return the genomic region
	 */
	public static GenomicRegion overlap(Chromosome chr, 
			int start1,
			int end1,
			GenomicRegion region2) {
		if (!chr.equals(region2.mChr)) {
			return null;
		}

		return overlap(chr, 
				start1, 
				end1, 
				region2.mStart, 
				region2.mEnd);
	}

	/**
	 * Overlap.
	 *
	 * @param chr the chr
	 * @param start1 the start 1
	 * @param end1 the end 1
	 * @param start2 the start 2
	 * @param end2 the end 2
	 * @return the genomic region
	 */
	public static GenomicRegion overlap(Chromosome chr, 
			int start1,
			int end1,
			int start2,
			int end2) {
		if (end1 < start2 || end2 < start1) {
			return null;
		}

		int start = Math.max(start1, start2);
		int end = Math.min(end1, end2);

		return new GenomicRegion(chr, start, end);
	}

	/**
	 * Overlap type.
	 *
	 * @param region1 the region 1
	 * @param region2 the region 2
	 * @return the overlap type
	 */
	public static OverlapType overlapType(GenomicRegion region1, 
			GenomicRegion region2) {
		if (!region1.mChr.equals(region2.mChr)) {
			return OverlapType.NONE;
		}

		return overlapType(region1.mChr, 
				region1.mStart, 
				region1.mEnd, 
				region2.mStart, 
				region2.mEnd);
	}


	/**
	 * Overlap type.
	 *
	 * @param chr the chr
	 * @param start1 the start 1
	 * @param end1 the end 1
	 * @param start2 the start 2
	 * @param end2 the end 2
	 * @return the overlap type
	 */
	public static OverlapType overlapType(Chromosome chr, 
			int start1,
			int end1,
			int start2,
			int end2) {
		if (end1 < start2 || end2 < start1) {
			return OverlapType.NONE;
		}

		if (start1 >= start2 && end1 <= end2) {
			return OverlapType.WITHIN;
		} else if (start1 < start2 && end1 > end2) {
			return OverlapType.COMPLETE;
		} else {
			return OverlapType.PARTIAL;
		}
	}

	/**
	 * Overlaps.
	 *
	 * @param chr the chr
	 * @param start1 the start 1
	 * @param end1 the end 1
	 * @param start2 the start 2
	 * @param end2 the end 2
	 * @return true, if successful
	 */
	public static boolean overlaps(Chromosome chr,
			int start1,
			int end1,
			int start2,
			int end2) {
		return (start1 >= start2 && start1 < end2) || 
				(end1 >= start2 && end1 < end2) ||
				(start2 >= start1 && start2 < end1) ||
				(end2 >= start1 && end2 < end1);
	}

	/**
	 * Use only if start1 is before start2.
	 *
	 * @param start1 the start 1
	 * @param end1 the end 1
	 * @param start2 the start 2
	 * @param end2 the end 2
	 * @return true, if successful
	 */
	public static boolean overlapOrdereds(int start1,
			int end1,
			int start2,
			int end2) {
		return end1 >= start2;
	}

	/**
	 * Returns true if region2 overlaps with region1.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return true, if successful
	 */
	public static boolean overlaps(GenomicRegion region1, GenomicRegion region2) {
		return overlap(region1, region2) != null;
	}

	/**
	 * Returns the mid point of a region.
	 *
	 * @param region the region
	 * @return the int
	 */
	public static int mid(GenomicRegion region) {
		return mid(region.mStart, region.mEnd); //new GenomicRegion(region.mChr, mid, mid);
	}

	/**
	 * Mid.
	 *
	 * @param start the start
	 * @param end the end
	 * @return the int
	 */
	public static int mid(int start, int end) {
		return (start + end) / 2; //new GenomicRegion(region.mChr, mid, mid);
	}

	/**
	 * Return the mid point of a region i.e. the start and end will be
	 * the same mid point of the genomic region.
	 *
	 * @param region the region
	 * @return the genomic region
	 */
	public static GenomicRegion midRegion(GenomicRegion region) {
		if (region == null) {
			return null;
		}

		int mid = mid(region);

		return new GenomicRegion(region.mChr, mid, mid);
	}

	/**
	 * Return the distance between the midpoint of region1 and region2.
	 * If region1 is greater than region2, a positive value will be returned.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the int
	 */
	public static int midDist(GenomicRegion region1, GenomicRegion region2) {
		return mid(region1) - mid(region2);
	}

	/**
	 * Mid abs dist.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the int
	 */
	public static int midAbsDist(GenomicRegion region1, GenomicRegion region2) {
		return Math.abs(midDist(region1, region2));
	}

	/**
	 * Returns true if the string is of the form chrX:A-B where X is
	 * either a number or letter and A and B are numbers.
	 *
	 * @param text the text
	 * @return true, if is region
	 */
	public static boolean isGenomicRegion(String text) {
		if (text == null) {
			return false;
		}

		return GENOMIC_REGEX.matcher(text).find(); //value.matches("chr.+?:\\d+(-\\d+)?.*");
	}

	/**
	 * Returns true if the value appears to be a chromosome.
	 *
	 * @param value the value
	 * @return true, if is chr
	 */
	public static boolean isChr(String value) {
		if (value == null) {
			return false;
		}

		return value.startsWith("chr");
	}

	/**
	 * Returns true if a point is within a region.
	 *
	 * @param start the start
	 * @param region the region
	 * @return true, if successful
	 */
	public static boolean within(int start, GenomicRegion region) {
		return start >= region.getStart() && start <= region.getEnd();
	}

	/**
	 * Extend.
	 *
	 * @param regions the regions
	 * @param startOffset the start offset
	 * @param endOffset the end offset
	 * @return the list
	 */
	public static List<GenomicRegion> extend(List<GenomicRegion> regions,
			int startOffset, int endOffset) {
		List<GenomicRegion> ret = new ArrayList<GenomicRegion>();

		for (GenomicRegion region : regions) {
			ret.add(extend(region, startOffset, endOffset));
		}

		return ret;
	}

	/**
	 * Extend.
	 *
	 * @param region the region
	 * @param startOffset the start offset
	 * @param endOffset the end offset
	 * @return the genomic region
	 */
	public static GenomicRegion extend(GenomicRegion region, 
			int startOffset, 
			int endOffset) {
		return new GenomicRegion(region.mChr, 
				region.mStart - Math.abs(startOffset), 
				region.mEnd + Math.abs(endOffset));
	}

	/**
	 * Extend around a position.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param startOffset the start offset
	 * @param endOffset the end offset
	 * @return the genomic region
	 */
	public static GenomicRegion extend(Chromosome chr,
			int start,
			int startOffset, 
			int endOffset) {
		return new GenomicRegion(chr, 
				start - Math.abs(startOffset), 
				start + Math.abs(endOffset));
	}

	/**
	 * Return the sequences for a set of regions.
	 *
	 * @param genome the genome
	 * @param regions the regions
	 * @param genomeAssembly the genome assembly
	 * @return the sequences
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static List<SequenceRegion> getSequences(String genome,
			List<GenomicRegion> regions,
			GenomeAssembly genomeAssembly) throws IOException, ParseException {
		return getSequences(genome, regions, true, RepeatMaskType.UPPERCASE, genomeAssembly);
	}

	/**
	 * Gets the sequences.
	 *
	 * @param genome the genome
	 * @param regions the regions
	 * @param displayUpper the display upper
	 * @param repeatMaskType the repeat mask type
	 * @param genomeAssembly the genome assembly
	 * @return the sequences
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static List<SequenceRegion> getSequences(String genome,
			Collection<GenomicRegion> regions,
			boolean displayUpper,
			RepeatMaskType repeatMaskType,
			GenomeAssembly genomeAssembly) throws IOException, ParseException {
		List<SequenceRegion> sequences = new ArrayList<SequenceRegion>();

		for (GenomicRegion region : regions) {
			System.err.println("Getting sequence for region " + region.getLocation() + "...");

			sequences.add(genomeAssembly.getSequence(genome, region, displayUpper, repeatMaskType));
		}

		return sequences;
	}

	/**
	 * Sort regions by start and group by chromosome.
	 *
	 * @param <T> the generic type
	 * @param regions the regions
	 * @return the map
	 */
	public static <T extends GenomicRegion> Map<Chromosome, List<T>> sortByStart(Iterable<T> regions) {
		Map<Chromosome, Map<Integer, T>> map =
				new HashMap<Chromosome, Map<Integer, T>>();

		for (T region : regions) {
			if (!map.containsKey(region.getChr())) {
				map.put(region.getChr(), new TreeMap<Integer, T>());
			}

			map.get(region.getChr()).put(region.getStart(), region);
		}

		Map<Chromosome, List<T>> ret =
				new HashMap<Chromosome, List<T>>();

		for (Chromosome chr : map.keySet()) {
			ret.put(chr, new ArrayList<T>());

			for (int start : map.get(chr).keySet()) {
				ret.get(chr).add(map.get(chr).get(start));
			}
		}

		return ret;
	}

	/**
	 * Sorts the regions on the assumption they are on the same chromosome.
	 * Thus it returns a list with the regions sorted by start.
	 *
	 * @param <T> the generic type
	 * @param regions the regions
	 * @return the list
	 */
	public static <T extends GenomicRegion> List<T> sortByStartSingleChr(List<T> regions) {
		Map<Integer, T> map = new TreeMap<Integer, T>();

		for (T region : regions) {
			map.put(region.getStart(), region);
		}

		List<T> ret = new ArrayList<T>();

		for (int start : map.keySet()) {
			ret.add(map.get(start));
		}

		return ret;
	}

	/**
	 * Sort single chr.
	 *
	 * @param <T> the generic type
	 * @param regions the regions
	 * @return the list
	 */
	public static <T extends GenomicRegion> List<T> sortSingleChr(List<T> regions) {
		Map<Integer, T> map = new TreeMap<Integer, T>();

		for (T region : regions) {
			map.put(region.getStart(), region);
			map.put(region.getEnd(), region);
		}

		List<T> ret = new ArrayList<T>();

		for (int start : map.keySet()) {
			ret.add(map.get(start));
		}

		return ret;
	}

	/**
	 * Shift a region by a number of bases.
	 *
	 * @param region the region
	 * @param shift the shift
	 * @param sizes the sizes
	 * @return the genomic region
	 */
	public static GenomicRegion shift(GenomicRegion region, 
			int shift, 
			ChromosomeSizes sizes) {

		return shift(region, shift, 0, sizes);

	}
	
	public static GenomicRegion shift(GenomicRegion region, 
			int shift,
			int minSep,
			ChromosomeSizes sizes) {

		int size = sizes.getSize(region.mChr);
		
		// bound the positions so they dont exceed the chromosome bounds
		int start = Math.min(size, Math.max(1, region.mStart + shift));
		int end = Math.min(size, Math.max(start + minSep, region.mEnd + shift));

		return new GenomicRegion(region.mChr, start, end);

	}
	
	public static GenomicRegion add(GenomicRegion region, 
			int shift) {

		return add(region, shift, 0);
	}
	
	public static GenomicRegion add(GenomicRegion region, 
			int shift,
			int minSep) {

		// bound the positions so they dont exceed the chromosome bounds
		int start = Math.max(1, region.mStart + shift);
		int end = Math.max(start + minSep, region.mEnd + shift);

		return new GenomicRegion(region.mChr, start, end);

	}

	/**
	 * Returns true if region1 is within region2 (does not extend outside
	 * boundary).
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return true, if successful
	 */
	public static boolean within(GenomicRegion region1, GenomicRegion region2) {
		if (region1 == null || region2 == null) {
			return false;
		}

		return region1.getChr().equals(region2.getChr()) &&
				region1.getStart() >= region2.getStart() &&
				region1.getEnd() <= region2.getEnd();
	}

	/**
	 * Returns the closest absolute distance between two regions.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the int
	 */
	public static int minAbsDist(GenomicRegion region1, GenomicRegion region2) {
		return Math.min(Math.abs(minDist(region1, region2)), Math.abs(minDist(region2, region1)));
	}

	/**
	 * Min dist.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the int
	 */
	public static int minDist(GenomicRegion region1, GenomicRegion region2) {
		return region2.getStart() - region1.getEnd();
	}

	/**
	 * Returns the width of region1 as a percentage of region2.
	 *
	 * @param region1 the region1
	 * @param region2 the region2
	 * @return the double
	 */
	public static double p(GenomicRegion region1, GenomicRegion region2) {
		return (double)region1.getLength() / (double)region2.getLength();
	}

	/**
	 * To location.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the string
	 */
	public static String toLocation(Chromosome chr, int start, int end) {
		return chr + ":" + toRange(start, end);
	}
	
	public static String toRange(int start, int end) {
		return start + "-" + end;
	}

	/**
	 * Formatted location.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the string
	 */
	public static String formattedLocation(Chromosome chr, int start, int end) {
		NumberFormatter f = Formatter.number();

		return chr + ":" + f.format(start) + "-" + f.format(end);
	}
	
	/**
	 * Creates a special kind of genomic region with no chromosome. Should 
	 * be used in situations where the chromosome is irrelevent, but the
	 * method call expects it.
	 *
	 * @param start the start
	 * @param end the end
	 * @return 		A genomic region with an invalid chromosome.
	 */
	public static GenomicRegion create(int start, int end) {
		return create(Chromosome.NO_CHR, start, end);
	}

	/**
	 * Creates the.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the genomic region
	 */
	public static GenomicRegion create(String chr, int start, int end) {
		return create(ChromosomeService.getInstance().parse(chr), start, end);
	}

	/**
	 * Creates the.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the genomic region
	 */
	public static GenomicRegion create(Chromosome chr, int start, int end) {
		return create(chr, start, end, Strand.NONE);
	}

	/**
	 * Creates the.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 * @return the genomic region
	 */
	public static GenomicRegion create(Chromosome chr, int start, int end, Strand strand) {
		return new GenomicRegion(chr, start, end, strand);
	}

	/**
	 * Center regions.
	 *
	 * @param regions the regions
	 * @return the list
	 */
	public static List<GenomicRegion> center(List<GenomicRegion> regions) {
		List<GenomicRegion> ret = 
				new ArrayList<GenomicRegion>(regions.size());

		for (GenomicRegion region : regions) {
			ret.add(center(region));
		}

		return ret;
	}

	/**
	 * Center.
	 *
	 * @param region the region
	 * @return the genomic region
	 */
	public static GenomicRegion center(GenomicRegion region) {
		int mid = mid(region);

		return new GenomicRegion(region.mChr, mid, mid);
	}



}
