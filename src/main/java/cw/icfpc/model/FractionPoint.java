package cw.icfpc.model;

import cw.icfpc.utils.MathUtils;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;

public class FractionPoint implements Comparable<FractionPoint>, Flipable<FractionPoint>
{
    private BigFraction x;
    private BigFraction y;
    
    public static final FractionPoint ZERO = FractionPoint.valueOf(BigFraction.ZERO, BigFraction.ZERO);
    
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

    public FractionPoint add(FractionPoint b) {
        return FractionPoint.valueOf(this.x.add(b.getX()), this.y.add(b.getY()));
    }
    
    public FractionPoint subtract(FractionPoint b) {
        return FractionPoint.valueOf(this.x.subtract(b.getX()), this.y.subtract(b.getY()));
    }

    public FractionPoint divide(int by) {
        return FractionPoint.valueOf(this.x.divide(by), this.y.divide(by));
    }

    public double absSquared() {
        return getX().pow(2).add(getY().pow(2)).doubleValue();
    }

    public BigFraction absSquaredFraction() {
        return getX().pow(2).add(getY().pow(2));
    }

    public static BigFraction dot(FractionPoint a, FractionPoint b)
    {
        return a.getX().multiply(b.getX()) . add( a.getY().multiply(b.getY()) );
    }
    
    public static BigFraction cross(FractionPoint a, FractionPoint b)
    {
        return a.getX().multiply(b.getY()) . subtract( a.getY().multiply(b.getX()) );
    }    

    @Override
    public FractionPoint flip(Edge relativeTo) {
        return MathUtils.reflect(this, relativeTo);
    }
    
    private static String fractionToString(BigFraction f)
    {
        return f.toString().replaceAll(" ", "");
    }
    
    public String toSimpleString() 
    {
        return fractionToString(x) + "," + fractionToString(y);
    }

    @Override
    public String toString()
    {
        return "FractionPoint{" + toSimpleString() + '}';
    }
    
    public boolean closeTo(FractionPoint other) 
    {
        double d = this.subtract(other).absSquared();
        return d < 1e-6;
    }

    @Override
    public boolean equals(Object o) 
    {
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
