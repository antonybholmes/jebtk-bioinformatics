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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomeService;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;
import org.jebtk.core.text.TextUtils;

/**
 * Genomic region plus value.
 * 
 * @author Antony Holmes Holmes
 *
 */
public class BedRegion extends UCSCTrackRegion implements Iterable<String> {

  /**
   * The member name.
   */
  protected List<String> mNames;

  /**
   * Instantiates a new bed region.
   *
   * @param region the region
   */
  public BedRegion(GenomicRegion region) {
    this(region.getChr(), region.getStart(), region.getEnd());
  }

  /**
   * Instantiates a new bed region.
   *
   * @param chr the chr
   * @param start the start
   * @param end the end
   */
  public BedRegion(Chromosome chr, int start, int end) {
    this(chr, start, end, GenomicRegion.toLocation(chr, start, end));
  }

  /**
   * Create a BED region with no orientation ('.') so that when visualized, will
   * appear as a solid block.
   *
   * @param chr the chr
   * @param start the start
   * @param end the end
   * @param name the name
   */
  public BedRegion(Chromosome chr, int start, int end, String name) {
    this(chr, start, end, name, Strand.SENSE, null);
  }

  /**
   * Instantiates a new bed region.
   *
   * @param chromosome the chromosome
   * @param start the start
   * @param end the end
   * @param name the name
   * @param strand the strand
   * @param color the color
   */
  public BedRegion(Chromosome chromosome, int start, int end, String name,
      Strand strand, Color color) {
    super(chromosome, start, end, strand, color);

    mNames = TextUtils.scSplit(name);
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return mNames.get(0);
  }

  /**
   * Gets the names.
   *
   * @return the names
   */
  public List<String> getNames() {
    return Collections.unmodifiableList(mNames);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.columbia.rdf.lib.bioinformatics.external.ucsc.UCSCTrackRegion#
   * formattedTxt(java.lang.Appendable)
   */
  @Override
  public void formattedTxt(Appendable buffer) throws IOException {
    super.formattedTxt(buffer);
    buffer.append(TextUtils.TAB_DELIMITER);
    buffer.append(TextUtils.scJoin(mNames));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<String> iterator() {
    return mNames.iterator();
  }

  /**
   * Parses the.
   *
   * @param line the line
   * @return the bed region
   */
  public static BedRegion parse(String genome, String line) {
    // System.err.println("bed: " + line);

    List<String> tokens = TextUtils.tabSplit(line);

    // convert first part to chromosome (replacing x,y and m) {
    Chromosome chr = GenomeService.instance().chr(genome, tokens.get(0));

    if (chr == null) {
      return null;
    }

    // Apply UCSC conventions
    int start = Integer.parseInt(tokens.get(1)) + 1;
    int end = Integer.parseInt(tokens.get(2));

    if (tokens.size() > 8) {
      String name = tokens.get(3);
      Strand strand = Strand.parse(tokens.get(5));

      Matcher matcher = Bed.COLOR_PATTERN.matcher(tokens.get(8));

      Color color = null;

      if (matcher.find()) {
        color = UCSCTrack.parseColor(matcher);
      }

      BedRegion region = new BedRegion(chr, start, end, name, strand, color);

      if (tokens.size() > 11) {
        // blocks mode

        int count = Integer.parseInt(tokens.get(9));

        List<Integer> sizes = TextUtils
            .toInt(TextUtils.commaSplit(tokens.get(10)));
        List<Integer> starts = TextUtils
            .toInt(TextUtils.commaSplit(tokens.get(11)));

        for (int i = 0; i < count; ++i) {
          region.mSubRegions.add(new GenomicRegion(chr, start + starts.get(i),
              start + starts.get(i) + sizes.get(i)));
        }
      }

      return region;
    } else if (tokens.size() > 3) {
      String name = tokens.get(3);

      return new BedRegion(chr, start, end, name);
    } else {
      return new BedRegion(chr, start, end);
    }
  }

  /**
   * Creates the.
   *
   * @param region the region
   * @return the bed region
   */
  public static BedRegion create(GenomicRegion region) {
    return new BedRegion(region);
  }

}
