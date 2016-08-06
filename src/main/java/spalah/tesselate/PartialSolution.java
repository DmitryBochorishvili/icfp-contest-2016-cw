package spalah.tesselate;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class PartialSolution {

    private List<AtomicPolygon> tiles;
    private List<Edge> connectableEdges = new LinkedList<>();

    private double area;

    private PartialSolution(List<AtomicPolygon> tiles, List<Edge> connectableEdges, double area) {
        this.tiles = tiles;
        this.connectableEdges = connectableEdges;
        this.area = area;
    }

    private static PartialSolution valueOf(List<AtomicPolygon> tiles, List<Edge> connectableEdges, double area) {
        // TODO: memoize
        PartialSolution partialSolution = new PartialSolution(tiles, connectableEdges, area);
        return partialSolution;
    }
    
    public boolean isComplete() {
        return Math.abs(area-1) < 1e-6;
    }
    
    public List<Edge> getConnectableEdges() {
        return connectableEdges;
    }
    
    public List<AtomicPolygon> getConnectableTiles() {
        throw new NotImplementedException("");
    }

    /**
     * @param tile An already rotated and finely positioned polygon to add to our square.
     */
    boolean canPlace(AtomicPolygon tile) {
        if (exceedsOneSquare(tile)) {
            return false;
        }

        for (AtomicPolygon t : tiles) {
            if (tile.overlaps(t)) {
                return false;
            }
        }

        return true;
    }

    private boolean exceedsOneSquare(AtomicPolygon tile) {
        return tile.getVertices()
                .stream()
                .noneMatch(v -> {
                    return v.getX().compareTo(BigFraction.ONE) > 0
                            || v.getX().compareTo(BigFraction.ZERO) < 0
                            || v.getX().compareTo(BigFraction.ONE) > 0
                            || v.getY().compareTo(BigFraction.ZERO) < 0;
                });
    }

    /**
     * @param tile        Tile to be added to the solution.
     * @param tileEdge    Tile's edge to try connecting
     * @param connectable a Edge from this PartialSolution to which to connect a new polygon.
     * @return null if the tile cannot be connected, a new PartialSolution otherwise.
     */
    PartialSolution addTile(AtomicPolygon tile, Edge tileEdge, Edge connectable) {
        if (!tileEdge.equals(connectable)) {
            return null;
        }

        if (!canPlace(tile)) {
            return null;
        }

        List<AtomicPolygon> tiles = new ArrayList<>(this.tiles);
        tiles.add(tile);

        List<Edge> connectableEdges = new LinkedList<>(this.connectableEdges);
        List<Edge> newEdges = new LinkedList<>(tile.getEdges());
        newEdges.remove(tileEdge);
        connectableEdges.remove(connectable);
        connectableEdges.addAll(newEdges);
        
        return PartialSolution.valueOf(tiles, connectableEdges, this.area + tile.getArea());
    }

}
