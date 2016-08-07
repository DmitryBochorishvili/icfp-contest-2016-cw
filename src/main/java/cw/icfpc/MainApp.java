package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.DecisionTree;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainApp
{

    public static void main(String[] args) throws ParseException, IOException
    {
        Options options = new Options();
        options.addOption("f", true, "file with problem description");
        options.addOption("d", true, "directory with problems");

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

        if (cmd.hasOption('f'))
        {
            long time = System.currentTimeMillis();
            String file = cmd.getOptionValue('f');
            State solution = solveProblem(file, false);
            System.out.println("Problem " + file + (solution == null ? " NOT" : "") + " solved in " + (System.currentTimeMillis() - time));
        }

        if (cmd.hasOption('d'))
        {
            String directory = cmd.getOptionValue('d');
            Files.walk(Paths.get(cmd.getOptionValue('d'))).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    long time = System.currentTimeMillis();
                    State solution = null;
                    try
                    {
                        solution = solveProblem(filePath.toString(), false);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("Problem " + filePath + (solution == null ? " NOT" : "") + " solved in " + (System.currentTimeMillis() - time));
                }
            });
        }

        System.out.println("Finished");
    }

    private static State solveProblem(String problemFile, boolean drawStates)
    {
        State solution = null;

        StateVisualizer vis = null;
        try
        {
            ProblemReader r = new ProblemReader();
            State s = r.readProblemFromFile(problemFile);

            List<State> nodes = new LinkedList<>();
            nodes.add(s);

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
            while (solution == null && !nodes.isEmpty() && step < 20)
            {
                step++;
                State currentState = nodes.remove(0);
                if (drawStates)
                    vis.addScene(currentState, false);

                List<State> decisions = DecisionTree.generateDecisionNodes(currentState);
//                System.out.println("Generated " + decisions.size() + " decision nodes on step: " + step
//                        + ". Total decisions to check: " + (nodes.size() + decisions.size()));


                if (drawStates)
                    for (State n : decisions)
                    {
                        vis.addScene(n, true);
                    }

                solution = decisions.stream().filter(State::isFinalState).findFirst().orElse(null);
                if (solution != null) {
//                    System.out.println(String.format("Solution for %s found!!!", problemFile));
                    break;
                }

                nodes.addAll(decisions);

                // sort new states by heuristic
                nodes.sort((o1, o2) -> o1.getHeuristic() < o2.getHeuristic() ? 1
                        : o1.getHeuristic() == o2.getHeuristic() ? 0 : -1);
            }

            if (solution == null) {
//                System.out.println(String.format("Solution for %s not found :((", file));
            } else {
                List<State> path = new ArrayList<>(solution.getIteration()+1);

                State pathPointer = solution;
                while (pathPointer != null) {
                    path.add(pathPointer);
                    pathPointer = pathPointer.getDerivedFrom();
                }
                Collections.reverse(path);

                if (drawStates)
                    for (State st: path) {
                        vis.addScene(st, false);
                    }
            }

            if (drawStates)
            {
                String simpleFileName = new File(problemFile).getName();
                String picFileName = "pic_" + simpleFileName;
                vis.drawToFile(picFileName);
            }
        }
        catch (IOException e) {
            System.out.println("Got an exception while trying to solve problem " + problemFile);
            e.printStackTrace();
            return null;
        }

        return solution;
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
