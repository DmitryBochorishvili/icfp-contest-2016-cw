package cw.icfpc.model;

public class Facet
{
    private FractionPoint a;
    private FractionPoint b;

    public static Facet valueOf(FractionPoint pointA, FractionPoint pointB) {
        // TODO: memoize!
        return new Facet(pointA, pointB);
    }

    /**
     * @deprecated use memoizing factory instead
     */
    @Deprecated
    public Facet(FractionPoint a, FractionPoint b)
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
        return "Facet{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Facet facet = (Facet)o;

        if (!a.equals(facet.a)) return false;
        return b.equals(facet.b);

    }

    @Override
    public int hashCode()
    {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }
}
