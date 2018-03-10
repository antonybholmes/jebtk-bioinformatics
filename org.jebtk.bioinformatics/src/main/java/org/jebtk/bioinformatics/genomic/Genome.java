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

import org.jebtk.core.AppService;
import org.jebtk.core.NameProperty;

// TODO: Auto-generated Javadoc
/**
 * The enum Genome.
 */
public class Genome implements NameProperty {

  /**
   * The H g18.
   */
  public static final String HG18 = "hg18";

  /** The Constant HG19. */
  public static final String HG19 = "hg19";
  
  public static final String GRCH38 = "grch38";

  /** The Constant MM10. */
  public static final String MM10 = "mm10";
  
  public static final String GRCM38 = "grcm38";
  
  public static final Path GENOME_HOME = AppService.RES_HOME.resolve("genomes");
  
  /** Local genome dir of app */
  public static final Path GENOME_DIR = AppService.RES_DIR.resolve("genomes");

  private Chromosomes mChrs;

  private String mName;
  
  public Genome(String name) {
    mName = name;
  }

  @Override
  public String getName() {
    return mName;
  }
  
  public String getGenome() {
    return getName();
  }

  public Chromosomes chrs() {
    if (mChrs == null) {
      mChrs = new Chromosomes(mName);
    }
    
    return mChrs;
  }
  
  /**
   * Invalidate the cache so it is rebuilt.
   */
  public void cache() {
    //mChrs.cache();
    mChrs = null;
  }
  
  public Chromosome chr(String chr) {
    return chrs().chr(chr);
  }
  
  public Chromosome chr(int chr) {
    return chrs().chr(chr);
  }
  
  public Chromosome chr(Chromosome chr) {
    return chr(chr.toString());
  }
  
  public Chromosome randChr() {
    return chrs().randChr();
  }
}
