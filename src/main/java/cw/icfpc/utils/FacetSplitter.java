package cw.icfpc.utils;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;

/**
 * Doesn't work (and probably shouldn't)
 */
@Deprecated
public class FacetSplitter {

    List<Edge> edges;
    Map<FractionPoint, Set<Edge>> pointEdges = new HashMap<>();
    Set<AtomicPolygon> facets = new HashSet<>();
//    Map<Edge, AtomicPolygon> edgeToFacet = new HashMap<>();

    Map<Edge, Integer> edgeUsage = new HashMap<>();
    int totalEdgeUsage = 0;
    
    public FacetSplitter(List<Edge> edges) {
        this.edges = edges;
        for (Edge e: edges) {
            if (!pointEdges.containsKey(e.getA())) {
                pointEdges.put(e.getA(), new HashSet<>());
            }
            pointEdges.get(e.getA()).add(e);
            pointEdges.get(e.getB()).add(e);
        }
    }
    
    void addEdgeToCycle(List<Edge> cycle, Edge e) {
        edgeUsage.put(e, edgeUsage.getOrDefault(e, 0)+1);
        totalEdgeUsage++;
        cycle.add(e);
    }
    
    FractionPoint getCycleEnd(List<Edge> cycle) {
        Edge lastEdge = cycle.get(cycle.size()-1);
        
        FractionPoint cycleEnd = lastEdge.getB();
        if (cycle.size() > 1) {
            Edge prevEdge = cycle.get(cycle.size()-2);
            if (prevEdge.contains(cycleEnd)) {
                cycleEnd = lastEdge.getA();
            }
            if (prevEdge.contains(cycleEnd)) {
                throw new IllegalStateException("OutOfBrainException: Found a loop and didn't notice it");
            }
        }
        return cycleEnd;
    }
    
    List<Edge> findCycle() {
        List<Edge> cycle = new LinkedList<>();

        Edge lastEdge = edges.stream()
                .filter(e -> edgeUsage.getOrDefault(e, 0) < 2)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Bad graph or me?"));

        addEdgeToCycle(cycle, lastEdge);
        
        while (true) {
            FractionPoint cycleEnd = getCycleEnd(cycle);
            for (Edge nextEdge: pointEdges.get(cycleEnd)) {
                if (edgeUsage.getOrDefault(nextEdge, 0) == 2) {
                    continue;
                }

                addEdgeToCycle(cycle, nextEdge);

                if (cycle.get(0).contains(getCycleEnd(cycle))) {
                    if (cycle.size() > 2) {
                        return cycle;
                    } else {
                        throw new IllegalStateException("Cycle of length <3");
                    }
                }
            }
        }
    }

    public List<AtomicPolygon> intoFacets()
    {
        while (totalEdgeUsage < edges.size()*2) {
            findCycle();
        }
        
        throw new NotImplementedException("nothing to see here!");
    }
}
