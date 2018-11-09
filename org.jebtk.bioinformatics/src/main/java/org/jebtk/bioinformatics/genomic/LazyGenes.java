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

  public LazyGenes(Path file, Genome genome, GeneParser parser) {
    super(genome);

    mFile = file;
    mParser = parser;
  }

  /**
   * We only load genes on request
   */
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

  /*
   * @Override public void autoFindMainVariants() { autoLoad();
   * 
   * super.autoFindMainVariants(); }
   */

  @Override
  public List<GenomicEntity> getGenes(Genome genome, String id)
      throws IOException {
    autoLoad();

    return super.getGenes(genome, id);
  }

  @Override
  public List<GenomicEntity> findGenes(Genome genome, GenomicRegion region)
      throws IOException {
    autoLoad();

    return super.findGenes(genome, region);
  }

  @Override
  public List<GenomicEntity> findClosestGenes(Genome genome,
      GenomicRegion region) throws IOException {
    autoLoad();

    return super.findClosestGenes(genome, region);
  }

  @Override
  public List<GenomicEntity> findClosestGenesByTss(Genome genome,
      GenomicRegion region) throws IOException {
    autoLoad();

    return super.findClosestGenesByTss(genome, region);
  }

  @Override
  public Iterable<String> getIds(String type) {
    autoLoad();

    return super.getIds(type);
  }
}
