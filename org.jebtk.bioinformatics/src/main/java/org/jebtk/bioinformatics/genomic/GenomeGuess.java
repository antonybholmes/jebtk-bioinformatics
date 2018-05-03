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

/**
 * The Class ChrSpeciesGuess guess the species from a name.
 */
public class GenomeGuess {

  /**
   * Guess.
   *
   * @param id the id
   * @return the string
   */
  public String guess(String id) {
    String lid = id.toLowerCase();

    if (lid.contains("mm10")) {
      return "mm10";
    } else if (lid.contains("grcm38")) {
      return "grcm38";
    } else if (lid.contains("grch38")) {
      return "grch38";
    } else if (lid.contains("hg18")) {
      return "hg18";
    } else if (lid.contains("mouse")) {
      return "mm10";
    } else {
      return "hg19";
    }
  }
}
