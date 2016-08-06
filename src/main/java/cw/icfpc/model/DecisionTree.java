package cw.icfpc.model;

import java.util.LinkedList;
import java.util.List;

public class DecisionTree
{
    public static List<State> generateDecisionNodes(State state)
    {
        List<State> nodes = new LinkedList<>();

        double currentStateArea = state.getSimpleArea();

        state.getEdges().forEach(edge -> {
            Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
            compounds.forEach(cp -> {
                CompoundPolygon flippedCompound = cp.flip(edge);
                Iterable<CompoundPolygon> sourceCompoundSubsets = AdjacentPolyGenerator.getAllSourceSubCompounds(cp);
                sourceCompoundSubsets.forEach(srcCpSubset -> {
                    State newState = state.addRemoveCompound(flippedCompound, srcCpSubset);

                    // new state is valid only if its ares is bigger than area of previous state
                    if (newState.getSimpleArea() > currentStateArea)
                    {
                        nodes.add(newState);
                    }
                });
            });
        });

        return nodes;
    }
}
