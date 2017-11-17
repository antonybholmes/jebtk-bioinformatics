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
package org.jebtk.bioinformatics.genomic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jebtk.core.collections.IterMap;
import org.jebtk.core.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// TODO: Auto-generated Javadoc
/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public abstract class GeneParser {
	public static final Logger LOG = 
			LoggerFactory.getLogger(GeneParser.class);

	protected Set<GeneType> mLevels = new HashSet<GeneType>();

	/** Whether to add exons to gene structure */
	protected boolean mKeepExons = true;
	protected Set<String> mMatchTags = new HashSet<String>();
	protected Set<String> mExcludeTags = new HashSet<String>();

	public GeneParser() {
		//setLevels(GeneType.GENE);
	}

	public GeneParser(GeneParser parser) {
		mLevels.addAll(parser.mLevels);
		mKeepExons = parser.mKeepExons;
		mMatchTags.addAll(parser.mMatchTags);
		mExcludeTags.addAll(parser.mExcludeTags);
	}

	public GeneParser setKeepExons(boolean keep) {
		GeneParser parser = create(this);

		parser._setKeepExons(keep);

		return parser;
	}

	protected void _setKeepExons(boolean keep) {
		mKeepExons = keep;
	}

	/**
	 * Exclude entries matching given tags.
	 * 
	 * @param tag
	 * @param tags
	 * @return 
	 */
	public GeneParser excludeByTag(String tag, String... tags) {
		GeneParser parser = create(this);

		parser.mExcludeTags.add(tag);

		for (String t : tags) {
			parser.mExcludeTags.add(t);
		}

		return parser;
	}

	public GeneParser excludeByTag(Collection<String> excludeTags) {
		GeneParser parser = create(this);

		parser.mExcludeTags.addAll(excludeTags);

		return parser;
	}

	public GeneParser matchOnTag(String tag, String... tags) {
		GeneParser parser = create(this);

		parser.mMatchTags.add(tag);

		for (String t : tags) {
			parser.mMatchTags.add(t);
		}

		return parser;
	}

	public GeneParser setLevels(GeneType level, GeneType... levels) {
		GeneParser parser = create(this);

		parser._setLevels(level, levels);

		return parser;
	}

	protected void _setLevels(GeneType level, GeneType... levels) {
		mLevels.clear();
		mLevels.add(level);

		for (GeneType l : levels) {
			mLevels.add(l);
		}
	}

	public GeneParser setLevels(Collection<GeneType> levels) {
		GeneParser parser = create(this);

		parser._setLevels(levels);

		return parser;
	}

	protected void _setLevels(Collection<GeneType> levels) {
		mLevels.clear();

		_addLevels(levels);
	}

	public GeneParser addLevels(Collection<GeneType> levels) {
		GeneParser parser = create(this);

		parser._addLevels(levels);

		return parser;
	}

	protected void _addLevels(Collection<GeneType> levels) {
		mLevels.addAll(levels);
	}

	public abstract GeneParser create(GeneParser parser);

	/**
	 * Parses the gene table.
	 *
	 * @param file the file
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Genes parse(Path file) throws IOException {
		Genes genes = new Genes();

		parse(file, genes);

		return genes;
	}

	public void parse(Path file, Genes genes) throws IOException {
		BufferedReader reader = FileUtils.newBufferedReader(file);

		try {
			parse(file, reader, genes);
		} finally {
			reader.close();
		}
	}

	/**
	 * Parses the gene table.
	 *
	 * @param reader the reader
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Genes parse(Path file, BufferedReader reader) throws IOException {
		Genes genes = new Genes();

		parse(file, reader, genes);

		return genes;
	}

	protected void parse(Path file, 
			BufferedReader reader, 
			Genes genes) throws IOException {
		// Do nothin
	}



	public Genes parse(Path file, Chromosome chr) throws IOException {
		Genes genes = new Genes();

		parse(file, genes, chr);

		return genes;
	}

	protected Genes parse(Path file, BufferedReader reader, Chromosome chr) throws IOException {
		Genes genes = new Genes();

		parse(file, reader, genes, chr);

		return genes;
	}

	public void parse(Path file, Genes genes, Chromosome chr) throws IOException {
		BufferedReader reader = FileUtils.newBufferedReader(file);

		try {
			parse(file, reader, genes, chr);
		} finally {
			reader.close();
		}
	}

	protected void parse(Path file, 
			BufferedReader reader, 
			Genes genes,
			Chromosome chr) throws IOException {
		parse(file, reader, genes);
	}


	public Genes parse(Path file, Chromosome chr, int window) throws IOException {
		Genes genes = new Genes();

		parse(file, genes, chr, window);

		return genes;
	}

	protected Genes parse(Path file, 
			BufferedReader reader, 
			Chromosome chr, 
			int window) throws IOException {
		Genes genes = new Genes();

		parse(file, reader, genes, chr, window);

		return genes;
	}

	public void parse(Path file, Genes genes, Chromosome chr, int window) throws IOException {
		BufferedReader reader = FileUtils.newBufferedReader(file);

		try {
			parse(file, reader, genes, chr, window);
		} finally {
			reader.close();
		}
	}

	protected void parse(Path file, 
			BufferedReader reader, 
			Genes genes,
			Chromosome chr,
			int window) throws IOException {
		parse(file, reader, genes, chr);
	}


	public Map<String, Set<String>> idMap(Path file,
			String id1,
			String id2) throws IOException {
		BufferedReader reader = FileUtils.newBufferedReader(file);

		Map<String, Set<String>> ret;

		try {
			ret = idMap(file, reader, id1, id2);
		} finally {
			reader.close();
		}

		return ret;
	}

	/**
	 * Parses the gene table.
	 *
	 * @param reader the reader
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract Map<String, Set<String>> idMap(Path file, 
			BufferedReader reader,
			String id1,
			String id2) throws IOException; 

	public boolean containsLevel(GeneType level) {
		if (mLevels.size() == 0) {
			return true;
		}

		return mLevels.contains(level);
	}

	public static Gene addAttributes(GeneType type,
			final GenomicRegion region,
			final IterMap<String, String> attributeMap) {

		Gene gene = Gene.create(type, region);

		// Add the ids
		for (String id : attributeMap) {
			String name = attributeMap.get(id);

			gene.setId(id, name);
		}

		// If there are any tags


		//genes.add(gene, gene);

		return gene;
	}
}