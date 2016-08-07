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

}
