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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.core.http.UrlBuilder;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;

/**
 * Find overlapping genes using a web service.
 *
 * @author Antony Holmes
 */
public class WebGenes extends Genes {

  // private UrlBuilder mUrl;
  private UrlBuilder mFindUrl;
  private UrlBuilder mSearchUrl;
  private UrlBuilder mGeneDbsUrl;

  public WebGenes(URL url) {
    this(new UrlBuilder(url));
  }

  public WebGenes(UrlBuilder url) {
    // mUrl = url;

    // UrlBuilder genesUrl = url.resolve("genes");
    // Default to returning transcripts
    mFindUrl = url.resolve("find").param("t", "t");
    // mClosestUrl = mGenesUrl.resolve("closest");
    mSearchUrl = url.resolve("search").param("t", "t");
    mGeneDbsUrl = url.resolve("databases");
  }

  @Override
  public List<GenomicEntity> findGenes(Genome genome, GenomicRegion region) {
    UrlBuilder url = mFindUrl.param("genome", genome.getName())
        .param("assembly", genome.getAssembly())
        .param("track", genome.getTrack()).param("chr", region.mChr.toString())
        .param("s", region.mStart).param("e", region.mEnd);

    List<GenomicEntity> ret = parseGenes(genome, url);

    return ret;
  }

  @Override
  public List<GenomicEntity> getGenes(Genome genome, String search) {
    UrlBuilder url = mSearchUrl.param("genome", genome.getName())
        .param("assembly", genome.getAssembly())
        .param("track", genome.getTrack()).param("s", search);

    List<GenomicEntity> ret = parseGenes(genome, url);

    return ret;
  }

  private static List<GenomicEntity> parseGenes(Genome genome, UrlBuilder url) {
    List<GenomicEntity> ret = new ArrayList<GenomicEntity>();

    JsonParser parser = new JsonParser();

    System.err.println(url);

    try {
      Json json = parser.parse(url);

      // Position searches embed the results in an array called genes so
      // we will use that if found, otherwise just assume the json represents
      // an array.
      if (json.containsKey("genes")) {
        json = json.get("genes");
      }

      for (int i = 0; i < json.size(); ++i) {
        Json geneJson = json.get(i);

        GenomicRegion l = GenomicRegion.parse(genome,
            geneJson.getString("loc"));

        Strand s = Strand.parse(geneJson.getString("strand"));

        Transcript gene = new Transcript(l, s);

        Json exonsJson = geneJson.get("exons");

        for (int j = 0; j < exonsJson.size(); ++j) {
          GenomicRegion exon = GenomicRegion.parse(genome,
              exonsJson.get(j).getString("loc"));

          gene.addExon(exon);
        }

        Json idsJson = geneJson.get("ids");

        addId(GenomicEntity.GENE_ID_TYPE, idsJson, gene);
        addId(GenomicEntity.TRANSCRIPT_ID_TYPE, idsJson, gene);
        // addId("symbol", idJson, gene);
        addId(GenomicEntity.GENE_NAME_TYPE, idsJson, gene);

        ret.add(gene);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ret;
  }

  @Override
  public Iterable<Genome> getGenomes() {
    return getGeneDBs(mGeneDbsUrl);
  }

  private static List<Genome> getGeneDBs(UrlBuilder url) {
    List<Genome> ret = new ArrayList<Genome>();

    JsonParser parser = new JsonParser();

    try {
      Json json = parser.parse(url);

      for (int i = 0; i < json.size(); ++i) {
        Json dbJson = json.get(i);

        Genome genome = new Genome(GenomeService.getInstance().guessGenome(
            dbJson.getString("assembly")), dbJson.getString("track"));

        System.err.println("aha " + genome);

        ret.add(genome);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ret;
  }

  private static void addId(String name, Json json, GenomicEntity gene) {
    if (json.containsKey(name)) {
      gene.setProperty(name, json.getString(name));
    }
  }

}