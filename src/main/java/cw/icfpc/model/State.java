package cw.icfpc.model;

import cw.icfpc.utils.GraphUtils;
import cw.icfpc.utils.MathUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;
import java.util.stream.Collectors;

public final class State
{
    private Set<Edge> edges;

    private List<AtomicPolygon> atomicPolygons;
    
    private List<AtomicPolygon> destFacets;
    private List<AtomicPolygon> facets;
    private List<AtomicPolygon> neverMoved;

    private List<FractionPoint> destinationVertexes;
    private Map<FractionPoint, FractionPoint[]> destinationVertexUnfolded;

    private MultiValuedMap<Edge, AtomicPolygon> adjacentEdges = new HashSetValuedHashMap<>();
    private MultiValuedMap<AtomicPolygon, AtomicPolygon> adjacentPolygons = new HashSetValuedHashMap<>();

    private int iteration = 0;
    private State derivedFrom;

    /**
     * Makes sure it's a first-generation State with all the proper indexes.
     */
    public static State createNew(List<AtomicPolygon> polygons) {
        State state = new State(polygons);
        state.facets = new ArrayList<>();
        state.destFacets = new ArrayList<>();
        return state;
    }

    /**
     * Please only create State-s with #createNew or with State's methods.
     * @param atomicPolygons
     */
    private State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;
        this.neverMoved = new ArrayList<>(atomicPolygons);

        edges = new HashSet<>();
        atomicPolygons.forEach(polygon -> edges.addAll(polygon.getEdges()));

        for (int i = 0; i < atomicPolygons.size(); i++)
            for (int k = i + 1; k < atomicPolygons.size(); k++)
            {
                AtomicPolygon pi = atomicPolygons.get(i);
                AtomicPolygon pk = atomicPolygons.get(k);
                Edge adjacentEdge = pi.getAdjacentEdge(pk);
                if (adjacentEdge != null)
                {
                    adjacentEdges.put(adjacentEdge, pi);
                    adjacentEdges.put(adjacentEdge, pk);
                    adjacentPolygons.put(pi, pk);
                    adjacentPolygons.put(pk, pi);
                }
            }
    }

    public boolean isAdjacent(AtomicPolygon p1, AtomicPolygon p2)
    {
        return adjacentPolygons.get(p1).contains(p2);
    }

    public Collection<AtomicPolygon> getAdjacentPolygons(AtomicPolygon p)
    {
        return adjacentPolygons.get(p);
    }

    public double getHeuristic() {
        return 1000 - atomicPolygons.size() - iteration;
    }

    public Collection<Edge> getEdges()
    {
        return edges;
    }

    public List<AtomicPolygon> getAtomicPolygons()
    {
        return atomicPolygons;
    }

    // returns sum of all atomic polygons areas
    // doesn't take into account a possibility that a polygon could present in multiple layers.
    public double getSimpleArea()
    {
        return atomicPolygons.stream().mapToDouble(AtomicPolygon::getArea).sum();
    }

    public State addCompound(CompoundPolygon flippedCompound) {
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
        atomicPolygons.addAll(flippedCompound.getPolygons());
        return State.createNew(atomicPolygons);
    }

    public State mergeAdjacentAtomicPolygons(Edge edge) {
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
        Collection<AtomicPolygon> adjacentPolygons = adjacentEdges.get(edge);

        assert adjacentPolygons.size() == 2;

        ArrayList<AtomicPolygon> polygons = new ArrayList<>();
        polygons.addAll(adjacentPolygons);
        AtomicPolygon merged = GraphUtils.merge(polygons.get(0), polygons.get(1));

        if(merged == null)
            return null;

        atomicPolygons.remove(polygons.get(0));
        atomicPolygons.remove(polygons.get(1));
        atomicPolygons.add(merged);

        State newState = State.createNew(atomicPolygons);

        newState.iteration = this.getIteration() + 1;
        newState.derivedFrom = this;

        return newState;
    }

    public enum FlipOptions {
        TryMerge,
        FlipOnly,
        Duplicate
    }

    public State addRemoveFlippedCompound(
            CompoundPolygon sourceCompound, 
            CompoundPolygon flippedCompound, 
            CompoundPolygon toRemove,
            FlipOptions merge)
    {
        // merge first flipped atomic with first compound atomic
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);

        atomicPolygons.addAll(flippedCompound.getPolygons());
        atomicPolygons.removeAll(toRemove.getPolygons());

        if(merge == FlipOptions.TryMerge) {
            AtomicPolygon p1 = flippedCompound.getPolygons().get(0);
            AtomicPolygon p2 = sourceCompound.getPolygons().get(0);
            AtomicPolygon merged = GraphUtils.merge(p1, p2);
            if(merged == null)
                return null;
            atomicPolygons.remove(p1);
            atomicPolygons.remove(p2);
            atomicPolygons.add(merged);
        }
        else if(merge == FlipOptions.FlipOnly) {
            atomicPolygons.removeAll(sourceCompound.getPolygons());
        }

        State newState = State.createNew(atomicPolygons);

        newState.iteration = this.getIteration() + 1;
        newState.derivedFrom = this;
        
        newState.facets = new ArrayList<>(this.facets);
        AtomicPolygon flippedFacet = flippedCompound.getContour();
        newState.facets.add(flippedFacet);
        
        newState.destFacets = new ArrayList<>(this.destFacets);
        AtomicPolygon origFacet = sourceCompound.getContour();
        newState.destFacets.add(origFacet);
        
        newState.neverMoved = new ArrayList<>(this.neverMoved);
        newState.neverMoved.removeAll(toRemove.getPolygons());
        
        return newState;
    }

    /**
     * Returns true if there is only 1 polygon adjacent to this edge;
     */
    public boolean isEdgeOuter(Edge edge)
    {
        return adjacentEdges.get(edge).isEmpty();
    }

    public boolean allPolygonsAdjacent()
    {
        Set<AtomicPolygon> visited = new HashSet<>();
        Stack<AtomicPolygon> toVisit = new Stack<>();

        toVisit.push(atomicPolygons.get(0));

        while (!toVisit.empty())
        {
            AtomicPolygon p = toVisit.pop();
            visited.add(p);
            adjacentPolygons.get(p).forEach(adj -> {
                if (!visited.contains(adj))
                    toVisit.push(adj);
            });
        }

        return visited.size() == atomicPolygons.size();
    }

    /**
     * Returns true if current state is valid: all polygons are transitively adjacent, areas is not more than 1
     * and linear size is less than sqrt(2)
     */
    public boolean isStateValid()
    {
        return allPolygonsAdjacent() && getSimpleArea() <= 1
                && (atomicPolygons.size() > 1 || getSimpleArea() <= 0.5);
    }
    
    // I'm not proud of this.
    private List<Edge> outerEdges;
    
    /**
     * Returns true if the state is final i.e. 1x1 square
     */
    public boolean isFinalState()
    {
        if (Math.abs(1 - getSimpleArea()) < MathUtils.EPSILON) {
            AtomicPolygon merged = GraphUtils.merge(atomicPolygons);
            if (merged.getVertices().size() == 4) {
                this.outerEdges = merged.getEdges();
                double distance = MathUtils.distance(outerEdges.get(0).getA(), outerEdges.get(0).getB());
                return outerEdges.stream().allMatch(e -> Math.abs(MathUtils.distance(e.getA(), e.getB()) - distance) < MathUtils.EPSILON);
            }
        }
        return false;
    }
    
    private AtomicPolygon translate(AtomicPolygon what, FractionPoint movement, FractionPoint tangent) {
        return what.move(movement).rotate(tangent);
    }

    /**
     * Prerequisite: #isFinalState() returned true.
     * @return a State translated to a unit (0,0-1,1) square.
     */
    public State alignToUnit() {

        FractionPoint leftmost = outerEdges.stream()
                .map(Edge::getA)
                .min((a, b) -> (a.getX().doubleValue() - b.getX().doubleValue()) > 0 ? 1 : -1)
                .orElseThrow(() -> new IllegalStateException("What, no leftmost edge?"));

        FractionPoint lowest = outerEdges.stream()
                .map(Edge::getA)
                .min((a, b) -> (a.getY().doubleValue() - b.getY().doubleValue()) > 0 ? 1 : -1)
                .orElseThrow(() -> new IllegalStateException("What, no lowest edge?"));
        
        FractionPoint movement = leftmost.negate();
        FractionPoint tangent = lowest.move(movement);

        State moved = State.createNew(
                atomicPolygons.stream()
                        .map(p -> translate(p, movement, tangent))
                        .collect(Collectors.toList())
        );

        moved.iteration = this.getIteration();
        moved.derivedFrom = this.derivedFrom;

        moved.facets = new ArrayList<>(this.facets.stream()
                .map(p -> translate(p, movement, tangent))
                .collect(Collectors.toList()));
        moved.destFacets = new ArrayList<>(this.destFacets.stream()
                .map(p -> translate(p, movement, tangent))
                .collect(Collectors.toList()));
        moved.neverMoved = new ArrayList<>(this.neverMoved.stream()
                .map(p -> translate(p, movement, tangent))
                .collect(Collectors.toList()));

        return moved;
    }

    public int getIteration()
    {
        return iteration;
    }

    public State getDerivedFrom() {
        return derivedFrom;
    }
    
    public String toSolution() {
        assert this.neverMoved.size() == 1;
        assert this.facets.size() <= 1;
        this.facets.addAll(this.neverMoved);

        State sourceState = this;
        
        Set<FractionPoint> sourcePointSet = new HashSet<>();
        for (AtomicPolygon f: sourceState.facets) {
            sourcePointSet.addAll(f.getVertices());
        }
        List<FractionPoint> sourcePoints = new ArrayList<>(sourcePointSet);
        
        Map<Integer, FractionPoint> destPoints = new HashMap<>();
        
        // An "already the solution" case
        if (sourceState.derivedFrom == null) {
            sourceState.destFacets.addAll(sourceState.facets);
        }
        
        for (AtomicPolygon f: sourceState.destFacets) {
            for (FractionPoint p: f.getVertices()) {
                if (p.destId < 0) {
                    throw new IllegalStateException("Point not mapped");
                }
                destPoints.put(p.destId, p);
            }
        }
        
        // Source positions
        StringBuilder sb = new StringBuilder();
        sb.append(sourcePoints.size()).append('\n');
        for (FractionPoint p: sourcePoints) {
            sb.append(p.toSimpleString()).append('\n');
        }

        // Source facets
        sb.append(sourceState.facets.size()).append('\n');
        for (AtomicPolygon f: sourceState.facets) {
            String separator = "";
            sb.append(f.getVertices().size()).append(' ');
            for (FractionPoint p: f.getVertices()) {
                
                // FIXME: Use a better structure.
                int sourceIndex = sourcePoints.indexOf(p);
                
                sb.append(separator);
                separator = " ";
                sb.append(sourceIndex);
            }
            sb.append('\n');
        }

        List<AtomicPolygon> a = sourceState.neverMoved;

        // Destination positions
        for (int i=0; i<sourcePoints.size(); i++) {
            FractionPoint destPoint = destPoints.get( sourcePoints.get(i).destId );
            if (destPoint == null) {
                throw new IllegalStateException("Point not mapped");
            }
            sb.append(destPoint.toSimpleString()).append('\n');
        }
        
        return sb.toString();
    }
}
