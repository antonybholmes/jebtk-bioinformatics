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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jebtk.bioinformatics.genomic.GenomicRegion;

// TODO: Auto-generated Javadoc
/**
 * The Class RTreeNode.
 */
public class RTreeNode implements Comparable<RTreeNode>, Iterable<RTreeNode> {
	
	/** The m width. */
	private int mWidth;
	
	/** The m start. */
	private int mStart;

	/** The m indices. */
	private Set<Integer> mIndices = 
			new TreeSet<Integer>();

	/** The m children. */
	private Map<Integer, RTreeNode> mChildren = 
			new TreeMap<Integer, RTreeNode>();

	/** The m bins. */
	private int mBins;
	
	/** The m min width. */
	private int mMinWidth;
	
	/** The m level. */
	private int mLevel;
	
	/** The m bin. */
	private int mBin;
	
	/** The m node count. */
	private int mNodeCount = 0;

	/**
	 * Instantiates a new r tree node.
	 *
	 * @param level the level
	 * @param bin the bin
	 * @param start the start
	 * @param width the width
	 * @param bins the bins
	 * @param minWidth the min width
	 */
	public RTreeNode(int level,
			int bin,
			int start,
			int width, 
			int bins,
			int minWidth) {
		mLevel = level;
		mBin = bin;
		mStart = start;
		mWidth = width;
		mBins = bins;
		mMinWidth = minWidth;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return mStart;
	}
	
	/**
	 * Gets the bin.
	 *
	 * @return the bin
	 */
	public int getBin() {
		return mBin;
	}

	/**
	 * The node width, e.g. 1000,10000,100000 etc.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * Gets the max bins.
	 *
	 * @return the max bins
	 */
	public int getMaxBins() {
		return mBins;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Map<Integer, RTreeNode> getChildren() {
		return Collections.unmodifiableMap(mChildren);
	}

	/**
	 * Returns a read only copy of the indices associated with this node.
	 *
	 * @return the indices
	 */
	public Set<Integer> getIndices() {
		return Collections.unmodifiableSet(mIndices);
	}
	
	/**
	 * Adds the child.
	 *
	 * @param <T> the generic type
	 * @param feature the feature
	 * @param i the i
	 */
	public <T extends GenomicRegion> void addChild(T feature, int i) {
		
		// Fix starts and ends to nearest block
		int s = feature.getStart() / mMinWidth * mMinWidth;
		int e = feature.getEnd() / mMinWidth * mMinWidth;
		
		for (int p = s; p <= e; p += mMinWidth) {
			addChild(p, i);
		}
		
		//addChild(feature.getStart(), i);
		//addChild(feature.getEnd(), i);
	}

	/**
	 * Adds the child.
	 *
	 * @param p the p
	 * @param index the index
	 * @return the r tree node
	 */
	public RTreeNode addChild(int p, int index) {
		RTreeNode current = this;

		// By design, the only way to introduce nodes into the tree is from
		// the root. Since the root is designed to hold a whole chromosome, it
		// follows that we can immediately add the indices to the current
		// node since we know p must be contained within it.
		current.mIndices.add(index);

		do {
			//
			// See if we need to create sub nodes.
			//

			int newWidth = current.mWidth / mBins;
			int start = p / newWidth * newWidth;
			int bin = start / newWidth;

			if (current.mChildren.containsKey(bin)) {
				current = current.mChildren.get(bin);
			} else {
				RTreeNode child = new RTreeNode(current.mLevel + 1,
						bin,
						start, 
						newWidth, 
						mBins,
						mMinWidth);

				current.mChildren.put(bin, child);

				current = child;
				
				++mNodeCount;
			}

			current.mIndices.add(index);
		} while (current.mWidth > mMinWidth);

		return current;
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RTreeNode n) {
		// Order by start
		if (mStart < n.mStart) {
			return -1;
		} else if (mStart > n.mStart) {
			return 1;
		} else {
			// Order by level so bigger nodes come first if starts are equal
			if (mLevel < n.mLevel) {
				return - 1;
			} else if (mLevel > n.mLevel) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof RTreeNode) {
			return compareTo((RTreeNode)o) == 0;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (mLevel + ":" + mStart).hashCode();
	}

	/**
	 * Return the number of nodes (including itself) in the tree.
	 *
	 * @return the node count
	 */
	public int getNodeCount() {
		return getChildNodeCount() + 1;
	}
	
	/**
	 * Gets the child node count.
	 *
	 * @return the child node count
	 */
	public int getChildNodeCount() {
		return mNodeCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<RTreeNode> iterator() {
		return mChildren.values().iterator();
	}
}
