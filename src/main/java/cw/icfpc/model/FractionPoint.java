package cw.icfpc.model;

import cw.icfpc.utils.MathUtils;
import org.apache.commons.lang3.math.Fraction;

public class FractionPoint implements Comparable<FractionPoint>, Flipable<FractionPoint>
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
    public FractionPoint flip(Edge relativeTo) {
        return MathUtils.reflect(this, relativeTo);
    }
    
    private static String fractionToString(Fraction f) 
    {
        if (f.getDenominator() == 1) {
            return String.valueOf(f.getNumerator());
        }
        return f.toString();
    }

    @Override
    public String toString()
    {
        return "FractionPoint{" + fractionToString(x) + "," + fractionToString(y) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FractionPoint that = (FractionPoint) o;

        return x.equals(that.x) && y.equals(that.y);
    }

    @Override
    public int hashCode()
    {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    @Override
    public int compareTo(FractionPoint o)
    {
        int compareX = x.compareTo(o.getX());
        return compareX != 0 ? compareX : y.compareTo(o.getY());
    }
}
