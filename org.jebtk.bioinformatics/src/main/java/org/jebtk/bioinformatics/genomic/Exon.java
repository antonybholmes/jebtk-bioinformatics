package org.jebtk.bioinformatics.genomic;

public class Exon extends GenomicEntity {
  public Exon(GenomicRegion region) {
    this(region, Strand.SENSE);
  }

  public Exon(GenomicRegion region, Strand strand) {
    super(GenomicType.EXON, region, strand);
  }
}
