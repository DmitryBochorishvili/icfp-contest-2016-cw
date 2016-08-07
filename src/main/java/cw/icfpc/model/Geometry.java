package cw.icfpc.model;

public interface Geometry<T> {
    T flip(Edge relativeTo);
    
    T rotate(FractionPoint angleTangent);

    T move(FractionPoint by);
}
