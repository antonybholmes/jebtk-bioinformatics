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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * The class Gene.
 */
public class Gene implements Comparable<Gene>, Iterable<GenomicRegion> {

	/** The Constant SYMBOL_TYPE. */
	public static final String SYMBOL_TYPE = "symbol";

	/** The Constant REFSEQ_TYPE. */
	public static final String REFSEQ_TYPE = "refseq";

	/** The Constant ENTREZ_TYPE. */
	public static final String ENTREZ_TYPE = "entrez";

	public static final String TRANSCRIPT_ID_TYPE = "transcript_id";

	/** The m id map. */
	private IterMap<String, String> mIdMap = 
			DefaultTreeMap.create(TextUtils.NA); //new IterTreeMap<String, String>();

	private Set<String> mTags = new TreeSet<String>();

	/** The m exons. */
	private List<GenomicRegion> mExons = new ArrayList<GenomicRegion>();
	
	private List<GenomicRegion> m5pUtrs = new ArrayList<GenomicRegion>();
	
	private List<GenomicRegion> m3pUtrs = new ArrayList<GenomicRegion>();

	/** The m utr 5 p. */
	//private List<Exon> mUtr5p = new ArrayList<Exon>();

	/** The m text. */
	private String mText;

	protected final GenomicRegion mRegion;

	private Gene mParent = null;
	
	private GeneType mType = GeneType.TRANSCRIPT;


	public Gene(GenomicRegion region) {
		mRegion = region;
	}
	
	public Gene(GeneType type, GenomicRegion region) {
		this(region);
		
		mType = type;
	}

	public Gene(String name, GenomicRegion region) {
		this(region);

		setSymbol(name);
	}
	
	public GeneType getType() {
		return mType;
	}

	public GenomicRegion getRegion() {
		return mRegion;
	}

	public void setParent(Gene gene) {
		mParent = gene;
	}
	
	public Gene getParent() {
		return mParent;
	}
	
	@Override
	public int compareTo(Gene g) {
		for (String id : mIdMap.keySet()) {
			// Find the first point where they differ and return that

			if (g.mIdMap.containsKey(id)) {
				String id2 = g.mIdMap.get(id);

				if (!id.equals(id2)) {
					return id.compareTo(id2);
				}
			}
		}

		if (mTags.size() > g.mTags.size()) {
			return 1;
		} else if (mTags.size() < g.mTags.size()) {
			return -1;
		} else {
			// Do nothing
		}

		// Compare exons
		return mRegion.compareTo(g.mRegion);
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
		StringBuilder buffer = new StringBuilder();

		for (String id : mIdMap) {
			buffer.append(id).append("=").append(getId(id)).append(", ");
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
	public Iterator<GenomicRegion> iterator() {
		return mExons.iterator();
	}

	/**
	 * Adds the exon.
	 *
	 * @param exon the exon
	 */
	public void addExon(GenomicRegion exon) {
		mExons.add(exon);
	}
	
	public void add5pUtr(GenomicRegion exon) {
		m5pUtrs.add(exon);
	}
	
	public void add3pUtr(GenomicRegion exon) {
		m3pUtrs.add(exon);
	}
	
	public Iterable<GenomicRegion> get3pUtrs() {
		return m3pUtrs;
	}
	
	public Iterable<GenomicRegion> get5pUtrs() {
		return m5pUtrs;
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

	public String getTranscriptId() {
		return getId(TRANSCRIPT_ID_TYPE);
	}

	public Gene setTranscriptId(String name) {
		return setId(TRANSCRIPT_ID_TYPE, name);
	}

	/**
	 * Gets the tss.
	 *
	 * @return the tss
	 */
	public GenomicRegion getTss() {
		return tssRegion(this);
	}

	/**
	 * Return a gene id. If the id does not exist 'n/a' is returned.
	 *
	 * @param type the type
	 * @return the id
	 */
	public String getId(String type) {
		return mIdMap.get(type);
	}

	public void addTag(String tag) {
		mTags.add(tag);
	}

	/**
	 * Add new tags.
	 * 
	 * @param tags
	 */
	public void addTags(final Collection<String> tags) {
		mTags.addAll(tags);
	}

	public Iterable<String> getTags() {
		return mTags;
	}

	public boolean hasTag(String name) {
		return mTags.contains(name);
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
	
	/**
	 * Returns the number of exons in the gene.
	 * 
	 * @return
	 */
	public int getExonCount() {
		return mExons.size();
	}

	//
	// Static methods
	//

	/**
	 * To symbols.
	 *
	 * @param features the features
	 * @return the sets the
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static Set<String> toSymbols(List<Gene> genes) {
		Set<String> ret = new TreeSet<String>();

		for (Gene gene : genes) {
			ret.add(gene.getSymbol());
		}

		return ret;
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

		if (gene.mRegion.mStrand == Strand.SENSE) {
			return new GenomicRegion(gene.mRegion.mChr, gene.mRegion.mStart, gene.mRegion.mStart);
		} else {
			return new GenomicRegion(gene.mRegion.mChr, gene.mRegion.mEnd, gene.mRegion.mEnd);
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

		if (gene.mRegion.mStrand == Strand.SENSE) {
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
	 * @param chr		The chromosome.
	 * @param start		The start.
	 * @param end		The end.
	 * @param strand	The strand.
	 * @return			The new gene.
	 */
	public static Gene create(GenomicRegion region) {
		return new Gene(region);
	}

	public static Gene create(GeneType type, GenomicRegion region) {
		return new Gene(type, region);
	}

	

	




}
