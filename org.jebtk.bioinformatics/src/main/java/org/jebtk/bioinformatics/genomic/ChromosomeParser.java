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

import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ChromosomeParser.
 */
public abstract class ChromosomeParser {

  /**
   * Cleans up the chr name and returns the short name variant where the chr
   * prefix is removed to leave just the number or letter.
   *
   * @param chr the chr
   * @return the short name
   */
  public String getShortName(String chr) {
    return chr.toUpperCase().replace("CHROMOSOME", TextUtils.EMPTY_STRING)
        .replace("CHR_", TextUtils.EMPTY_STRING)
        .replace("CHR-", TextUtils.EMPTY_STRING)
        .replace("CHR", TextUtils.EMPTY_STRING)
        .replaceFirst("P.*", TextUtils.EMPTY_STRING)
        .replaceFirst("Q.*", TextUtils.EMPTY_STRING);
  }

  /**
   * Gets the id.
   *
   * @param chr the chr
   * @return the id
   */
  public abstract int getId(String chr);

  /**
   * Gets the species.
   *
   * @return the species
   */
  public abstract String getSpecies();

  public Chromosome parse(String chr) {
    int id = getId(chr);
    String shortName = getShortName(chr);

    return new Chromosome(id, shortName);
  }

  /**
   * Returns an integer value for the chromosome.
   * 
   * @param chr
   * @return
   */
  public abstract int valueOf(Chromosome chr);

  public abstract int randChrId();

  public Chromosome randChr() {
    return parse("chr" + randChrId());
  }
}
