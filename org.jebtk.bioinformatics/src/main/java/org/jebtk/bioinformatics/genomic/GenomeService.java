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
   * Gets the single instance of SettingsService.
   *
   * @return single instance of SettingsService
   */
  public static GenomeService getInstance() {
    return GenomeLoader.INSTANCE;
  }
  
  private static final Logger LOG = LoggerFactory.getLogger(GenomeService.class);


  private static final Path RES_DIR = PathUtils.getPath("res/genomes");

  private static final String EXT = "genome.txt.gz";

  private IterMap<String, Genome> mGenomeMap = 
      new IterTreeMap<String, Genome>();

  private GenomeGuess mGenomeGuess = new GenomeGuess();
  
  private boolean mAutoLoad = true;

  /**
   * Instantiates a new chromosomes.
   */
  private GenomeService() {
    // Do nothing
    
    // Load a default, which can be replaced if desired.
    add(Chromosomes.HG19);
  }

  private void autoLoad() throws IOException {
    if (mAutoLoad ) {
      List<Path> files = FileUtils.ls(RES_DIR, true, false, true);

      for (Path file : files) {
        if (PathUtils.getName(file).endsWith(EXT)) {
          LOG.info("Loading genome info from {}", file);
          
          load(file);
        }
      }
      
      mAutoLoad = false;
    }
  }
  
  public void load(Path file) throws IOException {
    add(Chromosomes.parse(file));
  }
  
  public void add(Chromosomes chrs) {
    LOG.info("Add genome {} {}", chrs.getGenome(), chrs.getSpecies());
    
     mGenomeMap.put(formatKey(chrs.getGenome()), new Genome(chrs));
  }
  
  public Genome genome(String genome) {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return mGenomeMap.get(formatKey(genome));
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
    return mGenomeMap.keySet().iterator();
  }

  private String formatKey(String key) {
    return key.toLowerCase();
  }
}
