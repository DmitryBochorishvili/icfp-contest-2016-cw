package cw.icfpc.model

import cw.icfpc.utils.PolyFormat
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFractionPointList

class StateSpec extends Specification
{
    def 'final state: true for initial square'()
    {
        given:
            def poly = new AtomicPolygon(getFractionPointList('0,0 1,0 1,1 0,1'))

        expect:
            State.createNew([poly]).isFinalState() == true
    }

    def 'final state: true for moved square'()
    {
        given:
            def poly = new AtomicPolygon(getFractionPointList('1/2,0 3/2,0 3/2,1 1/2,1'))

        expect:
            State.createNew([poly]).isFinalState() == true
    }

    def 'final state: false for not a square'()
    {
        given:
            def poly = new AtomicPolygon(getFractionPointList('0,0 1,1 2,1 1,0'))

        expect:
            State.createNew([poly]).isFinalState() == false
    }

    // To consider, do not delete this test
//    def 'calculate adjacent atomics correctly'()
//    {
//        given:
//            def p1 = new AtomicPolygon(getFractionPointList('0,1 0,0 1/2,1/2'))
//            def p2 = new AtomicPolygon(getFractionPointList('1,0 1,1 0,0'))
//            def p3 = new AtomicPolygon(getFractionPointList('0,1 1,1 1/2,1/2'))
//
//        when:
//            def state = new State([p1, p2, p3]);
//
//        then:
//            state.getAdjacentPolygons(p1).size() == 2
//            state.getAdjacentPolygons(p2).size() == 2
//            state.getAdjacentPolygons(p3).size() == 2
//    }
}
