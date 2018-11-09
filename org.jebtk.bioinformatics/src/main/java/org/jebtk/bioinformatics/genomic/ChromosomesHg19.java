/**
 * Copyright 2018 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.bioinformatics.genomic;

/**
 * Represents the chromosomes in hg19, compiled for use.
 */
public class ChromosomesHg19 extends ChromosomeDirs {

  /**
   * Instantiates a new chromosomes hg 19.
   */
  public ChromosomesHg19() {
    super(Genome.HG19);

    add(Chromosome.newChr("chr1", Genome.HG19, 249250621));
    add(Chromosome.newChr("chr2", Genome.HG19, 243199373));
    add(Chromosome.newChr("chr3", Genome.HG19, 198022430));
    add(Chromosome.newChr("chr4", Genome.HG19, 191154276));
    add(Chromosome.newChr("chr5", Genome.HG19, 180915260));
    add(Chromosome.newChr("chr6", Genome.HG19, 171115067));
    add(Chromosome.newChr("chr7", Genome.HG19, 159138663));
    add(Chromosome.newChr("chr8", Genome.HG19, 146364022));
    add(Chromosome.newChr("chr9", Genome.HG19, 141213431));
    add(Chromosome.newChr("chr10", Genome.HG19, 135534747));
    add(Chromosome.newChr("chr11", Genome.HG19, 135006516));
    add(Chromosome.newChr("chr12", Genome.HG19, 133851895));
    add(Chromosome.newChr("chr13", Genome.HG19, 115169878));
    add(Chromosome.newChr("chr14", Genome.HG19, 107349540));
    add(Chromosome.newChr("chr15", Genome.HG19, 102531392));
    add(Chromosome.newChr("chr16", Genome.HG19, 90354753));
    add(Chromosome.newChr("chr17", Genome.HG19, 81195210));
    add(Chromosome.newChr("chr18", Genome.HG19, 78077248));
    add(Chromosome.newChr("chr19", Genome.HG19, 59128983));
    add(Chromosome.newChr("chr20", Genome.HG19, 63025520));
    add(Chromosome.newChr("chr21", Genome.HG19, 48129895));
    add(Chromosome.newChr("chr22", Genome.HG19, 51304566));
    add(Chromosome.newChr("chrX", Genome.HG19, 155270560));
    add(Chromosome.newChr("chrY", Genome.HG19, 59373566));
    add(Chromosome.newChr("chrM", Genome.HG19, 16571));
  }
}
