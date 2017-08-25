package org.jebtk.bioinformatics.dna;

import org.jebtk.core.RuntimeMessageException;

public class InvalidDnaException extends RuntimeMessageException {

	private static final long serialVersionUID = 1L;

	public InvalidDnaException(String message) {
		super(message);
	}

}
