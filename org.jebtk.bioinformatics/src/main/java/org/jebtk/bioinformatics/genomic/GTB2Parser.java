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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;



// TODO: Auto-generated Javadoc
/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public class GTB2Parser extends GTBParser {

	public GTB2Parser() {
		//_setLevels(GeneType.GENE);
	}

	public GTB2Parser(GeneParser parser) {
		super(parser);
	}

	/**
	 * Parses the gene table.
	 *
	 * @param reader the reader
	 * @return the genes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void parse(Path file, 
			BufferedReader reader, 
			Genes genes,
			Chromosome chr) throws IOException {
		LOG.info("Parsing GTB2 file {}, levels: {}...", file, mLevels);

		String line;
		List<String> tokens;

		Gene gene = null;

		// Skip header
		reader.readLine();

		Splitter splitter = Splitter.on(';');

		// Add the exons
		boolean hasExonLevel = containsLevel(GeneType.EXON);
		boolean has3pUtrLevel = containsLevel(GeneType.UTR_3P);
		boolean has5pUtrLevel = containsLevel(GeneType.UTR_5P);

		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			boolean add = true;

			tokens = Splitter.onTab().text(line);

			chr = Chromosome.parse(tokens.get(0));

			// Skip random and unofficial chromosomes
			if (chr.toString().contains("_")) {
				continue;
			}

			Strand strand = Strand.parse(tokens.get(1));
			int start = Integer.parseInt(tokens.get(2));
			int end = Integer.parseInt(tokens.get(3));

			//int exonCount = Integer.parseInt(tokens.get(4));

			// Because of the UCSC using zero based start and one
			// based end, we need to increment the start by 1


			List<String> tags = getTags(splitter, tokens);

			if (mExcludeTags.size() > 0) {
				for (String tag : tags) {
					if (mExcludeTags.contains(tag)) {
						add = false;
						break;
					}
				}
			}

			if (mMatchTags.size() > 0) {
				add = false;

				for (String tag : tags) {
					if (mMatchTags.contains(tag)) {
						add = true;
						break;
					}
				}
			}

			if (!add) {
				continue;
			}

			IterMap<String, String> attributeMap = 
					getAttributes(splitter, tokens);

			// Create the gene

			gene = addAttributes(GeneType.TRANSCRIPT,
					GenomicRegion.create(chr, start, end, strand),
					attributeMap);

			if (containsLevel(GeneType.TRANSCRIPT)) {
				genes.add(gene);
			}

			if (hasExonLevel || mKeepExons) {
				List<Integer> starts = 
						TextUtils.splitInts(tokens.get(5), TextUtils.SEMI_COLON_DELIMITER);

				List<Integer> ends = 
						TextUtils.splitInts(tokens.get(6), TextUtils.SEMI_COLON_DELIMITER);

				for (int i = 0; i < starts.size(); ++i) {
					// Again correct for the ucsc
					GenomicRegion region = GenomicRegion.create(chr, 
							starts.get(i), 
							ends.get(i), 
							strand);

					if (mKeepExons) {
						if (gene != null) {
							gene.addExon(region);
						}
					}

					if (hasExonLevel) {
						Gene g = addAttributes(GeneType.EXON, region, attributeMap);

						g.setParent(gene);

						genes.add(g);
					}
				}
			}

			if (has5pUtrLevel) {
				processUTR(tokens, gene, attributeMap, 7, GeneType.UTR_5P, genes);
			}

			if (has3pUtrLevel) {
				processUTR(tokens, gene, attributeMap, 10, GeneType.UTR_3P, genes);
			}
		}
	}

	private static void processUTR(List<String> tokens,
			Gene gene,
			IterMap<String, String> attributeMap,
			int offset,
			GeneType type,
			Genes ret) {
		
		int count = Integer.parseInt(tokens.get(offset));
		
		if (count == 0) {
			return;
		}
		
		List<Integer> starts = 
				TextUtils.splitInts(tokens.get(offset + 1), TextUtils.SEMI_COLON_DELIMITER);

		List<Integer> ends = 
				TextUtils.splitInts(tokens.get(offset + 2), TextUtils.SEMI_COLON_DELIMITER);

		for (int i = 0; i < starts.size(); ++i) {
			GenomicRegion region = GenomicRegion.create(gene.mRegion.mChr, 
					starts.get(i), 
					ends.get(i), 
					gene.mRegion.mStrand);

			Gene g = addAttributes(type, region, attributeMap);

			g.setParent(gene);

			ret.add(g);
		}
	}

	@Override
	public Map<String, Set<String>> idMap(Path file, 
			BufferedReader reader,
			String id1,
			String id2) throws IOException {
		LOG.info("Creating id map from GTB2 file {}, levels: {}...", file, mLevels);


		Map<String, Set<String>> ret = 
				DefaultTreeMap.create(new TreeSetCreator<String>());

		String line;
		List<String> tokens;

		// Skip header
		reader.readLine();

		Splitter splitter = Splitter.on(';');


		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			boolean add = true;

			tokens = Splitter.onTab().text(line);

			Chromosome chr = Chromosome.parse(tokens.get(0));

			// Skip random and unofficial chromosomes
			if (chr.toString().contains("_")) {
				continue;
			}

			List<String> tags = getTags(splitter, tokens);

			if (mExcludeTags.size() > 0) {
				for (String tag : tags) {
					if (mExcludeTags.contains(tag)) {
						add = false;
						break;
					}
				}
			}

			if (mMatchTags.size() > 0) {
				add = false;

				for (String tag : tags) {
					if (mMatchTags.contains(tag)) {
						add = true;
						break;
					}
				}
			}

			if (!add) {
				continue;
			}

			IterMap<String, String> attributeMap = 
					getAttributes(splitter, tokens);

			String name1 = attributeMap.get(id1);
			String name2 = attributeMap.get(id2);

			//System.err.println("id " + id1 + " " + name1 + " " + id2 + " " + name2);

			if (name1 != null && name2 != null) {
				ret.get(name1).add(name2);
			}	
		}

		return ret;
	}
	
	@Override
	public GeneParser create(GeneParser parser) {
		return new GTB2Parser(parser);
	}

	private static IterMap<String, String> getAttributes(Splitter splitter, 
			final List<String> tokens) {
		return getAttributes(splitter, tokens.get(13));
	}
	
	private static List<String> getTags(Splitter splitter, final List<String> tokens) {
		return TextUtils.removeNA(splitter.text(tokens.get(14)));
	}

}