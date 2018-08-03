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
public class LazyGenes extends FixedGapGenes {
  private GeneParser mParser;
  private boolean mAutoLoad = true;
  private Path mFile;

  public LazyGenes(Path file, String db, String genome, GeneParser parser) {
    super(db, genome);

    mFile = file;
    mParser = parser;
  }

  /**
   * We only load genes on request
   */
  private void autoLoad() {
    if (mAutoLoad) {
      try {
        mParser.parse(mFile, mDb, mGenome, this);
      } catch (IOException e) {
        e.printStackTrace();
      }

      mAutoLoad = false;
    }
  }

  /*
   * @Override public void autoFindMainVariants() { autoLoad();
   * 
   * super.autoFindMainVariants(); }
   */

  @Override
  public List<GenomicEntity> getGenes(String db, String genome, String id)
      throws IOException {
    autoLoad();

    return super.getGenes(db, genome, id);
  }

  @Override
  public List<GenomicEntity> findGenes(String db, GenomicRegion region)
      throws IOException {
    autoLoad();

    return super.findGenes(db, region);
  }

  @Override
  public List<GenomicEntity> findClosestGenes(String db, GenomicRegion region)
      throws IOException {
    autoLoad();

    return super.findClosestGenes(db, region);
  }

  @Override
  public List<GenomicEntity> findClosestGenesByTss(String db,
      GenomicRegion region) throws IOException {
    autoLoad();

    return super.findClosestGenesByTss(db, region);
  }

  @Override
  public Iterable<String> getIds(String type) {
    autoLoad();

    return super.getIds(type);
  }
}
