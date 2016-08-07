package cw.icfpc.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DecisionTree
{
    private static final int THRESHOLD_TIME = 1000;

    private static long startTime;

    public static List<State> generateDecisionNodes(State state)
    {
        double currentStateArea = state.getSimpleArea();

        List<State> nodes = new LinkedList<>();

        startTime = System.currentTimeMillis();

        state.getEdges().forEach(edge -> {
            if (System.currentTimeMillis() - startTime > THRESHOLD_TIME)
                return;
            List<Edge> atomicEdges = new ArrayList<>();
            Edge fullEdge = state.getFullEdge(edge, atomicEdges);
            nodes.addAll(tryUnfoldByEdge(state, currentStateArea, atomicEdges, fullEdge));

            if (!state.isEdgeOuter(edge)) {
                nodes.addAll(trytoMergeByInnerEdge(state, currentStateArea, edge));
            }
        });

        return nodes;
    }

    private static List<State> trytoMergeByInnerEdge(State state, double currentStateArea, Edge edge) {
        List<State> proposedStates = new LinkedList<State>();
        Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds2(state, edge);

        State newState = state.mergeAdjacentAtomicPolygons(edge);
        if (newState != null && newState.isStateValid())
        {
            proposedStates.add(newState);
        }
        return proposedStates;
    }

    private static List<State> tryUnfoldByEdge(State state, double currentStateArea, List<Edge> edges, Edge combinedEdge) {
        List<State> proposedStates = new LinkedList<State>();

        Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds2(state, edges);
        compounds.forEach(sourceCompound -> {
            if (System.currentTimeMillis() - startTime > THRESHOLD_TIME)
                return;

            CompoundPolygon flippedCompound = sourceCompound.flip(combinedEdge);
            Iterable<CompoundPolygon> sourceCompoundSubsets = AdjacentPolyGenerator.getAllSourceSubCompoundsToRemove(sourceCompound);
            sourceCompoundSubsets.forEach(srcCpSubset -> {
                if (System.currentTimeMillis() - startTime > THRESHOLD_TIME)
                    return;

                // first try to prepare state where we merge atomics by which we unfold
                State newStateMerged = state.addRemoveFlippedCompound(sourceCompound, flippedCompound, srcCpSubset, State.FlipOptions.TryMerge);

                // new state is valid only if its ares is bigger than area of previous state
                if (newStateMerged != null && newStateMerged.getSimpleArea() >= currentStateArea && newStateMerged.isStateValid())
                {
                    proposedStates.add(newStateMerged);
                }

                State newStateNotMerged = state.addRemoveFlippedCompound(sourceCompound, flippedCompound, srcCpSubset, State.FlipOptions.Duplicate);
                if (newStateNotMerged != null && newStateNotMerged.getSimpleArea() >= currentStateArea && newStateNotMerged.isStateValid())
                {
                    proposedStates.add(newStateNotMerged);
                }

                State newStateJustFlipped = state.addRemoveFlippedCompound(sourceCompound, flippedCompound, srcCpSubset, State.FlipOptions.FlipOnly);
                if (newStateJustFlipped != null && newStateJustFlipped.getSimpleArea() >= currentStateArea && newStateJustFlipped.isStateValid())
                {
                    proposedStates.add(newStateJustFlipped);
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
