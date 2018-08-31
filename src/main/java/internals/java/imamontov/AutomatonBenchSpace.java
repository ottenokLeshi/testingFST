package internals.java.imamontov;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.apache.lucene.util.automaton.RegExp;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 4, time = 3)
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
public class AutomatonBenchSpace {
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

    private final String[] termsLong = new String[] {
            "calvin klein jeans women big flat winter cheap warm xl",
            "jeans women sunny beautiful green little skinny",
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

    private final String[] termsReqLong = new String[] {
            "c[ ]?a[ ]?l[ ]?v[ ]?i[ ]?n[ ]?k[ ]?l[ ]?e[ ]?i[ ]?n[ ]?j[ ]?e[ ]?a[ ]?n[ ]?s[ ]?w[ ]?o[ ]?m[ ]?e[ ]?n[ ]?b[ ]?i[ ]?g[ ]?f[ ]?l[ ]?a[ ]?t[ ]?w[ ]?i[ ]?n[ ]?t[ ]?e[ ]?r[ ]?c[ ]?h[ ]?e[ ]?a[ ]?p[ ]?w[ ]?a[ ]?r[ ]?m[ ]?x[ ]?l",
            "j[ ]?e[ ]?a[ ]?n[ ]?s[ ]?w[ ]?o[ ]?m[ ]?e[ ]?n[ ]?s[ ]?u[ ]?n[ ]?n[ ]?y[ ]?b[ ]?e[ ]?a[ ]?u[ ]?t[ ]?i[ ]?f[ ]?u[ ]?l[ ]?g[ ]?r[ ]?e[ ]?e[ ]?n[ ]?l[ ]?i[ ]?t[ ]?t[ ]?l[ ]?e[ ]?s[ ]?k[ ]?i[ ]?n[ ]?n[ ]?y[ ]?",
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

    private RegExp regExp;
    private LevenshteinAutomata automata;
    private Automaton automaton;
    private int result;

    private int getAutomaton(String[] termsArr, int n) {
        result = 0;
        for (String aTermsArr : termsArr) {
            automata = new LevenshteinAutomata(aTermsArr, false);
            automaton = automata.toAutomaton(n);
            result += automaton.hashCode();
        }
        return result;
    }

    private int getAutomatonReqExp(String[] termsArr) {
        result = 0;
        for (String aTermsArr : termsArr) {
            regExp = new RegExp(aTermsArr);
            automaton = regExp.toAutomaton();
            result += automaton.hashCode();
        }
        return result;
    }

    @Benchmark
    public int calc2() {
        return getAutomaton(terms,2);
    }

    @Benchmark
    public int calc1() {
        return getAutomaton(terms, 1);
    }

    @Benchmark
    public int calc2Long() {
        return getAutomaton(termsLong,2);
    }

    @Benchmark
    public int calc1Long() {
        return getAutomaton(termsLong, 1);
    }

    @Benchmark
    public int calcReq() {
        return getAutomatonReqExp(termsReq);
    }

    @Benchmark
    public int calcRegLong() {
        return getAutomatonReqExp(termsReqLong);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(AutomatonBenchSpace.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}