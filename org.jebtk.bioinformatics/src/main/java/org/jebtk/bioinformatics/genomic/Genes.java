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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public abstract class Genes {

  /** Empty gene set that can be used as a placeholder */
  public static final Genes EMPTY_GENES = new Genes() {

    @Override
    public Iterable<Genome> getGenomes() {
      return Collections.emptyList();
    }
  };

  public void add(GenomicEntity gene) {
    add(gene, gene);
  }

  public void add(GenomicRegion region, GenomicEntity gene) {
    // Do nothing
  }

  /**
   * Lookup a gene by either symbol or refseq.
   *
   * @param id the id
   * @return the gene
   * @throws IOException
   */
  // public abstract GenomicEntity lookup(String genome, String id) throws
  // IOException;

  public GenomicEntity getGene(Genome genome, String id) throws IOException {
    List<GenomicEntity> genes = getGenes(genome, id);

    if (genes.size() > 0) {
      return genes.get(0);
    } else {
      return null;
    }
  }

  public List<GenomicEntity> getGenes(Genome g, String id) throws IOException {
    return Collections.emptyList();
  }

  public List<GenomicEntity> getGenes() throws IOException {
    return Collections.emptyList();
  }

  public List<GenomicEntity> findGenes(Genome genome, String region)
      throws IOException {
    return findGenes(genome, GenomicRegion.parse(genome, region));
  }

  /**
   * Find genes.
   *
   * @param region the region
   * @return the list
   * @throws IOException
   */
  public List<GenomicEntity> findGenes(Genome genome, GenomicRegion region)
      throws IOException {
    return Collections.emptyList();
  }

  /**
   * Find closest genes.
   *
   * @param region the region
   * @return the list
   * @throws IOException
   */
  public List<GenomicEntity> findClosestGenes(Genome genome,
      GenomicRegion region) throws IOException {
    return Collections.emptyList();
  }

  /**
   * Find closest genes by tss.
   *
   * @param region the region
   * @return the list
   * @throws IOException
   */
  public List<GenomicEntity> findClosestGenesByTss(Genome genome,
      GenomicRegion region) throws IOException {
    Collection<GenomicEntity> genes = findClosestGenes(genome, region); // findGenes(region);

    List<GenomicEntity> ret = new ArrayList<GenomicEntity>();

    int minD = Integer.MAX_VALUE;

    for (GenomicEntity gene : genes) {
      GenomicRegion tss = Gene.tssRegion(gene);

      minD = Math.min(minD, GenomicRegion.midAbsDist(region, tss));
    }

    for (GenomicEntity gene : genes) {
      GenomicRegion tss = Gene.tssRegion(gene);

      int d = GenomicRegion.midAbsDist(region, tss);

      if (d == minD) {
        ret.add(gene);
      }
    }

    return ret;
  }

  // public List<Gene> findGenes(Chromosome c, int start) {
  // ChromosomeBins bins = mPositionMap.get(c);
  //
  // return bins.findGenes(start);
  // }

  /**
   * Find the representative gene by name. Search is case insensitive. Returns
   * null if gene not found.
   *
   * @param name the name
   * @return the gene
   */
  // public GenomicEntity findMainVariant(String name) {
  // return null;
  // }

  /**
   * Return the RefSeq ids used to index these genes.
   *
   * @return the ref seq ids
   */
  public Iterable<String> getRefSeqIds() {
    return getIds(Gene.REFSEQ_TYPE);
  }

  /**
   * Return the set of ids (e.g. RefSeq ids) associated with a given id type.
   *
   * @param type the type
   * @return the ids
   */
  public Iterable<String> getIds(String type) {
    return Collections.emptyList();
  }

  public Iterable<String> getNames() throws IOException {
    return getIds(Gene.GENE_NAME_TYPE);
  }

  public boolean contains(Chromosome chr) {
    return false;
  }

  /**
   * Should return the supported databases
   * 
   * @return
   */
  public abstract Iterable<Genome> getGenomes();

  //
  // Static methods
  //

  public static String sanitize(String name) {
    return name.toUpperCase();
  }

  public static GFF3Parser gff3Parser() {
    return new GFF3Parser();
  }

  public static GTB1Parser gtbParser() {
    return new GTB1Parser();
  }

  public static GTB2Parser gtb2Parser() {
    return new GTB2Parser();
  }

  public List<GenomicEntity> getOverlappingGenes(Genome genome,
      GenomicRegion region,
      int minBp) throws IOException {
    List<GenomicEntity> ret = new ArrayList<GenomicEntity>();

    getOverlappingGenes(genome, region, minBp, ret);

    return ret;
  }

  public void getOverlappingGenes(Genome genome,
      GenomicRegion region,
      int minBp,
      List<GenomicEntity> ret) throws IOException {
    List<GenomicEntity> genes = findGenes(genome, region);

    if (genes.size() == 0) {
      return;
    }

    for (GenomicEntity g : genes) {
      GenomicRegion overlap = GenomicRegion.overlap(region, g);

      if (overlap != null && overlap.getLength() > minBp) {
        ret.add(g);
      }
    }
  }
}