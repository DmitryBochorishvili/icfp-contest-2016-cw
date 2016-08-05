package cw.icfpc.model;

import java.util.*;
import java.util.stream.Stream;

public final class AdjacentPolyGenerator
{

    private static List<AtomicPolygon> getAdjacentTo(State state, Facet facet) {
        List<AtomicPolygon> adjacent = new ArrayList<>(2);
        for (AtomicPolygon a : state.getAtomicPolygons()) {
            if (a.getFacets().contains(facet)) {
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
    
    
    public static List<CompoundPolygon> getAllCompounds(State state, Facet facet) 
    {
        List<AtomicPolygon> adjacent = getAdjacentTo(state, facet);
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
}
