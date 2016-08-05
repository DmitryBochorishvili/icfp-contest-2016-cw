package cw.icfpc.model;

import java.util.*;

public class AtomicPolygon
{
    private Set<FractionPoint> vertices;
    private List<Edge> edges;
    
    public static AtomicPolygon valueOf(List<FractionPoint> vertices) {
        // FIXME: memoize
        return new AtomicPolygon(vertices);
    }

    /**
     * @deprecated use memoizing factory instead
     */
    @Deprecated
    public AtomicPolygon(List<FractionPoint> vertices)
    {
        this.vertices = new LinkedHashSet<>(vertices);
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
        if (other == this) {
            return false;
        }
        
        for (Edge f : this.edges) {
            if (other.getEdges().contains(f)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AtomicPolygon))
            return false;

        AtomicPolygon that = (AtomicPolygon) o;
        return vertices.equals(that.vertices);
    }

    @Override
    public int hashCode() {
        return vertices.hashCode();
    }

}
