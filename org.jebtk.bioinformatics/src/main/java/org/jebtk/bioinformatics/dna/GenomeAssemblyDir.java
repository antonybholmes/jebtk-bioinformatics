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
package org.jebtk.bioinformatics.dna;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomeAssembly;
import org.jebtk.bioinformatics.genomic.RepeatMaskType;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;


// TODO: Auto-generated Javadoc
/**
 * Fast search of genome sequence files to get get actual genomic data.
 *
 * @author Antony Holmes Holmes
 */
public abstract class GenomeAssemblyDir extends GenomeAssembly {
	
	/** The Constant EMPTY_BYTES. */
	protected static final byte[] EMPTY_BYTES = new byte[0];

	/**
	 * The member directory.
	 */
	protected Path mDirectory;

	/**
	 * The member file map.
	 */
	protected Map<Chromosome, Path> mFileMap = 
			new HashMap<Chromosome, Path>();

	/**
	 * Directory containing genome files which must be of the form
	 * chr.n.txt. Each file must contain exactly one line consisting
	 * of the entire chromosome.
	 *
	 * @param directory the directory
	 */
	public GenomeAssemblyDir(Path directory) {
		mDirectory = directory;
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param file the file
	 * @param start the start
	 * @param end the end
	 * @return the sequence
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getSequence(Path file,
			int start,
			int end) throws IOException {
		
		byte[] buf = getBytes(file, start - 1, end - 1);
		
		return String.valueOf(Io.intToChar(buf)).toUpperCase();
	}
	
	/**
	 * Gets the bytes.
	 *
	 * @param file the file
	 * @param start the start
	 * @param end the end
	 * @return the bytes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] getBytes(Path file, 
			int start, 
			int end) throws IOException {
		
		//System.err.println(file + " " + FileUtils.exists(file));
		
		if (!FileUtils.exists(file)) {
			return EMPTY_BYTES;
		}
		
		//("Extract sequence for {} from {}...", start, end);

		
		InputStream in = FileUtils.newBufferedInputStream(file);
		
		//GZIPInputStream in = new GZIPInputStream(new FileInputStream(file), 65536);

		int l = end - start + 1;

		byte[] buf = new byte[l];

		try {
			in.skip(start);
			in.read(buf);
		} finally {
			in.close();
		}
		
		return buf; //Io.unsignedToSigned(buf);
	}
	
	/**
	 * To char.
	 *
	 * @param v the v
	 * @param repeatMaskType the repeat mask type
	 * @return the char
	 */
	public static char toChar(int v, RepeatMaskType repeatMaskType) {
		char c = toChar(v);
		
		switch(repeatMaskType) {
		case UPPERCASE:
			
			switch(c) {
			case 'a':
				return 'A';
			case 'c':
				return 'C';
			case 'g':
				return 'G';
			case 't':
				return 'T';
			default:
				return c;
			}
		case N:
			switch(c) {
			case 'a':
			case 'c':
			case 'g':
			case 't':
				return 'N';
			default:
				return c;
			}
		default:
			// default to the lower case in which case return the sequence
			// as is since this will include the mask
			return c;
		}
	}
	
	/**
	 * To char.
	 *
	 * @param v the v
	 * @return the char
	 */
	public static char toChar(int v) {
		switch (v) {
		case 0:
			return 'A';
		case 1:
			return 'C';
		case 2:
			return 'G';
		case 3:
			return 'T';
		case 4:
			return 'a';
		case 5:
			return 'c';
		case 6:
			return 'g';
		case 7:
			return 't';
		default:
			return 'N';
		}
	}
	
	/**
	 * Convert character to lower case.
	 *
	 * @param c the c
	 * @return the char
	 */
	public static char toLower(int c) {
		switch(c) {
		case 'A':
			return 'a';
		case 'C':
			return 'c';
		case 'G':
			return 'g';
		case 'T':
			return 't';
		default:
			return 'n';
		}
	}
	
	/**
	 * To lower.
	 *
	 * @param c the c
	 * @return the char
	 */
	public static char toLower(char c) {
		switch(c) {
		case 'A':
			return 'a';
		case 'C':
			return 'c';
		case 'G':
			return 'g';
		case 'T':
			return 't';
		default:
			return 'n';
		}
	}
}
