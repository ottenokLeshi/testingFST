package internals.java.imamontov;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 5, time = 3)
@Fork(value = 1, jvmArgs = {
        "-Xms4G", "-Xmx4G",
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
        "-XX:+DebugNonSafepoints",
        "-XX:+UnlockCommercialFeatures",
        "-XX:+FlightRecorder",
        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class FSTBenchSpaceSearch {
    private final String[] terms = new String[] {
//            "calvin klein jeans women big flat winter cheap warm xl",
            "out of the door",
            "tee spoon",
            "calvin klein jeans",
            "klein jeans women",
            "calvin klein",
            "klein jeans",
            "jeans women",
            "calvin",
            "armoney",
            "klein",
            "jeans",
            "women"
    };

    private final String[] termsReq = new String[] {
//            "c[ ]?a[ ]?l[ ]?v[ ]?i[ ]?n[ ]?k[ ]?l[ ]?e[ ]?i[ ]?n[ ]?j[ ]?e[ ]?a[ ]?n[ ]?s[ ]?w[ ]?o[ ]?m[ ]?e[ ]?n[ ]?b[ ]?i[ ]?g[ ]?f[ ]?l[ ]?a[ ]?t[ ]?w[ ]?i[ ]?n[ ]?t[ ]?e[ ]?r[ ]?c[ ]?h[ ]?e[ ]?a[ ]?p[ ]?w[ ]?a[ ]?r[ ]?m[ ]?x[ ]?l",
            "o[ ]?u[ ]?t[ ]?o[ ]?f[ ]?t[ ]?h[ ]?e[ ]?d[ ]?o[ ]?o[ ]?r",
            "t[ ]?e[ ]?e[ ]?s[ ]?p[ ]?o[ ]?o[ ]?n",
            "c[ ]?a[ ]?l[ ]?v[ ]?i[ ]?n[ ]?k[ ]?l[ ]?e[ ]?i[ ]?n[ ]?j[ ]?e[ ]?a[ ]?n[ ]?s",
            "k[ ]?l[ ]?e[ ]?i[ ]?n[ ]?j[ ]?e[ ]?a[ ]?n[ ]?s[ ]?w[ ]?o[ ]?m[ ]?e[ ]?n",
            "c[ ]?a[ ]?l[ ]?v[ ]?i[ ]?n[ ]?k[ ]?l[ ]?e[ ]?i[ ]?n",
            "k[ ]?l[ ]?e[ ]?i[ ]?n[ ]?j[ ]?e[ ]?a[ ]?n[ ]?s",
            "j[ ]?e[ ]?a[ ]?n[ ]?s[ ]?w[ ]?o[ ]?m[ ]?e[ ]?n",
            "c[ ]?a[ ]?l[ ]?v[ ]?i[ ]?n",
            "a[ ]?r[ ]?m[ ]?o[ ]?n[ ]?e[ ]?y",
            "k[ ]?l[ ]?e[ ]?i[ ]?n",
            "j[ ]?e[ ]?a[ ]?n[ ]?s",
            "w[ ]?o[ ]?m[ ]?e[ ]?n"
    };

    private static final String INDEX_PATH = "/Users/alivanov/Desktop/IndexWikipedia/Index";

    private DirectoryReader reader;
    private IndexSearcher searcher;
    private TotalHitCountCollector totalHitCountCollector;
    private Term term;
    private Query query;

//    @Param({"1", "2" })
    @Param({"2"})
    private int distance;

//    @Param({"0", "1", "2"})
    @Param({"2"})
    private int prefixLength;

    @Param({"false"})
    private boolean transpositions;

    @Setup
    public void init() throws IOException {
        FSDirectory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    private int searchFuzzy(String[] terms) throws IOException {
        int result = 0;
        totalHitCountCollector = new TotalHitCountCollector();

        for (String string : terms){
            term = new Term("body", string);
            query = new FuzzyQuery(term, distance, prefixLength, 10, transpositions);
            searcher.search(query, totalHitCountCollector);
            result += totalHitCountCollector.getTotalHits();
        }
        return result;
    }

    private int searchReq(String[] terms) throws IOException {
        int result = 0;
        totalHitCountCollector = new TotalHitCountCollector();

        for (String string : terms){
            term = new Term("body", string);
            query = new RegexpQuery(term);
            searcher.search(query, totalHitCountCollector);
            result += totalHitCountCollector.getTotalHits();
        }
        return result;
    }

    @Benchmark
    public int calcFuzzy() throws IOException {
        return searchFuzzy(terms);
    }

    @Benchmark
    public int calcReq() throws IOException {
        return searchReq(termsReq);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(FSTBenchSpaceSearch.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}