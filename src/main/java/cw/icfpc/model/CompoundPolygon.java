package cw.icfpc.model;

import java.util.List;

public class CompoundPolygon
{
    private List<AtomicPolygon> polygons;

    public CompoundPolygon(List<AtomicPolygon> polygons)
    {
        this.polygons = polygons;
    }

    public List<AtomicPolygon> getPolygons()
    {
        return polygons;
    }

}
