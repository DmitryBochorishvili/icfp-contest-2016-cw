package cw.icfpc.utils

import cw.icfpc.model.Facet
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction
import spock.lang.Specification

import static cw.icfpc.utils.PolyFormat.getFacet
import static cw.icfpc.utils.PolyFormat.getFractionPoint

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
            getFractionPoint('3/5,1/2') == MathUtils.reflect(getFractionPoint('1/2,3/5'), getFacet('0,0 1,1'))
    }

    def 'line intersection'()
    {
        expect:
            // parallel lines
            null == MathUtils.getLineIntersection(getFacet('0,0 2,1'), getFacet('1,0 3,1'))

            // intersection out of facets
            getFractionPoint('4,2') == MathUtils.getLineIntersection(getFacet('0,0 2,1'), getFacet('0,2 2,2'))
    }

    def 'facet intersection'()
    {
        expect:
            getFractionPoint('1,2') == MathUtils.getFacetIntersection(getFacet('0,0 2,4'), getFacet('0,2 10,2'))

            // parallel lines
            null == MathUtils.getFacetIntersection(getFacet('0,0 2,1'), getFacet('1,0 3,1'))

            // intersection out of one of the facets
            null == MathUtils.getFacetIntersection(getFacet('0,0 2,1'), getFacet('0,2 10,2'))
    }

}
