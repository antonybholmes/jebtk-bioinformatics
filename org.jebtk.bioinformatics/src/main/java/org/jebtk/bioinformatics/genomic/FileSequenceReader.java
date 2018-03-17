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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.dna.Ext2BitMemSequenceReader;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.PathUtils;

// TODO: Auto-generated Javadoc
/**
 * Encodes DNA in a 2 bit file representing ACGT. All other characters such as N
 * map to A. Bases are encoded in two bits, so 4 bases per byte. A = 0, C = 1, G
 * = 2, T = 3. Files can be accompanied by a corresponding n
 * 
 *
 * @author Antony Holmes Holmes
 *
 */
public class FileSequenceReader extends SequenceReader {

  /** The m map. */
  protected Map<String, SequenceReader> mMap = new HashMap<String, SequenceReader>();

  /** The m directory. */
  protected final List<Path> mDirs = new ArrayList<Path>();

  /**
   * Directory containing genome Paths which must be of the form chr.n.txt. Each
   * Path must contain exactly one line consisting of the entire chromosome.
   *
   * @param directory the directory
   */
  public FileSequenceReader(Path dir, Path... dirs) {
    mDirs.add(dir);

    for (Path d : dirs) {
      mDirs.add(d);
    }
  }

  @Override
  public String getName() {
    return "fs";
  }

  public Path getDir() {
    return mDirs.get(0);
  }

  /**
   * Return the directories to search for assembly files.
   * 
   * @return
   */
  public Iterable<Path> getDirs() {
    return mDirs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomeAssembly#getGenomes()
   */
  @Override
  public List<String> getGenomes() throws IOException {

    List<String> ret = new ArrayList<String>();

    for (Path dir : mDirs) {
      List<Path> subDirs = FileUtils.lsdir(dir);

      for (Path sd : subDirs) {
        ret.add(PathUtils.getName(sd));
      }
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.columbia.rdf.lib.bioinformatics.genome.GenomeAssembly#getSequence(edu.
   * columbia.rdf.lib.bioinformatics.genome.GenomicRegion, boolean,
   * edu.columbia.rdf.lib.bioinformatics.genome.RepeatMaskType)
   */
  @Override
  public final SequenceRegion getSequence(GenomicRegion region,
      boolean displayUpper,
      RepeatMaskType repeatMaskType) throws IOException {
    String genome = region.getGenome();
    
    createGenomeEntry(genome, mMap);

    return mMap.get(genome)
        .getSequence(region, displayUpper, repeatMaskType);
  }

  @Override
  public List<SequenceRegion> getSequences(Collection<GenomicRegion> regions,
      boolean displayUpper,
      RepeatMaskType repeatMaskType) throws IOException {
    String genome = regions.iterator().next().getGenome();
    
    createGenomeEntry(genome, mMap);

    return mMap.get(genome)
        .getSequences(regions, displayUpper, repeatMaskType);
  }

  protected void createGenomeEntry(String genome,
      Map<String, SequenceReader> map) {
    if (!map.containsKey(genome)) {

      for (Path dir : mDirs) {
        if (FileUtils.isDirectory(dir)) {
          Path d = dir.resolve(genome);

          if (FileUtils.isDirectory(d)) {
            map.put(genome, new Ext2BitMemSequenceReader(d)); // new
            // GenomeAssemblyExt2Bit(dir));
          }
        }
      }
    }
  }
}
