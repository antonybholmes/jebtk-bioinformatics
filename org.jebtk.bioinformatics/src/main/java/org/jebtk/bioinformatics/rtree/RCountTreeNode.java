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
import java.util.TreeMap;

import org.jebtk.bioinformatics.genomic.GenomicRegion;

// TODO: Auto-generated Javadoc
/**
 * The Class RCountTreeNode.
 *
 * @param <T> the generic type
 */
public class RCountTreeNode<T extends GenomicRegion> implements Comparable<RCountTreeNode<T>>, Iterable<RCountTreeNode<T>> {
	
	/** The m width. */
	private int mWidth;
	
	/** The m start. */
	private int mStart;
	
	/** The Constant MIN_LEVEL. */
	// 10^1 so min width of block is 10 bp
	public static final int MIN_LEVEL = 2;

	/** The m children. */
	private Map<Integer, RCountTreeNode<T>> mChildren = 
			new TreeMap<Integer, RCountTreeNode<T>>();

	/** The m level. */
	private int mLevel;
	
	/** The m count. */
	private int mCount = 0;

	/**
	 * Instantiates a new r count tree node.
	 *
	 * @param level the level
	 * @param start the start
	 */
	public RCountTreeNode(int level,
			int start) {
		mLevel = level;
		mStart = start;
		mWidth = (int)Math.pow(10, level);
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
	 * The node width, e.g. 1000,10000,100000 etc.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Map<Integer, RCountTreeNode<T>> getChildren() {
		return Collections.unmodifiableMap(mChildren);
	}
	
	/**
	 * Adds the child.
	 *
	 * @param feature the feature
	 */
	public void addChild(T feature) {
		
		++mCount;
		
		if (mLevel == MIN_LEVEL) {
			return;
		}
		
		int newWidth = mWidth / 10; //(int)Math.pow(10, mLevel + 1);
		
		
		// Fix starts and ends to nearest block
		int s = (feature.getStart() - mStart) / newWidth;
		int e = (feature.getEnd() - mStart) / newWidth;

		for (int bin = s; bin <= e; ++bin) {
			//SysUtils.err().println("error level", mStart, mLevel, mWidth, feature, bin);
			
			if (!mChildren.containsKey(bin)) {
				RCountTreeNode<T> child = new RCountTreeNode<T>(mLevel - 1,
						mStart + bin * newWidth);
				
				mChildren.put(bin, child);
			}
			
			mChildren.get(bin).addChild(feature);
		}
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RCountTreeNode<T> n) {
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
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (o instanceof RCountTreeNode) {
			return compareTo((RCountTreeNode)o) == 0;
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
		return mCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<RCountTreeNode<T>> iterator() {
		return mChildren.values().iterator();
	}

	/**
	 * Gets the bin count.
	 *
	 * @return the bin count
	 */
	public int getBinCount() {
		return mChildren.size();
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		// TODO Auto-generated method stub
		return mLevel;
	}
}
