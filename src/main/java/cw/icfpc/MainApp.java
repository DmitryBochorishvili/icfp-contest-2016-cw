package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.DecisionTree;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainApp
{

    public static void main(String[] args)
    {
        //State s = createExampleState();
        ProblemReader r = new ProblemReader();
        try {
            State s = r.readProblemFromFile(null);
            StateVisualizer vis = StateVisualizer.builder(s);


            List<State> nodes = new LinkedList<>();

            if (args.length > 0) {
                for (String file : args) {
                    s = r.readProblemFromFile(file);
                    s.setIteration(0);
                    nodes.add(s);

                    int step = 0;
                    while (step < 5)
                    {
                        step++;
                        State currentState = nodes.remove(0);
                        vis.addScene(currentState, false);

                        if (currentState.getSimpleArea() > 1 - 1e-6)
                        {
                            System.out.println("Solution found!!!");
                            break;
                        }
                        nodes.addAll(DecisionTree.generateDecisionNodes(currentState));

                        // sort new states by heuristic
                        nodes.sort((o1, o2) -> o1.getHeuristic() < o2.getHeuristic() ? 1 : -1);

                        System.out.println("Have " + nodes.size() + " decision nodes on step: " + step);
                        for (State n : nodes)
                        {
                            vis.addScene(n, true);
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
