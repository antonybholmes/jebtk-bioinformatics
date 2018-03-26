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
package org.jebtk.bioinformatics.conservation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;
import org.jebtk.core.network.UrlBuilder;

// TODO: Auto-generated Javadoc
/**
 * Maintains a connection to a caArray server.
 *
 * @author Antony Holmes Holmes
 */
public class ConservationAssemblyWeb extends ConservationAssembly {
  /**
   * The member url.
   */
  private UrlBuilder mUrl;

  /**
   * The member score url.
   */
  private UrlBuilder mScoreUrl;

  /**
   * The member parser.
   */
  private JsonParser mParser;

  /**
   * Instantiates a new conservation assembly web.
   *
   * @param url the url
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ConservationAssemblyWeb(URL url) throws IOException {
    mUrl = new UrlBuilder(url);

    mScoreUrl = new UrlBuilder(mUrl).resolve("scores");

    mParser = new JsonParser();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.columbia.rdf.lib.bioinformatics.conservation.ConservationAssembly#
   * getScores(edu.columbia.rdf.lib.bioinformatics.genome.GenomicRegion)
   */
  @Override
  public List<Double> getScores(GenomicRegion region)
      throws IOException, ParseException {
    List<Double> scores = new ArrayList<Double>();

    try {
      URL url = new UrlBuilder(mScoreUrl).resolve(region.getChr().toString())
          .resolve(region.getStart()).resolve(region.getEnd()).toURL();

      // System.err.println(url);

      Json json = mParser.parse(url);

      Json scoresJson = json.get(0).get("scores");

      for (int i = 0; i < scoresJson.size(); ++i) {
        scores.add(scoresJson.get(i).getAsDouble());
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return scores;
  }
}
