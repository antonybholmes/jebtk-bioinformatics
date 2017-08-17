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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.core.collections.ArrayListCreator;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.io.FileUtils;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class RTreeFeatureTree.
 */
public class RTreeFeatureTree extends RTree<RTreeFeature> {

	/** The m gene map. */
	private Map<Chromosome, List<RTreeFeature>> mGeneMap =
			DefaultTreeMap.create(new ArrayListCreator<RTreeFeature>());

	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.rtree.RTree#add(org.jebtk.bioinformatics.genome.GenomicRegion)
	 */
	public void add(RTreeFeature feature) {
		super.add(feature);
		
		mGeneMap.get(feature.getGene()).add(feature);
	}
	
	/**
	 * Gets the gene features.
	 *
	 * @param gene the gene
	 * @return the gene features
	 */
	public Collection<RTreeFeature> getGeneFeatures(String gene) {
		return mGeneMap.get(gene);
	}
	
	/**
	 * Load xml.
	 *
	 * @param file the file
	 * @return the r tree
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static RTree<RTreeFeature> loadXml(Path file) throws SAXException, IOException, ParserConfigurationException {
		if (file == null || !FileUtils.exists(file)) {
			return null;
		}

		InputStream stream = FileUtils.newBufferedInputStream(file);

		RTree<RTreeFeature> ret = null;
		
		try {
			ret = loadXml(stream);
		} finally {
			stream.close();
		}
		
		return ret;
	}

	/**
	 * Load xml.
	 *
	 * @param is the is
	 * @return true, if successful
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	public static RTree<RTreeFeature> loadXml(InputStream is) throws SAXException, IOException, ParserConfigurationException {
		if (is == null) {
			return null;
		}

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		RTreeXmlHandler handler = new RTreeXmlHandler();

		saxParser.parse(is, handler);

		return handler.getRTreeMap();
	}

}
