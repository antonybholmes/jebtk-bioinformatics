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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a genomic entity such as a gene or exon. An entity is a
 * genomic region with a collection of annotations and sub entities to
 * describe a genomic feature such as a gene, transcript or exon. It is
 * designed for use with different annotation databases such as Refseq and
 * Ensembl where different ids and nomenclature are used.
 */
@JsonPropertyOrder({"l", "ids"})
public class GenomicEntity extends GenomicRegion {
  /** The Constant SYMBOL_TYPE. */
  public static final String SYMBOL_TYPE = "symbol";

  /** The Constant REFSEQ_TYPE. */
  public static final String REFSEQ_TYPE = "refseq";

  /** The Constant ENTREZ_TYPE. */
  public static final String ENTREZ_TYPE = "entrez";

  public static final String TRANSCRIPT_ID_TYPE = "transcript_id";

  public static final String ENSEMBL_TYPE = "ensembl";

  /** The m id map. */
  protected IterMap<String, String> mIdMap = DefaultTreeMap.create(TextUtils.NA); // new
  // IterTreeMap<String,
  // String>();

  private Set<String> mTags = new TreeSet<String>();

  private IterMap<GenomicType, List<GenomicEntity>> mElemMap = DefaultTreeMap
      .create(new UniqueArrayListCreator<GenomicEntity>());

  /** The m utr 5 p. */
  // private List<Exon> mUtr5p = new ArrayList<Exon>();

  /** The m text. */
  private String mText;

  private GenomicEntity mParent = null;

  @JsonIgnore
  public final GenomicType mType;

  public GenomicEntity(GenomicType type, GenomicRegion region) {
    super(region);

    mType = type;
  }

  @JsonIgnore
  public GenomicType getType() {
    return mType;
  }

  /**
   * Set a parent entity to indicate this is a child of another.
   * 
   * @param gene
   */
  public void setParent(GenomicEntity gene) {
    mParent = gene;
  }

  @JsonIgnore
  public GenomicEntity getParent() {
    return mParent;
  }



  /**
   * Add an entity as a child of this one. For example an exon could be
   * a child of transcript.
   * 
   * @param e   A genomic entity.
   */
  public void add(GenomicEntity e) {
    mElemMap.get(e.mType).add(e);
  }

  /**
   * Return child entities of this one of a specific type.
   * 
   * @param type    a genomic type.
   * @return
   */
  @JsonIgnore
  public Iterable<GenomicEntity> getEntities(GenomicType type) {
    return mElemMap.get(type);
  }

  @JsonIgnore
  public int getEntityCount(GenomicType type) {
    return mElemMap.get(type).size();
  }

  public GenomicEntity setId(String type, String name) {
    mIdMap.put(type, name);

    if (type.contains("transcript")) {
      mIdMap.put(TRANSCRIPT_ID_TYPE, name);
      
      mIdMap.remove("transcript");
    }

    // Gene name is the same as symbol
    if (type.contains("gene_name") || type.contains("gene_id")) {
      mIdMap.put(SYMBOL_TYPE, name);
    }

    // Invalidate so it will be recreated
    mText = null;

    return this;
  }

  /**
   * Gets the tss.
   *
   * @return the tss
   */
  @JsonIgnore
  public GenomicRegion getTss() {
    return tssRegion(this);
  }

  /**
   * Gets the ref seq.
   *
   * @return the ref seq
   */
  @JsonIgnore
  public String getRefSeq() {
    return getId(REFSEQ_TYPE);
  }

  /**
   * Gets the entrez.
   *
   * @return the entrez
   */
  @JsonIgnore
  public String getEntrez() {
    return getId(ENTREZ_TYPE);
  }

  /**
   * Gets the symbol.
   *
   * @return the symbol
   */
  @JsonIgnore
  public String getSymbol() {
    return getId(SYMBOL_TYPE);
  }

  /**
   * Sets the symbol.
   *
   * @param name the name
   * @return the gene
   */
  public GenomicEntity setSymbol(String name) {
    return setId(SYMBOL_TYPE, name);
  }

  /**
   * Sets the refseq.
   *
   * @param name the name
   * @return the gene
   */
  public GenomicEntity setRefseq(String name) {
    return setId(REFSEQ_TYPE, name);
  }

  /**
   * Sets the entrez.
   *
   * @param name the name
   * @return the gene
   */
  public GenomicEntity setEntrez(String name) {
    return setId(ENTREZ_TYPE, name);
  }

  @JsonIgnore
  public String getTranscriptId() {
    return getId(TRANSCRIPT_ID_TYPE);
  }

  public GenomicEntity setTranscriptId(String name) {
    return setId(TRANSCRIPT_ID_TYPE, name);
  }

  
  @JsonGetter("ids")
  public Iterable<Entry<String, String>> getIds() {
    return mIdMap.entrySet();
  }
  
  /**
   * Return the different types of ids associated with this entity. Typically
   * this will be 'refseq' or 'gene_symbol'.
   *
   * @return the ids
   */
  @JsonIgnore
  public Iterable<String> getIdTypes() {
    return mIdMap.keySet();
  }

  @JsonIgnore
  public int getIdCount() {
    return mIdMap.size();
  }

  /**
   * Return the first available id.
   * 
   * @return
   */
  @JsonIgnore
  public String getId() {
    return getTranscriptId(); // getId(mIdMap.keySet().iterator().next());
  }

  @JsonIgnore
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

  /**
   * Add an arbitrary tag to the entity such as an some meta data better
   * describing it.
   * 
   * @param tag
   */
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

  @JsonIgnore
  public Iterable<String> getTags() {
    return mTags;
  }

  public boolean hasTag(String name) {
    return mTags.contains(name);
  }

  @JsonIgnore
  public int getTagCount() {
    return mTags.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomicRegion#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof GenomicEntity) {
      return toString().equals(((GenomicEntity) o).toString());
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
    if (mText == null) {
      StringBuilder buffer = new StringBuilder(super.toString());

      if (mIdMap.size() > 0) {
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
      }

      mText = buffer.toString();
    }

    return mText;
  }

  //
  // Static methods
  //


  /**
   * Tss region.
   *
   * @param gene the gene
   * @return the genomic region
   */
  public static GenomicRegion tssRegion(GenomicEntity gene) {
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
  public static int tssDist(GenomicEntity gene, GenomicRegion region) {
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
  public static int tssDist5p(GenomicEntity gene, GenomicRegion region) {
    GenomicRegion tssRegion = tssRegion(gene);

    return GenomicRegion.midDist(region, tssRegion);
  }
}
