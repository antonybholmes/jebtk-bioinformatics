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

	public LazyGenes(Path file, GeneParser parser) {
		mFile = file;
		mParser = parser;
	}
	
	private void autoLoad() {
		if (mAutoLoad) {
			try {
				mParser.parse(mFile, this);
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
	public Iterable<Gene> lookup(String type, String symbol) {
		autoLoad();
		
		return super.lookup(type, symbol);
	}

	@Override
	public List<Gene> findGenes(GenomicRegion region) {
		autoLoad();
		
		return super.findGenes(region);
	}

	@Override
	public List<Gene> findClosestGenes(GenomicRegion region) {
		autoLoad();
		
		return super.findClosestGenes(region);
	}

	@Override
	public List<Gene> findClosestGenesByTss(GenomicRegion region) {
		autoLoad();
		
		return super.findClosestGenesByTss(region);
	}

	@Override
	public Iterable<String> getIds(String type) {
		autoLoad();
		
		return super.getIds(type);
	}
}
