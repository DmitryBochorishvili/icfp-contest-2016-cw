package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.DecisionTree;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainApp
{

    private static class SolutionResult
    {
        public State solution;
        public long time;
        public long statesGenerated;
    }

    public static void main(String[] args) throws ParseException, IOException
    {
        Options options = new Options();
        options.addOption("f", true, "file with problem description");
        options.addOption("d", true, "directory with problems");
        options.addOption("i", false, "generate state images");
        options.addOption("s", false, "submit found solutions to the server");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (!(cmd.hasOption("f") || cmd.hasOption("d")))
        {
            System.out.println("Please specify either input file or input directory");
            System.out.println("<program> -f <input file>");
            System.out.println("or");
            System.out.println("<program> -d <input directory>");
            return;
        }

        boolean drawStateImages = cmd.hasOption('i');

        if (cmd.hasOption('f'))
        {
            String file = cmd.getOptionValue('f');
            SolutionResult solution = solveProblem(file, drawStateImages, cmd.hasOption('s'));
            System.out.printf("Problem %s%s solved in %dms, states generated %d\n", file, solution.solution == null ? " NOT" : "",
                    solution.time, solution.statesGenerated);
        }

        if (cmd.hasOption('d'))
        {
            String directory = cmd.getOptionValue('d');
            Files.walk(Paths.get(directory)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    SolutionResult solution = null;
                    try
                    {
                        solution = solveProblem(filePath.toString(), drawStateImages, cmd.hasOption('s'));
                        System.out.printf("Problem %s%s solved in %dms, states generated %d\n", filePath, solution.solution == null ? " NOT" : "",
                                solution.time, solution.statesGenerated);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.printf("Problem %s NOT solved, exception\n", filePath);
                    }
                }
            });
        }

        System.out.println("Finished");
    }
    
    private static void touch(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            new FileOutputStream(f).close();
        }
        f.setLastModified(System.currentTimeMillis());
    }

    private static SolutionResult solveProblem(String problemFile, boolean drawStates, boolean submitToServer)
    {
        SolutionResult result = new SolutionResult();
        result.time = System.currentTimeMillis();

        String strId = new File(problemFile).getName();
        try {
            int id = Integer.parseInt(strId);
        }
        catch (NumberFormatException e) {
            System.out.println("It's apparently not a problem file: " + strId);
            return result;
        }
        String submittedDirPath = "downloadedProblems/.submitted/";
        String submittedPath = submittedDirPath + strId;

        if (new File(submittedPath).exists()) {
            System.out.printf("Problem #%s already submitted to server, doing nothing%n", strId);
            return result;
        }
        
        State solution = null;

        StateVisualizer vis = null;

        Set<Integer> foundStates = new HashSet<>();

        try
        {
            ProblemReader r = new ProblemReader();
            State s = r.readProblemFromFile(problemFile);

            List<State> nodes = new LinkedList<>();
            nodes.add(s);
            foundStates.add(s.stateHash());

            if (drawStates)
            {
                vis = StateVisualizer.builder(s);
                vis.addScene(s, true);
            }

            if (s.isFinalState())
            {
                solution = s;
//                System.out.println(String.format("Solution for %s found immediately!!!", problemFile));
            }

            int step = 0;
            while (solution == null && !nodes.isEmpty()
                    && (System.currentTimeMillis() - result.time < 5000)
                    && step < 500)
            {
                step++;
                if((step%100) == 0)
                    System.out.print(".." + step);
                State currentState = nodes.remove(0);
                if (drawStates)
                    vis.addScene(currentState, false);

                List<State> decisions = DecisionTree.generateDecisionNodes(currentState);
                decisions = decisions.stream().filter(d -> !foundStates.contains(d.stateHash())).collect(Collectors.toList());
                result.statesGenerated += decisions.size();

                if (drawStates)
                {
                    int i = 0;
                    for (State n : decisions)
                    {
                        vis.addScene(n, true);
                        if (i++ > 50)
                            break;
                    }
                }

                solution = decisions.stream().filter(State::isFinalState).findFirst().orElse(null);
                if (solution != null) {
                    break;
                }

                nodes.addAll(decisions);
                decisions.forEach(d -> foundStates.add(d.stateHash()));

                // sort new states by heuristic
                nodes.sort((o1, o2) -> o1.getHeuristic() < o2.getHeuristic() ? 1
                        : o1.getHeuristic() == o2.getHeuristic() ? 0 : -1);
            }

            if (solution != null)
            {
                solution = solution.alignToUnit();
                List<State> path = new ArrayList<>(solution.getIteration()+1);

                State pathPointer = solution;
                while (pathPointer != null) {
                    path.add(pathPointer);
                    pathPointer = pathPointer.getDerivedFrom();
                }
                Collections.reverse(path);

                if (drawStates) {
                    for (State st: path) {
                        vis.addScene(st, false);
                    }
                }

                if (submitToServer) {
                    String sol = solution.toSolution();
                    System.out.printf("Submitting #%s! Ta-da!..\n%s%n", strId, sol);
                    String submitResult = ServerCommunicator.submitSolution(strId, sol);

                    if (submitResult.contains("\"ok\":true,\"resemblance\":1.0")) {
                        new File(submittedDirPath).mkdirs();
                        touch(submittedPath);
                    }
                }
            }

        }
        catch (Exception e) {
            System.out.println("Got an exception while trying to solve problem " + problemFile);
            e.printStackTrace();

            result.time = System.currentTimeMillis() - result.time;
            result.solution = solution;
            return result;
        }
        finally {
            System.out.flush();
            System.err.flush();

            result.time = System.currentTimeMillis() - result.time;
            result.solution = solution;

            if (drawStates)
            {
                String simpleFileName = new File(problemFile).getName();
                String picFileName = "pic_" + simpleFileName + ".png";
                try
                {
                    vis.drawToFile(picFileName);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private static State createExampleState() {
        List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>();
        List<FractionPoint> vertices = new ArrayList<FractionPoint>();
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE_HALF, BigFraction.ONE_HALF));
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ONE_HALF));

        polygons.add(new AtomicPolygon(vertices));

        return State.createNew(polygons);
    }
}
