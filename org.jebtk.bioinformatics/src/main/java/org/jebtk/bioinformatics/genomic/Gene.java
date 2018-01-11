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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * The class Gene.
 */
public class Gene extends GenomicRegion {

  /** The Constant SYMBOL_TYPE. */
  public static final String SYMBOL_TYPE = "symbol";

  /** The Constant REFSEQ_TYPE. */
  public static final String REFSEQ_TYPE = "refseq";

  /** The Constant ENTREZ_TYPE. */
  public static final String ENTREZ_TYPE = "entrez";

  public static final String TRANSCRIPT_ID_TYPE = "transcript";

  /** The m id map. */
  private IterMap<String, String> mIdMap = DefaultTreeMap.create(TextUtils.NA); // new
                                                                                // IterTreeMap<String,
                                                                                // String>();

  private Set<String> mTags = new TreeSet<String>();

  private IterMap<GeneType, List<Gene>> mElemMap = DefaultTreeMap
      .create(new UniqueArrayListCreator<Gene>());

  /** The m utr 5 p. */
  // private List<Exon> mUtr5p = new ArrayList<Exon>();

  /** The m text. */
  private String mText;

  private Gene mParent = null;

  public final GeneType mType;

  public Gene(GenomicRegion region) {
    this(GeneType.GENE, region);
  }

  public Gene(GeneType type, GenomicRegion region) {
    super(region);

    mType = type;
  }

  public Gene(String name, GeneType type, GenomicRegion region) {
    this(type, region);

    setSymbol(name);
  }

  public GeneType getType() {
    return mType;
  }

  public void setParent(Gene gene) {
    mParent = gene;
  }

  public Gene getParent() {
    return mParent;
  }

  /*
   * @Override public int compareTo(Region r) { if (r instanceof Gene) { Gene g
   * = (Gene)r;
   * 
   * for (String id : mIdMap.keySet()) { // Find the first point where they
   * differ and return that
   * 
   * if (g.mIdMap.containsKey(id)) { String id2 = g.mIdMap.get(id);
   * 
   * if (!id.equals(id2)) { return id.compareTo(id2); } } }
   * 
   * if (mTags.size() > g.mTags.size()) { return 1; } else if (mTags.size() <
   * g.mTags.size()) { return -1; } else { // Do nothing } }
   * 
   * // Compare exons return super.compareTo(r); }
   */

  public Gene setId(GeneIdType type, String name) {
    return setId(type.toString().toLowerCase(), name);
  }

  /**
   * Assign a gene id to the gene (e.g. a symbol or RefSeq Id).
   *
   * @param type the type
   * @param name the name
   * @return The instance of the gene
   */
  public Gene setId(String type, String name) {
    if (!TextUtils.isNullEmptyNA(type) && !TextUtils.isNullEmptyNA(name)) {
      mIdMap.put(type, name);

      if (type.contains("transcript")) {
        mIdMap.put(TRANSCRIPT_ID_TYPE, name);
      }

      // Gene name is the same as symbol
      if (type.contains("gene_name") || type.contains("gene_id")) {
        mIdMap.put(SYMBOL_TYPE, name);
      }

      setText();
    }

    return this;
  }

  /**
   * Set a text description of the gene.
   */
  private void setText() {
    StringBuilder buffer = new StringBuilder(super.toString());
    buffer.append(" [");

    Iterator<String> iter = mIdMap.iterator();

    while (iter.hasNext()) {
      String id = iter.next();

      buffer.append(id).append("=").append(getId(id));

      if (iter.hasNext()) {
        buffer.append(", ");
      }
    }

    buffer.append("]");

    mText = buffer.toString();
  }

  /**
   * Gets the ids.
   *
   * @return the ids
   */
  public Iterable<String> getIdTypes() {
    return mIdMap.keySet();
  }

  /**
   * Adds the exon.
   *
   * @param exon the exon
   */
  public void addExon(GenomicRegion exon) {
    add(Gene.create(GeneType.EXON, exon));
  }

  public void add5pUtr(GenomicRegion exon) {
    add(Gene.create(GeneType.UTR_5P, exon));
  }

  public void add3pUtr(GenomicRegion exon) {
    add(Gene.create(GeneType.UTR_3P, exon));
  }

  public void add(Gene gene) {
    mElemMap.get(gene.mType).add(gene);
  }

  public Iterable<Gene> get3pUtrs() {
    return getElements(GeneType.UTR_3P);
  }

  public Iterable<Gene> get5pUtrs() {
    return getElements(GeneType.UTR_5P);
  }

  public Iterable<Gene> getExons() {
    return getElements(GeneType.EXON);
  }

  public Iterable<Gene> getElements(GeneType type) {
    return mElemMap.get(type);
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
   * Return the first available id.
   * 
   * @return
   */
  public String getId() {
    return getTranscriptId(); // getId(mIdMap.keySet().iterator().next());
  }

  public String getId(GeneIdType type) {
    return getId(type.toString().toLowerCase());
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

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomicRegion#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Gene) {
      return mText.equals(((Gene) o).mText);
    } else {
      return super.equals(o);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomicRegion#toString()
   */
  @Override
  public String toString() {
    return mText;
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

  public static Gene create(GeneType type, GenomicRegion region) {
    return new Gene(type, region);
  }
}
