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

import org.jebtk.core.Mathematics;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ChrParserHuman.
 */
public class ChrParserHuman extends ChromosomeParser {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.bioinformatics.genome.ChromosomeParser#getShortName(java.lang.
   * String)
   */
  @Override
  public String getShortName(String chr) {
    String ret = super.getShortName(chr);

    ret = ret.replace("23", "X").replace("24", "Y").replace("25", "M");

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jebtk.bioinformatics.genome.ChromosomeParser#getId(java.lang.String)
   */
  @Override
  public int getId(String chr) {
    String shortName = getShortName(chr);

    if (TextUtils.isInt(shortName)) {
      return Integer.parseInt(shortName);
    } else if (shortName.equals("X")) {
      return 23;
    } else if (shortName.equals("Y")) {
      return 24;
    } else if (shortName.equals("M")) {
      return 25;
    } else {
      return -1;
    }
  }

  @Override
  public int valueOf(Chromosome chr) {
    if (chr.toString().endsWith("X")) {
      return 23;
    } else if (chr.toString().endsWith("Y")) {
      return 24;
    } else if (chr.toString().endsWith("M")) {
      return 25;
    } else {
      return Integer.parseInt(chr.getShortName());
    }
  }

  @Override
  public int randChrId() {
    return Mathematics.rand(25) + 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.ChromosomeParser#getSpecies()
   */
  @Override
  public String getSpecies() {
    return "human";
  }

}
