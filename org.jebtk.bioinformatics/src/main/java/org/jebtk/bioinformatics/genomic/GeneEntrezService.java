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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.core.Resources;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Map symbols to entrez.
 *
 * @author Antony Holmes Holmes
 */
public class GeneEntrezService {

  /**
   * The Class GeneEntrezServiceLoader.
   */
  private static class GeneEntrezServiceLoader {

    /** The Constant INSTANCE. */
    private static final GeneEntrezService INSTANCE = new GeneEntrezService();
  }

  /**
   * Gets the single instance of SettingsService.
   *
   * @return single instance of SettingsService
   */
  public static GeneEntrezService getInstance() {
    return GeneEntrezServiceLoader.INSTANCE;
  }

  /**
   * The constant DEFAULT_FILE.
   */
  public static final String DEFAULT_FILE = "res/ucsc_refseq_exons_entrez_hg19.txt";

  // public static final String DEFAULT_FILE =
  // "res/hugo_gene_symbol_entrez.txt";

  // public static final String DEFAULT_FILE =
  // "res/hugo_gene_symbol_refseq_entrez.txt";

  /**
   * The constant LOG.
   */
  private static final Logger LOG = LoggerFactory
      .getLogger(GeneEntrezService.class);

  /**
   * The member symbol map.
   */
  private Map<String, GeneSymbol> mSymbolMap = new HashMap<String, GeneSymbol>();

  /**
   * The member entrez map.
   */
  private Map<String, GeneSymbol> mEntrezMap = new HashMap<String, GeneSymbol>();

  /**
   * Instantiates a new gene entrez service.
   */
  private GeneEntrezService() {
    // do nothing
  }

  /**
   * Load.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public final void load() throws IOException {
    LOG.info("Parsing gene reference {}...", DEFAULT_FILE);

    load(Resources.getResGzipReader(DEFAULT_FILE));
  }

  /**
   * Load.
   *
   * @param file the file
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public final void load(File file) throws IOException {
    LOG.info("Parsing gene reference {}", file);

    load(new BufferedReader(new FileReader(file)));
  }

  /**
   * Load.
   *
   * @param reader the reader
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private final void load(BufferedReader reader) throws IOException {

    String line;
    List<String> tokens;

    try {
      // skip header
      line = reader.readLine();

      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

        String symbol = tokens.get(1);
        // String symbol = tokens.get(0);

        if (symbol.length() == 0) {
          continue;
        }

        String entrez = tokens.get(0);
        // String entrez = tokens.get(1);

        mSymbolMap.put(symbol.toLowerCase(), new GeneSymbol(entrez, symbol));
        mEntrezMap.put(entrez.toLowerCase(), new GeneSymbol(entrez, symbol));
      }
    } finally {
      reader.close();
    }
  }

  /**
   * Lookup by symbol.
   *
   * @param symbol the symbol
   * @return the gene symbol
   */
  public GeneSymbol lookupBySymbol(String symbol) {
    if (symbol == null) {
      return null;
    }

    return mSymbolMap.get(symbol.toLowerCase());
  }

  /**
   * Lookup by entrez.
   *
   * @param entrez the entrez
   * @return the gene symbol
   */
  public GeneSymbol lookupByEntrez(String entrez) {
    return mEntrezMap.get(entrez.toLowerCase());
  }

  /**
   * Checks if is valid symbol.
   *
   * @param symbol the symbol
   * @return true, if is valid symbol
   */
  public boolean isValidSymbol(String symbol) {
    return mSymbolMap.containsKey(symbol.toLowerCase());
  }

  /**
   * Checks if is valid entrez.
   *
   * @param entrez the entrez
   * @return true, if is valid entrez
   */
  public boolean isValidEntrez(String entrez) {
    return mEntrezMap.containsKey(entrez.toLowerCase());
  }

  /**
   * Gets the gene count.
   *
   * @return the gene count
   */
  public int getGeneCount() {
    return mEntrezMap.size();
  }
}