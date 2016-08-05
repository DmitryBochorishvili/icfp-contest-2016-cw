package cw.icfpc.model;

import org.apache.commons.lang3.math.Fraction;

public class FractionPoint
{
    private Fraction x;
    private Fraction y;

    public FractionPoint(Fraction x, Fraction y)
    {
        this.x = x;
        this.y = y;
    }


    public Fraction getX()
    {
        return x;
    }

    public Fraction getY()
    {
        return y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FractionPoint that = (FractionPoint)o;

        if (!x.equals(that.x)) return false;
        return y.equals(that.y);

    }

    @Override
    public String toString()
    {
        return "FractionPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public int hashCode()
    {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
}
