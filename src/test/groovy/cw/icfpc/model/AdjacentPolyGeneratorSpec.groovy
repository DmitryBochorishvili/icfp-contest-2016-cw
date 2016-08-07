package cw.icfpc.model

import cw.icfpc.utils.PolyFormat
import org.junit.Test
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static org.junit.Assert.assertEquals

class AdjacentPolyGeneratorSpec extends Specification
{


    def 'getAllSourceSubCompounds tries to remove tailing polygons'()
    {
        given:
            def polies = [
                    new AtomicPolygon(PolyFormat.getFractionPointList('0,0 0,1 1,1')),
                    new AtomicPolygon(PolyFormat.getFractionPointList('0,0 1,1 1,0')),
                    new AtomicPolygon(PolyFormat.getFractionPointList('1,0 1,1 2,1 2,0')),
            ]
            def compound = new CompoundPolygon(polies)

        when:
            def result = AdjacentPolyGenerator.getAllSourceSubCompoundsToRemove(compound)

//            def vis = StateVisualizer.builder(new State([]));
//            result.each {vis.addScene(new State(it.getPolygons()), true)}
//            vis.drawToFile(null);

        then:
            result.size() == 3

            result[0].polygons.size() == 2
            result[1].polygons.size() == 1
            result[2].polygons.empty
    }


    def 'get all compounds 2'()
    {
        given:
            def a = PolyFormat.getAtomic('0,0 0,1 1,1 1,0')
            def b = PolyFormat.getAtomic('1,0 1,1 2,1 2,0')
            def c = PolyFormat.getAtomic('1,1 1,2 2,2 2,1')
            def d = PolyFormat.getAtomic('2,0 2,1 3,1 3,0')
            def e = PolyFormat.getAtomic('2,1 2,2 3,2 3,1')
            def f = PolyFormat.getAtomic('3,0 3,1 4,1 4,0')

            //     c - e
            //     |   |
            // a - b - d - f
            //

            def state = State.createNew([a, b, c, d, e, f])

        when:
            def result = AdjacentPolyGenerator.getAllCompounds2(state, getEdge('0,0 0,1'))


        then:
            result.contains(new CompoundPolygon([a]))
            result.contains(new CompoundPolygon([a, b]))
            result.contains(new CompoundPolygon([a, b, c]))
            result.contains(new CompoundPolygon([a, b, d]))
            result.contains(new CompoundPolygon([a, b, c, d]))
            result.contains(new CompoundPolygon([a, b, c, e]))
            result.contains(new CompoundPolygon([a, b, d, e]))
            result.contains(new CompoundPolygon([a, b, c, d, e]))
            result.contains(new CompoundPolygon([a, b, d, f]))
            result.contains(new CompoundPolygon([a, b, c, d, f]))
            result.contains(new CompoundPolygon([a, b, d, e, f]))
            result.contains(new CompoundPolygon([a, b, c, d, e, f]))

            result.size() == 12
    }
}
