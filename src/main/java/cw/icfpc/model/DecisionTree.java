package cw.icfpc.model;

import java.util.LinkedList;
import java.util.List;

public class DecisionTree
{
    public static List<State> generateDecisionNodes(State state)
    {
        double currentStateArea = state.getSimpleArea();

        List<State> nodes = new LinkedList<>();

        state.getEdges().forEach(edge -> {

            // we try to fold only across outer edges
            if (!state.isEdgeOuter(edge))
                return;

            Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
            compounds.forEach(cp -> {
                CompoundPolygon flippedCompound = cp.flip(edge);
                Iterable<CompoundPolygon> sourceCompoundSubsets = AdjacentPolyGenerator.getAllSourceSubCompoundsToRemove(cp);
                sourceCompoundSubsets.forEach(srcCpSubset -> {
                    State newState = state.addRemoveCompound(flippedCompound, srcCpSubset);

                    // new state is valid only if its ares is bigger than area of previous state
                    if (newState.getSimpleArea() > currentStateArea && newState.isStateValid())
                    {
                        //nodes.add(newState);

                        // merge first flipped atomic with first compound atomic
                        AtomicPolygon p1 = flippedCompound.getPolygons().get(0);
                        AtomicPolygon p2 = cp.getPolygons().get(0);
                        newState = newState.mergePolygons(p1, p2);
                        newState.setIteration(state.getIteration() + 1);
                        nodes.add(newState);
                    }
                });

//                // compound contains only 1 atomic polygon. Try to merge it with background
//                if (cp.getPolygons().size() == 1)
//                {
//                    AtomicPolygon p = cp.getPolygons().get(0);
//                    state.getAdjacentPolygons(p).forEach(adjacent -> {
//                        State newState = state.mergePolygons(p, adjacent).addCompound(flippedCompound);
//                        nodes.add(newState);
//                    });
//                }
//
//                // Try to merge first atomic in flipped compound with bg
//                List<State> mergedFlippedCompound = new LinkedList<State>();
//                nodes.forEach(st -> {
//                    AtomicPolygon p = flippedCompound.getPolygons().get(0);
//                    st.getAdjacentPolygons(p).forEach(adjacent -> {
//                        State newState = st.mergePolygons(p, adjacent).addCompound(flippedCompound);
//                        mergedFlippedCompound.add(newState);
//                    });
//                });
//                nodes.addAll(mergedFlippedCompound);

            });
        });

        return nodes;
    }
}
