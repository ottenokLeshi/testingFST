package internals.java.imamontov;


import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

public class MainQuery {
    public static void main(String[] args) {
        Term term = new Term("field", "Value");
        FuzzyQuery query = new FuzzyQuery(term, 2, 0, 10, true);

        LevenshteinAutomata automata = new LevenshteinAutomata("Value", false);
        Automaton automaton = automata.toAutomaton(1, "val");
        System.out.println(automaton.getNumStates());
        System.out.println(automaton.getNumTransitions());
        System.out.println(automaton.toDot());
    }
}
