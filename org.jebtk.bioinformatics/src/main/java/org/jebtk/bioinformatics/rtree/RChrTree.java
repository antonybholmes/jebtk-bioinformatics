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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.HashMapCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class RChrTree.
 *
 * @param <T> the generic type
 */
public class RChrTree<T extends GenomicRegion> {
	
	/** The Constant LOG. */
	private static final Logger LOG = 
			LoggerFactory.getLogger(RChrTree.class);
	
	/** The m root. */
	private RTreeNode mRoot = null;

	/** The m features. */
	private List<T> mFeatures = new ArrayList<T>();
	
	/** The m flat node map. */
	private Map<Integer, Map<Integer, RTreeNode>> mFlatNodeMap =
			DefaultHashMap.create(new HashMapCreator<Integer, RTreeNode>());

	/** The m chr. */
	private Chromosome mChr = null;

	/** The m bins. */
	private int mBins = -1;

	/** The auto build. */
	private boolean autoBuild = true;

	/** The m min width. */
	private int mMinWidth = -1;

	/** The m max width. */
	private int mMaxWidth = -1;

	/**
	 * Instantiates a new r chr tree.
	 *
	 * @param chr the chr
	 */
	public RChrTree(Chromosome chr) {
		this(chr, 
				RTree.DEFAULT_MIN_WIDTH, 
				RTree.DEFAULT_MAX_WIDTH, 
				RTree.DEFAULT_BINS);
	}
	
	/**
	 * Instantiates a new r chr tree.
	 *
	 * @param chr the chr
	 * @param minWidth the min width
	 * @param maxWidth the max width
	 * @param bins the bins
	 */
	public RChrTree(Chromosome chr, int minWidth, int maxWidth, int bins) {
		mChr = chr;
		mBins = bins;
		mMinWidth = minWidth;
		mMaxWidth = maxWidth;
	}

	/**
	 * Gets the chr.
	 *
	 * @return the chr
	 */
	public Chromosome getChr() {
		return mChr;
	}

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public RTreeNode getRoot() {
		return autoBuild();
	}
	
	/**
	 * Gets the flattened tree.
	 *
	 * @return the flattened tree
	 */
	public Map<Integer, Map<Integer, RTreeNode>> getFlattenedTree() {
		autoBuild();
		
		return mFlatNodeMap;
	}

	/**
	 * Adds the.
	 *
	 * @param feature the feature
	 */
	public void add(T feature) {
		if (!feature.getChr().equals(mChr)) {
			return;
		}

		mFeatures.add(feature);

		autoBuild = true;
	}

	/**
	 * Create the tree if it has not been instanciated. This is lazy
	 * creation so we only create parts of the tree that are actually
	 * in use.
	 *
	 * @return the r tree node
	 */
	public RTreeNode autoBuild() {
		if (autoBuild) {
			LOG.info("Auto build r-tree for {}...", mChr);
			
			mRoot = new RTreeNode(0, 0, 0, mMaxWidth, mBins, mMinWidth);

			// Ensure mfeatures are sorted by chr and start
			Collections.sort(mFeatures);

			for (int i = 0; i < mFeatures.size(); ++i) {
				mRoot.addChild(mFeatures.get(i), i);
			}
			
			flatten();
			
			autoBuild = false;
			
			LOG.info("Finished.");
		}

		return mRoot;
	}
	
	/**
	 * Flatten.
	 */
	public void flatten() {
		mFlatNodeMap.clear();
		
		Deque<RTreeNode> stack = new ArrayDeque<RTreeNode>();
		
		stack.push(mRoot);
		
		while (!stack.isEmpty()) {
			RTreeNode node = stack.pop();
			
			mFlatNodeMap.get(node.getWidth()).put(node.getBin(), node);
			
			for (RTreeNode child : node) {
				stack.push(child);
			}
		}
	}

	/**
	 * Gets the features.
	 *
	 * @return the features
	 */
	public List<T> getFeatures() {
		return Collections.unmodifiableList(mFeatures);
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
	 * Gets the feature.
	 *
	 * @param i the i
	 * @return the feature
	 */
	public T getFeature(int i) {
		return mFeatures.get(i);
	}

	/**
	 * Search tree.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param start the start
	 * @param end the end
	 * @return the list
	 */
	public static <T1 extends GenomicRegion> List<T1> searchTree(final RChrTree<T1> tree, 
			int start, 
			int end) {

		int[] range = new int[2];

		searchTreeByRange(tree,
				start, 
				end,
				range);
		
		List<T1> ret = new ArrayList<T1>(range[1] - range[0] + 1);

		for (int i = range[0]; i <= range[1]; ++i) {
			if (i != -1) {
				ret.add(tree.getFeature(i));
			}
		}
		
		return ret;
	}

	/**
	 * Search tree by range.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param start the start
	 * @param end the end
	 * @param range the range
	 */
	public static <T1 extends GenomicRegion> void searchTreeByRange(RChrTree<T1> tree, 
			int start, 
			int end,
			int[] range) {
		range[0] = -1;
		range[1] = -1;

		RTreeNode leftClosestNode = tree.findClosestNode(start);
		
		Set<Integer> indicesSet = new HashSet<Integer>();

		getIndices(leftClosestNode, indicesSet);

		if (indicesSet.size() == 0) {
			return;
		}

		RTreeNode rightClosestNode = tree.findClosestNode(end);

		getIndices(rightClosestNode, indicesSet);

		List<Integer> indices = CollectionUtils.sort(indicesSet);

		range[0] = indices.get(0);
		range[1] = indices.get(indices.size() - 1);
	}

	/**
	 * Finds the closest node in an RTree to a given position.
	 *
	 * @param <T1> the generic type
	 * @param p the p
	 * @return the r tree node
	 */
	public <T1 extends GenomicRegion> RTreeNode findClosestNode(int p) {
		//return findClosestNode(tree.getRoot(), p);
		
		Map<Integer, Map<Integer, RTreeNode>> map = getFlattenedTree();
		
		int binWidth = RTree.DEFAULT_MIN_WIDTH;
		
		while (binWidth <= RTree.DEFAULT_MAX_WIDTH) {
			int bin = p / binWidth;
			
			if (map.get(binWidth).containsKey(bin)) {
				return map.get(binWidth).get(bin);
			}
			
			binWidth *= RTree.DEFAULT_BINS;
		}
		
		return null;
	}

	/*
	public RTreeNode findClosestNode(int p) {
		RTreeNode node = root;

		int bins = node.getMaxBins();

		int childWidth = node.getWidth() / bins;
		int b = p / childWidth;

		boolean end = true;

		do {
			end = true;

			if (node.getChildren().containsKey(b)) {
				// make the node of interest the child
				node = node.getChildren().get(b);

				//System.err.println("b " + b  + " " + node.getStart() + " " + node.getWidth());

				// The bin is now the width of the children
				childWidth = node.getWidth() / bins;
				b = p / childWidth;
				end = false;
			}
		} while (!end);

		return node;
	}
	*/
	
	/**
	 * Gets the indices.
	 *
	 * @param root the root
	 * @return the indices
	 */
	public static Set<Integer> getIndices(RTreeNode root) {
		Set<Integer> indices = new TreeSet<Integer>();

		getIndices(root, indices);

		return indices;
	}

	/**
	 * Append all the indices found in a node and its children to an
	 * existing collection.
	 *
	 * @param root the root
	 * @param indices the indices
	 * @return the indices
	 */
	private static void getIndices(final RTreeNode root, 
			Collection<Integer> indices) {
		getIndices(root, 1000, indices);
	}

	/**
	 * Gets the indices.
	 *
	 * @param root the root
	 * @param minWidth the min width
	 * @param indices the indices
	 * @return the indices
	 */
	private static void getIndices(final RTreeNode root,
			int minWidth,
			Collection<Integer> indices) {
		Deque<RTreeNode> stack = new ArrayDeque<RTreeNode>();

		stack.push(root);

		RTreeNode node;
		Map<Integer, RTreeNode> children;

		while (!stack.isEmpty()) {
			node = stack.pop();

			//System.err.println("indices " + node.getStart() + " " + node.getWidth() + " " + node.getIndices());

			if (node.getWidth() == minWidth) {
				indices.addAll(node.getIndices());
			} else {
				children = node.getChildren();

				for (int bin : children.keySet()) {
					stack.push(children.get(bin));
				}
			}
		}
	}

	/**
	 * Gets the features.
	 *
	 * @param <T1> the generic type
	 * @param tree the tree
	 * @param si the si
	 * @param ei the ei
	 * @return the features
	 */
	public static <T1 extends GenomicRegion> List<T1> getFeatures(final RChrTree<T1> tree, 
			int si,
			int ei) {
		List<T1> features = new ArrayList<T1>();

		for (int i = si; i <= ei; ++i) {
			features.add(tree.getFeatures().get(i));
		}

		return features;		
	}

	/**
	 * Gets the node count.
	 *
	 * @return the node count
	 */
	public int getNodeCount() {
		// The number of children in the root and the root itself
		return mRoot.getChildNodeCount() + 1;
	}
}
