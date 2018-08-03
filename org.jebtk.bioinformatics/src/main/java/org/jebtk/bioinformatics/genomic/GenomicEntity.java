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

import org.jebtk.core.NameProperty;
import org.jebtk.core.collections.ArrayListCreator;
import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// TODO: Auto-generated Javadoc
/**
 * Represents a genomic entity such as a gene or exon. An entity is a genomic
 * region with a collection of annotations and sub entities to describe a
 * genomic feature such as a gene, transcript or exon. It is designed for use
 * with different annotation databases such as Refseq and Ensembl where
 * different ids and nomenclature are used.
 */
@JsonPropertyOrder({ "loc", "strand", "type", "ids", "tags" })
public class GenomicEntity extends GenomicRegion implements NameProperty {
  /** The Constant SYMBOL_TYPE. */
  //public static final String SYMBOL_TYPE = "symbol";
  
  public static final String GENE_ID_TYPE = "gene_id";
  
  /** The Constant GENE_NAME_TYPE. */
  public static final String GENE_NAME_TYPE = "gene_name";

  /** The Constant REFSEQ_TYPE. */
  public static final String REFSEQ_TYPE = "refseq";

  /** The Constant ENTREZ_TYPE. */
  public static final String ENTREZ_ID_TYPE = "entrez";

  /** The Constant TRANSCRIPT_ID_TYPE. */
  public static final String TRANSCRIPT_ID_TYPE = "transcript_id";

  /** The Constant ENSEMBL_TYPE. */
  public static final String ENSEMBL_TYPE = "ensembl";

  /** The m id map. */
  protected IterMap<String, String> mIdMap = DefaultTreeMap
      .create(TextUtils.NA); // new
  // IterTreeMap<String,
  // String>();

  /** The m tags. */
  private Set<String> mTags = new TreeSet<String>();

  /** The m elem map. */
  private IterMap<GenomicType, List<GenomicEntity>> mElemMap = DefaultTreeMap
      .create(new UniqueArrayListCreator<GenomicEntity>());

  /** The m utr 5 p. */
  // private List<Exon> mUtr5p = new ArrayList<Exon>();

  /** The m text. */
  private String mText;

  /** The m parent. */
  private GenomicEntity mParent = null;

  /** The m type. */
  @JsonIgnore
  public final GenomicType mType;

  /**
   * Instantiates a new genomic entity.
   *
   * @param type the type
   * @param l the l
   */
  public GenomicEntity(GenomicType type, GenomicRegion l) {
    super(l);

    mType = type;
  }

  /**
   * Instantiates a new genomic entity.
   *
   * @param type the type
   * @param l the l
   * @param s the s
   */
  public GenomicEntity(GenomicType type, GenomicRegion l, Strand s) {
    super(l, s);

    mType = type;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @JsonGetter("type")
  public GenomicType getType() {
    return mType;
  }

  /**
   * Set a parent entity to indicate this is a child of another.
   *
   * @param gene the new parent
   */
  public void setParent(GenomicEntity gene) {
    mParent = gene;
  }

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  @JsonIgnore
  public GenomicEntity getParent() {
    return mParent;
  }

  /**
   * Add an entity as a child of this one. For example an exon could be a child
   * of transcript.
   * 
   * @param e A genomic entity.
   */
  public void add(GenomicEntity e) {
    mElemMap.get(e.mType).add(e);
  }

  /**
   * Gets the child types.
   *
   * @return the child types
   */
  @JsonIgnore
  public Set<GenomicType> getChildTypes() {
    return mElemMap.keySet();
  }

  /**
   * Return child entities of this one of a specific type.
   *
   * @param type a genomic type.
   * @return the children
   */
  @JsonIgnore
  public Iterable<GenomicEntity> getChildren(GenomicType type) {
    return mElemMap.get(type);
  }

  /**
   * Gets the child count.
   *
   * @param type the type
   * @return the child count
   */
  @JsonIgnore
  public int getChildCount(GenomicType type) {
    return mElemMap.get(type).size();
  }

  /**
   * Sets the id.
   *
   * @param type the type
   * @param name the name
   * @return the genomic entity
   */
  public GenomicEntity setId(String type, String name) {
    
    String lc = type.toLowerCase();
    
    // Gene name is the same as symbol
    if (lc.contains("symbol")) {
      mIdMap.put(GENE_NAME_TYPE, name);
    } else if (lc.contains("transcript")) {
      mIdMap.put(TRANSCRIPT_ID_TYPE, name);
    } else {
      mIdMap.put(lc, name);
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
    return getId(ENTREZ_ID_TYPE);
  }

  /**
   * Gets the gene symbol. Equivalent to {@code getGeneName()}.
   *
   * @return the gene symbol.
   */
  @JsonIgnore
  public String getSymbol() {
    return getGeneName();
  }
  
  /* (non-Javadoc)
   * @see org.jebtk.core.NameProperty#getName()
   */
  @JsonIgnore
  @Override
  public String getName() {
    return getGeneName();
  }
  
  /**
   * Gets the gene id. Equivalent to {@code getGeneId()}.
   *
   * @return the id
   */
  @JsonIgnore
  public String getId() {
    return getGeneId();
  }
  
  /**
   * Gets the gene id.
   *
   * @return the gene id
   */
  @JsonIgnore
  public String getGeneId() {
    return getId(GENE_ID_TYPE);
  }
  
  /**
   * Gets the gene name.
   *
   * @return the gene name
   */
  @JsonIgnore
  public String getGeneName() {
    return getId(GENE_NAME_TYPE);
  }

  /**
   * Sets the symbol.
   *
   * @param name the name
   * @return the gene
   */
  public GenomicEntity setName(String name) {
    return setGeneName(name);
  }
  
  /**
   * Sets the symbol.
   *
   * @param name the name
   * @return the genomic entity
   */
  public GenomicEntity setSymbol(String name) {
    return setGeneName(name);
  }
  
  /**
   * Sets the gene name.
   *
   * @param name the name
   * @return the genomic entity
   */
  public GenomicEntity setGeneName(String name) {
    return setId(GENE_NAME_TYPE, name);
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
    return setId(ENTREZ_ID_TYPE, name);
  }

  /**
   * Gets the transcript id.
   *
   * @return the transcript id
   */
  @JsonIgnore
  public String getTranscriptId() {
    return getId(TRANSCRIPT_ID_TYPE);
  }

  /**
   * Sets the transcript id.
   *
   * @param name the name
   * @return the genomic entity
   */
  public GenomicEntity setTranscriptId(String name) {
    return setId(TRANSCRIPT_ID_TYPE, name);
  }

  /**
   * Gets the ids.
   *
   * @return the ids
   */
  @JsonGetter("ids")
  public Iterable<Entry<String, String>> getIds() {
    return mIdMap;
  }

  /**
   * Returns true if the entity contains an id with a given name.
   * 
   * @param name the name of the id.
   * @return true if the name exists, false otherwise.
   */
  @JsonIgnore
  public boolean containsId(String name) {
    return mIdMap.containsKey(name);
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

  /**
   * Gets the id count.
   *
   * @return the id count
   */
  @JsonIgnore
  public int getIdCount() {
    return mIdMap.size();
  }

  /**
   * Gets the id.
   *
   * @param type the type
   * @return the id
   */
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
   * @param tag the tag
   */
  public void addTag(String tag) {
    mTags.add(tag);
  }

  /**
   * Add new tags.
   *
   * @param tags the tags
   */
  public void addTags(final Collection<String> tags) {
    mTags.addAll(tags);
  }

  /**
   * Gets the tags.
   *
   * @return the tags
   */
  @JsonGetter("tags")
  public Iterable<String> getTags() {
    return mTags;
  }

  /**
   * Checks for tag.
   *
   * @param name the name
   * @return true, if successful
   */
  public boolean hasTag(String name) {
    return mTags.contains(name);
  }

  /**
   * Gets the tag count.
   *
   * @return the tag count
   */
  @JsonIgnore
  public int getTagCount() {
    return mTags.size();
  }

  /*
   * @Override public boolean equals(Object o) { if (o instanceof GenomicEntity)
   * { return toString().equals(((GenomicEntity) o).toString()); } else { return
   * super.equals(o); } }
   */

  /* (non-Javadoc)
   * @see org.jebtk.bioinformatics.genomic.GenomicRegion#compareTo(org.jebtk.bioinformatics.genomic.Region)
   */
  @Override
  public int compareTo(Region r) {
    int c = super.compareTo(r);

    // Different location so sort
    if (c != 0) {
      return c;
    }

    // Same location, have to test toString

    if (r instanceof GenomicEntity) {
      c = toString().compareTo(((GenomicEntity) r).toString());
    }

    // Return whatever we have concluded.
    return c;
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

      buffer.append(" ").append(getType());

      if (mIdMap.size() > 0) {
        buffer.append(" [");

        Iterator<Entry<String, String>> iter = mIdMap.iterator();

        while (iter.hasNext()) {
          String id = iter.next().getKey();

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

  /**
   * To map.
   *
   * @param genes the genes
   * @return the iter map
   */
  public static IterMap<Chromosome, List<GenomicEntity>> toMap(
      Collection<GenomicEntity> genes) {
    IterMap<Chromosome, List<GenomicEntity>> ret = DefaultHashMap
        .create(new ArrayListCreator<GenomicEntity>());

    for (GenomicEntity g : genes) {
      ret.get(g.mChr).add(g);
    }

    return ret;
  }

}
