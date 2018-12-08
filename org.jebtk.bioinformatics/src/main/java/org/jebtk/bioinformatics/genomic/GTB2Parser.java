
package org.jebtk.bioinformatics.genomic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.TreeSetCreator;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.io.TokenFunction;
import org.jebtk.core.text.Splitter;
import org.jebtk.core.text.TextUtils;

/**
 * Genes lookup to m.
 *
 * @author Antony Holmes Holmes
 */
public class GTB2Parser extends GTBParser {

  public GTB2Parser() {
    // _setLevels(GeneType.GENE);
  }

  public GTB2Parser(GeneParser parser) {
    super(parser);
  }

  /**
   * Parses the gene table.
   *
   * @param reader the reader
   * @return the genes
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Override
  protected void parse(final Path file,
      BufferedReader reader,
      final Genome genome,
      final GenesDB genes) throws IOException {
    LOG.info("Parsing GTB2 file {}, levels: {}...", file, mLevels);

    final Splitter splitter = Splitter.on(';');

    // Add the exons
    final boolean hasExonLevel = containsLevel(GenomicType.EXON);
    // final boolean hasTranscriptLevel = containsLevel(GenomicType.TRANSCRIPT);
    final boolean has3pUtrLevel = containsLevel(GenomicType.UTR_3P);
    final boolean has5pUtrLevel = containsLevel(GenomicType.UTR_5P);

    FileUtils.tokenize(reader, true, new TokenFunction() {
      @Override
      public void parse(final List<String> tokens) {
        // System.err.println("gtb2 " + line);

        boolean add = true;

        Chromosome chr = GenomeService.getInstance().chr(genome, tokens.get(0));

        // Skip random and unofficial chromosomes
        if (chr.toString().contains("_")) {
          return;
        }

        Strand strand = Strand.parse(tokens.get(1));
        int start = Integer.parseInt(tokens.get(2));
        int end = Integer.parseInt(tokens.get(3));

        // int exonCount = Integer.parseInt(tokens.get(4));

        // Because of the UCSC using zero based start and one
        // based end, we need to increment the start by 1

        List<Tag> tags = getTags(splitter, tokens);

        if (mExcludeTags.size() > 0) {
          for (Tag tag : tags) {
            if (mExcludeTags.contains(tag.toString())) {
              add = false;
              break;
            }
          }
        }

        if (mMatchTags.size() > 0) {
          add = false;

          for (Tag tag : tags) {
            if (mMatchTags.contains(tag.toString())) {
              add = true;
              break;
            }
          }
        }

        if (!add) {
          return;
        }

        IterMap<String, String> attributeMap = getAttributes(splitter, tokens);

        // Create the gene

        GenomicEntity gene = addAttributes(GenomicEntity.TRANSCRIPT,
            GenomicRegion.create(chr, start, end, strand),
            attributeMap);

        if (containsLevel(GenomicType.TRANSCRIPT)) {
          genes.add(gene);
        }

        if (hasExonLevel || mKeepExons) {
          List<Integer> starts = TextUtils.splitInts(tokens.get(5),
              TextUtils.SEMI_COLON_DELIMITER);

          List<Integer> ends = TextUtils.splitInts(tokens.get(6),
              TextUtils.SEMI_COLON_DELIMITER);

          for (int i = 0; i < starts.size(); ++i) {
            // Again correct for the ucsc
            GenomicRegion region = GenomicRegion
                .create(chr, starts.get(i), ends.get(i), strand);

            GenomicEntity exon = addAttributes(GenomicEntity.EXON,
                region,
                attributeMap);

            if (mKeepExons) {
              if (gene != null) {
                gene.add(exon);
              }
            }

            if (hasExonLevel) {
              exon.setParent(gene);

              genes.add(exon);
            }
          }
        }

        if (has5pUtrLevel) {
          processUTR(tokens,
              gene,
              attributeMap,
              7,
              GenomicEntity.UTR_5P,
              genes);
        }

        if (has3pUtrLevel) {
          processUTR(tokens,
              gene,
              attributeMap,
              10,
              GenomicEntity.UTR_3P,
              genes);
        }
      }
    });
  }

  private static void processUTR(List<String> tokens,
      GenomicEntity gene,
      IterMap<String, String> attributeMap,
      int offset,
      String type,
      GenesDB genes) {

    int count = Integer.parseInt(tokens.get(offset));

    if (count == 0) {
      return;
    }

    Splitter splitter = Splitter.onSC();

    List<Integer> starts = splitter.stream(tokens.get(offset + 1)).asInt()
        .toList(); // TextUtils.splitInts(tokens.get(offset
    // + 1),
    // TextUtils.SEMI_COLON_DELIMITER);

    List<Integer> ends = splitter.stream(tokens.get(offset + 2)).asInt()
        .toList(); // TextUtils.splitInts(tokens.get(offset
    // + 2),
    // TextUtils.SEMI_COLON_DELIMITER);

    for (int i = 0; i < starts.size(); ++i) {
      GenomicRegion region = GenomicRegion
          .create(gene.mChr, starts.get(i), ends.get(i), gene.mStrand);

      GenomicEntity g = addAttributes(type, region, attributeMap);

      g.setParent(gene);

      if (genes != null) {
        genes.add(g);
      }
    }
  }

  @Override
  public Map<String, Set<String>> idMap(Path file,
      BufferedReader reader,
      String id1,
      String id2) throws IOException {
    LOG.info("Creating id map from GTB2 file {}, levels: {}...", file, mLevels);

    Genome genome = GenomeService.getInstance().guessGenome(file);

    Map<String, Set<String>> ret = DefaultTreeMap
        .create(new TreeSetCreator<String>());

    String line;
    List<String> tokens;

    // Skip header
    reader.readLine();

    Splitter splitter = Splitter.on(';');

    while ((line = reader.readLine()) != null) {
      if (Io.isEmptyLine(line)) {
        continue;
      }

      boolean add = true;

      tokens = Splitter.onTab().text(line);

      Chromosome chr = GenomeService.getInstance().chr(genome, tokens.get(0));

      // Skip random and unofficial chromosomes
      if (chr.toString().contains("_")) {
        continue;
      }

      List<Tag> tags = getTags(splitter, tokens);

      if (mExcludeTags.size() > 0) {
        for (Tag tag : tags) {
          if (mExcludeTags.contains(tag.toString())) {
            add = false;
            break;
          }
        }
      }

      if (mMatchTags.size() > 0) {
        add = false;

        for (Tag tag : tags) {
          if (mMatchTags.contains(tag.toString())) {
            add = true;
            break;
          }
        }
      }

      if (!add) {
        continue;
      }

      IterMap<String, String> attributeMap = getAttributes(splitter, tokens);

      String name1 = attributeMap.get(id1);
      String name2 = attributeMap.get(id2);

      // System.err.println("id " + id1 + " " + name1 + " " + id2 + " " +
      // name2);

      if (name1 != null && name2 != null) {
        ret.get(name1).add(name2);
      }
    }

    return ret;
  }

  @Override
  public GeneParser create(GeneParser parser) {
    return new GTB2Parser(parser);
  }

  private static IterMap<String, String> getAttributes(Splitter splitter,
      final List<String> tokens) {
    return getAttributes(splitter, tokens.get(13));
  }

  private static List<Tag> getTags(Splitter splitter,
      final List<String> tokens) {
    return Tag.toTags(TextUtils.removeNA(splitter.text(tokens.get(14))));
  }

  public static GenomicEntity parse(Genome genome, final String line) {
    return parse(genome, TextUtils.tabSplit(line));
  }

  public static GenomicEntity parse(Genome genome, final List<String> tokens) {
    return parse(genome,
        Collections.<String>emptySet(),
        Collections.<String>emptySet(),
        tokens);
  }

  public static GenomicEntity parse(Genome genome,
      final Set<String> matchTags,
      final Set<String> excludeTags,
      final List<String> tokens) {
    final Splitter splitter = Splitter.on(';');

    Chromosome chr = GenomeService.getInstance().chr(genome, tokens.get(0));

    Strand strand = Strand.parse(tokens.get(1));
    int start = Integer.parseInt(tokens.get(2));
    int end = Integer.parseInt(tokens.get(3));

    // int exonCount = Integer.parseInt(tokens.get(4));

    // Because of the UCSC using zero based start and one
    // based end, we need to increment the start by 1

    List<Tag> tags = getTags(splitter, tokens);

    IterMap<String, String> attributeMap = getAttributes(splitter, tokens);

    // Create the gene

    GenomicEntity transcript = addAttributes(GenomicEntity.TRANSCRIPT,
        GenomicRegion.create(chr, start, end, strand),
        attributeMap);

    transcript.addTags(tags);

    List<Integer> starts = TextUtils.splitInts(tokens.get(5),
        TextUtils.SEMI_COLON_DELIMITER);

    List<Integer> ends = TextUtils.splitInts(tokens.get(6),
        TextUtils.SEMI_COLON_DELIMITER);

    for (int i = 0; i < starts.size(); ++i) {
      // Again correct for the ucsc
      GenomicRegion region = GenomicRegion
          .create(chr, starts.get(i), ends.get(i), strand);

      GenomicEntity exon = addAttributes(GenomicEntity.EXON,
          region,
          attributeMap);

      // GenomicEntity exon = new Exon(region);

      transcript.add(exon);
      exon.setParent(transcript);
    }

    processUTR(tokens, transcript, attributeMap, 7, GenomicEntity.UTR_5P, null);

    processUTR(tokens,
        transcript,
        attributeMap,
        10,
        GenomicEntity.UTR_3P,
        null);

    return transcript;
  }
}