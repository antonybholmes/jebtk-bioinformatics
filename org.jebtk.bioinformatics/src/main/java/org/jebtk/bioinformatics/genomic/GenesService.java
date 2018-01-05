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

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeMapCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Keep track of genes associated with genomes.
 *
 * @author Antony Holmes Holmes
 */
public class GenesService implements Iterable<String> {

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
  private IterMap<String, IterMap<String, Genes>> mGenesMap = DefaultTreeMap
      .create(new TreeMapCreator<String, Genes>());

  private String mCurrentDb;

  /**
   * Instantiates a new gene entrez service.
   */
  private GenesService() {
    // do nothing
  }

  /**
   * Get the genes associated with the current database. If there is more than one
   * database associated with the genome, the first is chosen alphabetically by
   * name.
   * 
   * @param genome
   * @return
   */
  public Genes getGenes(String genome) {
    return getGenes(genome, mGenesMap.get(genome).first());
  }

  /**
   * Return the genes on a particular genome and database.
   *
   * @param genome
   *          the genome
   * @param db
   *          the db
   * @return the genes
   */
  public Genes getGenes(String genome, String db) {
    if (!mGenesMap.containsKey(genome) || !mGenesMap.get(genome).containsKey(db)) {
      return Genes.EMPTY_GENES;
    }

    return mGenesMap.get(genome).get(db);
  }

  public boolean containsGenes(String genome, String db) {
    return mGenesMap.containsKey(genome) && mGenesMap.get(genome).containsKey(db);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<String> iterator() {
    return mGenesMap.iterator();
  }

  /**
   * Gets the names.
   *
   * @param genome
   *          the genome
   * @return the names
   */
  public Iterable<String> getNames(String genome) {
    return mGenesMap.get(genome);
  }

  /**
   * Put.
   *
   * @param genome
   *          the genome
   * @param name
   *          the name
   * @param genes
   *          the genes
   */
  public void put(String genome, String db, Genes genes) {
    mGenesMap.get(genome).put(db, genes);

    mCurrentDb = db;
  }

  /**
   * Gets the genomes.
   *
   * @return the genomes
   */
  public Collection<String> getGenomes() {
    return mGenesMap.keySet();
  }

  public String getCurrentDb() {
    return mCurrentDb;
  }

  public String getCurrentDb(String genome) {
    return mGenesMap.get(genome).first();
  }

}