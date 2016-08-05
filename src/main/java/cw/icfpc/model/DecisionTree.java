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
                State newState = state.addCompound(flippedCompound);
                nodes.add(newState);
                
//                cp.getSubsets().forEach(compoundSubset -> {

                    // из начального состояния вычесть compound
                    // добавить flippedCompound в его возмножных конфигурациях
                    // добавить сабсет в его возможных конфигурациях

//                        State newState = compoundSubset
//                });
            });
        });

        return nodes;
    }
}
