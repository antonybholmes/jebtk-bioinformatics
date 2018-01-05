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
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Service for extracting DNA from sequences.
 *
 * @author Antony Holmes Holmes
 */
public class GenomeAssemblyService extends GenomeAssembly {

  /**
   * The Class GenomeAssemblyServiceLoader.
   */
  private static class GenomeAssemblyServiceLoader {

    /** The Constant INSTANCE. */
    private static final GenomeAssemblyService INSTANCE = new GenomeAssemblyService();
  }

  /**
   * Gets the single instance of GenomeAssemblyService.
   *
   * @return single instance of GenomeAssemblyService
   */
  public static GenomeAssemblyService getInstance() {
    return GenomeAssemblyServiceLoader.INSTANCE;
  }

  /** The m assemblies. */
  private List<GenomeAssembly> mAssemblies = new ArrayList<GenomeAssembly>();

  @Override
  public String getName() {
    return "genome-service";
  }

  /**
   * Adds the.
   *
   * @param assembly
   *          the assembly
   */
  public void add(GenomeAssembly assembly) {
    mAssemblies.add(assembly);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.bioinformatics.genome.GenomeAssembly#getSequence(java.lang.String,
   * org.jebtk.bioinformatics.genome.GenomicRegion, boolean,
   * org.jebtk.bioinformatics.genome.RepeatMaskType)
   */
  @Override
  public SequenceRegion getSequence(String genome, GenomicRegion region, boolean displayUpper,
      RepeatMaskType repeatMaskType) throws IOException {
    SequenceRegion ret = null;

    // Iterate over all assemblies until one works.

    for (GenomeAssembly a : mAssemblies) {
      try {
        // System.err.println(a);

        ret = a.getSequence(genome, region, displayUpper, repeatMaskType);
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (ret != null) {
        break;
      }
    }

    return ret;
  }

}