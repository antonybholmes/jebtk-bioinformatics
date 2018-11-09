package org.jebtk.bioinformatics.genomic;

public interface ChromosomeReader extends Iterable<Chromosome> {
  public Genome getGenome();
}
