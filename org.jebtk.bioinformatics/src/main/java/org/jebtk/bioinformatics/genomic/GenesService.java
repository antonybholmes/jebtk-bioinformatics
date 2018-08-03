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

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jebtk.core.collections.ArrayListCreator;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.IterTreeMap;
import org.jebtk.core.io.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keep track of genes associated with genomes.
 *
 * @author Antony Holmes Holmes
 */
public class GenesService implements Iterable<Entry<GeneDb, Genes>> {

  /**
   * The Class GenesServiceLoader.
   */
  private static class GenesServiceLoader {

    /** The Constant INSTANCE. */
    private static final GenesService INSTANCE = new GenesService();
  }

  /**
   * Gets the single instance of SettingsService.
   *
   * @return single instance of SettingsService
   */
  public static GenesService getInstance() {
    return GenesServiceLoader.INSTANCE;
  }

  /**
   * The constant LOG.
   */
  private static final Logger LOG = LoggerFactory.getLogger(GenesService.class);

  /**
   * The member symbol map.
   */
  private IterMap<GeneDb, Genes> mGenesMap = new IterTreeMap<GeneDb, Genes>();

  /**
   * Track dbs by genome
   */
  private IterMap<String, List<GeneDb>> mGenomeMap = DefaultTreeMap
      .create(new ArrayListCreator<GeneDb>());

  private GeneDb mCurrentDb;

  private GenomeDbGuess mDbGuess = new GenomeDbGuess();

  /**
   * Instantiates a new gene service.
   */
  private GenesService() {
    // do nothing
  }

  public Genes getGenes(GeneDb g) {
    if (mGenesMap.containsKey(g)) {
      return mGenesMap.get(g);
    } else {
      return Genes.EMPTY_GENES;
    }
  }

  /**
   * Return the genes on a particular genome and database.
   *
   * @param genome the genome
   * @param db the db
   * @return the genes
   */
  public Genes getGenes(String db, String genome) {
    return getGenes(new GeneDb(db, genome));
  }

  public boolean contains(String db, String genome) {
    return contains(new GeneDb(db, genome));
  }

  public boolean contains(GeneDb g) {
    return mGenesMap.containsKey(g);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<Entry<GeneDb, Genes>> iterator() {
    return mGenesMap.iterator();
  }

  public void put(Genes genes) {
    for (GeneDb g : genes.getGeneDBs()) {
      put(g, genes);
    }
  }

  public void put(GeneDb g, Genes genes) {
    mGenesMap.put(g, genes);

    mGenomeMap.get(g.getGenome()).add(g);

    mCurrentDb = g;
  }

  /**
   * Put.
   *
   * @param genome the genome
   * @param name the name
   * @param genes the genes
   */
  public void put(String db, String genome, Genes genes) {
    put(new GeneDb(db, genome), genes);
  }

  public GeneDb getCurrentDb() {
    return mCurrentDb;
  }

  /**
   * Gets the genomes.
   *
   * @return the genomes
   */
  public Iterable<GeneDb> getGenomes() {
    return mGenesMap.keySet();
  }

  public String guessDb(String name) {
    return mDbGuess.guess(name);
  }

  public String guessDb(Path file) {
    return guessDb(PathUtils.getName(file));
  }

  /**
   * Return a list of gene dbs for a given genome.
   * 
   * @param genome
   * @return
   */
  public Iterable<GeneDb> getGeneDbs(String genome) {
    return mGenomeMap.get(genome);
  }

  /**
   * Returns the first available gene database for a genome. This is a helper
   * method for when it is desirable to get gene metadata where position is not
   * important.
   * 
   * @param genome
   * @return
   */
  public GeneDb getFirstGeneDb(String genome) {
    if (mGenomeMap.get(genome).size() > 0) {
      return mGenomeMap.get(genome).get(0);
    } else {
      return null;
    }
  }
}