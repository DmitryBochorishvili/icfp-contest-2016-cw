package spalah.tesselate;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.utils.MathUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fills a 1x1 square with the given polygons, respecting their adjacency. 
 */
public class Parquet {

    /**
     * Creates a new AtomicPolygon by rotating its tileEdge to match target
     * @param tile Tile to rotate
     * @param tileEdge
     * @param target
     * @return 4 variants of the rotated AtomicPolygon
     */
    List<AtomicPolygon> rotateToMatch(AtomicPolygon tile, Edge tileEdge, Edge target) {
        FractionPoint otherVertex = tile.getVertices()
                .stream()
                .filter(v -> !tileEdge.contains(v))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("An AtomicPolygon is supposed to contain at least 3 vertexes"));
        Edge[] cornerEdges = tile.getEdges()
                .stream()
                .filter(p -> p != tileEdge)
                .collect(Collectors.toList())
                .toArray(new Edge[2]);
        assert cornerEdges.length == 2;

        FractionPoint otherVertexMoved1 = MathUtils.getCirclesIntersection(
                target.getA(), target.getB(),
                cornerEdges[0].getLengthSquaredFraction(),
                cornerEdges[1].getLengthSquaredFraction()
        )[0];
        // The same side of the line, reverse radiuses. 
        FractionPoint otherVertexMoved2 = MathUtils.getCirclesIntersection(
                target.getA(), target.getB(),
                cornerEdges[1].getLengthSquaredFraction(),
                cornerEdges[0].getLengthSquaredFraction()
        )[0];

        FractionPoint otherVertexMoved3 = MathUtils.reflect(otherVertexMoved1, target);
        FractionPoint otherVertexMoved4 = MathUtils.reflect(otherVertexMoved2, target);

        return Arrays.stream(
                new FractionPoint[] {otherVertexMoved1, otherVertexMoved2, otherVertexMoved3, otherVertexMoved4})
                .map(p -> new AtomicPolygon(Arrays.asList(p, target.getA(), target.getB())))
                .collect(Collectors.toList()); 
    }
    
    List<PartialSolution> decisionNodesFrom(PartialSolution sol, List<AtomicPolygon> destination) {
        List<PartialSolution> nodes = new ArrayList<>();
        
        for (Edge connectableEdge: sol.getConnectableEdges()) {
            for (AtomicPolygon tile: destination) {
                for (Edge tileEdge: tile.getEdges()) {
                    if (!Precision.equals(tileEdge.getLengthSquared(), connectableEdge.getLengthSquared(), 1e-8)) {
                        continue;
                    }

                    List<AtomicPolygon> rotateds = rotateToMatch(tile, tileEdge, connectableEdge);
                    
                    for (AtomicPolygon rotated: rotateds) {
                        if (sol.canPlace(rotated)) {
                            nodes.add(sol.addTile(rotated, tileEdge, connectableEdge));
                        }
                    }
                }
            }
        }
        
        return nodes;
    }

    public static List<AtomicPolygon> tesselate(List<AtomicPolygon> destination) {
        throw new NotImplementedException("");
    }
    
}
