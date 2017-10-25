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

import org.jebtk.bioinformatics.GFF;
import org.jebtk.bioinformatics.gapsearch.BinaryGapSearch;
import org.jebtk.bioinformatics.gapsearch.GapSearch;
import org.jebtk.core.collections.DefaultHashMap;
import org.jebtk.core.collections.DefaultHashMapCreator;
import org.jebtk.core.collections.HashSetCreator;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.text.Splitter;
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
	private Map<String, Map<String, Set<Gene>>> mSymbolMap = 
			DefaultHashMap.create(new DefaultHashMapCreator<String, Set<Gene>>(new HashSetCreator<Gene>()));

	/**
	 * The member ref seq map.
	 */
	//private Map<String, Gene> mRefSeqMap = 
	//		new HashMap<String, Gene>();

	private boolean mFindMainVariants = true;

	/**
	 * Instantiates a new gene service.
	 */
	private Genes() {
		// do nothing
	}
	
	@Override
	public void add(GenomicRegion region, Gene gene) {
		super.add(region, gene);
		
		for (String id : gene.getIds()) {
			mSymbolMap.get(id).get(gene.getId(id).toLowerCase()).add(gene);
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

			Map<String, Set<Gene>> map = mSymbolMap.get(Gene.SYMBOL_TYPE);
			
			for (String name : map.keySet()) {
				if (map.get(name).size() == 1) {
					// no variants so add this as being representative

					mSymbolMainVariant.put(name, map.get(name).iterator().next());
				} else {
					// try and find variant 1

					int maxWidth = 0;

					for (Gene gene : map.get(name)) {
						int width = gene.mRegion.mLength;

						if (width > maxWidth) {
							mSymbolMainVariant.put(name, gene);

							maxWidth = width;
						}
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
		Gene gene = lookupByRefSeq(id);

		if (gene != null) {
			return gene;
		}

		gene = findMainVariant(id);

		if (gene != null) {
			return gene;
		}

		return null;
	}

	/**
	 * Lookup by ref seq.
	 *
	 * @param refseq the refseq
	 * @return the gene
	 */
	public Gene lookupByRefSeq(String refseq) {
		Collection<Gene> genes = lookup(Gene.REFSEQ_TYPE, refseq.toLowerCase());
		
		if (genes.size() > 0) {
			return genes.iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * Lookup by entrez.
	 *
	 * @param entrez the entrez
	 * @return the sets the
	 */
	public Collection<Gene> lookupByEntrez(String entrez) {
		return lookup(Gene.ENTREZ_TYPE, entrez.toLowerCase());
	}

	/**
	 * Lookup by symbol.
	 *
	 * @param symbol the symbol
	 * @return the sets the
	 */
	public Collection<Gene> lookupBySymbol(String symbol) {
		return lookup(Gene.SYMBOL_TYPE, symbol);
	}
	
	/**
	 * Lookup.
	 *
	 * @param type the type
	 * @param symbol the symbol
	 * @return the collection
	 */
	public Collection<Gene> lookup(String type, String symbol) {
		return mSymbolMap.get(type).get(symbol.toLowerCase());
	}


	/**
	 * Find genes.
	 *
	 * @param region the region
	 * @return the list
	 */
	public List<Gene> findGenes(GenomicRegion region) {
		return getOverlappingFeatures(region, 10).toList();
	}
	
	/**
	 * Find closest genes.
	 *
	 * @param region the region
	 * @return the list
	 */
	public List<Gene> findClosestGenes(GenomicRegion region) {
		return getClosestFeatures(region);
	}

	/**
	 * Find closest genes by tss.
	 *
	 * @param region the region
	 * @return the list
	 */
	public List<Gene> findClosestGenesByTss(GenomicRegion region) {
		List<Gene> genes = findClosestGenes(region); //findGenes(region);

		List<Gene> ret = new ArrayList<Gene>(genes.size());

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

		return mSymbolMainVariant.get(name.toLowerCase());
	}
	
	/**
	 * Return the RefSeq ids used to index these genes.
	 *
	 * @return the ref seq ids
	 */
	public Collection<String> getRefSeqIds() {
		return getIds(Gene.REFSEQ_TYPE);
	}
	
	/**
	 * Return the set of ids (e.g. RefSeq ids) associated with a given
	 * id type.
	 *
	 * @param type the type
	 * @return the ids
	 */
	public Collection<String> getIds(String type) {
		return mSymbolMap.get(type).keySet();
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
			ret = load(reader);
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
	public static GapSearch<Gene> load(BufferedReader reader) throws IOException {
		Genes ret = new Genes();
		
		String line;
		List<String> tokens;

		//List<Gene> genes = new ArrayList<Gene>();

		//Map<Chromosome, Set<Integer>> positionMap = 
		//		new HashMap<Chromosome, Set<Integer>>();

		// skip header
		line = reader.readLine();

		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			tokens = TextUtils.tabSplit(line);

			String refseq = tokens.get(1);
			String entrez = tokens.get(2);
			String symbol = tokens.get(5);
			Chromosome chr = Chromosome.parse(tokens.get(8));
			Strand strand = Strand.parse(tokens.get(9)); //.charAt(0);
			// Because of the UCSC using zero based start and one
			// based end, we need to increment the start by 1
			int start = Integer.parseInt(tokens.get(10)) + 1;
			int end = Integer.parseInt(tokens.get(11));

			Gene gene = Gene.create(GenomicRegion.create(chr, start, end, strand))
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

			//mRefSeqMap.put(refseq.toLowerCase(), gene);
			//mEntrezMap.get(entrez.toLowerCase()).add(gene);
			///mSymbolMap.get(symbol.toLowerCase()).add(gene);
			//mapGene(gene, ret.mSymbolMap);

			// add the start and end to the positionMap
			ret.add(gene.mRegion, gene);
		}
		
		return ret;
	}
	
	/**
	 * Index gene based on its ids.
	 *
	 * @param gene the gene
	 * @param symbolMap the symbol map
	 */
	private static void mapGene(Gene gene, Map<String, Map<String, Set<Gene>>> symbolMap) {
		for (String type : gene.getIds()) {
			String name = gene.getId(type).toLowerCase();
			
			if (!name.equals(TextUtils.NA)) {
				symbolMap.get(type).get(name).add(gene);
			}
		}
	}

	/**
	 * From GFF 3.
	 *
	 * @param file the file
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Genes fromGFF3(Path file) throws IOException {
		LOG.info("Parsing {}...", file);

		BufferedReader reader = FileUtils.newBufferedReader(file);

		Genes ret = null;
		
		try {
			ret = fromGFF3(reader);
		} finally {
			reader.close();
		}
		
		return ret;
	}

	/**
	 * From GFF 3.
	 *
	 * @param reader the reader
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Genes fromGFF3(BufferedReader reader) throws IOException {
		Genes ret = new Genes();
		
		String line;
		List<String> tokens;

		Gene gene = null;

		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			tokens = Splitter.onTab().text(line);

			Chromosome chr = Chromosome.parse(tokens.get(0));
			
			// Skip random and unofficial chromosomes
			if (chr.toString().contains("_")) {
				continue;
			}

			//System.err.println("gff3 " + line);
			
			String type = tokens.get(2);
			int start = Integer.parseInt(tokens.get(3));
			int end = Integer.parseInt(tokens.get(4));

			Strand strand = Strand.parse(tokens.get(6));

			
			
			// Because of the UCSC using zero based start and one
			// based end, we need to increment the start by 1

			//List<String> attributes = Splitter.on(';').text(tokens.get(8));	
			String attributes = tokens.get(8);

			//Map<String, String> attributeMap = Splitter.toMap(attributes, '=');
			
			Map<String, String> attributeMap = GFF.parseAttributes(attributes);

			if (type.equals("gene")) {
				gene = new Gene(GenomicRegion.create(chr, start, end, strand))
						.setSymbol(attributeMap.get("symbol"))
						.setRefseq(attributeMap.get("refseq"));

				//ret.mSymbolMap.get(Gene.REFSEQ_TYPE).get(gene.getRefSeq().toLowerCase()).add(gene);
				//ret.mSymbolMap.get(Gene.SYMBOL_TYPE).get(gene.getSymbol().toLowerCase()).add(gene);
				//mapGene(gene, ret.mSymbolMap);
				
				//mRefSeqMap.put(gene.getRefSeq().toLowerCase(), gene);
				//mSymbolMap.get(gene.getSymbol().toLowerCase()).add(gene);
				ret.add(gene.mRegion, gene);
				
				//System.err.println("gff3 " + gene.getSymbol() + " " + strand + " " + tokens.get(6));
				
			} else if (type.contains("exon")) {
				if (gene != null) {
					gene.addExon(GenomicRegion.create(chr, start, end, type));
				}
			} else {
				// Do nothing
			}
		}
		
		return ret;
	}

	/**
	 * Parses the gene table.
	 *
	 * @param file the file
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Genes parseGeneTable(Path file) throws IOException {
		LOG.info("Parsing {}...", file);

		BufferedReader reader = FileUtils.newBufferedReader(file);

		Genes ret = null;
		
		try {
			ret = parseGeneTable(reader);
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
	public static Genes parseGeneTable(BufferedReader reader) throws IOException {
		Genes ret = new Genes();
		
		String line;
		List<String> tokens;

		Gene gene = null;
		
		// Skip header
		reader.readLine();

		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			tokens = Splitter.onTab().text(line);

			Chromosome chr = Chromosome.parse(tokens.get(0));
			
			// Skip random and unofficial chromosomes
			if (chr.toString().contains("_")) {
				continue;
			}
			
			Strand strand = Strand.parse(tokens.get(1));
			int start = Integer.parseInt(tokens.get(2));
			int end = Integer.parseInt(tokens.get(3));

			//int exonCount = Integer.parseInt(tokens.get(4));
			
			List<Integer> starts = 
					TextUtils.splitInts(tokens.get(5), TextUtils.SEMI_COLON_DELIMITER);

			List<Integer> ends = 
					TextUtils.splitInts(tokens.get(6), TextUtils.SEMI_COLON_DELIMITER);
			
			
			// Because of the UCSC using zero based start and one
			// based end, we need to increment the start by 1

			List<String> attributes = Splitter.on(';').text(tokens.get(7));

			IterMap<String, String> attributeMap = Splitter.toMap(attributes, '=');
			
			// Create the gene
			gene = Gene.create(GenomicRegion.create(chr, start, end, strand));

			// Add the exons
			for (int i = 0; i < starts.size(); ++i) {
				// Again correct for the ucsc
				GenomicRegion exon = 
						GenomicRegion.create(chr, starts.get(i) + 1, ends.get(i), "exon");

				gene.addExon(exon);
			}
			
			// Add the ids
			for (String type : attributeMap) {
				gene.setId(type, TextUtils.unquote(attributeMap.get(type)));
			}
			
			//mapGene(gene, ret.mSymbolMap);
			
			ret.add(gene.mRegion, gene);
		}
		
		return ret;
	}

	
}