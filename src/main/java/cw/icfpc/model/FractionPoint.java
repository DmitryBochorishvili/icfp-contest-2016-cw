package cw.icfpc.model;

import cw.icfpc.utils.MathUtils;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;

public class FractionPoint implements Comparable<FractionPoint>, Flipable<FractionPoint>
{
    private BigFraction x;
    private BigFraction y;

    private static FractionPoint valueOf(BigFraction x, BigFraction y) {
        return new FractionPoint(x, y);
    }

    public FractionPoint(BigFraction x, BigFraction y)
    {
        this.x = x;
        this.y = y;
    }

    public BigFraction getX()
    {
        return x;
    }

    public BigFraction getY()
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
    
    private static String fractionToString(BigFraction f)
    {
        if (f.getDenominator() == BigInteger.ONE) {
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
