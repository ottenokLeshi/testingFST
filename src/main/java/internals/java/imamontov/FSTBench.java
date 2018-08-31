package internals.java.imamontov;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2, jvmArgs = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
//        "-XX:+DebugNonSafepoints",
//        "-XX:+UnlockCommercialFeatures",
//        "-XX:+FlightRecorder",
//        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class FSTBench {

    private final String[] terms = new String[] {
            "calvin klein jeans women big flat winter cheap warm xl",
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

    private int getAutomaton(int n) {
        int result = 0;
        for (int i = 0; i < terms.length; i++) {
            LevenshteinAutomata automata = new LevenshteinAutomata(terms[i], false);
            Automaton automaton = automata.toAutomaton(n);
                result += automaton.hashCode();
        }
        return result;
    }

    @Benchmark
    public int calc2() {
        return getAutomaton(2);
    }

    @Benchmark
    public int calc1() {
        return getAutomaton(1);
    }

    @Benchmark
    public int calc0() {
        return getAutomaton(0);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(FSTBench.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}