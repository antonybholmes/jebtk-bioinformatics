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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.IterTreeMap;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * The class Gene.
 */
public class Gene extends GenomicRegion implements Iterable<Exon> {
	
	/** The Constant SYMBOL_TYPE. */
	public static final String SYMBOL_TYPE = "symbol";
	
	/** The Constant REFSEQ_TYPE. */
	public static final String REFSEQ_TYPE = "refseq";
	
	/** The Constant ENTREZ_TYPE. */
	public static final String ENTREZ_TYPE = "entrez";
	
	/** The m id map. */
	private IterMap<String, String> mIdMap = new IterTreeMap<String, String>();
	
	/** The m exons. */
	private List<Exon> mExons = new ArrayList<Exon>();
	
	/** The m utr 5 p. */
	private List<Exon> mUtr5p = new ArrayList<Exon>();

	/** The m text. */
	private String mText;

	
	/**
	 * Instantiates a new gene.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 */
	public Gene(Chromosome chr, 
			int start, 
			int end,
			Strand strand) {
		super(chr, start, end, strand);
	}
	
	/**
	 * Assign a gene id to the gene (e.g. a symbol or RefSeq Id).
	 *
	 * @param type the type
	 * @param name the name
	 * @return 		The instance of the gene
	 */
	public Gene setId(String type, String name) {
		if (type != null && 
				name != null && 
				!type.equals(TextUtils.NA) && 
				!name.equals(TextUtils.NA)) {
			mIdMap.put(type, name);
			
			setText();
		}
		
		return this;
	}
	
	/**
	 * Set a text description of the gene.
	 */
	private void setText() {
		StringBuilder buffer = new StringBuilder(getSymbol());
		
		for (String id : mIdMap) {
			buffer.append(id).append("=").append(getId(id)).append(" ");
		}
		
		buffer.append("[").append(super.toString()).append("]");
		
		mText = buffer.toString();
	}


	/**
	 * Gets the ids.
	 *
	 * @return the ids
	 */
	public Iterable<String> getIds() {
		return mIdMap.keySet();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Exon> iterator() {
		return mExons.iterator();
	}

	/**
	 * Adds the exon.
	 *
	 * @param exon the exon
	 */
	public void addExon(Exon exon) {
		mExons.add(exon);
	}
	
	/**
	 * Gets the ref seq.
	 *
	 * @return the ref seq
	 */
	public String getRefSeq() {
		return getId(REFSEQ_TYPE);
	}
	
	/**
	 * Gets the entrez.
	 *
	 * @return the entrez
	 */
	public String getEntrez() {
		return getId(ENTREZ_TYPE);
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	public String getSymbol() {
		return getId(SYMBOL_TYPE);
	}
	
	/**
	 * Sets the symbol.
	 *
	 * @param name the name
	 * @return the gene
	 */
	public Gene setSymbol(String name) {
		return setId(SYMBOL_TYPE, name);
	}
	
	/**
	 * Sets the refseq.
	 *
	 * @param name the name
	 * @return the gene
	 */
	public Gene setRefseq(String name) {
		return setId(REFSEQ_TYPE, name);
	}
	
	/**
	 * Sets the entrez.
	 *
	 * @param name the name
	 * @return the gene
	 */
	public Gene setEntrez(String name) {
		return setId(ENTREZ_TYPE, name);
	}
	
	/**
	 * Gets the tss.
	 *
	 * @return the tss
	 */
	public GenomicRegion getTss() {
		if (mStrand == Strand.SENSE) {
			return new GenomicRegion(mChr, mStart, mStart);
		} else {
			return new GenomicRegion(mChr, mEnd, mEnd);
		}
	}

	/**
	 * Return a gene id.
	 *
	 * @param type the type
	 * @return the id
	 */
	public String getId(String type) {
		return mIdMap.get(type);
	}

	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Gene) {
			return mText.equals(((Gene)o).mText);
		} else {
			return super.equals(o);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#toString()
	 */
	@Override
	public String toString() {
		return mText;
	}
	
	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.GenomicRegion#hashCode()
	 */
	@Override
	public int hashCode() {
		return mText.hashCode();
	}
	

	
	/**
	 * Tss region.
	 *
	 * @param gene the gene
	 * @return the genomic region
	 */
	public static GenomicRegion tssRegion(Gene gene) {
		if (gene == null) {
			return null;
		}
		
		if (gene.mStrand == Strand.SENSE) {
			return new GenomicRegion(gene.mChr, gene.mStart, gene.mStart);
		} else {
			return new GenomicRegion(gene.mChr, gene.mEnd, gene.mEnd);
		}
	}

	/**
	 * Tss dist.
	 *
	 * @param gene the gene
	 * @param region the region
	 * @return the int
	 */
	public static int tssDist(Gene gene, GenomicRegion region) {
		GenomicRegion tssRegion = tssRegion(gene);
		
		if (gene.mStrand == Strand.SENSE) {
			return GenomicRegion.midDist(region, tssRegion);
		} else {
			return GenomicRegion.midDist(tssRegion, region);
		}
	}
	
	/**
	 * Tss dist5p.
	 *
	 * @param gene the gene
	 * @param region the region
	 * @return the int
	 */
	public static int tssDist5p(Gene gene, GenomicRegion region) {
		GenomicRegion tssRegion = tssRegion(gene);
		
		return GenomicRegion.midDist(region, tssRegion);
	}

	/**
	 * Create a new gene.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the gene
	 */
	public static Gene create(Chromosome chr, 
			int start, 
			int end) {
		return create(chr, start, end, Strand.SENSE);
	}
	
	/**
	 * Create a new gene.
	 * 
	 * @param chr		The chromosome.
	 * @param start		The start.
	 * @param end		The end.
	 * @param strand	The strand.
	 * @return			The new gene.
	 */
	public static Gene create(Chromosome chr, 
			int start, 
			int end,
			Strand strand) {
		return new Gene(chr, start, end, strand);
	}
}
