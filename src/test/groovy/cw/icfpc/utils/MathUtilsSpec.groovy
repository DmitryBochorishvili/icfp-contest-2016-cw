package cw.icfpc.utils

import cw.icfpc.model.FractionPoint
import org.apache.commons.math3.fraction.BigFraction
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getEdge
import static cw.icfpc.utils.PolyFormat.getFractionPoint
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class MathUtilsSpec extends Specification
{

    def 'reflect: across horizontal line'()
    {
        expect:
            getFractionPoint('1,-3') == MathUtils.reflect(getFractionPoint("1,3"), getEdge("0,0 1,0"))
            getFractionPoint('1/2,-3/5') == MathUtils.reflect(getFractionPoint("1/2,3/5"), getEdge("0,0 1,0"))
    }

    def 'reflect: across vertical line'()
    {
        given:
            def p = getFractionPoint("0,0")
            def mirror1 = getEdge("3,0 3,3")
        expect:
            def actual = MathUtils.reflect(p, mirror1)
            actual == getFractionPoint("6,0")
        
            getFractionPoint('-1/2,3/5') == MathUtils.reflect(getFractionPoint("1/2,3/5"), getEdge("0,0 0,1"))
    }

    def 'reflect: across 45 degrees line'()
    {
        expect:
            getFractionPoint('3/5,1/2') == MathUtils.reflect(getFractionPoint('1/2,3/5'), getEdge('0,0 1,1'))
    }

    def 'reflect: across more random line'()
    {
        expect:
            getFractionPoint('-8/5,-1/5') == MathUtils.reflect(getFractionPoint('-1,-2'), getEdge('-1,-1 -4,-2'))
    }

    def 'line intersection'()
    {
        expect:
            // parallel lines
            null == MathUtils.getLineIntersection(getEdge('0,0 2,1'), getEdge('1,0 3,1'))

            // intersection out of edges
            getFractionPoint('4,2') == MathUtils.getLineIntersection(getEdge('0,0 2,1'), getEdge('0,2 2,2'))
    }

    def 'edge intersection'()
    {
        expect:
            getFractionPoint('1,2') == MathUtils.getEdgeIntersection(getEdge('0,0 2,4'), getEdge('0,2 10,2'))

            // parallel lines
            null == MathUtils.getEdgeIntersection(getEdge('0,0 2,1'), getEdge('1,0 3,1'))

            // intersection out of one of the edges
            null == MathUtils.getEdgeIntersection(getEdge('0,0 2,1'), getEdge('0,2 10,2'))
    }

    def 'isClockwise'()
    {
        given:
            def loop1 = getFractionPointList("0,0 0,1 1,1")
            def loop2 = getFractionPointList("1,1 0,0 1,0")
            def twistedLoop = getFractionPointList("0,0 -1,0 -1,1 0,1 0,2 -2,2 -2,-1 0,-1")
        expect:
            MathUtils.isClockwise(loop1)
            !MathUtils.isClockwise(loop2)
            !MathUtils.isClockwise(twistedLoop)
    }

    def 'split by intersections'()
    {
        given:
            def edges = [
                    getEdge('0,0 10,10'),
                    getEdge('10,0 0,10')
            ]

        when:
            def split = MathUtils.splitByIntersections(edges)

        then:
            split as Set == [
                    getEdge('5,5 0,0'),
                    getEdge('0,10 5,5'),
                    getEdge('5,5 10,0'),
                    getEdge('5,5 10,10')
            ] as Set
    }

    def 'split by intersections: no intersections'()
    {
        given:
            def edges = [
                    getEdge('0,0 10,10'),
                    getEdge('0,0 0,10')
            ]

        when:
            def split = MathUtils.splitByIntersections(edges)

        then:
            split as Set == [
                    getEdge('0,0 10,10'),
                    getEdge('0,0 0,10')
            ] as Set
    }
    
    FractionPoint p(s) 
    {
        return getFractionPoint(s)
    }
    
    def 'Angle between'() 
    {
        expect:
            MathUtils.angleBetween(p('0,0'), p('0,1'), p('1,1')) == -Math.PI/2
            MathUtils.angleBetween(p('1,0'), p('0,0'), p('1,1')) == -3 * Math.PI/4
            MathUtils.angleBetween(p('1,0'), p('1/2,1/2'), p('0,0')) == Math.PI/2
    }
    
    def 'Circles intersection' ()
    {
        given:
            def c1 = new FractionPoint(BigFraction.ZERO, BigFraction.ZERO)
            def r1Square = BigFraction.ONE
            def c2 = new FractionPoint(BigFraction.ONE, BigFraction.ONE)
            def r2Square = BigFraction.ONE
        expect:
            def inter = MathUtils.getCirclesIntersection(c1, c2, r1Square, r2Square)[0]
            // TODO: Test that it returns BOTH points 0,1 and 1,0
            inter.closeTo(new FractionPoint(new BigFraction(1, 1), new BigFraction(0, 1)))
    }

    def 'rotate'()
    {
        expect:
            getFractionPoint('1,0') == MathUtils.rotate(getFractionPoint('4/5,3/5'), getFractionPoint('4/5,3/5'))
    }
}
