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

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.IterTreeMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.text.Join;
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
@JsonPropertyOrder({ "loc", "strand", "type", "properties", "tags" })
public class GenomicElement extends GenomicRegion {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** The m id map. */
  protected IterMap<String, Tag> mPropertyMap = 
      new IterTreeMap<String, Tag>();

  /** The m tags. */
  private Set<Tag> mTags = new TreeSet<Tag>();

  /** The m elem map. */
  private IterMap<String, List<GenomicElement>> mElemMap = DefaultTreeMap
      .create(new UniqueArrayListCreator<GenomicElement>());

  /** The m utr 5 p. */
  // private List<Exon> mUtr5p = new ArrayList<Exon>();

  /** The m text. */
  private String mText;

  /** The m parent. */
  private GenomicElement mParent = null;

  /** The m type. */
  @JsonIgnore
  public final String mType;

  private Color mColor = Color.BLACK;

  /**
   * Instantiates a new genomic entity.
   *
   * @param type the type
   * @param l the l
   */
  public GenomicElement(String type, GenomicRegion l) {
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
  public GenomicElement(String type, GenomicRegion l, Strand s) {
    super(l, s);

    mType = type;
  }

  public GenomicElement(String type, Chromosome chr, int start, int end) {
    this(type, chr, start, end, Strand.SENSE);
  }

  public GenomicElement(String type, Chromosome chr, int start, int end,
      Strand strand) {
    super(chr, start, end, strand);

    mType = type;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @JsonGetter("type")
  public String getType() {
    return mType;
  }

  @JsonIgnore
  public Color getColor() {
    return mColor;
  }

  public GenomicElement setColor(Color color) {
    mColor = color;

    return this;
  }

  /**
   * Set a parent entity to indicate this is a child of another.
   *
   * @param gene the new parent
   */
  public void setParent(GenomicElement gene) {
    mParent = gene;
  }

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  @JsonIgnore
  public GenomicElement getParent() {
    return mParent;
  }

  /**
   * Add an entity as a child of this one. For example an exon could be a child
   * of transcript.
   * 
   * @param e A genomic entity.
   */
  public void add(GenomicElement e) {
    mElemMap.get(e.mType).add(e);
  }

  /**
   * Gets the child types.
   *
   * @return the child types
   */
  @JsonIgnore
  public Iterable<String> getChildTypes() {
    return mElemMap.keySet();
  }

  /**
   * Return child entities of this one of a specific type.
   *
   * @param type a genomic type.
   * @return the children
   */
  @JsonIgnore
  public Iterable<GenomicElement> getChildren(String type) {
    return mElemMap.get(type);
  }
  
  @JsonIgnore
  public Set<Entry<String, List<GenomicElement>>> getChildren() {
    return mElemMap.entrySet();
  }

  /**
   * Gets the child count.
   *
   * @param type the type
   * @return the child count
   */
  @JsonIgnore
  public int getChildCount(String type) {
    return mElemMap.get(type).size();
  }

  /**
   * Sets the id.
   *
   * @param type the type
   * @param name the name
   * @return the genomic entity
   */
  public GenomicElement setProperty(String name, String value) {
    return setProperty(name, new TextTag(value));
  }
  
  public GenomicElement setProperty(String name, int value) {
    return setProperty(name, new IntTag(value));
  }
  
  public GenomicElement setProperty(String name, double value) {
    return setProperty(name, new DoubleTag(value));
  }
  
  public GenomicElement setProperty(String name, Tag property) {
    if (TextUtils.isNullOrEmpty(name)) {
      return this;
    }
    
    name = Tag.format(name);
    
    mPropertyMap.put(name, property);
    
    return this;
  }


  /**
   * Gets the ids.
   *
   * @return the ids
   */
  @JsonGetter("properties")
  public IterMap<String, Tag> getProperties() {
    return mPropertyMap;
  }

  /**
   * Returns true if the entity contains an id with a given name.
   * 
   * @param name the name of the id.
   * @return true if the name exists, false otherwise.
   */
  @JsonIgnore
  public boolean hasProperty(String name) {
    return mPropertyMap.containsKey(name);
  }

  /**
   * Return the different types of ids associated with this entity. Typically
   * this will be 'refseq' or 'gene_symbol'.
   *
   * @return the ids
   */
  @JsonIgnore
  public Iterable<String> getPropertyNames() {
    return mPropertyMap.keySet();
  }

  /**
   * Gets the id count.
   *
   * @return the id count
   */
  @JsonIgnore
  public int getPropertyCount() {
    return mPropertyMap.size();
  }

  /**
   * Gets the id.
   *
   * @param type the type
   * @return the id
   */
  @JsonIgnore
  public Tag getProperty(GeneIdType type) {
    return getProperty(type.toString());
  }

  /**
   * Return a property. If the property does not exist, a property with the
   * string value of 'n/a' will be automatically created so that a null is
   * never returned.
   *
   * @param type the type
   * @return the id
   */
  public Tag getProperty(String name) {
    name = Tag.format(name);
    
    if (!mPropertyMap.containsKey(name)) {
      setProperty(name, TextUtils.NA);
    }
    
    return mPropertyMap.get(name);
  }
  
  /**
   * Return a string version of the property.
   * 
   * @param name
   * @return
   */
  public String getProp(String name) {
    return getProperty(name).toString();
  }

  /**
   * Add an arbitrary tag to the entity such as an some meta data better
   * describing it.
   *
   * @param tag the tag
   * @return
   */
  public GenomicElement addTag(String tag) {
    mTags.add(new TextTag(tag));

    return this;
  }
  
  public GenomicElement addTag(int tag) {
    mTags.add(new IntTag(tag));

    return this;
  }
  
  public GenomicElement addTag(double tag) {
    mTags.add(new DoubleTag(tag));

    return this;
  }
  
  public GenomicElement addTags(Collection<Tag> tags) {
    mTags.addAll(tags);
    
    return this;
  }

  /**
   * Gets the tags.
   *
   * @return the tags
   */
  @JsonGetter("tags")
  public Set<Tag> getTags() {
    return mTags;
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


  /**
   * Gets the tss.
   *
   * @return the tss
   */
  @JsonIgnore
  public GenomicRegion getTss() {
    return tssRegion(this);
  }
  
  /*
   * @Override public boolean equals(Object o) { if (o instanceof GenomicEntity)
   * { return toString().equals(((GenomicEntity) o).toString()); } else { return
   * super.equals(o); } }
   */

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genomic.GenomicRegion#compareTo(org.jebtk.
   * bioinformatics.genomic.Region)
   */
  @Override
  public int compareTo(Region r) {
    int c = super.compareTo(r);

    // Different location so sort
    if (c != 0) {
      return c;
    }

    // Same location, have to test toString

    if (r instanceof GenomicElement) {
      c = toString().compareTo(((GenomicElement) r).toString());
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
      StringBuilder buffer = new StringBuilder();

      buffer.append(getType());
      buffer.append(" ").append(super.toString());
      buffer.append(" ").append(getStrand());

      if (mPropertyMap.size() > 0) {
        buffer.append(" [");

        Iterator<Entry<String, Tag>> iter = mPropertyMap.iterator();

        while (iter.hasNext()) {
          String id = iter.next().getKey();

          buffer.append(id).append("=").append(getProperty(id));

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
  public static GenomicRegion tssRegion(GenomicElement gene) {
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
  public static int tssDist(GenomicElement gene, GenomicRegion region) {
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
  public static int tssDist5p(GenomicElement gene, GenomicRegion region) {
    GenomicRegion tssRegion = tssRegion(gene);

    return GenomicRegion.midDist(region, tssRegion);
  }

  /**
   * To map.
   * @param <T>
   *
   * @param genes the genes
   * @return the iter map
   */
  public static <T extends GenomicElement> IterMap<Chromosome, Set<GenomicElement>> toMap(Collection<T> genes) {
    IterMap<Chromosome, Set<GenomicElement>> ret = DefaultTreeMap
        .create(new TreeSetCreator<GenomicElement>());

    for (GenomicElement g : genes) {
      ret.get(g.mChr).add(g);
    }

    return ret;
  }

}
