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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jebtk.core.IdProperty;
import org.jebtk.core.NameProperty;
import org.jebtk.core.text.TextUtils;

/**
 * Represents a chromosome that can be sorted.
 *
 * @author Antony Holmes
 */
public class Chromosome
implements Comparable<Chromosome>, IdProperty, NameProperty {

  public static final Pattern CHR_REGEX = Pattern
      .compile("chr(?:[1-9][0-9]?|[XYM])");

  public static final Pattern CHR_GROUP_REGEX = Pattern
      .compile("(chr(?:[1-9][0-9]?|[XYM]))");

  public static final Pattern CHR_NUM_GROUP_REGEX = Pattern
      .compile("([1-9][0-9]*)");

  /**
   * Max number of a numerical chr (2^24)
   */
  public static final int MAX_NUM_CHR = 16777216;

  /**
   * Represents an invalid chromosome.
   */
  public static final Chromosome NO_CHR = new Chromosome(-1, "chrU", "U", null, -1);

  public static final ChrParser DEFAULT_PARSER = new ChrParser();

  public static final ChrParser HUMAN_PARSER = new HumanChrParser();

  public static final ChrParser MOUSE_PARSER = new MouseChrParser();

  /**
   * The member chr.
   */
  public final String mChr;

  /** The m short name. */
  public final String mShortName;
  // private String mSpecies;

  /** The m id. */
  public final int mId;

  public final int mSize;

  public final Genome mGenome;

  /**
   * 
   * @param id          Numerical id of chr (for fast sorting).
   * @param chr         Chromosome string, e.g. "chr10".
   * @param shortName   Chromosome name without chr prefix, e.g "10".
   * @param genome      Genome which this chromosome belongs.
   * @param size        Size of chromosome in bp.
   */
  private Chromosome(int id, String chr, String shortName, Genome genome, int size) {
    // mSpecies = parser.getSpecies();

    // The suffix of the chromosome without the chr prefix.
    mId = id;
    mChr = chr;
    mShortName = shortName;
    mSize = size;

    mGenome = genome;
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

  public Genome getGenome() {
    return mGenome;
  }

  /**
   * Returns the size of the chromosome in bp or -1 if this has not been
   * specified.
   * 
   * @return
   */
  public int getSize() {
    return mSize;
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
    // + c.mChr);

    // Check if genomes match and if not sort by genome

    if (mGenome != null && c.mGenome != null) {
      int ret = mGenome.compareTo(c.mGenome);

      if (ret != 0) {
        return ret;
      }
    }

    // ret = mChr.compareTo(c.mChr);

    // if (ret != 0) {
    // return ret;
    // } else {
    // Sort by id so that chr2 comes before chr10

    //if (mId != -1 && c.mId != -1) {
    // Compare by id for numerical sorting

    if ((mId < MAX_NUM_CHR && c.mId < MAX_NUM_CHR) || 
        (mId >= MAX_NUM_CHR && c.mId >= MAX_NUM_CHR)) {

      // If both ids are in the lower 24 bits (numerical chr) or the upper 8
      // bits (X, Y, M etc) then do simple numerical comparison.

      if (mId > c.mId) {
        return 1;
      } else if (mId < c.mId) {
        return -1;
      } else {
        return 0;
      }
    } else if (mId < MAX_NUM_CHR) {
      // c is a letter so this chr is numerical and should come first.
      return -1;
    } else {
      // This chr is a letter so c is numerical so should come second.
      return 1;
    }
    //} else {
    // If chr has a non numerical suffix (e.g. chrX), do a conventional
    // text comparison
    //  return mChr.compareTo(c.mChr);
    //}
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
  public static boolean isChr(String s) {
    if (s == null) {
      return false;
    }

    return CHR_REGEX.matcher(s).matches(); // value.toLowerCase().startsWith("chr");
  }


  public static String search(String s) {
    if (s == null) {
      return null;
    }

    Matcher matcher = CHR_GROUP_REGEX.matcher(s);

    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  /**
   * Cleans up the chr name and returns the short name variant where the chr
   * prefix is removed to leave just the number or letter.
   *
   * @param chr the chr
   * @return the short name
   */
  public static String getShortName(String chr) {
    return chr.toUpperCase()
        //.replace("CHROMOSOME", TextUtils.EMPTY_STRING)
        .replace("CHR_", TextUtils.EMPTY_STRING)
        .replace("CHR-", TextUtils.EMPTY_STRING)
        .replace("CHR", TextUtils.EMPTY_STRING);
    // .replaceFirst("P.*", TextUtils.EMPTY_STRING)
    // .replaceFirst("Q.*", TextUtils.EMPTY_STRING);
  }

  public static Chromosome newChr(String chr) {
    return newChr(chr, null);
  }

  public static Chromosome newChr(String chr, Genome genome) {
    return newChr(chr, genome, -1);
  }

  public static Chromosome newChr(String chr, int size) {
    return newChr(chr, null, size);
  }

  public static Chromosome newChr(String chr, Genome genome, int size) {
    return newChr(chr, genome, size, DEFAULT_PARSER);
  }

  public static Chromosome newHumanChr(String chr, Genome genome, int size) {
    return newChr(chr, genome, size, HUMAN_PARSER);
  }

  public static Chromosome newMouseChr(String chr, Genome genome, int size) {
    return newChr(chr, genome, size, MOUSE_PARSER);
  }

  public static Chromosome newChr(String chr, Genome genome, int size, ChrParser parser) {
    String shortName = getShortName(chr);
    int id = parser.getId(shortName);
    chr = "chr" + shortName;

    return new Chromosome(id, chr, shortName, genome, size);

  }
}
