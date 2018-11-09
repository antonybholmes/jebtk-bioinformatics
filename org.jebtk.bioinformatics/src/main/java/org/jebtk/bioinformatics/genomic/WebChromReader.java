/**
 * Copyright 2017 Antony Holmes
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jebtk.core.http.UrlBuilder;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonParser;

/**
 * The Class ChromosomeParser.
 */
public class WebChromReader implements ChromosomeReader {

  // private static final Logger LOG = LoggerFactory
  // .getLogger(WebChromReader.class);

  private Genome mGenome;

  private List<Chromosome> mChrs = new ArrayList<Chromosome>();

  private boolean mAutoLoad = true;

  private UrlBuilder mUrl;

  private Json chrJson;

  public WebChromReader(UrlBuilder url, Genome genome) {
    mGenome = genome;

    mUrl = url.resolve("genomes").resolve(genome).resolve("chrs");
  }

  private void autoLoad() throws IOException {
    if (mAutoLoad) {
      JsonParser parser = new JsonParser();

      Json json = parser.parse(mUrl);

      for (int i = 0; i < json.size(); ++i) {
        chrJson = json.get(i);

        Chromosome chr = Chromosome.newChr(chrJson.getString("chr"), mGenome,
            chrJson.getInt("bp"));

        mChrs.add(chr);
      }

      Collections.sort(mChrs);

      mAutoLoad = false;
    }
  }

  @Override
  public Iterator<Chromosome> iterator() {
    try {
      autoLoad();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return mChrs.iterator();
  }

  /**
   * Returns the genome reference, for example hg19.
   * 
   * @return
   */
  @Override
  public Genome getGenome() {
    return mGenome;
  }
}
