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
import java.util.Collections;
import java.util.List;

import org.jebtk.bioinformatics.DataSource;



// TODO: Auto-generated Javadoc
/**
 * Fast search of genome sequence files to get get actual genomic data.
 *
 * @author Antony Holmes Holmes
 */
public abstract class GenomeAssembly {
	
	/** The Constant HG18. */
	public static final String HG18 = "hg18";
	
	/**
	 * The constant HG19.
	 */
	public static final String HG19 = "hg19";
	
	/**
	 * The constant MM10.
	 */
	public static final String MM10 = "mm10";
	
	/**
	 * Process a sequence of the form chrN:X-Y.
	 *
	 * @param genome the genome
	 * @param position the position
	 * @param displayUpper the display upper
	 * @param repeatMaskType the repeat mask type
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome,
			String position,
			boolean displayUpper,
			RepeatMaskType repeatMaskType) throws IOException {
		return getSequence(genome,
				GenomicRegion.parse(position),
				displayUpper,
				repeatMaskType);
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param position the position
	 * @param repeatMaskType the repeat mask type
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome,
			String position,
			RepeatMaskType repeatMaskType) throws IOException {
		return getSequence(genome,
				GenomicRegion.parse(position),
				true,
				repeatMaskType);
	}

	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome,
			Chromosome chr, 
			int start, 
			int end) throws IOException {
		return getSequence(genome, chr, start, end, true, RepeatMaskType.UPPERCASE);
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param displayUpper the display upper
	 * @param repeatMaskType the repeat mask type
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome,
			Chromosome chr, 
			int start, 
			int end,
			boolean displayUpper,
			RepeatMaskType repeatMaskType) throws IOException {
		return getSequence(genome,
				new GenomicRegion(chr, start, end),
				displayUpper,
				repeatMaskType);
	}

	/**
	 * Default return sequence without repeats, uppercase.
	 *
	 * @param genome the genome
	 * @param region the region
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome, GenomicRegion region) throws IOException {
		return getSequence(genome,
				region, 
				true, 
				RepeatMaskType.UPPERCASE);
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param sequence the sequence
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome, String sequence) throws IOException {
		return getSequence(genome, sequence, true, RepeatMaskType.UPPERCASE);
	}
	
	/**
	 * Return upper case DNA sequence, except where there are repeats.
	 *
	 * @param genome the genome
	 * @param region the region
	 * @param displayUpper the display upper
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome, 
			GenomicRegion region,
			boolean displayUpper) throws IOException {
		return getSequence(genome, region, displayUpper, RepeatMaskType.UPPERCASE);
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param region the region
	 * @param repeatMaskType the repeat mask type
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public SequenceRegion getSequence(String genome,
			GenomicRegion region,
			RepeatMaskType repeatMaskType) throws IOException {
		return getSequence(genome, region, true, repeatMaskType);
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param genome the genome
	 * @param region the region
	 * @param displayUpper the display upper
	 * @param repeatMaskType the repeat mask type
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract SequenceRegion getSequence(String genome,
			GenomicRegion region,
			boolean displayUpper,
			RepeatMaskType repeatMaskType) throws IOException;

	/**
	 * Should return a list of the available genomes.
	 *
	 * @return the genomes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> getGenomes() throws IOException {
		return Collections.emptyList();
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public DataSource getDataSource() {
		return DataSource.LOCAL;
	}
}
