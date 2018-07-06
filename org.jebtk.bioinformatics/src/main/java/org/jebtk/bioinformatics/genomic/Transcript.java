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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The class Gene.
 */
@JsonPropertyOrder({"l", "exons", "ids"})
public class Transcript extends GenomicEntity {

  public Transcript(GenomicRegion region) {
    super(GenomicType.TRANSCRIPT, region);
  }

  /**
   * Create a transcript with a given transcript id.
   * 
   * @param name
   * @param region
   */
  public Transcript(String name, GenomicRegion region) {
    this(region);

    setTranscriptId(name); 
  }

  /**
   * Adds the exon.
   *
   * @param exon the exon
   */
  public void addExon(GenomicRegion exon) {
    add(new Exon(exon));
  }

  public void add5pUtr(GenomicRegion exon) {
    add(new UTR3p(exon));
  }

  public void add3pUtr(GenomicRegion exon) {
    add(new UTR3p(exon));
  }

  @JsonIgnore
  public Iterable<GenomicEntity> get3pUtrs() {
    return getEntities(GenomicType.UTR_3P);
  }

  @JsonIgnore
  public Iterable<GenomicEntity> get5pUtrs() {
    return getEntities(GenomicType.UTR_5P);
  }

  @JsonGetter("exons")
  public Iterable<GenomicEntity> getExons() {
    return getEntities(GenomicType.EXON);
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

  public GenomicEntity setId(GeneIdType type, String name) {
    return setId(type.toString().toLowerCase(), name);
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
  public static Set<String> toSymbols(List<Transcript> genes) {
    Set<String> ret = new TreeSet<String>();

    for (Transcript gene : genes) {
      ret.add(gene.getSymbol());
    }

    return ret;
  }
}
