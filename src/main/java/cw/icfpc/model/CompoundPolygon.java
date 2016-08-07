package cw.icfpc.model;

import cw.icfpc.utils.GraphUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundPolygon implements Geometry<CompoundPolygon>
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
    public CompoundPolygon flip(Edge relativeTo) {
        List<AtomicPolygon> flipped = this.polygons.stream()
                .map(p -> p.flip(relativeTo))
                .collect(Collectors.toList());
        return valueOf(flipped);
    }

    @Override
    public CompoundPolygon rotate(FractionPoint angleTangent) {
        List<AtomicPolygon> flipped = this.polygons.stream()
                .map(p -> p.rotate(angleTangent))
                .collect(Collectors.toList());
        return valueOf(flipped);
    }

    @Override
    public CompoundPolygon move(FractionPoint by) {
        List<AtomicPolygon> flipped = this.polygons.stream()
                .map(p -> p.move(by))
                .collect(Collectors.toList());
        return valueOf(flipped);
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
        return new HashSet<>(polygons).hashCode();
    }

    @Override
    public String toString() {
        return "CompoundPolygon{" + polygons + '}';
    }

    public AtomicPolygon getContour() {
        if (polygons.size() == 1) {
            return polygons.get(0);
        }
        return GraphUtils.merge(polygons);
    }
}
