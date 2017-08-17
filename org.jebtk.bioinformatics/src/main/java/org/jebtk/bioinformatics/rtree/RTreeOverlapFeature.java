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
package org.jebtk.bioinformatics.rtree;

import org.jebtk.bioinformatics.genomic.GenomicRegion;

// TODO: Auto-generated Javadoc
/**
 * The Class RTreeOverlapFeature.
 *
 * @param <T> the generic type
 */
public class RTreeOverlapFeature<T extends GenomicRegion> {

	/** The m overlap. */
	private int mOverlap;
	
	/** The m feature. */
	private T mFeature;
	
	/** The m overlap P. */
	private double mOverlapP;

	/**
	 * Instantiates a new r tree overlap feature.
	 *
	 * @param feature the feature
	 * @param overlap the overlap
	 */
	public RTreeOverlapFeature(T feature, int overlap) {
		mFeature = feature;
		
		mOverlap = overlap;
		
		mOverlapP = overlap / (double)feature.getLength();
	}

	/**
	 * Gets the overlap.
	 *
	 * @return the overlap
	 */
	public int getOverlap() {
		return mOverlap;
	}

	/**
	 * Gets the overlap P.
	 *
	 * @return the overlap P
	 */
	public double getOverlapP() {
		return mOverlapP;
	}

	/**
	 * Gets the feature.
	 *
	 * @return the feature
	 */
	public T getFeature() {
		return mFeature;
	}
}
