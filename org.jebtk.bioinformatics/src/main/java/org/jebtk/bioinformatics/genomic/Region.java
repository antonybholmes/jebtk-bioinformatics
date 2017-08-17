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

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.jebtk.core.io.Io;
import org.jebtk.core.text.FormattedTxt;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;


// TODO: Auto-generated Javadoc
/**
 * Describes a region of a genome.
 *
 * @author Antony Holmes Holmes
 */
public class Region implements FormattedTxt {
	
	/** The Constant REGION_REGEX. */
	private static final Pattern REGION_REGEX = Pattern.compile("\\d+(-\\d+)");
	
	/**
	 * The member start.
	 */
	protected int mStart;
	
	/**
	 * The member end.
	 */
	protected int mEnd;
	
	/**
	 * The member length.
	 */
	private int mLength;

	/**
	 * Instantiates a new region.
	 *
	 * @param region the region
	 */
	public Region(Region region) {
		this(region.mStart, region.mEnd);
	}
	
	/**
	 * Instantiates a new region.
	 *
	 * @param start the start
	 * @param end the end
	 */
	public Region(int start, 
			int end) {
		// The start must be at least 1
		mStart = start;
		mEnd = end;
		

		// Swap if the coordinates are the wrong way around
		if (mStart > mEnd) {
			int t = mStart;
			mStart = mEnd;
			mEnd = t;
		}
		
		mLength = mEnd - mStart + 1;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return mStart;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public int getEnd() {
		return mEnd;
	}
	
	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public int getLength() {
		return mLength;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return mStart + "-" + mEnd;
	}

	/* (non-Javadoc)
	 * @see org.abh.lib.FormattedTxt#formattedTxt(java.lang.Appendable)
	 */
	@Override
	public void formattedTxt(Appendable buffer) throws IOException {
		buffer.append(Integer.toString(mStart));
		buffer.append(TextUtils.TAB_DELIMITER);
		buffer.append(Integer.toString(mEnd));
		buffer.append(TextUtils.NEW_LINE);
	}
	
	/**
	 * Parses the region.
	 *
	 * @param location the location
	 * @return the region
	 */
	public static Region parseRegion(String location) {
		if (Io.isEmptyLine(location)) {
			return null;
		}
		
		if (location.contains(TextUtils.NA)) {
			return null;
		}
		
		location = location.trim().replaceAll(",", "");
		
		if (!isRegion(location)) {
			return null;
		}

		List<String> tokens;
		int start;
		int end;
		if (location.indexOf("-") != -1) {
			tokens = Splitter.on('-').text(location); //)    .(tokens.get(1), '-');

			start = Integer.parseInt(tokens.get(0));
			end = Integer.parseInt(tokens.get(1));
		} else {
			// single position

			start = Integer.parseInt(location);
			end = start;
		}

		return new Region(start, end);
	}

	/**
	 * Returns true if the text matches a region of the form m-n.
	 *
	 * @param region the region
	 * @return true, if is region
	 */
	protected static boolean isRegion(String region) {
		return REGION_REGEX.matcher(region).find();
	}

	/**
	 * Shift.
	 *
	 * @param region the region
	 * @param shift the shift
	 * @return the region
	 */
	public static Region shift(Region region, int shift) {
		// bound the positions so they dont exceed the chromosome bounds
		int start = Math.max(1, region.mStart + shift);
		int end = Math.max(1, region.mEnd + shift);
				
		return new Region(start, end);
	}
}
