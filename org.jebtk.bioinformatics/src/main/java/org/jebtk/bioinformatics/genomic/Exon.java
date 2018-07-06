package org.jebtk.bioinformatics.genomic;

public class Exon extends GenomicEntity {
  public Exon(GenomicRegion region) {
    super(GenomicType.EXON, region);
  }
}
