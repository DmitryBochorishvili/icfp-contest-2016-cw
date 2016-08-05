package cw.icfpc.model;

import java.util.List;
import java.util.stream.Stream;

public class State
{
    private List<Facet> facets;

    private List<AtomicPolygon> atomicPolygons;

    public State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;
    }

    public List<Facet> getFacets()
    {
        return facets;
    }

    public List<AtomicPolygon> getAtomicPolygons()
    {
        return atomicPolygons;
    }

}
