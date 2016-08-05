package cw.icfpc.model;

import java.util.ArrayList;
import java.util.List;

public class AtomicPolygon
{
    private List<FractionPoint> vertices;
    private List<Facet> facets;
    
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
        this.vertices = vertices;
        this.facets = new ArrayList<>(vertices.size());
        for (int i=1; i<vertices.size(); i++) {
            Facet f = Facet.valueOf(vertices.get(i-1), vertices.get(i));
            this.facets.add(f);
        }
        // and add last facet from last to first point
        Facet f = Facet.valueOf(vertices.get(vertices.size() - 1), vertices.get(0));
        this.facets.add(f);
    }

    public List<FractionPoint> getVertices()
    {
        return vertices;
    }

    public List<Facet> getFacets()
    {
        return facets;
    }
    
    public boolean isAdjacent(AtomicPolygon other) {
        if (other == this) {
            return false;
        }
        
        for (Facet f : this.facets) {
            if (other.getFacets().contains(f)) {
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
        int result = vertices.hashCode();
        result = 31 * result + facets.hashCode();
        return result;
    }

}
