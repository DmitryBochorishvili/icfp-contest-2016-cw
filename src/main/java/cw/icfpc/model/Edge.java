package cw.icfpc.model;

public class Edge
{
    private FractionPoint a;
    private FractionPoint b;

    public static Edge valueOf(FractionPoint pointA, FractionPoint pointB) {
        // TODO: memoize!
        return new Edge(pointA, pointB);
    }

    /**
     * @deprecated use memoizing factory instead
     */
    @Deprecated
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

    @Override
    public String toString()
    {
        return "Edge{" +
                "a=" + a +
                ", b=" + b +
                '}';
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
}
