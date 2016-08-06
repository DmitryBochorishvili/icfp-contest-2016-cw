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
}
