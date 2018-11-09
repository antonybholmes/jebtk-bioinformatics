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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jebtk.bioinformatics.gapsearch.FixedGapSearch;
import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.io.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public class FixedGapGenes extends SingleDbGenes {
  // private static final GeneService INSTANCE = new GeneService();

  /**
   * The constant DEFAULT_RES.
   */
  public static final String DEFAULT_RES = "res/ucsc_refseq_exons_entrez_hg19.txt.gz";

  /**
   * The constant DEFAULT_FILE.
   */
  public static final Path DEFAULT_FILE = PathUtils.getPath(DEFAULT_RES);

  /**
   * The constant LOG.
   */
  private static final Logger LOG = LoggerFactory
      .getLogger(FixedGapGenes.class);

  private FixedGapSearch<GenomicEntity> mSearch = 
      new FixedGapSearch<GenomicEntity>();

  /**
   * The member symbol main variant.
   */
  private Map<String, GenomicEntity> mSymbolMainVariant = new HashMap<String, GenomicEntity>();

  /**
   * The member entrez map.
   */
  // private Map<String, Set<Gene>> mEntrezMap =
  // DefaultHashMap.create(new HashSetCreator<Gene>());

  /**
   * The member symbol map.
   */

  private IterMap<String, Set<String>> mTypeMap = DefaultHashMap
      .create(new TreeSetCreator<String>());

  private IterMap<String, List<GenomicEntity>> mIdMap = DefaultHashMap
      .create(new UniqueArrayListCreator<GenomicEntity>());

  /**
   * The member ref seq map.
   */
  // private Map<String, Gene> mRefSeqMap =
  // new HashMap<String, Gene>();

  private boolean mFindMainVariants = true;

  public FixedGapGenes(Genome genome) {
    super(genome);
  }

  @Override
  public void add(GenomicRegion region, GenomicEntity gene) {
    mSearch.add(region, gene);

    // Map to exons

    /*
     * if (gene.getExonCount() > 0) { for (GenomicRegion exon : gene) {
     * super.add(exon, gene); } } else { super.add(region, gene); }
     */

    for (String tid : gene.getPropertyNames()) {
      String name = gene.getProp(tid);
      mTypeMap.get(tid).add(name);
      mIdMap.get(sanitize(name)).add(gene);
    }
  }

  /**
   * Auto find main variants.
   */
  public void autoFindMainVariants() {

    if (mFindMainVariants) {
      //
      // Find the representative gene e.g. variant 1
      //

      Set<String> symbols = mTypeMap.get(Gene.GENE_NAME_TYPE);

      for (String name : symbols) {
        List<GenomicEntity> genes = mIdMap.get(sanitize(name));

        // try and find variant 1

        int maxWidth = 0;

        for (GenomicEntity gene : genes) {
          int width = gene.mLength;

          if (width > maxWidth) {
            mSymbolMainVariant.put(name, gene);

            maxWidth = width;
          }
        }
      }

      mFindMainVariants = false;
    }

    // clear to indicate it can be garbage collected.
    // positionMap.clear();
    // mSymbolMap.clear();
  }

  /*
   * public GenomicEntity lookup(String genome, String id) throws IOException {
   * GenomicEntity gene = getGene(genome, id);
   * 
   * if (gene != null) { return gene; }
   * 
   * gene = findMainVariant(id);
   * 
   * return gene; }
   */

  /**
   * Lookup.
   *
   * @param mType the type
   * @param symbol the symbol
   * @return the collection
   * @throws IOException
   */
  @Override
  public List<GenomicEntity> getGenes(Genome genome, String id)
      throws IOException {
    return mIdMap.get(sanitize(id));
  }

  @Override
  public List<GenomicEntity> getGenes() throws IOException {
    return mSearch.getFeatures();
  }

  @Override
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
  @Override
  public List<GenomicEntity> findGenes(Genome genome, GenomicRegion region)
      throws IOException {
    return mSearch.getOverlappingFeatures(region, 10).toList();
  }

  /**
   * Find closest genes.
   *
   * @param region the region
   * @return the list
   * @throws IOException
   */
  @Override
  public List<GenomicEntity> findClosestGenes(Genome genome,
      GenomicRegion region) throws IOException {
    return mSearch.getClosestFeatures(region);
  }

  /*
   * @Override public GenomicEntity findMainVariant(String name) {
   * autoFindMainVariants();
   * 
   * return mSymbolMainVariant.get(name.toUpperCase()); }
   */

  /**
   * Return the set of ids (e.g. RefSeq ids) associated with a given id type.
   *
   * @param type the type
   * @return the ids
   */
  @Override
  public Iterable<String> getIds(String type) {
    return mTypeMap.get(type);
  }

  @Override
  public boolean contains(Chromosome chr) {
    return mSearch.contains(chr);
  }

}