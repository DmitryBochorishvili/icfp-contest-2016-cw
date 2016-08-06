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

                // compound contains only 1 atomic polygon. Try to merge it with background
                if (cp.getPolygons().size() == 1)
                {
                    AtomicPolygon p = cp.getPolygons().get(0);
                    state.getAdjacentPolygons(p).forEach(adjacent -> {
                        State newState = state.mergePolygons(p, adjacent).addCompound(flippedCompound);
                        nodes.add(newState);
                    });
                }

                // Try to merge first atomic in flipped compound with bg
                List<State> mergedFlippedCompound = new LinkedList<State>();
                nodes.forEach(st -> {
                    AtomicPolygon p = flippedCompound.getPolygons().get(0);
                    st.getAdjacentPolygons(p).forEach(adjacent -> {
                        State newState = st.mergePolygons(p, adjacent).addCompound(flippedCompound);
                        mergedFlippedCompound.add(newState);
                    });
                });
                nodes.addAll(mergedFlippedCompound);

            });
        });

        return nodes;
    }
}
