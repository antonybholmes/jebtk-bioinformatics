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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.IterTreeMap;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Deals with functions related to chromosomes.
 *
 * @author Antony Holmes Holmes
 *
 */
public class GenomeService implements Iterable<String> {
  /**
   * The Class ChromosomesLoader.
   */
  private static class GenomeLoader {

    /** The Constant INSTANCE. */
    private static final GenomeService INSTANCE = new GenomeService();
  }

  /**
   * Gets the single instance of GenomeService.
   *
   * @return single instance of GenomeService.
   */
  public static GenomeService instance() {
    return GenomeLoader.INSTANCE;
  }

  private static final Logger LOG = LoggerFactory.getLogger(GenomeService.class);



  //private static final String EXT2 = "genome.txt.gz";

  private IterMap<String, Genome> mGenomeMap = 
      new IterTreeMap<String, Genome>();

  private GenomeGuess mGenomeGuess = new GenomeGuess();


  /**
   * Directories to search.
   */
  private List<Path> mDirs = new ArrayList<Path>();



  private boolean mAutoLoad = true;

  /**
   * Instantiates a new chromosomes.
   */
  private GenomeService() {
    // Do nothing

    mDirs.add(Genome.GENOME_HOME);
    mDirs.add(Genome.GENOME_DIR);
  }

  /**
   * Set the directory where to search for genomes.
   * 
   * @param dir
   */
  public void setDir(Path dir) {
    mDirs.add(dir);
  }

  /**
   * Get a genome. Service will attempt to auto discover genome data and
   * populate the genome object. If no data exists, an empty genome will be
   * created.
   * 
   * @param genome
   * @return
   */
  public Genome genome(String genome) {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }

    //String fg = formatKey(genome);

    if (!mGenomeMap.containsKey(genome)) {
      // If the genome does not exist, create one
      mGenomeMap.put(genome, new Genome(genome));
    }

    return mGenomeMap.get(genome);
  }

  private void autoLoad() throws IOException {
    if (mAutoLoad) {
      for (Path dir : mDirs) {
        if (FileUtils.isDirectory(dir)) {
          for (Path subDir : FileUtils.lsdir(dir)) {
            if (FileUtils.isDirectory(subDir)) {
              String genome = PathUtils.getName(subDir);

              if (!mGenomeMap.containsKey(genome)) {
                LOG.info("Discovered genome {} in {}.", genome, subDir);

                mGenomeMap.put(genome, new Genome(genome));
              }
            }
          }
        }
      }

      mAutoLoad = false;
    }
  }

  /**
   * Invalidate the cache so it will be rebuilt.
   */
  public void cache() {
    mAutoLoad = true;
  }

  /**
   * Returns the chr from a given genome reference.
   * 
   * @param genome    The genome, e.g. 'Human'
   * @param chr       The chromosome, e.g. 'chr1'
   * 
   * @return          The chromosome object from the desired genome.
   */
  public Chromosome chr(String genome, String chr) {
    //LOG.info("chr {} {}", genome, chr);

    return genome(genome).chr(chr);
  }

  /**
   * Remap a chromosome to another genome by name.
   * 
   * @param genome
   * @param chr
   * @return
   */
  public Chromosome chr(String genome, Chromosome chr) {
    //LOG.info("chr {} {}", genome, chr);

    return chr(genome, chr.toString());
  }

  public Chromosome hg19(String chr) {
    return chr(Genome.HG19, chr);
  }

  public Chromosome guessChr(String genome, String chr) {
    return chr(guessGenome(genome), chr);
  }

  public Chromosome guessChr(Path file, String chr) {
    return guessChr(PathUtils.getName(file), chr);
  }

  /**
   * Set the object for guessing the genome from an id.
   *  
   * @param guess
   */
  public void setGenomeGuess(GenomeGuess guess) {
    mGenomeGuess = guess;
  }

  public String guessGenome(String name) {
    return mGenomeGuess.guess(name);
  }

  public String guessGenome(Path file) {
    return guessGenome(PathUtils.getName(file));
  }

  public Chromosome randChr(String genome) {
    return genome(genome).randChr();
  }

  @Override
  public Iterator<String> iterator() {
    return mGenomeMap.iterator();
  }

  private static String formatKey(String key) {
    return key.toLowerCase();
  }


}
