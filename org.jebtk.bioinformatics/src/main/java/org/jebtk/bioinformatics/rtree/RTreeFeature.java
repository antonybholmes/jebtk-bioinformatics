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
package org.jebtk.bioinformatics.rtree;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;

// TODO: Auto-generated Javadoc
/**
 * The Class RTreeFeature.
 */
public class RTreeFeature extends GenomicRegion {
	
	/** The m gene. */
	protected String mGene;
	
	/** The m transcript. */
	protected String mTranscript;
	
	/** The m type. */
	protected String mType;
	
	/** The m parent. */
	private RTreeFeature mParent;
	
	/** The m hash. */
	private int mHash;
	
	/** The m location. */
	private String mLocation;

	/**
	 * Instantiates a new r tree feature.
	 *
	 * @param parent the parent
	 * @param gene the gene
	 * @param transcript the transcript
	 * @param type the type
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 */
	public RTreeFeature(RTreeFeature parent,
			String gene, 
			String transcript, 
			String type, 
			Chromosome chr, 
			int start, 
			int end,
			Strand strand) {
		super(chr, start, end, strand);
		
		mParent = parent;
		mGene = gene;
		mTranscript = transcript;
		mType = type;
		mChr = chr;
		mStart = start;
		mEnd = end;
		mStrand = strand;
		mLocation = chr + ":" + start + "-" + end;
		
		mHash = (gene + start + end).hashCode();
	}
	
	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public RTreeFeature getParent() {
		return mParent;
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#getLocation()
	 */
	public String getLocation() {
		return mLocation;
	}

	/**
	 * Gets the gene.
	 *
	 * @return the gene
	 */
	public String getGene() {
		return mGene;
	}
	
	/**
	 * Gets the transcript.
	 *
	 * @return the transcript
	 */
	public String getTranscript() {
		return mTranscript;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return mType;
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#compareTo(org.jebtk.bioinformatics.genome.GenomicRegion)
	 */
	@Override
	public int compareTo(GenomicRegion f) {
		return mGene.compareTo(((RTreeFeature)f).mGene);
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return mHash == o.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#hashCode()
	 */
	@Override
	public int hashCode() {
		return mHash;
	}
	
	/**
	 * Checks if is overlapping.
	 *
	 * @param f1 the f 1
	 * @param f2 the f 2
	 * @return true, if is overlapping
	 */
	public static boolean isOverlapping(final RTreeFeature f1, 
			final RTreeFeature f2) {
		return f1.getChr().equals(f2.getChr()) && 
				(f1.getStart() >= f2.getStart() && f1.getStart() <= f2.getEnd()) ||
				(f1.getEnd() >= f2.getStart() && f1.getEnd() <= f2.getEnd());
	}
}
