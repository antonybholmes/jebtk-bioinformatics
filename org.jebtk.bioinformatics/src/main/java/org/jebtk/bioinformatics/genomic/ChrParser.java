package org.jebtk.bioinformatics.genomic;

import java.util.regex.Matcher;

public class ChrParser {

  /**
   * Look for the numerical part of the chromosome to give it a numerical order.
   * 
   * @param name
   * @return
   */
  public int getId(String name) {
    Matcher matcher = Chromosome.CHR_NUM_GROUP_REGEX.matcher(name);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    } else {
      // Assume a letter so subtract 65 (so A becomes 0, B 1 etc) and then
      // Shift into upper 8 bits of int
      return (name.charAt(0) - 65) << 24;
    }
  }
}
