package cw.icfpc.utils

import cw.icfpc.model.Facet
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFacet
import static cw.icfpc.utils.PolyFormat.getFractionPoint
import static cw.icfpc.utils.PolyFormat.getFractionPointList

class MathUtilsSpec extends Specification
{

    def 'reflect: across horizontal line'()
    {
        expect:
            getFractionPoint('1,-3') == MathUtils.reflect(getFractionPoint("1,3"), getFacet("0,0 1,0"))
            getFractionPoint('1/2,-3/5') == MathUtils.reflect(getFractionPoint("1/2,3/5"), getFacet("0,0 1,0"))
    }

    def 'reflect: across vertical line'()
    {
        expect:
            getFractionPoint('-1/2,3/5') == MathUtils.reflect(getFractionPoint("1/2,3/5"), getFacet("0,0 0,1"))
    }

    def 'reflect: across 45 degrees line'()
    {
        expect:
            getFractionPoint('3/5,1/2') == MathUtils.reflect(getFractionPoint("1/2,3/5"), getFacet("0,0 1,1"))
    }

    private FractionPoint point(int x, int y)
    {
        return new FractionPoint(Fraction.getFraction(x, 1), Fraction.getFraction(y, 1))
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

}
