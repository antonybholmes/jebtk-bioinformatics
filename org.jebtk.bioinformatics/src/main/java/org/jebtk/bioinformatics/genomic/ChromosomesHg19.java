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
public class ChromosomesHg19 extends Chromosomes {

  /**
   * Instantiates a new chromosomes hg 19.
   */
  public ChromosomesHg19() {
    super("Human", Genome.HG19);

    add(new Chromosome(1, "chr1", 249250621, Genome.HG19));
    add(new Chromosome(2, "chr2", 243199373, Genome.HG19));
    add(new Chromosome(3, "chr3", 198022430, Genome.HG19));
    add(new Chromosome(4, "chr4", 191154276, Genome.HG19));
    add(new Chromosome(5, "chr5", 180915260, Genome.HG19));
    add(new Chromosome(6, "chr6", 171115067, Genome.HG19));
    add(new Chromosome(7, "chr7", 159138663, Genome.HG19));
    add(new Chromosome(8, "chr8", 146364022, Genome.HG19));
    add(new Chromosome(9, "chr9", 141213431, Genome.HG19));
    add(new Chromosome(10, "chr10", 135534747, Genome.HG19));
    add(new Chromosome(11, "chr11", 135006516, Genome.HG19));
    add(new Chromosome(12, "chr12", 133851895, Genome.HG19));
    add(new Chromosome(13, "chr13", 115169878, Genome.HG19));
    add(new Chromosome(14, "chr14", 107349540, Genome.HG19));
    add(new Chromosome(15, "chr15", 102531392, Genome.HG19));
    add(new Chromosome(16, "chr16", 90354753, Genome.HG19));
    add(new Chromosome(17, "chr17", 81195210, Genome.HG19));
    add(new Chromosome(18, "chr18", 78077248, Genome.HG19));
    add(new Chromosome(19, "chr19", 59128983, Genome.HG19));
    add(new Chromosome(20, "chr20", 63025520, Genome.HG19));
    add(new Chromosome(21, "chr21", 48129895, Genome.HG19));
    add(new Chromosome(22, "chr22", 51304566, Genome.HG19));
    add(new Chromosome(23, "chrX", 155270560, Genome.HG19));
    add(new Chromosome(24, "chrY", 59373566, Genome.HG19));
    add(new Chromosome(25, "chrM", 16571, Genome.HG19));
  }
}
