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
            result.add(CompoundPolygon.valueOf(path));
            
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
        Set<AtomicPolygon> excluded = new TreeSet<>();
        excluded.addAll(adjacent);
        
        return generateAllSubsets(state, root, excluded);
    }

    /**
     * Returns list of possible atomic polygons to be retained in state after flip.
     */
    public static List<CompoundPolygon> getAllSourceSubCompounds(CompoundPolygon poly)
    {
        final int MAX_OPTIONS = 10;

        List<CompoundPolygon> result = new ArrayList<>();

        for (int i = 0; i < Math.pow(2, poly.getPolygons().size()) && i < MAX_OPTIONS; i++)
        {
            List<AtomicPolygon> atomics = new ArrayList<>();
            for (int k = 0; k < poly.getPolygons().size(); k++)
            {
                if ((i & (1L << k)) != 0)
                {
                    atomics.add(poly.getPolygons().get(k));
                }
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

        poly.getPolygons().stream().forEach(a -> edges.addAll(a.getEdges()));

        for (int i = 0; i < poly.getPolygons().size() - 1; i++)
        {
            edges.remove(poly.getPolygons().get(i).getAdjacentEdge(poly.getPolygons().get(i + 1)));
        }

        return Collections.singletonList(new CompoundPolygon(GraphUtils.minimumCycles(edges)));
    }

}
