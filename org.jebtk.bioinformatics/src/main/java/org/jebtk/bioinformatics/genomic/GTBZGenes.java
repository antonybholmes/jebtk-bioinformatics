package org.jebtk.bioinformatics.genomic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.TokenFunction;

/**
 * Only load genes when requested.
 * 
 * @author antony
 *
 */
public class GTBZGenes extends Genes {
	private GeneParser mParser;
	private boolean mAutoLoad = true;
	private Path mFile;
	private Map<String, Chromosome> mGeneMap = new HashMap<String, Chromosome>();

	public GTBZGenes(Path file, GeneParser parser) {
		mFile = file;
		mParser = parser;
	}

	private void geneChrMap() throws IOException {

		final ZipFile zipFile = FileUtils.newZipFile(mFile);

		try {
			ZipEntry entry = zipFile.getEntry("gene_chr.map");

			if (entry != null) {
				BufferedReader reader = 
						FileUtils.newBufferedReader(zipFile, entry);

				try {
					FileUtils.tokenize(new TokenFunction() {
						@Override
						public void parse(final List<String> tokens) {
							String name = Genes.sanitize(tokens.get(0));
							Chromosome chr = ChromosomeService.getInstance().guess(mFile, tokens.get(1));

							mGeneMap.put(name, chr);
						}})
					.skipHeader(true)
					.tokens(reader);
				} finally {
					reader.close();
				}
			}
		} finally {
			zipFile.close();
		}
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

	private void autoLoad(Chromosome chr) {
		if (!contains(chr)) {
			System.err.println("autoload " + chr);

			try {
				mParser.parse(mFile, this, chr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void autoLoad(String name) {
		// Load a mapping of gene ids to chrs if not done so
		if (mGeneMap.size() == 0) {
			try {
				geneChrMap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Chromosome chr = mGeneMap.get(Genes.sanitize(name));

		if (chr != null) {
			autoLoad(chr);
		}
	}

	@Override
	public void autoFindMainVariants() {
		autoLoad();

		super.autoFindMainVariants();
	}

	@Override
	public Collection<Gene> getGenes(String symbol) {
		autoLoad(symbol);

		return super.getGenes(symbol);
	}

	@Override
	public Collection<Gene> findGenes(GenomicRegion region) {
		autoLoad(region.mChr);

		return super.findGenes(region);
	}

	@Override
	public Collection<Gene> findClosestGenes(GenomicRegion region) {
		autoLoad(region.mChr);

		return super.findClosestGenes(region);
	}

	@Override
	public Collection<Gene> findClosestGenesByTss(GenomicRegion region) {
		autoLoad(region.mChr);

		return super.findClosestGenesByTss(region);
	}

	@Override
	public Iterable<String> getIds(String type) {
		autoLoad();

		return super.getIds(type);
	}
}
