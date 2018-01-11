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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.collections.UniqueArrayList;

// TODO: Auto-generated Javadoc
/**
 * The class GappedSearchFeatures.
 *
 * @param <T> the generic type
 */
public class SearchResults<T> implements Iterable<GenomicRegion> {

  public static final SearchResults<Object> EMPTY_RESULTS = new SearchResults<Object>();

  @SuppressWarnings("unchecked")
  public static final <T> SearchResults<T> emptyResults() {
    return (SearchResults<T>) EMPTY_RESULTS;
  }

  /**
   * The member features.
   */
  private Map<GenomicRegion, Set<T>> mFeatures = DefaultTreeMap
      .create(new TreeSetCreator<T>());

  /**
   * Adds the.
   *
   * @param feature the feature
   */
  public void add(GenomicRegion region, T feature) {
    mFeatures.get(region).add(feature);
  }

  public Set<T> getValues(GenomicRegion region) {
    return mFeatures.get(region);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<GenomicRegion> iterator() {
    return mFeatures.keySet().iterator();
  }

  /**
   * Size.
   *
   * @return the int
   */
  public int size() {
    return mFeatures.size();
  }

  public List<T> toList() {
    List<T> ret = new UniqueArrayList<T>(mFeatures.size());

    for (GenomicRegion r : this) {
      for (T t : this.getValues(r)) {
        ret.add(t);
      }
    }

    return ret;
  }

  public void addAll(SearchResults<T> s) {
    for (GenomicRegion r : s) {
      mFeatures.get(r).addAll(s.mFeatures.get(r));
    }
  }
}
