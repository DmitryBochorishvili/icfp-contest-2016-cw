package cw.icfpc.model;


import cw.icfpc.utils.GraphUtils;

import java.util.*;
import java.util.stream.Collector;

public final class AdjacentPolyGenerator
{

    private static List<AtomicPolygon> getAdjacentTo(State state, Edge edge) {
        List<AtomicPolygon> adjacent = new ArrayList<>(2);
        for (AtomicPolygon a : state.getAtomicPolygons()) {
            if (a.getEdges().contains(edge)) {
                adjacent.add(a);
            }
        }
        return adjacent;
    }
    
    private static List<AtomicPolygon> getPathTo(Map<AtomicPolygon, AtomicPolygon> pathFromTo, AtomicPolygon node) {
        List<AtomicPolygon> path = new LinkedList<>();
        while (node != null) {
            path.add(node);
            node = pathFromTo.getOrDefault(node, null);
        }
        Collections.reverse(path);
        return path;
    }

    static List<CompoundPolygon> generateAllSubsets(
            State state,
            AtomicPolygon start,
            Set<AtomicPolygon> excluded) 
    {
        List<CompoundPolygon> result = new LinkedList<>();
        Map<AtomicPolygon, AtomicPolygon> pathToFrom = new HashMap<>();
        
        // https://en.wikipedia.org/wiki/Depth-first_search
        Stack<AtomicPolygon> s = new Stack<>();
        
        s.push(start);
        while (!s.empty()) {
            AtomicPolygon v = s.pop();
            List<AtomicPolygon> path = getPathTo(pathToFrom, v);

            //if (path.size() < state.getAtomicPolygons().size())
            {
                // add path only if it doesn't include all atomics. In such case it would full flip of entire silhouette
                result.add(CompoundPolygon.valueOf(path));
            }
            
            if (!excluded.contains(v) && !s.contains(v)) {
                excluded.add(v);
                for (AtomicPolygon w: state.getAtomicPolygons()) {
                    if (!excluded.contains(w) && v.isAdjacent(w)) {
                        pathToFrom.put(w, v);
                        s.push(w);
                    }
                }
            }
        }
        
        return result;
    }
    
    
    public static List<CompoundPolygon> getAllCompounds(State state, Edge edge) 
    {
        List<AtomicPolygon> adjacent = getAdjacentTo(state, edge);
        if (adjacent.size() < 1) {
            return Collections.emptyList();
        }

        AtomicPolygon root = adjacent.get(0);
        adjacent.remove(0);
        assert adjacent.size() <= 1;
        Set<AtomicPolygon> excluded = new LinkedHashSet<>();
        excluded.addAll(adjacent);
        
        return generateAllSubsets(state, root, excluded);
    }

    static List<Set<AtomicPolygon>> generateAllSubtrees(
            State state,
            AtomicPolygon currentNode,
            Set<AtomicPolygon> parentTree,
            Set<AtomicPolygon> excluded)
    {
        List<Set<AtomicPolygon>> result = new ArrayList<>();

        // don't go deeper than 5 nodes
        if (parentTree.size() >= 3)
            return result;

        Set<AtomicPolygon> currentTree = new HashSet<AtomicPolygon>(parentTree);
        currentTree.add(currentNode);
        result.add(currentTree);

        state.getAdjacentPolygons(currentNode).stream()
                .filter(n -> !parentTree.contains(n) && !excluded.contains(n))
                .forEach(n -> {
                    result.addAll(generateAllSubtrees(state, n, currentTree, excluded));
                });

        return result;
    }


    public static Set<CompoundPolygon> getAllCompounds2(State state, Edge edge)
    {
        List<AtomicPolygon> adjacent = getAdjacentTo(state, edge);
        if (adjacent.size() < 1) {
            return Collections.emptySet();
        }

        AtomicPolygon root = adjacent.get(0);
        adjacent.remove(0);
        assert adjacent.size() <= 1;
        Set<AtomicPolygon> excluded = new LinkedHashSet<>();
        excluded.addAll(adjacent);

        List<Set<AtomicPolygon>> subtrees = generateAllSubtrees(state, root, Collections.emptySet(), excluded);

        // generate all combinations of subtrees
        long n = 1 << subtrees.size();

        Set<CompoundPolygon> result = new HashSet<>();
        for (long i = 1; i < n; i++)
        {
            Set<AtomicPolygon> atoms = new HashSet<>();
            for (int k = 0; k < subtrees.size(); k++)
            {
                if ((i & (1L << k)) != 0)
                    atoms.addAll(subtrees.get(k));
            }
            result.add(new CompoundPolygon(new ArrayList<>(atoms)));
        }
        return result;
    }

    /**
     * Returns list of possible atomic polygons to be removed in state after flip.
     * Suggests to remove all tailing polygons up to the first one.
     */
    public static List<CompoundPolygon> getAllSourceSubCompoundsToRemove(State state, CompoundPolygon poly)
    {
        List<CompoundPolygon> result = new ArrayList<>();

        long n = 1 << poly.getPolygons().size();

        // if poly contains all problem atomics then it doesn't make sense to return an option to remove all of them.
        // it will lead just to a dump flip of entire state
        if (poly.getPolygons().size() == state.getAtomicPolygons().size())
            n--;

        for (int i = 0; i < n; i++)
        {
            List<AtomicPolygon> atomics = new ArrayList<>();
            for (int k = 0; k < poly.getPolygons().size(); k++)
            {
                if ((i & (1L << k)) != 0)
                    atomics.add(poly.getPolygons().get(k));
            }
            result.add(new CompoundPolygon(atomics));
        }
        return result;
    }

    /**
     * Returns list of possible new atomic polygons after flip.
     */
    public static List<CompoundPolygon> getAllFlippedCompounds(CompoundPolygon poly)
    {
        Set<Edge> edges = new HashSet<>();

        poly.getPolygons().forEach(a -> edges.addAll(a.getEdges()));

        for (int i = 0; i < poly.getPolygons().size() - 1; i++)
        {
            edges.remove(poly.getPolygons().get(i).getAdjacentEdge(poly.getPolygons().get(i + 1)));
        }

        return Collections.singletonList(new CompoundPolygon(GraphUtils.minimumCycles(edges)));
    }

}
