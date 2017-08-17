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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.genomic.Gene;
import org.jebtk.core.collections.ArrayListCreator;
import org.jebtk.core.collections.DefaultTreeMap;

// TODO: Auto-generated Javadoc
/**
 * The Class RGeneTree.
 *
 * @param <T> the generic type
 */
public class RGeneTree<T extends Gene> extends RTree<T> {

	/** The m gene map. */
	private Map<String, List<T>> mGeneMap =
			DefaultTreeMap.create(new ArrayListCreator<T>());

	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.rtree.RTree#add(org.jebtk.bioinformatics.genome.GenomicRegion)
	 */
	public void add(T feature) {
		super.add(feature);
		
		mGeneMap.get(feature.getSymbol()).add(feature);
	}
	
	/**
	 * Gets the gene features.
	 *
	 * @param gene the gene
	 * @return the gene features
	 */
	public Collection<T> getGeneFeatures(Gene gene) {
		return getGeneFeatures(gene.getSymbol());
	}
	
	/**
	 * Gets the gene features.
	 *
	 * @param gene the gene
	 * @return the gene features
	 */
	public Collection<T> getGeneFeatures(String gene) {
		return mGeneMap.get(gene);
	}
}
