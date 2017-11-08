package org.jebtk.bioinformatics.genomic;

public enum GeneType {
	GENE,
	TRANSCRIPT,
	EXON,
	UTR_5P,
	UTR_3P,
	UNDEFINED;

	public static GeneType parse(String s) {
		if (s.startsWith("gene")) {
			return GENE;
		} else if (s.startsWith("transcript")) {
			return TRANSCRIPT;
		} else if (s.startsWith("exon")) {
			return EXON;
		} else if (s.startsWith("5p_utr")) {
			return UTR_5P;
		} else if (s.startsWith("3p_utr")) {
			return UTR_3P;
		} else {
			return UNDEFINED;
		}
	}

	public static boolean le(GeneType l1, GeneType l2) {
		return l1.compareTo(l2) < 0;
	}
	
	public static boolean lt(GeneType l1, GeneType l2) {
		int c = l1.compareTo(l2);
		
		return c == 0 || c < 0;
	}
}
