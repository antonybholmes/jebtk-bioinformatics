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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.TextUtils;



// TODO: Auto-generated Javadoc
/**
 * Server for genome feature annotations.
 *
 * @author Antony Holmes Holmes
 *
 */
public class ChromosomeSizes {
	//private static final ChromosomeSizes instance = new ChromosomeSizes();

	/**
	 * The constant DEFAULT_FILE.
	 */
	public static final File DEFAULT_HG19_FILE = 
			new File("res/ucsc_chromosome_sizes_hg19.txt.gz");

	//public static final ChromosomeSizes getInstance() {
	//	return instance;
	//}

	/**
	 * The size map.
	 */
	// genome, group, feature name
	private Map<Chromosome, Integer> mSizeMap = 
			new HashMap<Chromosome, Integer>();

	/**
	 * Instantiates a new chromosome sizes.
	 */
	public ChromosomeSizes() {
		// Do nothing
	}

	/**
	 * Returns the number of bases in the chromosome.
	 *
	 * @param chromosome the chromosome
	 * @return the size
	 */
	public int getSize(Chromosome chr) {
		if (!mSizeMap.containsKey(chr)) {
			return Integer.MAX_VALUE;
		}

		return mSizeMap.get(chr);
	}

	/**
	 * Parses the.
	 *
	 * @param file the file
	 * @return the chromosome sizes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ChromosomeSizes parse(Path file) throws IOException {

		BufferedReader buffer = FileUtils.newBufferedReader(file);

		ChromosomeSizes ret = null;

		try {
			ret = parse(buffer);
		} finally {
			buffer.close();
		}

		return ret;
	}

	/**
	 * Parses the.
	 *
	 * @param reader the reader
	 * @return the chromosome sizes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static ChromosomeSizes parse(BufferedReader reader) throws IOException {

		ChromosomeSizes sizes = new ChromosomeSizes();

		reader.readLine();

		String line;
		List<String> tokens;

		while ((line = reader.readLine()) != null) {
			if (Io.isEmptyLine(line)) {
				continue;
			}

			tokens = TextUtils.tabSplit(line);

			String chr = tokens.get(0);

			if (chr.contains("_")) {
				continue;
			}

			Chromosome chromosome = Chromosome.parse(chr);

			// skip non standard chrs
			if (chromosome == null) {
				continue;
			}


			int size = Integer.parseInt(tokens.get(1));

			sizes.mSizeMap.put(chromosome, size);
		}

		return sizes;
	}
}