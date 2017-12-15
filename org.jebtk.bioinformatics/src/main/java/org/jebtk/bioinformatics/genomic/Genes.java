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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jebtk.bioinformatics.gapsearch.BinaryGapSearch;
import org.jebtk.bioinformatics.gapsearch.GapSearch;
import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.collections.UniqueArrayListCreator;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.io.TokenFunction;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// TODO: Auto-generated Javadoc
/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public class Genes extends BinaryGapSearch<Gene> {
	//private static final GeneService INSTANCE = new GeneService();

	/**
	 * The constant DEFAULT_RES.
	 */
	public static final String DEFAULT_RES = 
			"res/ucsc_refseq_exons_entrez_hg19.txt.gz";

	/**
	 * The constant DEFAULT_FILE.
	 */
	public static final Path DEFAULT_FILE = PathUtils.getPath(DEFAULT_RES);

	/**
	 * The constant LOG.
	 */
	private static final Logger LOG = 
			LoggerFactory.getLogger(Genes.class);

	/** Empty gene set that can be used as a placeholder */
	public static final Genes EMPTY_GENES = new Genes() {
		@Override
		public void add(GenomicRegion region, Gene gene) {
			// Do nothing
		}
	};

	//public static final GeneService getInstance() {
	//	return INSTANCE;
	//}

	//private GapSearch<Gene> mPositionMap = 
	//		new GappedSearch<Gene>();

	/**
	 * The member position map.
	 */
	///private RTree<Gene> mGeneTree = 
	//		new RTree<Gene>();

	/**
	 * The member symbol main variant.
	 */
	private Map<String, Gene> mSymbolMainVariant = 
			new HashMap<String, Gene>();

	/**
	 * The member entrez map.
	 */
	//private Map<String, Set<Gene>> mEntrezMap = 
	//		DefaultHashMap.create(new HashSetCreator<Gene>());

	/**
	 * The member symbol map.
	 */

	private IterMap<String, Set<String>> mTypeMap = 
			DefaultHashMap.create(new TreeSetCreator<String>());

	private IterMap<String, List<Gene>> mIdMap = 
			DefaultHashMap.create(new UniqueArrayListCreator<Gene>());

	/**
	 * The member ref seq map.
	 */
	//private Map<String, Gene> mRefSeqMap = 
	//		new HashMap<String, Gene>();

	private boolean mFindMainVariants = true;

	/**
	 * Instantiates a new gene service.
	 */
	public Genes() {
		// do nothing
	}

	public void add(Gene gene) {
		add(gene, gene);
	}

	@Override
	public void add(GenomicRegion region, Gene gene) {
		super.add(region, gene);

		// Map to exons

		/*
		if (gene.getExonCount() > 0) {
			for (GenomicRegion exon : gene) {
				super.add(exon, gene);
			}
		} else {
			super.add(region, gene);
		}
		 */

		for (String tid : gene.getIdTypes()) {
			String name = gene.getId(tid);
			mTypeMap.get(tid).add(name);
			mIdMap.get(sanitize(name)).add(gene);
		}
	}

	/**
	 * Auto find main variants.
	 */
	public void autoFindMainVariants() {

		if (mFindMainVariants) {
			//
			// Find the representative gene e.g. variant 1
			//

			Set<String> symbols = mTypeMap.get(Gene.SYMBOL_TYPE);

			for (String name : symbols) {
				List<Gene> genes = mIdMap.get(sanitize(name));

				// try and find variant 1

				int maxWidth = 0;

				for (Gene gene : genes) {
					int width = gene.mLength;

					if (width > maxWidth) {
						mSymbolMainVariant.put(name, gene);

						maxWidth = width;
					}
				}
			}

			mFindMainVariants = false;
		}

		// clear to indicate it can be garbage collected.
		//positionMap.clear();
		//mSymbolMap.clear();
	}



	/**
	 * Lookup a gene by either symbol or refseq.
	 *
	 * @param id the id
	 * @return the gene
	 */
	public Gene lookup(String id) {
		Gene gene = getGene(id);

		if (gene != null) {
			return gene;
		}

		gene = findMainVariant(id);

		if (gene != null) {
			return gene;
		}

		return null;
	}

	public Gene getGene(String symbol) {
		Collection<Gene> genes = getGenes(symbol);

		if (genes.size() > 0) {
			return genes.iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * Lookup.
	 *
	 * @param type the type
	 * @param symbol the symbol
	 * @return the collection
	 */
	public Collection<Gene> getGenes(String symbol) {
		return mIdMap.get(sanitize(symbol));
	}


	/**
	 * Find genes.
	 *
	 * @param region the region
	 * @return the list
	 */
	public Collection<Gene> findGenes(GenomicRegion region) {
		return getOverlappingFeatures(region, 10).toList();
	}

	/**
	 * Find closest genes.
	 *
	 * @param region the region
	 * @return the list
	 */
	public Collection<Gene> findClosestGenes(GenomicRegion region) {
		return getClosestFeatures(region);
	}

	/**
	 * Find closest genes by tss.
	 *
	 * @param region the region
	 * @return the list
	 */
	public Collection<Gene> findClosestGenesByTss(GenomicRegion region) {
		Collection<Gene> genes = findClosestGenes(region); //findGenes(region);

		List<Gene> ret = new ArrayList<Gene>();

		int minD = Integer.MAX_VALUE;

		for (Gene gene : genes) {
			GenomicRegion tss = Gene.tssRegion(gene);

			minD = Math.min(minD, GenomicRegion.midAbsDist(region, tss));
		}

		for (Gene gene : genes) {
			GenomicRegion tss = Gene.tssRegion(gene);

			int d = GenomicRegion.midAbsDist(region, tss);

			if (d == minD) {
				ret.add(gene);
			}
		}

		return ret;
	}

	//public List<Gene> findGenes(Chromosome c, int start) {
	//	ChromosomeBins bins = mPositionMap.get(c);
	//	
	//	return bins.findGenes(start);
	//}

	/**
	 * Find the representative gene by name. Search is case
	 * insensitive. Returns null if gene not found.
	 *
	 * @param name the name
	 * @return the gene
	 */
	public Gene findMainVariant(String name) {
		autoFindMainVariants();

		return mSymbolMainVariant.get(name.toUpperCase());
	}

	/**
	 * Return the RefSeq ids used to index these genes.
	 *
	 * @return the ref seq ids
	 */
	public Iterable<String> getRefSeqIds() {
		return getIds(Gene.REFSEQ_TYPE);
	}

	/**
	 * Return the set of ids (e.g. RefSeq ids) associated with a given
	 * id type.
	 *
	 * @param type the type
	 * @return the ids
	 */
	public Iterable<String> getIds(String type) {
		return mTypeMap.get(type);
	}

	public Iterable<String> getSymbols() {
		return getIds(Gene.SYMBOL_TYPE);
	}

	/**
	 * Load.
	 *
	 * @param file the file
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static GapSearch<Gene> load(Path file) throws IOException {
		LOG.info("Parsing {}...", file);

		GapSearch<Gene> ret = null;

		BufferedReader reader = FileUtils.newBufferedReader(file);

		try {
			ret = load(ChromosomeService.getInstance().guess(file), reader);
		} finally {
			reader.close();
		}

		return ret;
	}

	/**
	 * Load.
	 *
	 * @param reader the reader
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static GapSearch<Gene> load(final String species, 
			BufferedReader reader) throws IOException {
		final Genes ret = new Genes();

		FileUtils.tokenize(reader, new TokenFunction() {

			@Override
			public void parse(List<String> tokens) {
				// TODO Auto-generated method stub

				String refseq = tokens.get(1);
				String entrez = tokens.get(2);
				String symbol = tokens.get(5);
				Chromosome chr = ChromosomeService.getInstance().parse(species, tokens.get(8));
				Strand strand = Strand.parse(tokens.get(9)); //.charAt(0);
				// Because of the UCSC using zero based start and one
				// based end, we need to increment the start by 1
				int start = Integer.parseInt(tokens.get(10)) + 1;
				int end = Integer.parseInt(tokens.get(11));

				Gene gene = Gene.create(GeneType.TRANSCRIPT, 
						GenomicRegion.create(chr, start, end, strand))
						.setSymbol(symbol)
						.setRefseq(refseq)
						.setEntrez(entrez);

				List<Integer> starts = 
						TextUtils.splitInts(tokens.get(13), TextUtils.COMMA_DELIMITER);

				List<Integer> ends = 
						TextUtils.splitInts(tokens.get(14), TextUtils.COMMA_DELIMITER);


				for (int i = 0; i < starts.size(); ++i) {
					// Again correct for the ucsc
					GenomicRegion exon = 
							GenomicRegion.create(chr, starts.get(i) + 1, ends.get(i));

					gene.addExon(exon);
				}

				//mRefSeqMap.put(refseq.toUpperCase(), gene);
				//mEntrezMap.get(entrez.toUpperCase()).add(gene);
				///mSymbolMap.get(symbol.toUpperCase()).add(gene);
				//mapGene(gene, ret.mSymbolMap);

				// add the start and end to the positionMap
				ret.add(gene, gene);
			}});

		return ret;
	}

	protected static String sanitize(String name) {
		return name.toUpperCase();
	}


	public static GFF3Parser gff3Parser() {
		return new GFF3Parser();
	}

	public static GTB1Parser gtbParser() {
		return new GTB1Parser();
	}

	public static GTB2Parser gtb2Parser() {
		return new GTB2Parser();
	}

}