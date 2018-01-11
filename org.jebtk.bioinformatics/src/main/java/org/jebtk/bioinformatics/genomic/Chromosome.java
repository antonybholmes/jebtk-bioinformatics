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

import org.jebtk.core.IdProperty;
import org.jebtk.core.NameProperty;
import org.jebtk.core.text.TextUtils;

// TODO: Auto-generated Javadoc
/**
 * Represents a chromosome that can be sorted.
 *
 * @author Antony Holmes Holmes
 */
public class Chromosome
    implements Comparable<Chromosome>, IdProperty, NameProperty {

  /**
   * Represents an invalid chromosome.
   */
  public static final Chromosome NO_CHR = new Chromosome(-1, "U");

  /**
   * The member chr.
   */
  private String mChr = null;

  /** The m short name. */
  private String mShortName;
  // private String mSpecies;

  /** The m id. */
  private int mId;

  /**
   * Instantiates a new chromosome.
   *
   * @param chr the chr
   * @param parser the parser
   */
  protected Chromosome(int id, String shortName) {
    // mSpecies = parser.getSpecies();

    // Ensures chromosome always begins with chr prefix and not cHr or
    // some other variant

    // The suffix of the chromosome without the chr prefix.
    mShortName = shortName;

    mId = id;

    mChr = "chr"
        + mShortName.toUpperCase().replaceAll("CHR", TextUtils.EMPTY_STRING);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.IdProperty#getId()
   */
  @Override
  public int getId() {
    return mId;
  }

  // public String getSpecies() {
  // return mSpecies;
  // }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.NameProperty#getName()
   */
  @Override
  public String getName() {
    return mChr;
  }

  /**
   * Gets the short name.
   *
   * @return the short name
   */
  public String getShortName() {
    return mShortName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mChr;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Chromosome c) {
    // System.err.println("compare chr " + mId + " " + c.mId + " " + mChr + " "
    // +
    // c.mChr);

    if (mId != -1 && c.mId != -1) {
      if (mId > c.mId) {
        return 1;
      } else if (mId < c.mId) {
        return -1;
      } else {
        return 0;
      }
    } else {
      return mChr.compareTo(c.mChr);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Chromosome) {
      return compareTo((Chromosome) o) == 0;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return mChr.hashCode();
  }

  /**
   * Returns true if a string starts with chr (case insensitive).
   *
   * @param value the value
   * @return true, if is chr
   */
  public static boolean isChr(String value) {
    if (value == null) {
      return false;
    }

    return value.toLowerCase().startsWith("chr");
  }
}
