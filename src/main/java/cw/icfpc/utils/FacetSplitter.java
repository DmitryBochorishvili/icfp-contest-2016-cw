package cw.icfpc.utils;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;

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
            if (!pointEdges.containsKey(e.getB())) {
                pointEdges.put(e.getB(), new HashSet<>());
            }
            pointEdges.get(e.getA()).add(e);
            pointEdges.get(e.getB()).add(e);
        }
    }
    
    void addEdgeToCycle(List<Edge> cycle, List<FractionPoint> cyclePoints, Edge e) {
        if (cycle.contains(e)) {
            throw new IllegalStateException("What, again?");
        }
        edgeUsage.put(e, edgeUsage.getOrDefault(e, 0)+1);
        totalEdgeUsage++;
        
        if (cyclePoints.isEmpty()) {
            cyclePoints.add(e.getA());
            cyclePoints.add(e.getB());
        }
        else if (cyclePoints.get(cyclePoints.size()-1).equals(e.getA())) {
            cyclePoints.add(e.getB());
        }
        
        cycle.add(e);
    }
    
    List<Edge> findCycle() {
        List<Edge> cycle = new LinkedList<>();
        List<FractionPoint> cyclePoints = new LinkedList<>();

        Edge lastEdge = edges.stream()
                .filter(e -> edgeUsage.getOrDefault(e, 0) == 0)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Bad graph or me?"));

        addEdgeToCycle(cycle, cyclePoints, lastEdge);
        
        while (true) {
            FractionPoint cycleEnd = cyclePoints.get(cyclePoints.size()-1);
            for (Edge nextEdge: edges) {
                // Somewhy doesn't retrieve proper entry.
//            for (Edge nextEdge: pointEdges.get(cycleEnd)) {
                if (nextEdge == lastEdge 
                        || edgeUsage.getOrDefault(nextEdge, 0) == 2
                        || !nextEdge.contains(cycleEnd)
                        || cycle.contains(nextEdge)) 
                {
                    continue;
                }

                addEdgeToCycle(cycle, cyclePoints, nextEdge);
                cycleEnd = cyclePoints.get(cyclePoints.size()-1);

                if (cyclePoints.get(0).equals(cycleEnd)) {
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
        List<List<Edge>> cycles = new ArrayList<>();
        while (totalEdgeUsage < edges.size()*2) {
            List<Edge> c = findCycle();
            cycles.add(c);
        }
        
        throw new NotImplementedException("nothing to see here!");
    }
}
