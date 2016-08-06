package spalah.tesselate;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

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
    AtomicPolygon[] rotateToMatch(AtomicPolygon tile, Edge tileEdge, Edge target) {
        FractionPoint otherVertex = tile.getVertices()
                .stream()
                .filter(v -> !tileEdge.contains(v))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("An AtomicPolygon is supposed to contain at least 3 vertexes"));

        FractionPoint move = target.getA().subtract(tileEdge.getA());

        //FractionPoint position1 = 
        throw new NotImplementedException("");
    }

    public static List<AtomicPolygon> tesselate(List<AtomicPolygon> destination) {
        throw new NotImplementedException("");
    }
    
}
