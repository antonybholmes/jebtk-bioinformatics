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
import java.util.Iterator;
import java.util.List;

import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.IterTreeMap;
import org.jebtk.core.collections.UniqueArrayList;

/**
 * Service for extracting DNA/RNA from sequences.
 *
 * @author Antony Holmes Holmes
 */
public class SequenceReaderService extends SequenceReader
    implements Iterable<String> {

  /**
   * The Class GenomeAssemblyServiceLoader.
   */
  private static class GenomeAssemblyServiceLoader {

    /** The Constant INSTANCE. */
    private static final SequenceReaderService INSTANCE = new SequenceReaderService();
  }

  /**
   * Gets the single instance of GenomeAssemblyService.
   *
   * @return single instance of GenomeAssemblyService
   */
  public static SequenceReaderService instance() {
    return GenomeAssemblyServiceLoader.INSTANCE;
  }

  private List<SequenceReader> mAssemblies = new UniqueArrayList<SequenceReader>();

  private IterMap<String, SequenceReader> mGenomeMap = new IterTreeMap<String, SequenceReader>();

  private boolean mAutoLoad = true;

  public void add(SequenceReader assembly) {
    mAssemblies.add(assembly);

  }

  /**
   * Indicate that the genome references have changed so it they may need to be
   * cached again.
   */
  private void autoLoad() {
    if (mAutoLoad) {
      // One assembly object can load multiple genomes potentially.
      for (SequenceReader assembly : mAssemblies) {
        try {
          for (String genome : assembly.getGenomes()) {
            mGenomeMap.put(genome, assembly);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      mAutoLoad = false;
    }
  }

  public SequenceReader get(String genome) {
    autoLoad();

    return mGenomeMap.get(genome);
  }

  @Override
  public String getName() {
    return "sequence-reader-service";
  }

  @Override
  public Iterator<String> iterator() {
    autoLoad();

    return mGenomeMap.iterator();
  }

  public void cache() {
    mAutoLoad = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomeAssembly#getSequence(java.lang.
   * String, org.jebtk.bioinformatics.genome.GenomicRegion, boolean,
   * org.jebtk.bioinformatics.genome.RepeatMaskType)
   */
  @Override
  public SequenceRegion getSequence(GenomicRegion region,
      boolean displayUpper,
      RepeatMaskType repeatMaskType) {

    String genome = region.getGenome();

    // Iterate over all assemblies until one works.

    SequenceReader a = get(genome);

    if (a == null) {
      return null;
    }

    SequenceRegion ret = null;

    try {
      ret = a.getSequence(region, displayUpper, repeatMaskType);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ret;
  }

}