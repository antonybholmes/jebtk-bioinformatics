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

import org.jebtk.bioinformatics.genomic.Gene;
import org.jebtk.bioinformatics.genomic.GenomicRegion;

// TODO: Auto-generated Javadoc
/**
 * The Class GFFGene.
 */
public class GFFGene extends Gene {

	/** The m type. */
	private String mType;
	
	/**
	 * Instantiates a new GFF gene.
	 *
	 * @param symbol the symbol
	 * @param type the type
	 * @param chr the chr
	 * @param start the start
	 * @param end the end
	 * @param strand the strand
	 */
	public GFFGene(String symbol,
			String type,
			GenomicRegion region) {
		super(region);
		
		setSymbol(symbol);
		
		mType = type;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return mType;
	}

	/* (non-Javadoc)
	 * @see org.jebtk.bioinformatics.genome.Gene#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " - " + mType;
	}
	
	@Override
	public int compareTo(Gene gene) {
		if (gene instanceof GFFGene) {
			GFFGene g = (GFFGene)gene;

			int c = mType.compareTo(g.mType);
			
			if (c != 0) {
				return c;
			}
		}
		
		// names are the same so look at coordinates
		return super.compareTo(gene);
	}
}
