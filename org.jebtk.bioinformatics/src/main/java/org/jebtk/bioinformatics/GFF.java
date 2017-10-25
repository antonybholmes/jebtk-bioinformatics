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
package org.jebtk.bioinformatics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jebtk.bioinformatics.gapsearch.FixedGapSearch;
import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.ChromosomeService;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.Strand;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class GFF.
 */
public class GFF {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(GFF.class);

	/**
	 * Instantiates a new gff.
	 */
	private GFF() {
		// Do nothing
	}

	/*
	public static RGeneTree<GFFGene> GFFToRTree(Path file) throws IOException {
		LOG.info("Creating r-tree from GFF {}...", file);

		List<GFFGene> features = parse(file);

		return GFFToRTree(features);
	}

	public static RGeneTree<GFFGene> GFFToRTree(List<GFFGene> features) {
		RGeneTree<GFFGene> tree = new RGeneTree<GFFGene>();

		for (GFFGene gene : features) {
			tree.add(gene);
		}

		return tree;
	}
	*/

	/**
	 * To symbols.
	 *
	 * @param features the features
	 * @return the sets the
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static Set<String> toSymbols(List<GFFGene> features) {
		Set<String> ret = new TreeSet<String>();

		for (GFFGene gene : features) {
			ret.add(gene.getSymbol());
		}

		return ret;
	}

	/**
	 * Parses the.
	 *
	 * @param file the file
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public static List<GFFGene> parse(Path file, String... types) throws IOException {
		List<GFFGene> features = new ArrayList<GFFGene>(50000);

		BufferedReader reader = FileUtils.newBufferedReader(file);

		String line;
		List<String> tokens;
		Chromosome chr;
		String type;
		int start;
		int end;
		Strand strand;
		Map<String, String> attributes;

		Splitter splitter = Splitter.onTab();
		
		try {
			while ((line = reader.readLine()) != null) {
				//System.err.println(line);

				tokens = splitter.text(line);

				chr = ChromosomeService.getInstance().parse(tokens.get(0));
				type = tokens.get(2);
				start = Integer.parseInt(tokens.get(3));
				end = Integer.parseInt(tokens.get(4));
				strand = Strand.parse(tokens.get(6));
				
				// Filter by type
				if (!filterByTypes(type, types)) {
					continue;
				}

				attributes = parseAttributes(tokens.get(8));

				String name = null;

				if (attributes.containsKey("gene_id")) {
					name = attributes.get("gene_id");
				}

				if (name == null) {
					if (attributes.containsKey("gene_name")) {
						name = attributes.get("gene_name");
					}
				}
				
				if (name == null) {
					if (attributes.containsKey("gene")) {
						name = attributes.get("gene");
					}
				}

				if (name != null) {
					GenomicRegion region = 
							GenomicRegion.create(chr, start, end, strand);
					
					GFFGene gene = new GFFGene(name, type, region);

					features.add(gene);
				}
			}
		} finally {
			reader.close();
		}

		return features;
	}
	
	private static boolean filterByTypes(String type, String... matchTypes) {
		if (matchTypes.length == 0) {
			return true;
		}
		
		for (String t : matchTypes) {
			if (type.equals(t)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Parses the attributes.
	 *
	 * @param attributes the attributes
	 * @return the map
	 */
	public static Map<String, String> parseAttributes(String attributes) {
		//System.err.println("attributes " + attributes);

		Map<String, String> ret = new TreeMap<String, String>();

		List<String> tokens = Splitter.on(';').text(attributes
				.replaceAll("; ", ";")
				.replaceFirst(";$", TextUtils.EMPTY_STRING)
				.replaceAll("\\\"", TextUtils.EMPTY_STRING)
				.replaceAll(" +", "="));

		for (String token : tokens) {
			List<String> values = Splitter.on('=').text(token);

			ret.put(values.get(0), values.get(1));
		}

		return ret;
	}

	/**
	 * Parses the GFF 3 attributes.
	 *
	 * @param tokens the tokens
	 * @return the map
	 */
	public static Map<String, String> parseGFF3Attributes(List<String> tokens) {
		return parseGFF3Attributes(tokens.get(8));
	}

	/**
	 * Parse gff3 formatted attributes, e.g. gene_id="BCL6";exon="2";
	 *
	 * @param attributes the attributes
	 * @return the map
	 */
	public static Map<String, String> parseGFF3Attributes(String attributes) {
		//System.err.println("attributes " + attributes);
		
		Map<String, String> ret = new HashMap<String, String>();

		List<String> tokens = Splitter.on(';').text(attributes);

		Splitter equalsSplitter = Splitter.on('=');

		for (String token : tokens) {
			List<String> values = equalsSplitter.text(token);

			if (values.size() > 1) {
				String name = values.get(0).trim();
				String value = values.get(1).trim().replace("\"", "");
				ret.put(name, value);
				
				//System.err.println("attribute " + name + " " + value);
			}
		}

		return ret;
	}

	/**
	 * Returns an attribute name where underscores are replaced with spaces
	 * and the name is converted to sentence case to make it more presentable
	 * in a table header etc.
	 *
	 * @param attribute the attribute
	 * @return the string
	 */
	public static String formatAttributeName(String attribute) {
		return TextUtils.titleCase(attribute.replace("_", " "));
	}
	
	public static FixedGapSearch<GFFGene> GFFToGapSearch(List<GFFGene> features) {
		FixedGapSearch<GFFGene> ret = new FixedGapSearch<GFFGene>(1000);

		for (GFFGene gene : features) {
			ret.add(gene.getRegion(), gene);
		}

		return ret;
	}
}
