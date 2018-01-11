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

// TODO: Auto-generated Javadoc
/**
 * The Class ChrSpeciesGuess guess the species from a name.
 */
public class ChrSpeciesGuess {

  /**
   * Guess.
   *
   * @param id the id
   * @return the string
   */
  public String guess(String id) {
    String lid = id.toLowerCase();

    if (lid.contains("mm10") || lid.contains("grcm38")
        || lid.contains("mouse")) {
      return "mouse";
    } else {
      return "human";
    }
  }
}
