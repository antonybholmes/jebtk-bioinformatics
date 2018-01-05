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
package org.jebtk.bioinformatics.search;

import org.jebtk.bioinformatics.genomic.Chromosome;

// TODO: Auto-generated Javadoc
/**
 * Represents a named genomic feature on a chromosome.
 *
 * @author Antony Holmes Holmes
 *
 */
public class Feature implements Comparable<Feature> {

  /**
   * The member name.
   */
  private String mName = null;

  /**
   * The member chr.
   */
  private Chromosome mChr;

  /**
   * The member start.
   */
  private int mStart = -1;

  /**
   * The member end.
   */
  private int mEnd = -1;

  /**
   * Instantiates a new feature.
   *
   * @param name
   *          the name
   * @param chromosome
   *          the chromosome
   * @param start
   *          the start
   * @param end
   *          the end
   */
  public Feature(String name, Chromosome chromosome, int start, int end) {
    mName = name;
    mChr = chromosome;
    mStart = start;
    mEnd = end;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public final String getName() {
    return mName;
  }

  /**
   * Gets the chromosome.
   *
   * @return the chromosome
   */
  public final Chromosome getChromosome() {
    return mChr;
  }

  /**
   * Gets the start.
   *
   * @return the start
   */
  public final int getStart() {
    return mStart;
  }

  /**
   * Gets the end.
   *
   * @return the end
   */
  public final int getEnd() {
    return mEnd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return mName + "_" + mStart;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o) {
    if (!(o instanceof Feature)) {
      return false;
    }

    return (toString().equals(o.toString()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Feature f) {
    return toString().compareTo(f.toString());
  }
}
