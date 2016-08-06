package cw.icfpc.model

import cw.icfpc.StateVisualizer
import cw.icfpc.utils.PolyFormat
import spock.lang.Specification

class AdjacentPolyGeneratorSpec extends Specification
{


    def 'getAllSourceSubCompounds'()
    {
        given:
            def polies = [
                    new AtomicPolygon(PolyFormat.getFractionPointList('0,0 0,1 1,1')),
                    new AtomicPolygon(PolyFormat.getFractionPointList('0,0 1,1 1,0')),
                    new AtomicPolygon(PolyFormat.getFractionPointList('1,0 1,1 2,1 2,0')),
            ]
            def compound = new CompoundPolygon(polies)

        when:
            def result = AdjacentPolyGenerator.getAllSourceSubCompounds(compound)

            def vis = StateVisualizer.builder(new State([]));
            result.each {vis.addScene(new State(it.getPolygons()), true)}
            vis.drawToFile(null);

        then:
            result.size() == 1
    }
}
