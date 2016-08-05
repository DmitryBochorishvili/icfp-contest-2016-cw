package cw.icfpc.model;

import java.util.List;

public class AtomicPolygon
{
    private List<FractionPoint> vertices;

    public AtomicPolygon(List<FractionPoint> vertices)
    {
        this.vertices = vertices;
    }

    public List<FractionPoint> getVertices()
    {
        return vertices;
    }

}
