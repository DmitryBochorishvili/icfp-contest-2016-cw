package cw.icfpc.model;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CompoundPolygon
{
    // TODO: Do we actually need their order? Especially that it's not always in adjacency order.
    private List<AtomicPolygon> polygons;

    public CompoundPolygon(List<AtomicPolygon> polygons)
    {
        this.polygons = polygons;
    }

    public List<AtomicPolygon> getPolygons()
    {
        return polygons;
    }

    public static CompoundPolygon valueOf(List<AtomicPolygon> polygons) {
        return new CompoundPolygon(polygons);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CompoundPolygon))
            return false;

        CompoundPolygon that = (CompoundPolygon) o;

        return new HashSet<>(polygons).equals(new HashSet<>(that.polygons));
    }

    @Override
    public int hashCode() {
        return polygons.hashCode();
    }
}
