package cw.icfpc.utils

import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class GraphUtilsSpec extends Specification
{

    def 'minimum cycles'()
    {
        given:
            def edges = [
                    getEdge('0,0 1,1'),
                    getEdge('1,1 2,0'),
                    getEdge('0,0 2,0'),
                    getEdge('0,0 1,-1'),
                    getEdge('1,-1 2,0')
            ]

        when:
            def cycles = GraphUtils.minimumCycles(edges)

        then:
            ['2,0 0,0 1,1', '2,0 0,0 1,-1'] == cycles.collect{PolyFormat.format(it.getVertices())}

    }
}
