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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.TreeMapCreator;
import org.jebtk.core.collections.UniqueArrayList;


// TODO: Auto-generated Javadoc
/**
 * Use fixed size blocks to find features.
 *
 * @author Antony Holmes Holmes
 * @param <T> the generic type
 */
public class FixedGapSearch<T> extends GapSearch<T> {
	
	/**
	 * The constant DEFAULT_BIN_SIZE.
	 */
	protected static final int DEFAULT_BIN_SIZE = 10000;


	/**
	 * The member features.
	 */
	protected Map<Chromosome, Map<Integer, GappedSearchFeatures<T>>> mFeatures =
			DefaultTreeMap.create(new TreeMapCreator<Integer, GappedSearchFeatures<T>>());


	/**
	 * The member size.
	 */
	protected int mSize = 0;
	
	/**
	 * The member bin size.
	 */
	protected int mBinSize;

	/**
	 * Instantiates a new fixed gap search.
	 */
	public FixedGapSearch() {
		this(DEFAULT_BIN_SIZE);
	}
	
	/**
	 * Instantiates a new fixed gap search.
	 *
	 * @param binSize the bin size
	 */
	public FixedGapSearch(int binSize) {
		mBinSize = Math.max(1, binSize);
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.gapsearch.GapSearch#addFeature(edu.columbia.rdf.lib.bioinformatics.genome.Chromosome, int, int, java.lang.Object)
	 */
	@Override
	public void addFeature(Chromosome chr, int start, int end, T feature) {
		int startBin = start / mBinSize;
		int endBin = end / mBinSize;

		for (int bin = startBin; bin <= endBin; ++bin) {
			if (!mFeatures.get(chr).containsKey(bin)) {
				mFeatures.get(chr).put(bin, new GappedSearchFeatures<T>(bin));
			}
			
			mFeatures.get(chr).get(bin).add(feature);
		}

		++mSize;
	}
	
	/**
	 * Adds the feature.
	 *
	 * @param chr the chr
	 * @param bin the bin
	 * @param feature the feature
	 */
	public void addFeature(Chromosome chr, int bin, T feature) {
		if (!mFeatures.get(chr).containsKey(bin)) {
			mFeatures.get(chr).put(bin, new GappedSearchFeatures<T>(bin));
		}
		
		mFeatures.get(chr).get(bin).add(feature);

		++mSize;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.gapsearch.GapSearch#size()
	 */
	@Override
	public int size() {
		return mSize;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.gapsearch.GapSearch#getFeatureList()
	 */
	@Override
	public List<T> getFeatures() {
		List<T> ret = new UniqueArrayList<T>();

		for (Chromosome chr : mFeatures.keySet()) {
			for (int start : mFeatures.get(chr).keySet()) {
				for (T item : mFeatures.get(chr).get(start)) {
					ret.add(item);
				}
			}
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.gapsearch.GapSearch#getFeatureList(edu.columbia.rdf.lib.bioinformatics.genome.Chromosome)
	 */
	@Override
	public List<T> getFeatures(Chromosome chr) {
		List<T> ret = new UniqueArrayList<T>();

		for (int start : mFeatures.get(chr).keySet()) {
			for (T item : mFeatures.get(chr).get(start)) {
				ret.add(item);
			}
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.columbia.rdf.lib.bioinformatics.gapsearch.GapSearch#getFeatures(edu.columbia.rdf.lib.bioinformatics.genome.Chromosome, int, int)
	 */
	@Override
	public List<GappedSearchFeatures<T>> getFeatures(Chromosome chr, 
			int start, 
			int end) {
		int is = start / mBinSize;
		int ie = end / mBinSize;
		
		return getFeaturesByBin(chr, is, ie);
	}
	
	/**
	 * Gets the features by bin.
	 *
	 * @param chr the chr
	 * @param sbin the sbin
	 * @param ebin the ebin
	 * @return the features by bin
	 */
	public List<GappedSearchFeatures<T>> getFeaturesByBin(Chromosome chr, 
			int sbin,
			int ebin) {
		Map<Integer, GappedSearchFeatures<T>> features = mFeatures.get(chr);

		if (features.size() == 0) {
			return Collections.emptyList();
		}
		
		List<GappedSearchFeatures<T>> range = 
				new ArrayList<GappedSearchFeatures<T>>();

		for (int i = sbin; i <= ebin; ++i) {
			GappedSearchFeatures<T> f = features.get(i);

			if (f != null) {
				range.add(f);
			}
		}

		return range;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Chromosome> iterator() {
		return mFeatures.keySet().iterator();
	}

	/**
	 * Adds the.
	 *
	 * @param gappedSearch the gapped search
	 */
	public void add(FixedGapSearch<T> gappedSearch) {
		for (Chromosome chr : gappedSearch.mFeatures.keySet()) {
			for (int bin : gappedSearch.mFeatures.get(chr).keySet()) {
				for (T item : gappedSearch.mFeatures.get(chr).get(bin)) {
					addFeature(chr, bin, item);
				}
			}
		}
	}
}
