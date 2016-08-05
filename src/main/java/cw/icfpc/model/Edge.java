package cw.icfpc.model;

import cw.icfpc.utils.PolyFormat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

public class Edge implements Comparable<Edge>
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
        return "Edge{" + PolyFormat.format(getEndpoints()) + '}';
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


    @Override
    public int compareTo(Edge o)
    {
        int compareA = a.compareTo(o.getA());
        return compareA != 0 ? compareA : b.compareTo(o.getB());
    }
}
