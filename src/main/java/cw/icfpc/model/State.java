package cw.icfpc.model;

import cw.icfpc.utils.GraphUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;

public final class State
{
    private Set<Edge> edges;

    private List<AtomicPolygon> atomicPolygons;

    private MultiValuedMap<Edge, AtomicPolygon> adjacentEdges = new HashSetValuedHashMap<>();
    private MultiValuedMap<AtomicPolygon, AtomicPolygon> adjacentPolygons = new HashSetValuedHashMap<>();

    private int iteration = -1;

    public State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;

        edges = new HashSet<Edge>();
        atomicPolygons.forEach(polygon -> {
            edges.addAll(polygon.getEdges());
        });

        for (int i = 0; i < atomicPolygons.size(); i++)
            for (int k = i + 1; k < atomicPolygons.size(); k++)
            {
                Edge adjacentEdge = atomicPolygons.get(i).getAdjacentEdge(atomicPolygons.get(k));
                if (adjacentEdge != null)
                {
                    adjacentEdges.put(adjacentEdge, atomicPolygons.get(i));
                    adjacentEdges.put(adjacentEdge, atomicPolygons.get(k));
                    adjacentPolygons.put(atomicPolygons.get(i), atomicPolygons.get(k));
                    adjacentPolygons.put(atomicPolygons.get(k), atomicPolygons.get(i));
                }
            }
    }

    public boolean isAdjacent(AtomicPolygon p1, AtomicPolygon p2)
    {
        return adjacentPolygons.get(p1).contains(p2);
    }

    public Collection<AtomicPolygon> getAdjacentPolygons(AtomicPolygon p)
    {
        return adjacentPolygons.get(p);
    }

    public State mergePolygons(AtomicPolygon p1, AtomicPolygon p2)
    {
        if (!isAdjacent(p1, p2))
            throw new RuntimeException("Cannot merge not adjacent polygons");

        AtomicPolygon merged = GraphUtils.merge(p1, p2);
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
        atomicPolygons.remove(p1);
        atomicPolygons.remove(p2);
        atomicPolygons.add(merged);
        return State.valueOf(atomicPolygons);
    }

    public double getHeuristic() {
        return 1000 - atomicPolygons.size();
    }

    public Collection<Edge> getEdges()
    {
        return edges;
    }

    public List<AtomicPolygon> getAtomicPolygons()
    {
        return atomicPolygons;
    }

    public static State valueOf(List<AtomicPolygon> polygons) {
        return new State(polygons);
    }

    // returns sum of all atomic polygons areas
    // doesn't take into account a possibility that a polygon could present in multiple layers.
    public double getSimpleArea()
    {
        return atomicPolygons.stream().mapToDouble(p -> p.getArea()).sum();
    }

    public State addCompound(CompoundPolygon flippedCompound) {
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
        atomicPolygons.addAll(flippedCompound.getPolygons());
        return State.valueOf(atomicPolygons);
    }

    public State addRemoveCompound(CompoundPolygon toAdd, CompoundPolygon toRemove) {
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
        atomicPolygons.addAll(toAdd.getPolygons());
        atomicPolygons.removeAll(toRemove.getPolygons());
        return State.valueOf(atomicPolygons);
    }

    /**
     * Returns true only if there is 1 polygon adjacent to this edge;
     */
    public boolean isEdgeOuter(Edge edge)
    {
        return adjacentEdges.get(edge).isEmpty();
    }

    public boolean allPolygonsAdjacent()
    {
        Set<AtomicPolygon> visited = new HashSet<>();
        Stack<AtomicPolygon> toVisit = new Stack<>();

        toVisit.push(atomicPolygons.get(0));

        while (!toVisit.empty())
        {
            AtomicPolygon p = toVisit.pop();
            visited.add(p);
            adjacentPolygons.get(p).forEach(adj -> {
                if (!visited.contains(adj))
                    toVisit.push(adj);
            });
        }

        return visited.size() == atomicPolygons.size();
    }

    /**
     * Returns true if current state is valid: all polygons are transitively adjacent, areas is not more than 1
     * and linear size is less than sqrt(2)
     */
    public boolean isStateValid()
    {
        return allPolygonsAdjacent() && getSimpleArea() <= 1;
    }

    public int getIteration()
    {
        return iteration;
    }

    public void setIteration(int interation)
    {
        this.iteration = interation;
    }
}
