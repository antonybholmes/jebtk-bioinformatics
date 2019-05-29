package org.jebtk.bioinformatics.genomic;

public enum GenomicType {
  GENE, TRANSCRIPT, EXON, UTR_5P, UTR_3P, CYTOBAND, SUPER_ENHANCER, ENHANCER, PEAK, REGION;

  public static GenomicType parse(String s) {
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
    } else if (s.startsWith("super_enhancer")) {
      return SUPER_ENHANCER;
    } else if (s.startsWith("peak")) {
      return PEAK;
    } else {
      return REGION;
    }
  }

  public static boolean le(GenomicType l1, GenomicType l2) {
    return l1.compareTo(l2) < 0;
  }

  public static boolean lt(GenomicType l1, GenomicType l2) {
    int c = l1.compareTo(l2);

    return c == 0 || c < 0;
  }
}
