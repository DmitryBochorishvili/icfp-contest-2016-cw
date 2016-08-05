package cw.icfpc.model;

public class DecisionTree
{
    public void generateDecisionNodes(State state)
    {

        AdjacentPolyGenerator polyGenerator = new AdjacentPolyGenerator();

        state.getEdges().forEach(edge -> {
            Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, edge);
            compounds.forEach(cp -> {
//                CompoundPolygon flippedCompound = cp.flip(edge);
//                cp.getSubsets().forEach(compoundSubset -> {

                    // из начального состояния вычесть compound
                    // добавить flippedCompound в его возмножных конфигурациях
                    // добавить сабсет в его возможных конфигурациях

//                        State newState =
//                        compoundSubset
//                });
            });
        });

    }
}
