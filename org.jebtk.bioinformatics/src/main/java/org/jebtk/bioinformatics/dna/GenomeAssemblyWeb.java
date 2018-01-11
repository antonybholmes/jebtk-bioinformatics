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
package org.jebtk.bioinformatics.dna;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.DataSource;
import org.jebtk.bioinformatics.genomic.GenomeAssembly;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.bioinformatics.genomic.RepeatMaskType;
import org.jebtk.bioinformatics.genomic.SequenceRegion;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;
import org.jebtk.core.network.UrlBuilder;

// TODO: Auto-generated Javadoc
/**
 * Maintains a connection to a caArray server.
 *
 * @author Antony Holmes Holmes
 */
public class GenomeAssemblyWeb extends GenomeAssembly {
  /**
   * The member url.
   */
  private UrlBuilder mUrl;

  /**
   * The member parser.
   */
  private JsonParser mParser;

  /** The m genomes url. */
  private UrlBuilder mGenomesUrl;

  /** The m dna url. */
  private UrlBuilder mDnaUrl;

  /**
   * Instantiates a new genome assembly web.
   *
   * @param url the url
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public GenomeAssemblyWeb(URL url) throws IOException {
    mUrl = new UrlBuilder(url);

    mDnaUrl = mUrl.resolve("dna");
    mGenomesUrl = mUrl.resolve("genomes");

    mParser = new JsonParser();
  }

  @Override
  public String getName() {
    return "web";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.columbia.rdf.lib.bioinformatics.genome.GenomeAssembly#getSequence(edu.
   * columbia.rdf.lib.bioinformatics.genome.GenomicRegion, boolean,
   * edu.columbia.rdf.lib.bioinformatics.genome.RepeatMaskType)
   */
  @Override
  public SequenceRegion getSequence(String genome,
      GenomicRegion region,
      boolean displayUpper,
      RepeatMaskType repeatMaskType) throws IOException {
    URL url;

    try {
      UrlBuilder tmpUrl = mDnaUrl.resolve(genome)
          .resolve(region.getChr().toString()).resolve(region.getStart())
          .resolve(region.getEnd()).param("strand", "s")
          .param("display", displayUpper ? "u" : "l");

      switch (repeatMaskType) {
      case UPPERCASE:
        tmpUrl = tmpUrl.param("mask", "u");
        break;
      case N:
        tmpUrl = tmpUrl.param("mask", "n");
        break;
      default:
        tmpUrl = tmpUrl.param("mask", "l");
        break;
      }

      url = tmpUrl.toUrl();

      // System.err.println(url);

      Json json = mParser.parse(url);

      String dna = json.get(0).get("seq").getAsString();

      SequenceRegion ret = new SequenceRegion(region, Sequence.create(dna));

      return ret;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomeAssembly#getGenomes()
   */
  @Override
  public List<String> getGenomes() throws IOException {

    List<String> ret = new ArrayList<String>(100);

    URL url;

    try {
      url = mGenomesUrl.toUrl();

      // System.err.println(url);

      Json json = mParser.parse(url);

      for (int i = 0; i < json.size(); ++i) {
        ret.add(json.get(i).getAsString());
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jebtk.bioinformatics.genome.GenomeAssembly#getDataSource()
   */
  @Override
  public DataSource getDataSource() {
    return DataSource.WEB;
  }
}
