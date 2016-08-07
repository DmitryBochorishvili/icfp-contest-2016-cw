package cw.icfpc.utils;

import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class MathUtils
{
    public static final double EPSILON = 1e-10;

    public static FractionPoint reflect(FractionPoint p, Edge edge)
    {
        // http://math.stackexchange.com/q/65503/67043
        BigFraction x1 = edge.getB().getX();
        BigFraction y1 = edge.getB().getY();
        BigFraction x0 = edge.getA().getX();
        BigFraction y0 = edge.getA().getY();

        BigFraction dx = x1.subtract(x0);
        BigFraction dy = y1.subtract(y0);

        BigFraction sumOfSquares = dx.pow(2).add(dy.pow(2));
        BigFraction a = (dx.pow(2).subtract(dy.pow(2))) . divide(sumOfSquares);
        BigFraction b = dx.add(dx).multiply(dy) . divide(sumOfSquares);

        BigFraction x2 = a.multiply(p.getX().subtract(x0)) . add( b.multiply(p.getY().subtract(y0)) ) . add(x0);
        BigFraction y2 = b.multiply(p.getX().subtract(x0)) . subtract( a.multiply(p.getY().subtract(y0)) ) . add(y0);

        return new FractionPoint(x2, y2);
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
        BigFraction x1 = f1.getA().getX();
        BigFraction x2 = f1.getB().getX();
        BigFraction x3 = f2.getA().getX();
        BigFraction x4 = f2.getB().getX();

        BigFraction y1 = f1.getA().getY();
        BigFraction y2 = f1.getB().getY();
        BigFraction y3 = f2.getA().getY();
        BigFraction y4 = f2.getB().getY();

        // (x1-x2)(y3-y4)-(y1-y2)(x3-x4)
        BigFraction denominator = x1.subtract(x2)
                .multiply(y3.subtract(y4))
                .subtract(
                        y1.subtract(y2).multiply(x3.subtract(x4)));

        if (Math.abs(denominator.doubleValue()) < 1e-6)
            return null; // lines are parallel or close to parallel

        // (x1y2 - y1x2)(x3 - x4)-(x1 - x2)(x3y4 - y3x4)
        BigFraction x_nominator = x1.multiply(y2).subtract(y1.multiply(x2))
                .multiply(x3.subtract(x4))
                .subtract(x1.subtract(x2).multiply(
                        x3.multiply(y4).subtract(y3.multiply(x4))));

        // (x1y2 - y1x2)(y3 - y4) - (y1 - y2)(x3y4 - y3x4)
        BigFraction y_nominator = x1.multiply(y2).subtract(y1.multiply(x2))
                .multiply(y3.subtract(y4))
                .subtract(y1.subtract(y2).multiply(
                        x3.multiply(y4).subtract(y3.multiply(x4))));

        FractionPoint intersection = new FractionPoint(x_nominator.divide(denominator).reduce(), y_nominator.divide(denominator).reduce());

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
        BigFraction x1 = f.getA().getX();
        BigFraction x2 = f.getB().getX();

        BigFraction y1 = f.getA().getY();
        BigFraction y2 = f.getB().getY();

        boolean withinX = x1.compareTo(x2) < 0
                ? x1.compareTo(p.getX()) <= 0 && x2.compareTo(p.getX()) >= 0
                : x1.compareTo(p.getX()) >= 0 && x2.compareTo(p.getX()) <= 0;

        boolean withinY = y1.compareTo(y2) < 0
                ? y1.compareTo(p.getY()) <= 0 && y2.compareTo(p.getY()) >= 0
                : y1.compareTo(p.getY()) >= 0 && y2.compareTo(p.getY()) <= 0;

        return withinX && withinY;
    }

    /**
     * Splits input facets into smaller facets by their intersection points.
     * The returned facets do not intersect with each other, they may only have
     * common edge.
     */
    public static Collection<Edge> splitByIntersections(Collection<Edge> edges)
    {
        List<Edge> newEdges = new ArrayList<>();
        edges.forEach(edge -> {
            List<FractionPoint> endpoints = new ArrayList<FractionPoint>();
            endpoints.addAll(edge.getEndpoints());
            List<FractionPoint> intersections = edges.stream().map(innerEdge -> getEdgeIntersection(edge, innerEdge))
                    .filter(intersection -> intersection != null && !edge.getEndpoints().contains(intersection))
                    .sorted()
                    .distinct()
                    .collect(Collectors.toList());

            intersections.addAll(edge.getEndpoints());
            intersections.sort(Comparator.<FractionPoint>naturalOrder());
            for (int i = 1; i < intersections.size(); i++)
                newEdges.add(new Edge(intersections.get(i - 1), intersections.get(i)));
        });
        return newEdges;
    }

    public static double distance(FractionPoint a, FractionPoint b)
    {
        double dx = a.getX().doubleValue() - b.getX().doubleValue();
        double dy = a.getY().doubleValue() - b.getY().doubleValue();

        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Calculates the angle between vectors AB and BC.
     */
    public static double angleBetween(FractionPoint a, FractionPoint b, FractionPoint c) {
        // http://stackoverflow.com/questions/21483999/using-atan2-to-find-angle-between-two-vectors
        FractionPoint v1 = b.subtract(a);
        FractionPoint v2 = c.subtract(b);

        return Math.atan2(FractionPoint.cross(v1, v2).doubleValue(), FractionPoint.dot(v1, v2).doubleValue());
    }

    /**
     * http://math.stackexchange.com/a/1033561/67043
     * FIXME: DO return both intersection points.
     * @param centerA
     * @param centerB
     * @param radiusASquare
     * @param radiusBSquare
     * @return 1..2 intersection points for the given circles (now only 1)
     */
    public static FractionPoint[] getCirclesIntersection(
            FractionPoint centerA, FractionPoint centerB,
            BigFraction radiusASquare, BigFraction radiusBSquare)
    {
        BigFraction dSquared = centerA.subtract(centerB).absSquaredFraction();
        double d = Math.sqrt(dSquared.doubleValue());
        BigFraction dApproximate = new BigFraction(d);
        BigFraction el = radiusASquare.subtract(radiusBSquare).add(dSquared).divide(2).divide(dApproximate);
        double h = Math.sqrt(radiusASquare.subtract(el.pow(2)).doubleValue());

        FractionPoint c2MinusC1 = centerB.subtract(centerA);

        BigFraction anotherAttempt = new BigFraction(h / d);
        BigFraction elByD = new BigFraction(el.doubleValue() / d);

        // We could generate 2 points, but we actually need them to be on one side of the (c1, c2) line.
        BigFraction x = elByD.multiply(c2MinusC1.getX()).add(centerA.getX()) . add(anotherAttempt.multiply(c2MinusC1.getY()));
        BigFraction y = elByD.multiply(c2MinusC1.getY()).add(centerA.getY()) . subtract(anotherAttempt.multiply(c2MinusC1.getX()));
        
        return new FractionPoint[] {new FractionPoint(x, y)};
    }

    /**
     * Just doesn't work.
     */
    @Deprecated
    public static BigFraction sqrt(BigFraction x) {
        // as we must get a rational fraction in the end, does this mean there 
        // must exist a rational square root of the fraction?
        BigInteger numeratorRoot = sqrt(x.getNumerator());
        BigInteger denominatorRoot = sqrt(x.getDenominator());
        assert numeratorRoot.pow(2).equals(x.getNumerator());
        assert denominatorRoot.pow(2).equals(x.getDenominator());
        return new BigFraction(numeratorRoot, denominatorRoot);
    }

    /**
     * Just doesn't work with a defined precision.
     */
    @Deprecated
    // http://stackoverflow.com/a/16804098/207791
    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
    }

    /**
     * Rotates given point by specified angle around 0,0.
     * Angle is specified as a coordinate of point that should end up
     * at 1,0 coordinates after rotation by that angle.
     */
    public static FractionPoint rotate(FractionPoint point, FractionPoint angleTangent)
    {
        BigFraction a = angleTangent.getX();
        BigFraction b = angleTangent.getY().negate();
        BigFraction c = BigFraction.ONE;

        BigFraction x = point.getX();
        BigFraction y = point.getY();

        // x1 = x * a / c - y * b / c
        BigFraction x1 = x.multiply(a) .subtract (y.multiply(b));

        // y1 = y * a / c + x * b / c
        BigFraction y1 = y.multiply(a) .add (x.multiply(b));

        return new FractionPoint(x1, y1);
    }
}
