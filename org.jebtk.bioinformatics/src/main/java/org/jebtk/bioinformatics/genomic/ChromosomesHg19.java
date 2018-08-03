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
    super("Human", Genome.HG19);

    add(new Chromosome("chr1", Genome.HG19, 249250621));
    add(new Chromosome("chr2", Genome.HG19, 243199373));
    add(new Chromosome("chr3", Genome.HG19, 198022430));
    add(new Chromosome("chr4", Genome.HG19, 191154276));
    add(new Chromosome("chr5", Genome.HG19, 180915260));
    add(new Chromosome("chr6", Genome.HG19, 171115067));
    add(new Chromosome("chr7", Genome.HG19, 159138663));
    add(new Chromosome("chr8", Genome.HG19, 146364022));
    add(new Chromosome("chr9", Genome.HG19, 141213431));
    add(new Chromosome("chr10", Genome.HG19, 135534747));
    add(new Chromosome("chr11", Genome.HG19, 135006516));
    add(new Chromosome("chr12", Genome.HG19, 133851895));
    add(new Chromosome("chr13", Genome.HG19, 115169878));
    add(new Chromosome("chr14", Genome.HG19, 107349540));
    add(new Chromosome("chr15", Genome.HG19, 102531392));
    add(new Chromosome("chr16", Genome.HG19, 90354753));
    add(new Chromosome("chr17", Genome.HG19, 81195210));
    add(new Chromosome("chr18", Genome.HG19, 78077248));
    add(new Chromosome("chr19", Genome.HG19, 59128983));
    add(new Chromosome("chr20", Genome.HG19, 63025520));
    add(new Chromosome("chr21", Genome.HG19, 48129895));
    add(new Chromosome("chr22", Genome.HG19, 51304566));
    add(new Chromosome("chrX", Genome.HG19, 155270560));
    add(new Chromosome("chrY", Genome.HG19, 59373566));
    add(new Chromosome("chrM", Genome.HG19, 16571));
  }
}
