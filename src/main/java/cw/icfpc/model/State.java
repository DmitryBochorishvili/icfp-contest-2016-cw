package cw.icfpc.model;

import com.sun.corba.se.impl.orbutil.graph.Graph;
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

    private MultiValuedMap<Edge, AtomicPolygon> adjacentEdges = new HashSetValuedHashMap<>();
    private MultiValuedMap<AtomicPolygon, AtomicPolygon> adjacentPolygons = new HashSetValuedHashMap<>();

    private int iteration = 0;
    private State derivedFrom;

    /**
     * Makes sure it's a first-generation State with all the proper indexes.
     */
    public static State createNew(List<AtomicPolygon> polygons) {
        return new State(polygons);
    }

    /**
     * Please only create State-s with #createNew or with State's methods.
     * @param atomicPolygons
     */
    private State(List<AtomicPolygon> atomicPolygons)
    {
        this.atomicPolygons = atomicPolygons;

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

//    public State mergeAdjacentAtomicPolygons(Edge edge) {
//        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);
//        Collection<AtomicPolygon> adjacentPolygons = adjacentEdges.get(edge);
//
//        assert adjacentPolygons.size() == 2;
//
//        ArrayList<AtomicPolygon> polygons = new ArrayList<>();
//        polygons.addAll(adjacentPolygons);
//        AtomicPolygon merged = GraphUtils.merge(polygons.get(0), polygons.get(1));
//        atomicPolygons.remove(polygons.get(0));
//        atomicPolygons.remove(polygons.get(1));
//        atomicPolygons.add(merged);
//
//        State newState = State.createNew(atomicPolygons);
//
//        newState.iteration = this.getIteration() + 1;
//        newState.derivedFrom = this;
//
//        return newState;
//    }

    public State addRemoveFlippedCompound(
            CompoundPolygon sourceCompound, 
            CompoundPolygon toAdd, 
            CompoundPolygon toRemove,
            boolean merge)
    {
        // merge first flipped atomic with first compound atomic
        List<AtomicPolygon> atomicPolygons = new ArrayList<>(this.atomicPolygons);

        atomicPolygons.addAll(toAdd.getPolygons());
        atomicPolygons.removeAll(toRemove.getPolygons());

        if(merge) {
            AtomicPolygon p1 = toAdd.getPolygons().get(0);
            AtomicPolygon p2 = sourceCompound.getPolygons().get(0);
            atomicPolygons.remove(p1);
            atomicPolygons.remove(p2);
            AtomicPolygon merged = GraphUtils.merge(p1, p2);
            atomicPolygons.add(merged);
        }

        State newState = State.createNew(atomicPolygons);

        newState.iteration = this.getIteration() + 1;
        newState.derivedFrom = this;
        
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
        return allPolygonsAdjacent() && getSimpleArea() <= 1;
    }

    /**
     * Returns true if the state is final i.e. 1x1 square
     */
    public boolean isFinalState()
    {
        if (Math.abs(1 - getSimpleArea()) < MathUtils.EPSILON
            && edges.size() - adjacentEdges.keySet().size() == 4)
        {
            List<Edge> outerEdges = edges.stream().filter(e -> adjacentEdges.get(e).isEmpty()).collect(Collectors.toList());
            double distance = MathUtils.distance(outerEdges.get(0).getA(), outerEdges.get(0).getB());
            return outerEdges.stream().allMatch(e -> MathUtils.distance(e.getA(), e.getB()) == distance);
        }
        return false;
    }

    public int getIteration()
    {
        return iteration;
    }
    
    public State getDerivedFrom() {
        return derivedFrom;
    }
}
