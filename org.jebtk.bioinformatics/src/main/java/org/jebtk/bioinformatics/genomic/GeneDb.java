/**
 * Copyright 2018 Antony Holmes
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

import org.jebtk.core.NameProperty;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// TODO: Auto-generated Javadoc
/**
 * Represents a gene database specifying a source such as GENCODE and a
 * particular genome.
 */
@JsonPropertyOrder({ "db", "genome" })
public class GeneDb implements NameProperty, Comparable<GeneDb> {

  /** The Constant GENCODE. */
  public static final String GENCODE = "gencode";

  /** The Constant UCSC. */
  public static final String UCSC = "ucsc";

  /** The m name. */
  private String mName;

  /** The m genome. */
  private String mGenome;

  /** The m hash. */
  private int mHash;

  /**
   * Instantiates a new gene db.
   *
   * @param name the name
   * @param genome the genome
   */
  public GeneDb(String name, String genome) {
    mName = name;
    mGenome = genome;
    mHash = (name + genome).hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.core.NameProperty#getName()
   */
  @Override
  @JsonGetter("db")
  public String getName() {
    return mName;
  }

  /**
   * Gets the genome.
   *
   * @return the genome
   */
  @JsonGetter("genome")
  public String getGenome() {
    return mGenome;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return mHash;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(GeneDb o) {
    int ret = mName.compareTo(o.mName);

    if (ret != 0) {
      return ret;
    }

    return mGenome.compareTo(o.mGenome);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof GeneDb) {
      return compareTo((GeneDb) o) == 0;
    } else {
      return false;
    }
  }
}
