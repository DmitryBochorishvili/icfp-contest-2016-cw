package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.DecisionTree;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.IOException;
import java.util.*;

public class MainApp
{

    public static void main(String[] args)
    {
        //State s = createExampleState();
        ProblemReader r = new ProblemReader();
        try {
            State s = r.readDefaultProblem();
            StateVisualizer vis = StateVisualizer.builder(s);

            List<State> nodes = new LinkedList<>();

            if (args.length > 0) {
                for (String file : args) {
                    s = r.readProblemFromFile(file);
                    nodes.add(s);
                    State solution = null;

                    vis.addScene(s, true);
                    if (s.isFinalState())
                    {
                        solution = s;
                        System.out.println(String.format("Solution for %s found immediately!!!", file));
                    }

                    int step = 0;
                    while (solution == null && !nodes.isEmpty() && step < 100)
                    {
                        step++;
                        State currentState = nodes.remove(0);
                        vis.addScene(currentState, false);

                        List<State> decisions = DecisionTree.generateDecisionNodes(currentState);
                        System.out.println("Generated " + decisions.size() + " decision nodes on step: " + step
                                + ". Total decisions to check: " + (nodes.size() + decisions.size()));
                        
                        solution = decisions.stream().filter(State::isFinalState).findFirst().orElse(null);
                        if (solution != null) {
                            System.out.println(String.format("Solution for %s found!!!", file));
                            break;
                        }

                        for (State n : decisions)
                        {
                            vis.addScene(n, true);
                        }
                        nodes.addAll(decisions);

                        // sort new states by heuristic
                        nodes.sort((o1, o2) -> o1.getHeuristic() < o2.getHeuristic() ? 1 : -1);
                    }

                    if (solution == null) {
                        System.out.println(String.format("Solution for %s not found :((", file));
                    } else {
                        List<State> path = new ArrayList<>(solution.getIteration()+1);
                        while (solution != null) {
                            path.add(solution);
                            solution = solution.getDerivedFrom();
                        }
                        Collections.reverse(path);
                        for (State st: path) {
                            vis.addScene(st, false);
                        }
                    }
                }
            }
            vis.drawToFile(null);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Alive!");
    }

    private static State createExampleState() {
        List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>();
        List<FractionPoint> vertices = new ArrayList<FractionPoint>();
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE_HALF, BigFraction.ONE_HALF));
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ONE_HALF));

        polygons.add(new AtomicPolygon(vertices));

        return new State(polygons);
    }
}
