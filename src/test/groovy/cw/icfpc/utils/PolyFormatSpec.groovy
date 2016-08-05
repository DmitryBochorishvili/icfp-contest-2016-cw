package cw.icfpc.utils

import cw.icfpc.model.Edge
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction
import org.apache.commons.math3.fraction.BigFraction
import spock.lang.Specification

class PolyFormatSpec extends Specification
{

    def 'format Fraction'()
    {
        expect:
            '4/5' == PolyFormat.format(BigFraction.FOUR_FIFTHS)
            '4' == PolyFormat.format(BigFraction.getReducedFraction(8, 2))
            '0' == PolyFormat.format(BigFraction.ZERO)
            '4/3' == PolyFormat.format(BigFraction.getReducedFraction(4, 3))
    }

    def 'format FractionPoint'()
    {
        expect:
            '4/3,1' == PolyFormat.format(new FractionPoint(BigFraction.getReducedFraction(4, 3), BigFraction.ONE))
    }

    def 'format list of points'()
    {
        expect:
            '1/2,1/3 2/3,2/5 3/4,3/5' == PolyFormat.format([new FractionPoint(BigFraction.ONE_HALF, BigFraction.ONE_THIRD),
                                                            new FractionPoint(BigFraction.TWO_THIRDS, BigFraction.TWO_FIFTHS),
                                                            new FractionPoint(BigFraction.THREE_QUARTERS, BigFraction.THREE_FIFTHS)])

    }

    def 'parse FractionPoint'()
    {
        expect:
            new FractionPoint(BigFraction.THREE_FIFTHS, BigFraction.TWO_THIRDS) == PolyFormat.getFractionPoint('3/5,2/3')
    }

    def 'parse Edge'()
    {
        given:
            def edge = new Edge(new FractionPoint(BigFraction.THREE_FIFTHS, BigFraction.TWO_THIRDS),
                    new FractionPoint(BigFraction.ONE_HALF, BigFraction.ONE_THIRD))

        when:
            def result = PolyFormat.getEdge('3/5,2/3 1/2,1/3')

        then:
            result == edge
    }

}
