package cw.icfpc.model;

import org.apache.commons.lang3.math.Fraction;

public class FractionPoint
{
    private Fraction x;
    private Fraction y;

    private static FractionPoint valueOf(Fraction x, Fraction y) {
        return new FractionPoint(x, y);
    }

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

    public FractionPoint subtract(FractionPoint b) {
        return FractionPoint.valueOf(this.x.subtract(b.getX()), this.y.subtract(b.getY()));
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
        return "FractionPoint{" + x + "," + y + '}';
    }

    @Override
    public int hashCode()
    {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
}
