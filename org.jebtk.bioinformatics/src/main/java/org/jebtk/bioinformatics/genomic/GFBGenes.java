
package org.jebtk.bioinformatics.genomic;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jebtk.core.collections.ArrayUtils;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.PathUtils;

/**
 * Encode genes in a binary format for quick lookup.
 *
 * @author Antony Holmes Holmes
 */
public class GFBGenes {

  public static final byte INT_BYTES = 4;
  public static final byte GENOME_BYTES = 8;
  public static final byte GENOME_BYTES_USABLE = GENOME_BYTES - 1;
  private static final byte[] BUFFER = new byte[64];
  private static final long WINDOW_BYTE_OFFSET = 1 + 1 + GENOME_BYTES;
  private static final long BINS_BYTE_OFFSET = WINDOW_BYTE_OFFSET + INT_BYTES;
  public static final byte TAG_BYTES = 16;
  public static final byte TAG_BYTES_USABLE = TAG_BYTES - 1;
  public static final byte ID_TYPE_BYTES = 16; // * BYTES_PER_INT;
  public static final byte ID_TYPE_BYTES_USABLE = ID_TYPE_BYTES - 1;
  public static final byte ID_VALUE_BYTES = 30;// * BYTES_PER_INT;
  public static final byte ID_VALUE_BYTES_USABLE = ID_VALUE_BYTES - 1;

  public static final byte HEADER_BYTES = 1 + 1 + GFBGenes.GENOME_BYTES
      + GFBGenes.INT_BYTES + GFBGenes.INT_BYTES;

  public static final byte ID_BYTES = ID_TYPE_BYTES + ID_VALUE_BYTES;

  private static final int[] GENE_ADDRESSES = new int[128];

  private Path mDir;
  private String mGenome;
  private int mWindow;

  public GFBGenes(String genome, int window, Path dir) {
    mGenome = genome;
    mWindow = window;
    mDir = dir;
  }

  public List<GenomicEntity> findGenes(String region) throws IOException {
    return findGenes(GenomicRegion.parse(mGenome, region));
  }
  
  public List<GenomicEntity> findGenes(GenomicRegion region)
      throws IOException {
    return findGenes(region.mChr, region.mStart, region.mEnd);
  }

  public List<GenomicEntity> findGenes(Chromosome chr, int start, int end)
      throws IOException {
    List<GenomicEntity> ret = new ArrayList<GenomicEntity>();

    int sb = start / mWindow;
    int eb = end / mWindow;

    int[] bins = ArrayUtils.array(sb, eb);

    Path file = mDir.resolve(getFileName(mGenome, chr, mWindow));

    RandomAccessFile reader = FileUtils.newRandomAccess(file);

    try {
      System.err.println(getVersion(reader));
      System.err.println(getGenome(reader));
      System.err.println(getWindow(reader));
      System.err.println(getBins(reader));

      int[] binAddresses = getBinAddresses(reader, bins);

      int n = getGeneAddresses(reader, binAddresses, GENE_ADDRESSES);

      Gene[] genes = getGenes(reader, chr, GENE_ADDRESSES, n);

      /*
      for (Gene gene : genes) {
        System.err.println(gene);
      }

      for (Gene gene : getOverlappingGenes(region, genes)) {
        System.err.println("overlap " + gene);
      }
      */
      
      ret.addAll(getOverlappingGenes(chr, start, end, genes));

      /*
       * for (int b = sb; b <= eb; ++b) { int binAddress = getBinAddress(reader,
       * b);
       * 
       * System.err.println("bin " + b + " " + binAddress);
       * 
       * int[] geneAddresses = getGeneAddresses(reader, binAddress);
       * 
       * System.err.println("genes " + Arrays.toString(geneAddresses));
       * 
       * List<Gene> genes = getGenes(reader, region.mChr, geneAddresses);
       * 
       * for (Gene gene : genes) { System.err.println(gene); } }
       */

    } finally {
      reader.close();
    }

    return ret;
  }

  public List<Gene> getOverlappingGenes(Chromosome chr, int start, int end, Gene[] genes) {
    return getOverlappingGenes(chr, start, end, genes, 1);
  }

  public List<Gene> getOverlappingGenes(Chromosome chr, int start, int end,
      Gene[] genes,
      int minBp) {
    if (CollectionUtils.isNullOrEmpty(genes)) {
      return Collections.emptyList();
    }

    List<Gene> ret = new ArrayList<Gene>(genes.length);

    for (Gene gene : genes) {
      GenomicRegion overlap = GenomicRegion.overlap(chr, start, end, gene);

      if (overlap == null || (minBp != -1 && overlap.getLength() < minBp)) {
        continue;
      }

      ret.add(gene);
    }

    return ret;
  }

  public static int getBinAddress(RandomAccessFile reader, int bin)
      throws IOException {
    reader.seek(HEADER_BYTES + bin * INT_BYTES);

    return reader.readInt();
  }

  public static int[] getBinAddresses(RandomAccessFile reader, int[] bins)
      throws IOException {
    int l = bins.length;

    int[] ret = new int[l];

    for (int i = 0; i < l; ++i) {
      ret[i] = getBinAddress(reader, bins[i]);
    }

    return ret;
  }

  public static int[] getGeneAddresses(RandomAccessFile reader, int binAddress)
      throws IOException {
    reader.seek(binAddress);

    int size = reader.readInt();

    int[] ret = new int[size];

    for (int i = 0; i < size; ++i) {
      ret[i] = reader.readInt();
    }

    return ret;
  }

  public static int getGeneAddresses(RandomAccessFile reader,
      final int[] binAddresses,
      int[] geneAddresses) throws IOException {

    int n = 0;

    Set<Integer> used = new HashSet<Integer>();
    
    // Gene address
    int ga;
    
    // How many genes are in the bin
    int size;
    
    for (int ba : binAddresses) {
      //System.err.println("bin address:" + ba);
      
      reader.seek(ba);
      
      size = reader.readInt();

      // Read how many addresses are in the bin and then extract them
      for (int i = 0; i < size; ++i) {
        ga = reader.readInt();

        if (!used.contains(ga)) {
          geneAddresses[n++] = ga;
          used.add(ga);
        }
      }
    }

    // Return the number of genes we found
    return n;
  }

  public static Gene[] getGenes(RandomAccessFile reader,
      Chromosome chr,
      int[] addresses) throws IOException {
    return getGenes(reader, chr, addresses, addresses.length);
  }

  public static Gene[] getGenes(RandomAccessFile reader,
      Chromosome chr,
      int[] addresses,
      int n) throws IOException {
    Gene[] ret = new Gene[n];

    for (int i = 0; i < n; ++i) {
      ret[i] = getGene(reader, chr, addresses[i]);
    }

    return ret;
  }

  public static Gene getGene(RandomAccessFile reader,
      Chromosome chr,
      int geneAddress) throws IOException {
    reader.seek(geneAddress);

    int start = reader.readInt();
    int end = reader.readInt();

    Gene gene = new Gene(GenomicRegion.create(chr, start, end));

    //
    // Exons
    //

    byte exons = reader.readByte();

    for (int i = 0; i < exons; ++i) {
      Exon exon = new Exon(
          GenomicRegion.create(chr, reader.readInt(), reader.readInt()));
      
      //System.err.println("exon " + exon);
      
      gene.addExon(exon);
    }

    //
    // Ids
    //

    byte ids = reader.readByte();

    for (int i = 0; i < ids; ++i) {
      // Read type
      String type = readString(reader, ID_TYPE_BYTES_USABLE);
      
      // Read value
      String name = readString(reader, ID_VALUE_BYTES_USABLE);

      gene.setId(type, name);
    }

    //
    // tags
    //

    byte tags = reader.readByte();

    for (int i = 0; i < tags; ++i) {
      gene.addTag(readString(reader, TAG_BYTES_USABLE));
    }
    
    return gene;
  }
  
  private static String readString(RandomAccessFile reader, int block) throws IOException {
    int n = reader.readByte();
    reader.read(BUFFER, 0, block);
    return new String(BUFFER, 0, n, StandardCharsets.UTF_8);
  }

  public static int getVersion(RandomAccessFile reader) throws IOException {
    reader.seek(1);

    return reader.readByte();
  }

  public static String getGenome(RandomAccessFile reader) throws IOException {
    reader.seek(2);
    int n = reader.readByte();
    reader.read(BUFFER, 0, GENOME_BYTES_USABLE);
    return new String(BUFFER, 0, n);
  }

  public static int getWindow(RandomAccessFile reader) throws IOException {
    reader.seek(WINDOW_BYTE_OFFSET);

    return reader.readInt();
  }

  public static int getBins(RandomAccessFile reader) throws IOException {
    reader.seek(BINS_BYTE_OFFSET);

    return reader.readInt();
  }

  public static final Path getFileName(String genome,
      Chromosome chr,
      int window) {
    return PathUtils.getPath(genome + "." + chr + "." + window + ".gfb");
  }
}