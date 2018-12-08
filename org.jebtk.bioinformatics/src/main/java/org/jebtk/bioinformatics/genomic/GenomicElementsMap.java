package org.jebtk.bioinformatics.genomic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeSetCreator;

public class GenomicElementsMap extends GenomicElementsDB implements Iterable<Entry<Chromosome, Set<GenomicElement>>> {
  private IterMap<Chromosome, Set<GenomicElement>> mElementMap = 
      DefaultTreeMap.create(new TreeSetCreator<GenomicElement>());
  
  private int mSize = 0;
  

  @Override
  public Iterator<Entry<Chromosome, Set<GenomicElement>>> iterator() {
    return mElementMap.iterator();
  }
  
  /**
   * Return the genomic elements as a flat list, ordered by chromosome and
   * position.
   * 
   * @return
   */
  public List<GenomicElement> toList() {
    List<GenomicElement> ret = new ArrayList<GenomicElement>();
    
    for (Entry<Chromosome, Set<GenomicElement>> item : this) {
      for (GenomicElement e : item.getValue()) {
        ret.add(e);
      }
    }
    
    return ret;
  }

  public int size() {
    return mSize;
  }


  @Override
  public void add(GenomicElement e) {
    mElementMap.get(e.mChr).add(e);
    
    ++mSize;
  }

  @Override
  public List<GenomicElement> getElements(Genome g, String search, String type)
      throws IOException {
    return Collections.emptyList();
  }


  @Override
  public List<GenomicElement> find(Genome genome,
      GenomicRegion region,
      String type) throws IOException {
    return GenomicRegions
        .getFixedGapSearch(mElementMap.get(region.mChr))
        .getFeatureSet(region);
  }
}
