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
package org.jebtk.bioinformatics.genomic;

import java.util.Collections;
import java.util.List;

import org.jebtk.bioinformatics.gapsearch.FixedGapSearch;
import org.jebtk.bioinformatics.gapsearch.GappedSearchFeatures;
import org.jebtk.core.collections.UniqueArrayList;

// TODO: Auto-generated Javadoc
/**
 * Improved fixed gap search for dealing with genomic coordinates.
 *
 * @author Antony Holmes Holmes
 * @param <T> the generic type
 */
public class GenomicFixedGapSearch<T extends GenomicRegion> extends FixedGapSearch<T> {

	/**
	 * Overlapping features.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the list
	 */
	public List<T> overlappingFeatures(Chromosome chr, int start, int end) {
		List<GappedSearchFeatures<T>> range = getFeatures(chr, start, end);

		if (range.size() == 0) {
			return Collections.emptyList();
		}

		GenomicRegion r = GenomicRegion.create(chr, start, end);

		List<T> ret = new UniqueArrayList<T>();

		for (GappedSearchFeatures<T> features : range) {
			for (T item : features) {
				if (GenomicRegion.overlaps(item, r)) {
					ret.add(item);
				}
			}
		}

		return ret;
	}

	/**
	 * Checks for overlapping features.
	 *
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return true, if successful
	 */
	public boolean hasOverlappingFeatures(Chromosome chr, int start, int end) {
		List<GappedSearchFeatures<T>> range = getFeatures(chr, start, end);

		if (range.size() == 0) {
			return false;
		}

		GenomicRegion r = GenomicRegion.create(chr, start, end);
		
		for (GappedSearchFeatures<T> features : range) {
			for (T item : features) {
				if (GenomicRegion.overlaps(item, r)) {
					return true;
				}
			}
		}

		return false;
	}
}
