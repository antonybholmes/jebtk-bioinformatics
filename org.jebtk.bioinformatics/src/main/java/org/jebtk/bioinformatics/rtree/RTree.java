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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;

// TODO: Auto-generated Javadoc
/**
 * The Class RTree.
 *
 * @param <T> the generic type
 */
public class RTree<T extends GenomicRegion> {
	
	/** The Constant ERR_CODE_WRONG_CHR. */
	public static final int ERR_CODE_WRONG_CHR = -1;
	
	/** The Constant ERR_CODE_NO_OVERLAP. */
	public static final int ERR_CODE_NO_OVERLAP = -2;
	
	/** The Constant ERR_CODE_WRONG_STRAND. */
	public static final int ERR_CODE_WRONG_STRAND = -3;
	
	/** The Constant DEFAULT_BINS. */
	public static final int DEFAULT_BINS = 10;
	
	/** The Constant DEFAULT_MIN_WIDTH. */
	public static final int DEFAULT_MIN_WIDTH = 1000;
	
	/** The Constant DEFAULT_MAX_WIDTH. */
	public static final int DEFAULT_MAX_WIDTH = 1000000000;
	
	
	/** The m tree map. */
	private Map<Chromosome, RChrTree<T>> mTreeMap = 
			new TreeMap<Chromosome, RChrTree<T>>();
		
	/** The m max width. */
	private int mMaxWidth;

	/** The m bins. */
	private int mBins;
	
	/** The m min width. */
	private int mMinWidth;

	/**
	 * Instantiates a new r tree.
	 */
	public RTree() {
		this(DEFAULT_MIN_WIDTH, DEFAULT_MAX_WIDTH, DEFAULT_BINS);
	}
	
	/**
	 * Instantiates a new r tree.
	 *
	 * @param minWidth the min width
	 * @param maxWidth the max width
	 * @param bins the bins
	 */
	public RTree(int minWidth, int maxWidth, int bins) {
		mMinWidth = minWidth;
		mMaxWidth = maxWidth;
		mBins = bins;
	}
	
	/**
	 * Gets the.
	 *
	 * @param chr the chr
	 * @return the r chr tree
	 */
	public RChrTree<T> get(Chromosome chr) {
		if (!mTreeMap.containsKey(chr)) {
			mTreeMap.put(chr, new RChrTree<T>(chr, mMinWidth, mMaxWidth, mBins));
		}
		
		return mTreeMap.get(chr);
	}
	
	/**
	 * Checks for chr.
	 *
	 * @param chr the chr
	 * @return true, if successful
	 */
	public boolean hasChr(Chromosome chr) {
		return mTreeMap.containsKey(chr);
	}
	

	/**
	 * Adds the.
	 *
	 * @param feature the feature
	 */
	public void add(T feature) {
		get(feature.getChr()).add(feature);
	}
	
	
	/**
	 * Search tree overlapping.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param region the region
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<RTreeOverlapFeature<T1>> searchTreeOverlapping(final RTree<T1> tree, 
			final GenomicRegion region) {
		return searchTreeOverlapping(tree, region.getChr(), region.getStart(), region.getEnd());
	}
	
	/**
	 * Search tree overlapping.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<RTreeOverlapFeature<T1>> searchTreeOverlapping(final RTree<T1> tree, 
			final Chromosome chr, 
			int start, 
			int end) {
		List<T1> features = RChrTree.searchTree(tree.get(chr), start, end);
		
		return overlapping(features, chr, start, end);
	}
	
	/**
	 * Search tree.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<T1> searchTree(final RTree<T1> tree, 
			final Chromosome chr, 
			int start, 
			int end) {
		return RChrTree.searchTree(tree.get(chr), start, end);
	}
	
	/**
	 * Search tree.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param region the region
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<T1> searchTree(final RTree<T1> tree, 
			final GenomicRegion region) {
		return searchTree(tree, region.getChr(), region.getStart(), region.getEnd());
	}

	/**
	 * Find closest node.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param chr the chr
	 * @param p the p
	 * @return the r tree node
	 */
	public static <T1 extends GenomicRegion> RTreeNode findClosestNode(final RTree<T1> tree, 
			Chromosome chr, 
			int p) {
		return tree.get(chr).findClosestNode(p);
	}

	/**
	 * Gets the features.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param chr the chr
	 * @param si the si
	 * @param ei the ei
	 * @return the features
	 */
	public static <T1 extends GenomicRegion> List<T1> getFeatures(final RTree<T1> tree, 
			Chromosome chr, 
			int si,
			int ei) {
		return RChrTree.getFeatures(tree.get(chr), si, ei);		
	}

	/**
	 * Overlapping.
	 *
	 * @param <T1> the generic type
	 * @param features the features
	 * @param region the region
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<RTreeOverlapFeature<T1>> overlapping(final List<T1> features, 
			GenomicRegion region) {
		return overlapping(features, 
				region.getChr(), 
				region.getStart(), 
				region.getEnd(),
				region.getStrand());
	}
	
	/**
	 * Overlapping.
	 *
	 * @param <T1> the generic type
	 * @param features the features
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<RTreeOverlapFeature<T1>> overlapping(final List<T1> features, 
			Chromosome chr, 
			int start,
			int end) {
		return overlapping(features, chr, start, end, Strand.NONE);
	}
	
	/**
	 * Overlapping.
	 *
	 * @param <T1> the generic type
	 * @param features the features
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<RTreeOverlapFeature<T1>> overlapping(final List<T1> features, 
			Chromosome chr, 
			int start,
			int end,
			Strand strand) {

		List<RTreeOverlapFeature<T1>> ret = 
				new ArrayList<RTreeOverlapFeature<T1>>();

		for (T1 feature : features) {
			int overlap = overlap(feature, chr, start, end, strand);
			
			if (overlap > 0) {
				RTreeOverlapFeature<T1> f = 
						new RTreeOverlapFeature<T1>(feature, overlap);
			
				ret.add(f);
			}
		}

		return ret;
	}

	/**
	 * Checks if is overlapping.
	 *
	 * @param <T1> the generic type
	 * @param feature the feature
	 * @param chr the chr
	 * @param p the p
	 * @return true, if is overlapping
	 */
	public static <T1 extends GenomicRegion> boolean isOverlapping(final T1 feature, 
			Chromosome chr, 
			int p) {
		return feature.getChr().equals(chr) && 
				p >= feature.getStart() && 
				p <= feature.getEnd();
	}
	
	/**
	 * Returns the overlap between a feature and a genomic region.
	 *
	 * @param <T1> the generic type
	 * @param feature the feature
	 * @param region the region
	 * @return the int
	 */
	public static <T1 extends GenomicRegion> int overlap(final T1 feature, 
			GenomicRegion region) {
		return overlap(feature, 
				region.getChr(), 
				region.getStart(), 
				region.getEnd(), 
				region.getStrand());
	}
	
	/**
	 * Overlap.
	 *
	 * @param <T1> the generic type
	 * @param feature the feature
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 * @return the int
	 */
	public static <T1 extends GenomicRegion> int overlap(final T1 feature, 
			Chromosome chr, 
			int start,
			int end,
			Strand strand) {
		
		if (strand != Strand.NONE && feature.getStrand() != strand) {
			return ERR_CODE_WRONG_STRAND;
		}
		
		if (!feature.getChr().equals(chr)) {
			return ERR_CODE_WRONG_CHR;
		}
		
		// If features overlap this will generate a positive value
		int ret = Math.min(end, feature.getEnd()) - 
				Math.max(start, feature.getStart()) + 1;
		
		if (ret > 0) {
			return ret;
		} else {
			return ERR_CODE_NO_OVERLAP;
		}
	}
}
