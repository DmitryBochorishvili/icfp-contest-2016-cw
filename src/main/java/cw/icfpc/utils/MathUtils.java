package cw.icfpc.utils;

import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.*;
import java.util.stream.Collectors;

public class MathUtils
{
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

    /**
     * Calculates the angle between vectors AB and BC.
     */
    public static double angleBetween(FractionPoint a, FractionPoint b, FractionPoint c) {
        // http://stackoverflow.com/questions/21483999/using-atan2-to-find-angle-between-two-vectors
        FractionPoint v1 = b.subtract(a);
        FractionPoint v2 = c.subtract(b);

        return Math.atan2(FractionPoint.cross(v1, v2).doubleValue(), FractionPoint.dot(v1, v2).doubleValue());
    }
}
