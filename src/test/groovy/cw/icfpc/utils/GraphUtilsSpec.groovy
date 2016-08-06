package cw.icfpc.utils

import cw.icfpc.model.AtomicPolygon
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

    def 'merge edges on the same line'()
    {
        given:
            def p = getFractionPointList('0,0 1/2,1/2 0,1/2 -1/2,1/2')

        expect:
            getFractionPointList('0,0 1/2,1/2 -1/2,1/2') == GraphUtils.mergeSameLineVertices(p)
    }

    def 'merge 2 adjacent polygons'()
    {
        given:
            def p1 = new AtomicPolygon(getFractionPointList('0,0 0,1 1,1 1,0'))
            def p2 = new AtomicPolygon(getFractionPointList('1,1 2,1 2,0 1,0'))

            def expectedEdges = [
                    getEdge('0,0 0,1'),
                    getEdge('0,1 2,1'),
                    getEdge('2,1 2,0'),
                    getEdge('2,0 0,0'),
            ]

        when:
            def merged = GraphUtils.merge(p1, p2)

        then:
            expectedEdges as Set == merged.getEdges() as Set
    }

    def 'merge non-adjacent polygons'()
    {
        given:
            def p1 = new AtomicPolygon(getFractionPointList('0,0 0,1 1,1'))
            def p2 = new AtomicPolygon(getFractionPointList('1,1 2,1 2,0 1,0'))

        expect:
            null == GraphUtils.merge(p1, p2)
    }

    def 'check for positive polygon or hole'()
    {
        given:
            def hole1 = getFractionPointList('0,0 0,1 1,1 1,0')
            def positive1 = getFractionPointList('0,0 1,0 1/2,1/2 0,1/2')
            def positive2 = getFractionPointList('0,0 1/2,0 1/2,2/3 0,2/3')
            def hole2 = getFractionPointList('0,2/3 1/2,2/3 1/2,0 0,0')
        expect:
            !GraphUtils.ifPositivePolygonInSilhouette(hole1)
            GraphUtils.ifPositivePolygonInSilhouette(positive1)
            GraphUtils.ifPositivePolygonInSilhouette(positive2)
            !GraphUtils.ifPositivePolygonInSilhouette(hole2)
    }

}
