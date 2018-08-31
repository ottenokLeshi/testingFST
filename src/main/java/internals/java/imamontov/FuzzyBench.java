package internals.java.imamontov;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 2)
@Fork(value = 1, jvmArgs = {
        "-Xms4G","-Xmx4G" ,
        "-XX:MaxInlineLevel=12",
        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
        "-XX:+LogCompilation",
        "-XX:LogFile=out.log",
        "-XX:+DebugNonSafepoints",
        "-XX:+UnlockCommercialFeatures",
        "-XX:+FlightRecorder",
        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class FuzzyBench {

    private static final String INDEX_PATH = "/Users/alivanov/Desktop/IndexWikipedia/Index";
    private static final String PATH_TO_FILE = "/Users/alivanov/Desktop/IndexWikipedia/data/";
    private DirectoryReader reader;
    private IndexSearcher searcher;
    private List<String> terms4;
    private List<String> terms10;
    private static final int SIZE = 1000;
    private Random random;

    @Param({"1", "2" })
    private int distance;

    @Param({"0", "1", "2" })
    private int prefixLength;

    @Param({"false", "true" })
    private boolean transpositions;


    @Setup
    public void init() throws IOException {
        random = new Random(0xDEAD_BEEF);
        FSDirectory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        terms4 = new ArrayList<>();
        terms10 = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            terms10.add("mastercard");
            terms4.add("card");
        }
//        try (Stream<String> stream = Files.lines(Paths.get(PATH_TO_FILE + "10.txt"), StandardCharsets.UTF_8)) {
//            stream.forEach(terms10::add);
//        }
//
//        try (Stream<String> stream = Files.lines(Paths.get(PATH_TO_FILE + "4.txt"), StandardCharsets.UTF_8)) {
//            stream.forEach(terms4::add);
//        }
    }

    private int makeSearch(List<String> terms) throws IOException {
        String text = terms.get(random.nextInt(SIZE));
        Term term = new Term("body", text);
        Query query = new FuzzyQuery(term, distance, prefixLength, 10, transpositions);
        TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
        searcher.search(query, totalHitCountCollector);
        return totalHitCountCollector.getTotalHits();
    }

    @Benchmark
    public int fuzzy4() throws IOException {
        return makeSearch(terms4);
    }

    @Benchmark
    public int fuzzy10() throws IOException {
        return makeSearch(terms10);
    }

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                .include(FuzzyBench.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .resultFormat(ResultFormatType.JSON)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}