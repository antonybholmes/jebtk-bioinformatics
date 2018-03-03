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
package org.jebtk.bioinformatics.dna;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.genomic.GenomeAssembly;
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
public class GenomeAssemblyZip extends GenomeAssemblyFS {

  public GenomeAssemblyZip(Path directory) {
    super(directory);
  }

  @Override
  public String getName() {
    return "zip";
  }

  @Override
  public List<String> getGenomes() throws IOException {

    List<Path> files = FileUtils.ext(mDirectory, "zip");

    List<String> ret = new ArrayList<String>(files.size());

    for (Path file : files) {
      ret.add(PathUtils.getNameNoExt(file));
    }

    return ret;
  }

  @Override
  protected void createGenomeEntry(String genome,
      Path dir,
      Map<String, GenomeAssembly> map) {
    if (!map.containsKey(genome)) {
      // Path d = dir.resolve(genome);

      Path zip = dir.resolve(genome + ".zip");

      map.put(genome, new GenomeAssemblyExt2BitZip(zip));
    }
  }
}
