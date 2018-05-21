/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.bioinformatics.genomic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;
import org.jebtk.core.network.UrlBuilder;

// TODO: Auto-generated Javadoc
/**
 * Maintains a connection to a caArray server.
 *
 * @author Antony Holmes Holmes
 */
public class URLGenes extends GenesDb {

  /**
   * The member url.
   */
  private UrlBuilder mUrl;

  /**
   * The member gene url.
   */
  private UrlBuilder mGeneUrl;

  /**
   * The member main url.
   */
  private UrlBuilder mMainUrl;

  /**
   * The member parser.
   */
  private JsonParser mParser;

  /**
   * Instantiates a new genes web.
   *
   * @param url the url
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public URLGenes(URL url) throws IOException {
    mUrl = new UrlBuilder(url);

    mGeneUrl = new UrlBuilder(mUrl).resolve("gene");

    mMainUrl = new UrlBuilder(mUrl).resolve("gene").resolve("main");

    mParser = new JsonParser();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.columbia.rdf.lib.bioinformatics.genome.GenesDb#getGenes(java.lang.
   * String)
   */
  @Override
  public List<Gene> getGenes(String id) throws IOException {
    List<Gene> genes = new ArrayList<Gene>();

    try {
      URL url = new UrlBuilder(mGeneUrl).resolve(id).toURL();

      Json json = mParser.parse(url);

      for (int i = 0; i < json.size(); ++i) {
        Json geneJson = json.get(i);

        Gene gene = new RdfGene(geneJson.getString("rdf"),
            geneJson.getString("refseq"), geneJson.getString("entrez"),
            geneJson.getString("symbol"),
            GenomicRegion.create(
                GenomeService.getInstance().chr(Genome.HG19,
                    geneJson.getString("chr")),
                geneJson.getInt("start"),
                geneJson.getInt("end"),
                Strand.parse(geneJson.getChar("strand"))));

        Json exonStartsJson = geneJson.get("exon_starts");
        Json exonEndsJson = geneJson.get("exon_ends");

        for (int j = 0; j < exonStartsJson.size(); ++j) {
          GenomicRegion exon = GenomicRegion.create(gene.mChr,
              exonStartsJson.get(j).getInt(),
              exonEndsJson.get(j).getInt());

          gene.addExon(exon);
        }

        genes.add(gene);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return genes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.columbia.rdf.lib.bioinformatics.genome.GenesDb#getMainGene(java.lang.
   * String)
   */
  @Override
  public Gene getMainGene(String id) throws IOException {
    Gene gene = null;

    try {
      URL url = new UrlBuilder(mMainUrl).resolve(id).toURL();

      Json json = mParser.parse(url);

      Json geneJson = json.get(0);

      gene = new RdfGene(geneJson.get("rdf").getString(),
          geneJson.getString("refseq"), geneJson.getString("entrez"),
          geneJson.getString("symbol"),
          GenomicRegion.create(
              GenomeService.getInstance().chr(Genome.HG19,
                  geneJson.getString("chr")),
              geneJson.getInt("start"),
              geneJson.getInt("end"),
              Strand.parse(geneJson.getChar("strand"))));

      Json exonStartsJson = geneJson.get("exon_starts");
      Json exonEndsJson = geneJson.get("exon_ends");

      for (int j = 0; j < exonStartsJson.size(); ++j) {
        GenomicRegion exon = GenomicRegion.create(gene.mChr,
            exonStartsJson.get(j).getInt(),
            exonEndsJson.get(j).getInt());

        gene.addExon(exon);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return gene;
  }
}
