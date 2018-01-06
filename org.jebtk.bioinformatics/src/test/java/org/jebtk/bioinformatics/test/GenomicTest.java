/**
 * Copyright 2016 Antony Holmes
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
package org.jebtk.bioinformatics.test;

import java.io.IOException;
import java.text.ParseException;

import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.junit.Test;

public class GenomicTest {
  @Test
  public void genomicParseTest() throws IOException, ParseException {
    System.err.println(GenomicRegion.parse("chrX:200-100"));
  }

  @Test
  public void genomicParseSingleTest() throws IOException, ParseException {
    System.err.println(GenomicRegion.parse("chrY:100"));
  }
}
