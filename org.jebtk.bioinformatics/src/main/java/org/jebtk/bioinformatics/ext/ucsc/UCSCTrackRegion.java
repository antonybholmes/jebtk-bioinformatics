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
package org.jebtk.bioinformatics.ext.ucsc;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;
import org.jebtk.core.text.TextUtils;

/**
 * A track consists of regions.
 *
 * @author Antony Holmes Holmes
 */
public class UCSCTrackRegion extends GenomicRegion {
  /**
   * The member sub regions.
   */
  protected List<GenomicRegion> mSubRegions = new ArrayList<GenomicRegion>();

  /**
   * The member strand.
   */
  protected Strand mStrand = Strand.SENSE;

  /**
   * The member color.
   */
  private Color mColor;

  /**
   * Instantiates a new UCSC track region.
   *
   * @param chromosome the chromosome
   * @param start the start
   * @param end the end
   */
  public UCSCTrackRegion(Chromosome chromosome, int start, int end) {
    this(chromosome, start, end, Strand.SENSE, null);
  }

  /**
   * Instantiates a new UCSC track region.
   *
   * @param chromosome the chromosome
   * @param start the start
   * @param end the end
   * @param strand the strand
   * @param color the color
   */
  public UCSCTrackRegion(Chromosome chromosome, int start, int end,
      Strand strand, Color color) {
    super(chromosome, start, end);

    mStrand = strand;

    setColor(color);
  }

  /**
   * Gets the strand.
   *
   * @return the strand
   */
  public Strand getStrand() {
    return mStrand;
  }

  /**
   * Gets the color.
   *
   * @return the color
   */
  public Color getColor() {
    return mColor;
  }

  /**
   * Sets the color.
   *
   * @param color the new color
   */
  public void setColor(Color color) {
    mColor = color;
  }

  /**
   * Gets the sub regions.
   *
   * @return the sub regions
   */
  public List<GenomicRegion> getSubRegions() {
    return mSubRegions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.columbia.rdf.lib.bioinformatics.genome.GenomicRegion#toString()
   */
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();

    try {
      formattedTxt(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return buffer.toString();
  }

  /**
   * Formatted txt.
   *
   * @return the string
   */
  public String formattedTxt() {
    StringBuilder buffer = new StringBuilder();

    try {
      formattedTxt(buffer);
      buffer.append(TextUtils.NEW_LINE);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return buffer.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.columbia.rdf.lib.bioinformatics.genome.GenomicRegion#formattedTxt(java.
   * lang.Appendable)
   */
  @Override
  public void formattedTxt(Appendable buffer) throws IOException {
    buffer.append(mChr.toString());
    buffer.append(TextUtils.TAB_DELIMITER);
    buffer.append(Integer.toString(mStart));
    buffer.append(TextUtils.TAB_DELIMITER);
    buffer.append(Integer.toString(mEnd));
    // buffer.append(TextUtils.TAB_DELIMITER);
    // buffer.append(mStrand);
  }
}
