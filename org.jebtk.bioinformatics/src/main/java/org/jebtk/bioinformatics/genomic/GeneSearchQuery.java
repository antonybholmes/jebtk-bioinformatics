package org.jebtk.bioinformatics.genomic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jebtk.core.search.SearchQuery;

public class GeneSearchQuery extends SearchQuery<GenomicEntity> {

  private GFBGenes mGenes;
  private GenomicType mType;

  public GeneSearchQuery(GFBGenes genes) {
    this(genes, GenomicType.GENE);
  }

  public GeneSearchQuery(GFBGenes genes, GenomicType type) {
    mGenes = genes;
    mType = type;
  }

  @Override
  public Collection<GenomicEntity> match(String s,
      boolean exact,
      boolean include) {

    String ls = s.toLowerCase();

    List<GenomicEntity> ret = new ArrayList<GenomicEntity>();

    // System.err.println("qs " + s);

    try {
      List<GenomicEntity> genes = mGenes.getGenes(ls, mType);

      // if (!include) {
      // Add all the genes not in this list of genes
      // genes = CollectionUtils.compliment(mGenes.getGenes(), genes);
      // }

      if (exact) {
        genes = exact(ls, genes);
      }

      ret.addAll(genes);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ret;
  }

  /**
   * Return only the exact matches.
   * 
   * @param s
   * @param genes
   * @return
   */
  private static List<GenomicEntity> exact(String s,
      Collection<GenomicEntity> genes) {
    List<GenomicEntity> ret = new ArrayList<GenomicEntity>(genes.size());

    for (GenomicEntity gene : genes) {
      boolean found = false;

      for (Entry<String, String> e : gene.getIds()) {
        if (e.getValue().toLowerCase().equals(s)) {
          found = true;
          break;
        }
      }

      if (found) {
        ret.add(gene);
      }
    }

    return ret;
  }

}
