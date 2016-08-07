package cw.icfpc.model;

import cw.icfpc.utils.MathUtils;
import cw.icfpc.utils.PolyFormat;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Edge implements Geometry<Edge>
{
    private FractionPoint a;
    private FractionPoint b;

    private Collection<FractionPoint> endpoints;

    public static Edge valueOf(FractionPoint pointA, FractionPoint pointB) {
        // TODO: memoize!
        return new Edge(pointA, pointB);
    }

    public Edge(FractionPoint a, FractionPoint b)
    {
        if (a == b) {
            throw new IllegalArgumentException("b");
        }
        if (a.subtract(b).absSquared() == 0) {
            throw new IllegalArgumentException("ab");
        }
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

    public Edge getReverse()
    {
        return new Edge(getB(), getA());
    }

    @Override
    public Edge flip(Edge relativeTo) {
        return valueOf(a.flip(relativeTo), b.flip(relativeTo));
    }
    
    @Override
    public Edge rotate(FractionPoint angleTangent) {
        return valueOf(a.rotate(angleTangent), b.rotate(angleTangent));
    }

    @Override
    public Edge move(FractionPoint by) {
        return valueOf(a.move(by), b.move(by));
    }

    public Collection<FractionPoint> getEndpoints()
    {
        Collection<FractionPoint> result = new ArrayList<>();
        result.add(a);
        result.add(b);
        return result;
    }

    @Override
    public String toString()
    {
        return "Edge[" + PolyFormat.format(getEndpoints()) + ']';
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Edge edge = (Edge) o;

        return a.equals(edge.a) && b.equals(edge.b) || a.equals(edge.b) && b.equals(edge.a);
    }

    @Override
    public int hashCode()
    {
        return a.hashCode() + b.hashCode();
    }

    public double getLengthSquared() {
        return a.subtract(b).absSquared();
    }

    public BigFraction getLengthSquaredFraction() {
        return a.subtract(b).absSquaredFraction();
    }

    public boolean contains(FractionPoint v) {
        return a.equals(v) || b.equals(v);
    }

    public Edge mergeWith(Edge other) {
        List<FractionPoint> pts = new ArrayList<>();
        pts.add(getA());
        pts.add(getB());
        pts.add(other.getA());
        pts.add(other.getB());

        FractionPoint same = null;
        for(int i = 0; i < 3; i++) {
            for(int k = i + 1; k < 4; k++) {
                if(pts.get(i).equals(pts.get(k))) {
                    same = pts.get(i);
                    pts.remove(same);
                    pts.remove(same);
                    assert pts.size() == 2;

                    double angle = MathUtils.angleBetween(pts.get(0), same, pts.get(1));
                    if(Math.abs(angle) > MathUtils.EPSILON)
                        return null;
                    return new Edge(pts.get(0), pts.get(1));
                }

            }
        }

        return null;
    }


}
