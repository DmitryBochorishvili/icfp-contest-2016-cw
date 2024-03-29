package cw.icfpc.utils

import cw.icfpc.model.AtomicPolygon
import cw.icfpc.model.Edge
import cw.icfpc.model.FractionPoint
import spock.lang.Ignore
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

    Edge e(String s) { getEdge(s) }
    
    List<Edge> pointsToEdgesSeq(s) {
        List<FractionPoint> points = getFractionPointList(s)
        
        FractionPoint prev = null
        List<Edge> result = []
        for (FractionPoint p: points) {
            if (prev != null) {
                result.add(new Edge(prev, p))
            }
            prev = p
        }

        result
    }
    
    def 'atomics simple envelope'()
    {
        given:
            List<Edge> skeleton = pointsToEdgesSeq('0,0 0,1 1,1 1,0 0,0 1,1') + e('0,1 1,0')
            List<Edge> atomicEdges = MathUtils.splitByIntersections(skeleton)
            List<AtomicPolygon> atomics = GraphUtils.minimumCycles(atomicEdges)
        expect:
            atomics.size() == 4
    }

    def 'atomics holed envelope'()
    {
        given:
            List<Edge> skeleton = 
                    pointsToEdgesSeq('0,0 0,1 1,1 1,0 0,0 1/3,1/3 1/3,2/3 2/3,2/3 2/3,1/3 1/3,1/3') +
                    [e('0,1 1/3,2/3'), e('1,1 2/3,2/3'), e('1,0 2/3,1/3')]
            List<Edge> atomicEdges = MathUtils.splitByIntersections(skeleton)
            List<AtomicPolygon> atomics = GraphUtils.minimumCycles(atomicEdges)
        expect:
            atomics.size() == 5
    }

    @Ignore
    def 'intersecting facets'()
    {
        given:
            List<Edge> skeleton = pointsToEdgesSeq('0,0 0,1 1,1 1,0 0,0 1,1') + e('0,1 1,0')
            List<AtomicPolygon> facets = GraphUtils.minimumCycles(skeleton)
        expect:
            skeleton.size() == 6
            skeleton[5].getLengthSquared() == 2
            skeleton[4].getLengthSquared() == 2

            facets.size() == 4
    }
    
    def 'merge edges on the same line'()
    {
        given:
            def p = getFractionPointList('0,0 1/2,1/2 0,1/2 -1/2,1/2')

        expect:
            getFractionPointList('0,0 1/2,1/2 -1/2,1/2') == GraphUtils.mergeSameLineVertices(p)
    }

    def 'merge edges on the same line with different length'()
    {
        expect:
            getFractionPointList('0,0 0,1') == GraphUtils.mergeSameLineVertices(getFractionPointList('0,0 0,1/3 0,1'))
            getFractionPointList('0,1 0,0') == GraphUtils.mergeSameLineVertices(getFractionPointList('0,1 0,1/3 0,0'))
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
