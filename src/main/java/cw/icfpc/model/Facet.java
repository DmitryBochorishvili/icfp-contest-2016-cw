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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Facet facet = (Facet) o;

        return a.equals(facet.a) && b.equals(facet.b) || a.equals(facet.b) && b.equals(facet.a);
    }

    @Override
    public int hashCode()
    {
        return a.hashCode() + b.hashCode();
    }
}
