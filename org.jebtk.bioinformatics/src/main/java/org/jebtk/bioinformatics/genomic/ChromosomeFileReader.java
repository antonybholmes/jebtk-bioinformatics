/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.bioinformatics.genomic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jebtk.core.io.FileUtils;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ChromosomeParser.
 */
public class ChromosomeFileReader extends GenomeDirs implements ChromosomeReader {

  private static final Logger LOG = LoggerFactory.getLogger(ChromosomeFileReader.class);

  private String mGenome;

  private List<Chromosome> mChrs = new ArrayList<Chromosome>();

  private boolean mAutoLoad = true;

  public static final String EXT = "chrs.gz";

  public ChromosomeFileReader(String genome) {
    this(genome, Genome.GENOME_HOME, Genome.GENOME_DIR);
  }

  public ChromosomeFileReader(String genome, Collection<Path> dirs) {
    super(dirs);
    
    mGenome = genome;
    
    LOG.info("chromosomes {}", dirs);
  }
  
  public ChromosomeFileReader(String genome, Path dir, Path... dirs) {
    super(dir, dirs);
    
    mGenome = genome;
  }

  private void autoLoad() throws IOException {
    if (mAutoLoad) {
      for (Path dir : mDirs) {
        Path genomeDir = dir.resolve(mGenome);
        
        LOG.info("Looking for chromosomes in {}", genomeDir);

        if (FileUtils.isDirectory(genomeDir)) {
          List<Path> files = FileUtils.endsWith(genomeDir, EXT);

          for (Path file : files) {
            load(file);
          }
        }
      }

      Collections.sort(mChrs);

      mAutoLoad = false;
    }
  }

  private void load(Path file) throws IOException {
    LOG.info("Discovered chromosome info in {}.", file);

    load(file, this);
  }

  @Override
  public Iterator<Chromosome> iterator() {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return mChrs.iterator();
  }

  /**
   * Returns the genome reference, for example hg19.
   * 
   * @return
   */
  public String getGenome() {
    return mGenome;
  }

  private static void load(Path file, ChromosomeFileReader ret) throws IOException {

    BufferedReader reader = FileUtils.newBufferedReader(file);

    try {
      // Skip header
      reader.readLine();
      reader.readLine();
      reader.readLine();

      String line;
      List<String> tokens;

      while ((line = reader.readLine()) != null) {
        tokens = TextUtils.tabSplit(line);

        int id = Integer.parseInt(tokens.get(0));
        String name = tokens.get(1);
        int size = Integer.parseInt(tokens.get(2));

        Chromosome chr = new Chromosome(id, name, size, ret.getGenome());

        ret.mChrs.add(chr);
      }
    } finally {
      reader.close();
    }
  }
}
