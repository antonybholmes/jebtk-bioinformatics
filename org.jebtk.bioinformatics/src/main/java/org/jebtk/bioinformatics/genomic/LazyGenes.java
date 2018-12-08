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
  public List<GenomicElement> getElements(Genome genome, String id, String type)
      throws IOException {
    autoLoad();

    return super.getElements(genome, id, type);
  }

  @Override
  public List<GenomicElement> find(Genome genome, GenomicRegion region, String type)
      throws IOException {
    autoLoad();

    return super.find(genome, region, type);
  }

  @Override
  public List<GenomicElement> closest(Genome genome,
      GenomicRegion region,
      String type) throws IOException {
    autoLoad();

    return super.closest(genome, region, type);
  }

  @Override
  public List<GenomicElement> closestByTss(Genome genome,
      GenomicRegion region,
      String type) throws IOException {
    autoLoad();

    return super.closestByTss(genome, region, type);
  }

  @Override
  public Iterable<String> getIds(String type) {
    autoLoad();

    return super.getIds(type);
  }
}
