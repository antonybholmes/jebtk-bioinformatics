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

import org.jebtk.core.Mathematics;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.IterHashMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ChromosomeParser.
 */
public class Chromosomes extends GenomeDirs implements Iterable<Chromosome> {

  private static final Logger LOG = LoggerFactory.getLogger(Chromosomes.class);

  private String mGenome;

  private IterMap<Integer, Chromosome> mChrIdMap = new IterHashMap<Integer, Chromosome>();

  private IterMap<String, Chromosome> mChrMap = new IterHashMap<String, Chromosome>();

  private List<Chromosome> mChrs = new ArrayList<Chromosome>();

  private String mSpecies;

  private boolean mAutoLoad = true;

  public static final String EXT = "chrs.gz";

  public Chromosomes(String genome) {
    this(genome, genome);
  }

  public Chromosomes(String species, String genome) {
    this(species, genome, Genome.GENOME_HOME, Genome.GENOME_DIR);
  }

  public Chromosomes(String genome, Collection<Path> dirs) {
    this(genome, genome, dirs);
  }
  
  public Chromosomes(String species, String genome, Collection<Path> dirs) {
    super(dirs);
    
    mSpecies = species;
    mGenome = genome;
    
    LOG.info("chromosomes {}", dirs);
  }
  
  public Chromosomes(String species, String genome, Path dir, Path... dirs) {
    super(dir, dirs);
    
    mSpecies = species;
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

  public void cache() {
    mAutoLoad = true;
  }

  /**
   * Gets the id.
   *
   * @param chr the chr
   * @return the id
   */
  public int getId(String chr) {
    Chromosome c = chr(chr);

    if (c != null) {
      return c.getId();
    } else {
      return -1;
    }
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

  public Chromosome chr(String chr) {
    // LOG.info("Request {} {}", chr, getMapId(chr));

    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String fc = formatKey(chr);

    if (!mChrMap.containsKey(fc)) {
      add(new Chromosome(-1, chr, mGenome));
    }

    return mChrMap.get(fc);
  }

  public Chromosome chr(int id) {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return mChrIdMap.get(id);
  }

  protected void add(Chromosome chr) {
    mChrIdMap.put(chr.getId(), chr);
    mChrMap.put(Integer.toString(chr.getId()), chr);
    mChrMap.put(chr.getShortName().toUpperCase(), chr);
    mChrs.add(chr);

    mAutoLoad = true;
  }

  /**
   * Returns the genome name, for example 'Human'.
   *
   * @return the genome name.
   */
  public String getSpecies() {
    return mSpecies;
  }

  /**
   * Returns the genome reference, for example hg19.
   * 
   * @return
   */
  public String getGenome() {
    return mGenome;
  }

  public Chromosome randChr() {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<String> ids = CollectionUtils.toList(mChrMap.keySet());

    return mChrMap.get(ids.get(Mathematics.rand(ids.size())));
  }

  public static Chromosomes parse(Path file) throws IOException {
    LOG.info("Reading chromosome info from {}", file);

    BufferedReader reader = FileUtils.newBufferedReader(file);

    Chromosomes ret = null;

    try {
      ret = parse(reader);
    } finally {
      reader.close();
    }

    return ret;
  }

  private static Chromosomes parse(BufferedReader reader) throws IOException {

    // The first token contains the names etc, ignore the rest of the line
    String species = TextUtils.tabSplit(reader.readLine()).get(0);
    String genome = TextUtils.tabSplit(reader.readLine()).get(0);

    Chromosomes ret = new Chromosomes(species, genome);

    // Skip header
    reader.readLine();

    String line;
    List<String> tokens;

    while ((line = reader.readLine()) != null) {
      tokens = TextUtils.tabSplit(line);

      int id = Integer.parseInt(tokens.get(0));
      String name = tokens.get(1);
      int size = Integer.parseInt(tokens.get(2));

      Chromosome chr = new Chromosome(id, name, size, genome);

      ret.add(chr);
    }

    return ret;
  }

  /**
   * Parses the.
   *
   * @param file the file
   * @return the chromosome sizes
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static Chromosomes parseJson(Path file) throws IOException {

    Json json = new JsonParser().parse(file);

    Chromosomes ret = new Chromosomes(json.getAsString("species"),
        json.getAsString("genome"));

    Json chrsJson = json.get("chromosomes");

    for (int i = 0; i < chrsJson.size(); ++i) {
      Json chrJson = chrsJson.get(i);

      int id = chrJson.getAsInt("id");
      String name = chrJson.getAsString("name");
      int size = chrJson.getAsInt("size");

      Chromosome chr = new Chromosome(id, name, size);

      ret.mChrIdMap.put(id, chr);
      ret.mChrMap.put(Integer.toString(id), chr);
      ret.mChrMap.put(chr.getShortName().toUpperCase(), chr);
    }

    return ret;
  }

  private static void load(Path file, Chromosomes ret) throws IOException {

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

        ret.add(chr);
      }
    } finally {
      reader.close();
    }
  }

  private static final String formatKey(String chr) {
    return Chromosome.getShortName(chr).toUpperCase();
  }

}
