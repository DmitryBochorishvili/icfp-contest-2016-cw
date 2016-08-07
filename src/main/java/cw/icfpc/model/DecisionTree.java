package cw.icfpc.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DecisionTree
{
    public static List<State> generateDecisionNodes(State state)
    {
        double currentStateArea = state.getSimpleArea();

        List<State> nodes = new LinkedList<>();

        state.getEdges().forEach(edge -> {
            if (state.isEdgeOuter(edge))
                nodes.addAll(tryUnfoldByEdge(state, currentStateArea, edge));
            else {
                //nodes.addAll(trytoMergeByInnerEdge(state, currentStateArea, edge));
            }
        });

        return nodes;
    }

//    private static List<State> trytoMergeByInnerEdge(State state, double currentStateArea, Edge edge) {
//        List<State> proposedStates = new LinkedList<State>();
//        Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
//
//        State newState = state.mergeAdjacentAtomicPolygons(edge);
//        if (newState.getSimpleArea() > currentStateArea && newState.isStateValid())
//        {
//            proposedStates.add(newState);
//        }
//        return proposedStates;
//    }

    private static List<State> tryUnfoldByEdge(State state, double currentStateArea, Edge edge) {
        List<State> proposedStates = new LinkedList<State>();

        // we try to fold only across outer edges
        if (!state.isEdgeOuter(edge))
            return proposedStates;

        Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
        compounds.forEach(sourceCompound -> {
            CompoundPolygon flippedCompound = sourceCompound.flip(edge);
            Iterable<CompoundPolygon> sourceCompoundSubsets = AdjacentPolyGenerator.getAllSourceSubCompoundsToRemove(sourceCompound);
            sourceCompoundSubsets.forEach(srcCpSubset -> {
                // first try to prepare state where we merge atomics by which we unfold
                State newStateMerged = state.addRemoveFlippedCompound(sourceCompound, flippedCompound, srcCpSubset, true);

                // new state is valid only if its ares is bigger than area of previous state
                if (newStateMerged.getSimpleArea() > currentStateArea && newStateMerged.isStateValid())
                {
                    proposedStates.add(newStateMerged);
                }
                else { // then, if merged was not successful, try once again but with non-merged
                    State newStateNotMerged = state.addRemoveFlippedCompound(sourceCompound, flippedCompound, srcCpSubset, false);
                    if (newStateNotMerged.getSimpleArea() > currentStateArea && newStateNotMerged.isStateValid())
                    {
                        proposedStates.add(newStateNotMerged);
                    }

                }
            });

//                // compound contains only 1 atomic polygon. Try to merge it with background
//                if (sourceCompound.getPolygons().size() == 1)
//                {
//                    AtomicPolygon p = sourceCompound.getPolygons().get(0);
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
        return proposedStates;
    }
}
