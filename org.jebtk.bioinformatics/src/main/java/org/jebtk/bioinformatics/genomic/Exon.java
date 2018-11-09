package org.jebtk.bioinformatics.genomic;

public class Exon extends GenomicEntity {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Exon(GenomicRegion region) {
    this(region, Strand.SENSE);
  }

  public Exon(GenomicRegion region, Strand strand) {
    super(EXON, region, strand);
  }

  public Exon(Chromosome chr, int start, int end) {
    super(EXON, chr, start, end);
  }
}
