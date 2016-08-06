package cw.icfpc.model;

import java.util.*;

public final class State
{
    private List<Edge> edges;

    private List<AtomicPolygon> atomicPolygons;

    public State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;

        edges = new ArrayList<Edge>();
        atomicPolygons.forEach(polygon -> {
            edges.addAll(polygon.getEdges());
        });
    }
    
    public int getHeuristic() {
        return atomicPolygons.size();
    }

    public List<Edge> getEdges()
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
    // doens't take into account a possibility that a polygon could present in multiple layers.
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
