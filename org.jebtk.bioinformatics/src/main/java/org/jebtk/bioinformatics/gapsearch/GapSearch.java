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
package org.jebtk.bioinformatics.gapsearch;

import java.util.Collections;
import java.util.List;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.collections.UniqueArrayList;

/**
 * Generic interface for quickly searching for features by genomic location.
 *
 * @author Antony Holmes Holmes
 * @param <T> the generic type
 */
public abstract class GapSearch<T> implements Iterable<Chromosome> {

  /**
   * Adds the feature.
   *
   * @param region the region
   * @param feature the feature
   */
  public abstract void add(GenomicRegion region, T feature);

  /**
   * Size.
   *
   * @return the int
   */
  public abstract int size();

  /**
   * Gets the feature list.
   *
   * @return the feature list
   */
  public abstract List<T> getFeatures();

  /**
   * Gets the feature list.
   *
   * @param region the region
   * @return the feature list
   */
  public List<T> getFeatureSet(GenomicRegion region) {
    List<GappedSearchFeatures<T>> range = getFeatures(region);

    if (range.size() == 0) {
      return Collections.emptyList();
    }

    List<T> ret = new UniqueArrayList<T>();

    for (GappedSearchFeatures<T> features : range) {
      for (GenomicRegion r : features) {
        for (T item : features.getValues(r)) {
          ret.add(item);
        }
      }
    }

    return ret;
  }

  /**
   * Gets the feature list.
   *
   * @param chr the chr
   * @return the feature list
   */
  public abstract List<T> getFeatures(Chromosome chr);

  public abstract boolean contains(Chromosome chr);

  /**
   * Gets the features.
   *
   * @param region the region
   * @return the features
   */
  public List<GappedSearchFeatures<T>> getFeatures(GenomicRegion region) {
    if (region == null) {
      return Collections.emptyList();
    }

    return getFeatures(region.getChr(), region.getStart(), region.getEnd());
  }

  /**
   * Gets the features.
   *
   * @param chr the chr
   * @param start the start
   * @param end the end
   * @return the features
   */
  public abstract List<GappedSearchFeatures<T>> getFeatures(Chromosome chr,
      int start,
      int end);

  public SearchResults<T> getOverlappingFeatures(GenomicRegion region,
      int minBp) {
    SearchResults<T> ret = new SearchResults<T>();

    getOverlappingFeatures(region, minBp, ret);

    return ret;
  }

  public void getOverlappingFeatures(GenomicRegion region,
      int minBp,
      SearchResults<T> ret) {
    List<GappedSearchFeatures<T>> allFeatures = getFeatures(region);

    if (allFeatures.size() == 0) {
      return;
    }

    for (GappedSearchFeatures<T> features : allFeatures) {
      for (GenomicRegion r : features) {
        GenomicRegion overlap = GenomicRegion.overlap(region, r);

        if (overlap == null || (minBp != -1 && overlap.getLength() < minBp)) {
          continue;
        }

        for (T item : features.getValues(r)) {
          ret.add(r, item);
        }
      }
    }
  }

  /**
   * Checks for overlapping features.
   *
   * @param chr the chr
   * @param start the start
   * @param end the end
   * @return true, if successful
   */
  public boolean hasOverlappingFeatures(GenomicRegion region, int minBp) {
    List<GappedSearchFeatures<T>> allFeatures = getFeatures(region);

    if (allFeatures.size() == 0) {
      return false;
    }

    for (GappedSearchFeatures<T> features : allFeatures) {
      for (GenomicRegion r : features) {
        GenomicRegion overlap = GenomicRegion.overlap(region, r);

        if (minBp == -1 || overlap.getLength() >= minBp) {
          return true;
        }
      }
    }

    return false;
  }
}
