package cw.icfpc.utils;

import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

public class MathUtils
{
    public static FractionPoint reflect(FractionPoint p, Edge edge)
    {
        double nx = edge.getA().getY().doubleValue() - edge.getB().getY().doubleValue();
        double ny = edge.getB().getX().doubleValue() - edge.getA().getX().doubleValue();
        double len = Math.sqrt(nx*nx + ny*ny);
        nx /= len;
        ny /= len;

        double dot2 = 2 * (nx * p.getX().doubleValue() + ny * p.getY().doubleValue());

        double rx = p.getX().doubleValue() - dot2 * nx;
        double ry = p.getY().doubleValue() - dot2 * ny;

        return new FractionPoint(Fraction.getFraction(rx), Fraction.getFraction(ry));
    }
    
    public static boolean isClockwise(List<FractionPoint> loop) 
    {
        // http://stackoverflow.com/a/1165943/207791
        double area = 0;
        for (int i=0; i<loop.size(); i++) {
            FractionPoint p1 = loop.get(i);
            FractionPoint p0 = i > 0 ? loop.get(i-1) : loop.get(loop.size()-1);
            area += (p1.getX().doubleValue() - p0.getX().doubleValue()) * (p1.getY().doubleValue() + p0.getY().doubleValue());
        }
        return area > 0;
    }


    /**
     * Returns intersection point for 2 lines specified as edges.
     * If lines do not intersect then returns null.
     */
    public static FractionPoint getLineIntersection(Edge f1, Edge f2)
    {
        Fraction x1 = f1.getA().getX();
        Fraction x2 = f1.getB().getX();
        Fraction x3 = f2.getA().getX();
        Fraction x4 = f2.getB().getX();

        Fraction y1 = f1.getA().getY();
        Fraction y2 = f1.getB().getY();
        Fraction y3 = f2.getA().getY();
        Fraction y4 = f2.getB().getY();

        // (x1-x2)(y3-y4)-(y1-y2)(x3-x4)
        Fraction denominator = x1.subtract(x2)
                .multiplyBy(y3.subtract(y4))
                .subtract(
                        y1.subtract(y2).multiplyBy(x3.subtract(x4)));

        if (Math.abs(denominator.doubleValue()) < 1e-6)
            return null; // lines are parallel or close to parallel

        // (x1y2 - y1x2)(x3 - x4)-(x1 - x2)(x3y4 - y3x4)
        Fraction x_nominator = x1.multiplyBy(y2).subtract(y1.multiplyBy(x2))
                .multiplyBy(x3.subtract(x4))
                .subtract(x1.subtract(x2).multiplyBy(
                        x3.multiplyBy(y4).subtract(y3.multiplyBy(x4))));

        // (x1y2 - y1x2)(y3 - y4) - (y1 - y2)(x3y4 - y3x4)
        Fraction y_nominator = x1.multiplyBy(y2).subtract(y1.multiplyBy(x2))
                .multiplyBy(y3.subtract(y4))
                .subtract(y1.subtract(y2).multiplyBy(
                        x3.multiplyBy(y4).subtract(y3.multiplyBy(x4))));

        FractionPoint intersection = new FractionPoint(x_nominator.divideBy(denominator).reduce(), y_nominator.divideBy(denominator).reduce());

        return intersection;
    }

    /**
     * Returns intersection point for 2 edges.
     * If edges do not intersect then returns null.
     */
    public static FractionPoint getEdgeIntersection(Edge f1, Edge f2)
    {
        FractionPoint p = getLineIntersection(f1, f2);

        if (p == null)
            return p;

        return isPointWithin(p, f1) && isPointWithin(p, f2) ? p : null;
    }

    public static boolean isPointWithin(FractionPoint p, Edge f)
    {
        Fraction x1 = f.getA().getX();
        Fraction x2 = f.getB().getX();

        Fraction y1 = f.getA().getY();
        Fraction y2 = f.getB().getY();

        boolean withinX = x1.compareTo(x2) < 0
                ? x1.compareTo(p.getX()) <= 0 && x2.compareTo(p.getX()) >= 0
                : x1.compareTo(p.getX()) >= 0 && x2.compareTo(p.getX()) <= 0;

        boolean withinY = y1.compareTo(y2) < 0
                ? y1.compareTo(p.getY()) <= 0 && y2.compareTo(p.getY()) >= 0
                : y1.compareTo(p.getY()) >= 0 && y2.compareTo(p.getY()) <= 0;

        return withinX && withinY;
    }
}
