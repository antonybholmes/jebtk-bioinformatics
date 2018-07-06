package org.jebtk.bioinformatics.genomic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Only load genes when requested.
 * 
 * @author antony
 *
 */
public class LazyGenes extends Genes {
  private GeneParser mParser;
  private boolean mAutoLoad = true;
  private Path mFile;
  private String mGenome;

  public LazyGenes(Path file, String genome, GeneParser parser) {
    mFile = file;
    mGenome = genome;
    mParser = parser;
  }

  private void autoLoad() {
    if (mAutoLoad) {
      try {
        mParser.parse(mFile, mGenome, this);
      } catch (IOException e) {
        e.printStackTrace();
      }

      mAutoLoad = false;
    }
  }

  @Override
  public void autoFindMainVariants() {
    autoLoad();

    super.autoFindMainVariants();
  }

  @Override
  public List<GenomicEntity> getGenes(String symbol) {
    autoLoad();

    return super.getGenes(symbol);
  }

  @Override
  public List<GenomicEntity> findGenes(GenomicRegion region) {
    autoLoad();

    return super.findGenes(region);
  }

  @Override
  public List<GenomicEntity> findClosestGenes(GenomicRegion region) {
    autoLoad();

    return super.findClosestGenes(region);
  }

  @Override
  public List<GenomicEntity> findClosestGenesByTss(GenomicRegion region) {
    autoLoad();

    return super.findClosestGenesByTss(region);
  }

  @Override
  public Iterable<String> getIds(String type) {
    autoLoad();

    return super.getIds(type);
  }
}
