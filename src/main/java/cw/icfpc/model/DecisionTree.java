package cw.icfpc.model;

import java.util.LinkedList;
import java.util.List;

public class DecisionTree
{
    public static List<State> generateDecisionNodes(State state)
    {
        List<State> nodes = new LinkedList<>();

        state.getEdges().forEach(edge -> {
            Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
            compounds.forEach(cp -> {
                CompoundPolygon flippedCompound = cp.flip(edge);
                Iterable<CompoundPolygon> flippedCompoundSubsets = AdjacentPolyGenerator.getAllFlippedCompounds(flippedCompound);
                Iterable<CompoundPolygon> sourceCompoundSubsets = AdjacentPolyGenerator.getAllSourceSubCompounds(cp);
                flippedCompoundSubsets.forEach(flipCpSubset -> {
                    sourceCompoundSubsets.forEach(srcCpSubset -> {
                        State newState = state.addRemoveCompound(flipCpSubset, srcCpSubset);
                        nodes.add(newState);
                    });
                });
            });
        });

        return nodes;
    }
}
