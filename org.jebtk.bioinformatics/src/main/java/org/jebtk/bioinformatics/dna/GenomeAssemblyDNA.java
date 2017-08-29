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

import org.jebtk.bioinformatics.genomic.GenomeAssembly;
import org.jebtk.bioinformatics.genomic.RepeatMaskType;


// TODO: Auto-generated Javadoc
/**
 * Fast search of genome sequence files to get get actual genomic data.
 *
 * @author Antony Holmes Holmes
 */
public abstract class GenomeAssemblyDNA extends GenomeAssembly {
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
