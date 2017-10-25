/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.bioinformatics.ext.samtools;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;
import org.jebtk.core.text.Parser;

import htsjdk.samtools.AbstractBAMFileIndex;
import htsjdk.samtools.BAMIndexMetaData;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SamUtils.
 */
public class SamUtils {
	
	/** The Constant SAM_REVERSE_STRAND_MASK. */
	private static final int SAM_REVERSE_STRAND_MASK = 16;
	
	/** The Constant SAM_UNMAPPED_MASK. */
	private static final int SAM_UNMAPPED_MASK = 4;

	/**
	 * Strand.
	 *
	 * @param samFlags the sam flags
	 * @return the strand
	 */
	public static Strand strand(int samFlags) {
		if (negStrand(samFlags)) {
			return Strand.ANTISENSE;
		} else {
			return Strand.SENSE;
		}
	}

	/**
	 * Pos strand.
	 *
	 * @param samFlags the sam flags
	 * @return true, if successful
	 */
	public static boolean posStrand(int samFlags) {
		return !negStrand(samFlags);
	}

	/**
	 * Returns true of the .
	 *
	 * @param samFlags the sam flags
	 * @return true, if successful
	 */
	public static boolean negStrand(int samFlags) {
		return (samFlags & SAM_REVERSE_STRAND_MASK) == SAM_REVERSE_STRAND_MASK;
	}

	/**
	 * Returns true if the flags mapped bit is set.
	 *
	 * @param samFlags the sam flags
	 * @return true, if is mapped
	 */
	public static boolean isMapped(int samFlags) {
		return !isUnmapped(samFlags);
	}

	/**
	 * Checks if is unmapped.
	 *
	 * @param samFlags the sam flags
	 * @return true, if is unmapped
	 */
	public static boolean isUnmapped(int samFlags) {
		return (samFlags & SAM_UNMAPPED_MASK) == SAM_UNMAPPED_MASK;
	}

	/**
	 * Given a starting location and a CIGAR string, creates all possible
	 * locations from it.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param cigar the cigar
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	public static List<GenomicRegion> parseCigar(Chromosome chr, 
			int start, 
			final String cigar) throws ParseException {

		StringBuilder buffer = new StringBuilder();

		int end;

		List<GenomicRegion> locations = new ArrayList<GenomicRegion>();

		for (int i = 0; i < cigar.length(); ++i) {
			switch(cigar.charAt(i)) {
			case 'M':
				// We have a matching portion so we know the end
				// is the size of the cigar integer plus the
				// current start
				end = start + Parser.toInt(buffer.toString()) - 1;

				locations.add(GenomicRegion.create(chr, start, end));

				// The new start is advanced one beyond the end
				start = end + 1;
				buffer.setLength(0);
				break;
			case 'I':
				// A gap requires the start to be shifted.
				start -= Parser.toInt(buffer.toString()) - 1;
				buffer.setLength(0);
				break;
			case 'D':
			case 'N':
				// A gap requires the start to be shifted.
				start += Parser.toInt(buffer.toString()) - 1;
				buffer.setLength(0);
				break;
			default:
				buffer.append(cigar.charAt(i));
				break;
			}
		}

		return locations;
	}

	/**
	 * Counts the number of reads in an indexed BAM file using the index
	 * statistics.
	 *
	 * @param file the file
	 * @return the total reads from indexed bam
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int getTotalReadsFromIndexedBam(Path file) throws IOException {
		SamReader inputSam = 
				SamReaderFactory.makeDefault().open(file.toFile());

		int count = 0;

		try {
			AbstractBAMFileIndex index = 
					(AbstractBAMFileIndex)inputSam.indexing().getIndex();

			for (int i = 0; i < index.getNumberOfReferences(); ++i) {
				BAMIndexMetaData meta = index.getMetaData(i);
				count += meta.getAlignedRecordCount();
			}
		} finally {
			inputSam.close();
		}

		return count;
	}

	/**
	 * Returns the length of the reads by examing the first record in the
	 * BAM file. The assumption is that reads are of equal length.
	 *
	 * @param file the file
	 * @return the read length from bam
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int getReadLengthFromBam(Path file) throws IOException {
		int ret = -1;
		
		SAMRecordIterator iter = null;

		SamReader inputSam = 
				SamReaderFactory.makeDefault().open(file.toFile());

		try {
			iter = inputSam.iterator();
			
			if (iter != null) {
				SAMRecord record = iter.next();
				
				ret = record.getReadLength();
			}
		} finally {
			inputSam.close();
		}
		
		return ret;
	}
	
	public static GenomicRegion getRegion(SAMRecord r) {
		return GenomicRegion.create(r.getReferenceName(), r.getStart(), r.getEnd());
	}

	/**
	 * Returns SAM string removing newlines.
	 * 
	 * @param record
	 * @return
	 */
	public static String getSam(SAMRecord record) {
		return record.getSAMString().substring(0, record.getSAMString().length() - 1);
	}
}