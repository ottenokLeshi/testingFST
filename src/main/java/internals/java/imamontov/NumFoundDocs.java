package internals.java.imamontov;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;



public class NumFoundDocs {

    private static final String INDEX_PATH = "/Users/alivanov/Desktop/IndexWikipedia/Index";
    private static final String PATH_TO_FILE = "/Users/alivanov/Desktop/IndexWikipedia/data/";
    private static DirectoryReader reader;
    private static IndexSearcher searcher;
    private static List<String> terms4;
    private static List<String> terms10;
    private static final int SIZE = 1000;
    private static Random random;

    private static int makeSearch(List<String> terms, int maxEdits) throws IOException {
        TreeMap<Integer, String> treeMap = new TreeMap<>((o1, o2) -> o1 - o2);
        for (int i = 0; i < SIZE; i++) {
            String text = terms.get(i);
            Term term = new Term("body", text);
            Query query = new FuzzyQuery(term, maxEdits, 0, 10, true);
            int amount = searcher.count(query);
            treeMap.put(amount, query.toString());
        }
        for (int i = 0; i < 50; i++) {
            System.out.println(treeMap.pollFirstEntry());

        }
        return 0;

    }

    public static void main(String[] args) throws Exception {
        random = new Random(0xDEAD_BEEF);
        FSDirectory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        terms4 = new ArrayList<>();
        terms10 = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(PATH_TO_FILE + "10.txt"), StandardCharsets.UTF_8)) {
            stream.forEach(terms10::add);
        }

        try (Stream<String> stream = Files.lines(Paths.get(PATH_TO_FILE + "4.txt"), StandardCharsets.UTF_8)) {
            stream.forEach(terms4::add);
        }
        makeSearch(terms10, 2);
    }
}