package cw.icfpc.model;

import cw.icfpc.utils.PolyFormat;

import java.util.ArrayList;
import java.util.Collection;

public class Edge implements Flipable<Edge>
{
    private FractionPoint a;
    private FractionPoint b;

    private Collection<FractionPoint> endpoints;

    public static Edge valueOf(FractionPoint pointA, FractionPoint pointB) {
        // TODO: memoize!
        return new Edge(pointA, pointB);
    }

    public Edge(FractionPoint a, FractionPoint b)
    {
        if (a == b) {
            throw new IllegalArgumentException("b");
        }
        if (a.subtract(b).absSquared() == 0) {
            throw new IllegalArgumentException("ab");
        }
        this.a = a;
        this.b = b;
    }

    public FractionPoint getA()
    {
        return a;
    }

    public FractionPoint getB()
    {
        return b;
    }

    public Edge getReverse()
    {
        return new Edge(getB(), getA());
    }

    @Override
    public Edge flip(Edge relativeTo) {
        return valueOf(a.flip(relativeTo), b.flip(relativeTo));
    }

    public Collection<FractionPoint> getEndpoints()
    {
        Collection<FractionPoint> result = new ArrayList<>();
        result.add(a);
        result.add(b);
        return result;
    }

    @Override
    public String toString()
    {
        return "Edge[" + PolyFormat.format(getEndpoints()) + ']';
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Edge edge = (Edge) o;

        return a.equals(edge.a) && b.equals(edge.b) || a.equals(edge.b) && b.equals(edge.a);
    }

    @Override
    public int hashCode()
    {
        return a.hashCode() + b.hashCode();
    }

    public double getLengthSquared() {
        return a.subtract(b).absSquared();
    }
}
