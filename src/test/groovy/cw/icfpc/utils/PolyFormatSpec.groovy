package cw.icfpc.utils

import cw.icfpc.model.Edge
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction
import spock.lang.Specification

class PolyFormatSpec extends Specification
{

    def 'format Fraction'()
    {
        expect:
            '4/5' == PolyFormat.format(Fraction.FOUR_FIFTHS)
            '4' == PolyFormat.format(Fraction.getReducedFraction(8, 2))
            '0' == PolyFormat.format(Fraction.ZERO)
            '4/3' == PolyFormat.format(Fraction.getFraction(4, 3))
    }

    def 'format FractionPoint'()
    {
        expect:
            '4/3,1' == PolyFormat.format(new FractionPoint(Fraction.getFraction(4, 3), Fraction.ONE))
    }

    def 'format list of points'()
    {
        expect:
            '1/2,1/3 2/3,2/5 3/4,3/5' == PolyFormat.format([new FractionPoint(Fraction.ONE_HALF, Fraction.ONE_THIRD),
                                                            new FractionPoint(Fraction.TWO_THIRDS, Fraction.TWO_FIFTHS),
                                                            new FractionPoint(Fraction.THREE_QUARTERS, Fraction.THREE_FIFTHS)])

    }

    def 'parse FractionPoint'()
    {
        expect:
            new FractionPoint(Fraction.THREE_FIFTHS, Fraction.TWO_THIRDS) == PolyFormat.getFractionPoint('3/5,2/3')
    }

    def 'parse Edge'()
    {
        given:
            def edge = new Edge(new FractionPoint(Fraction.THREE_FIFTHS, Fraction.TWO_THIRDS),
                    new FractionPoint(Fraction.ONE_HALF, Fraction.ONE_THIRD))

        when:
            def result = PolyFormat.getEdge('3/5,2/3 1/2,1/3')

        then:
            result == edge
    }

}
