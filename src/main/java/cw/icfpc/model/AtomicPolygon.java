package cw.icfpc.model;

import cw.icfpc.utils.MathUtils;
import cw.icfpc.utils.PolyFormat;

import java.util.*;
import java.util.stream.Collectors;

public class AtomicPolygon implements Flipable<AtomicPolygon>
{
    private List<FractionPoint> vertices;
    private List<Edge> edges;
    private double area = -1;
    
    public static AtomicPolygon valueOf(List<FractionPoint> vertices) {
        // FIXME: memoize
        return new AtomicPolygon(vertices);
    }

    public AtomicPolygon(List<FractionPoint> vertices)
    {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(vertices.size());
        for (int i=1; i<vertices.size(); i++) {
            Edge f = Edge.valueOf(vertices.get(i-1), vertices.get(i));
            this.edges.add(f);
        }
        // and add last edge from last to first point
        Edge f = Edge.valueOf(vertices.get(vertices.size() - 1), vertices.get(0));
        this.edges.add(f);
    }

    public Collection<FractionPoint> getVertices()
    {
        return vertices;
    }

    public List<Edge> getEdges()
    {
        return edges;
    }
    
    public boolean isAdjacent(AtomicPolygon other) {
        return getAdjacentEdge(other) != null;
    }

    public Edge getAdjacentEdge(AtomicPolygon other) {
        if (other == this) {
            return null;
        }

        for (Edge f : this.edges) {
            if (other.getEdges().contains(f)) {
                return f;
            }
        }
        return null;
    }

    public double getArea()
    {
        if (area < 0) // area has not been initialized yet
        {
            area = 0;
            int j = vertices.size() - 1;  // The last vertex is the 'previous' one to the first

            for (int i = 0; i < vertices.size(); i++)
            {
                // area = area +  (X[j]+X[i]) * (Y[j]-Y[i]);
                area += (vertices.get(j).getX().doubleValue() + vertices.get(i).getX().doubleValue())
                        * (vertices.get(j).getY().doubleValue() - vertices.get(i).getY().doubleValue());
                j = i;  //j is previous vertex to i
            }
            area /= 2;
            area = Math.abs(area);
        }
        return area;
    }

    @Override
    public AtomicPolygon flip(Edge relativeTo) {
        List<FractionPoint> flippedVertexes = vertices.stream()
                .map(p -> MathUtils.reflect(p, relativeTo))
                .collect(Collectors.toList());
        return valueOf(flippedVertexes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AtomicPolygon))
            return false;

        AtomicPolygon that = (AtomicPolygon) o;
        return new HashSet<>(vertices).equals(new HashSet<>(that.vertices));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(vertices).hashCode();
    }

    @Override
    public String toString() {
        return "AtomicPolygon{" +
                "vertices=[" + PolyFormat.format(vertices) + "]}";
    }

    /**
     * FIXME: Might be a real problem: unpredictable, if a point lies exactly on a polygon side.
     * http://stackoverflow.com/a/8721483/207791
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     */
    public boolean contains(FractionPoint test) {
        if (vertices.contains(test)) {
            return false;
        }
        
        FractionPoint[] points = vertices.toArray(new FractionPoint[vertices.size()]); 
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
//                if ((points[i].y > test.y) != (points[j].y > test.y) &&
//                        (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) 
//                                / (points[j].y-points[i].y) + points[i].x)) {
//                    result = !result;
//                }
            FractionPoint jMinusI = points[j].subtract(points[i]);
            if (points[i].getY().compareTo(test.getY()) != points[j].getY().compareTo(test.getY()) 
                    &&
                    test.getX().compareTo(
                            jMinusI.getX() .multiply( test.getY().subtract(points[i].getY()) ) . divide(
                                    jMinusI.getY()
                            ) . add( points[i].getX() ) )
                            < 0) 
            {
                result = !result;
            }

        }
        return result;
    }
    
    public FractionPoint getCenter() {
        FractionPoint sum = this.vertices.stream()
                .reduce(FractionPoint.ZERO, FractionPoint::add);
        return sum.divide(this.vertices.size());
    }
    
    public boolean overlaps(AtomicPolygon other) {
        
        for (FractionPoint p: vertices) {
            if (other.contains(p)) {
                return true;
            }
        }

        for (FractionPoint p: other.vertices) {
            if (this.contains(p)) {
                return true;
            }
        }
        
        // Can be sped up by keeping the center.
        return other.contains(this.getCenter()) || this.contains(other.getCenter());
    }
}
