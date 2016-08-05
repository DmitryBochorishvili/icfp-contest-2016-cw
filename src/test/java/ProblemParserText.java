import cw.icfpc.ProblemReader;
import cw.icfpc.model.State;
import org.junit.Test;

public class ProblemParserText {
    private class TestedProblemReader extends ProblemReader {
        public State testParsing(String problem) {
            return parseProblem(problem);
        }
    }

    @Test
    public void testProblemReader() {
        TestedProblemReader reader = new TestedProblemReader();
        // TODO: check some problems
    }
}
