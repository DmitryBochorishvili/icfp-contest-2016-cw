package cw.icfpc.model;

public class DecisionTree
{
    public void generateDecisionNodes(State state)
    {

        AdjacentPolyGenerator polyGenerator = new AdjacentPolyGenerator();

        state.getEdges().forEach(facet -> {
            Iterable<CompoundPolygon> compounds = AdjacentPolyGenerator.getAllCompounds(state, facet);
            compounds.forEach(cp -> {
//                CompoundPolygon flippedCompound = cp.flip(facet);
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
