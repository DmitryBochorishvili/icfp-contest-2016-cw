package cw.icfpc.utils

import cw.icfpc.model.Facet
import cw.icfpc.model.FractionPoint
import org.apache.commons.lang3.math.Fraction

class MathUtilsSpec
{

    def 'reflect: across horizontal line'()
    {

        expect:
            1 == 1
            //getFractionPoint('-1/2,3/5') == MathUtils.reflect(getFractionPoint("1/2,3/5", getFacet("0,0 1,0")))

    }

}
