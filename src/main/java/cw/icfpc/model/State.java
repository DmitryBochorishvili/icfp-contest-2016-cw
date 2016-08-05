package cw.icfpc.model;

import com.sun.javafx.scene.control.skin.VirtualFlow;

import java.util.*;

public final class State
{
    private List<Facet> facets;

    private List<AtomicPolygon> atomicPolygons;

    public State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;

        facets = new ArrayList<Facet>();
        atomicPolygons.forEach(polygon -> {
            facets.addAll(polygon.getFacets());
        });
    }

    public List<Facet> getFacets()
    {
        return facets;
    }

    public List<AtomicPolygon> getAtomicPolygons()
    {
        return atomicPolygons;
    }

    public static State valueOf(List<AtomicPolygon> polygons) {
        return new State(polygons);
    }
}
