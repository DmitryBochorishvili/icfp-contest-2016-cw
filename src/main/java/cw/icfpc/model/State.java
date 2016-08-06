package cw.icfpc.model;

import cw.icfpc.utils.GraphUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;

public final class State
{
    private Set<Edge> edges;

    private List<AtomicPolygon> atomicPolygons;

    private MultiValuedMap adjacentPolygons = new HashSetValuedHashMap<>();

    public State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;

        edges = new HashSet<Edge>();
        atomicPolygons.forEach(polygon -> {
            edges.addAll(polygon.getEdges());
        });

        for (int i = 0; i < atomicPolygons.size(); i++)
            for (int k = i + 1; k < atomicPolygons.size(); k++)
                if (atomicPolygons.get(i).isAdjacent(atomicPolygons.get(k)))
                {
                    adjacentPolygons.put(atomicPolygons.get(i), atomicPolygons.get(k));
                    adjacentPolygons.put(atomicPolygons.get(k), atomicPolygons.get(i));
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

    public int getHeuristic() {
        return atomicPolygons.size();
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
}
