package cw.icfpc.model;

import org.junit.Test;

import java.util.*;

import static cw.icfpc.utils.PolyFormat.getFractionPointList;
import static org.junit.Assert.*;

public class AdjacentPolyGeneratorTest {
    
    private class AtomicPolygonStub extends AtomicPolygon {
        Set<AtomicPolygon> neighbours = new HashSet<>();
        private String name;

        public AtomicPolygonStub(String name) {
            super(getFractionPointList("0,0 1,0 1,1"));
            this.name = name;
        }

        @Override
        public boolean isAdjacent(AtomicPolygon other) {
            return neighbours.contains(other);
        }
        
        public void addNeighbour(AtomicPolygonStub other) {
            neighbours.add(other);
            other.neighbours.add(this);
        }

        @Override
        public String toString() {
            return "AP{" + name + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof AtomicPolygonStub))
                return false;
            if (!super.equals(o))
                return false;

            AtomicPolygonStub that = (AtomicPolygonStub) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
    
    @Test
    public void generateAllSubsets() throws Exception {

        AtomicPolygonStub a = new AtomicPolygonStub("a");
        AtomicPolygonStub b = new AtomicPolygonStub("b");
        AtomicPolygonStub c = new AtomicPolygonStub("c");
        AtomicPolygonStub d = new AtomicPolygonStub("d");
        AtomicPolygonStub e = new AtomicPolygonStub("e");
        AtomicPolygonStub f = new AtomicPolygonStub("f");
        AtomicPolygonStub g = new AtomicPolygonStub("g");
        AtomicPolygonStub h = new AtomicPolygonStub("h");

        AtomicPolygon[] polys = {a, b, c, d, e, f, g, h};
        State state = new State(new ArrayList<>(Arrays.asList(polys)));
        
        //   A
        //   B
        //   C
        //   D---E
        // H-F-G
        a.addNeighbour(b);
        b.addNeighbour(c);
        c.addNeighbour(d);
        d.addNeighbour(e);
        d.addNeighbour(f);
        f.addNeighbour(g);
        f.addNeighbour(h);

        List<CompoundPolygon> compounds = AdjacentPolyGenerator.generateAllSubsets(state, a, new HashSet<>());
        
        assertEquals(8, compounds.size());
        assertEquals(1, compounds.get(0).getPolygons().size());
        assertEquals("[AP{a}]", compounds.get(0).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}]", compounds.get(1).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}]", compounds.get(2).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}, AP{d}]", compounds.get(3).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}, AP{d}, AP{f}]", compounds.get(4).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}, AP{d}, AP{f}, AP{h}]", compounds.get(5).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}, AP{d}, AP{f}, AP{g}]", compounds.get(6).getPolygons().toString());
        assertEquals("[AP{a}, AP{b}, AP{c}, AP{d}, AP{e}]", compounds.get(7).getPolygons().toString());
        
    }

}