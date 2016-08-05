package cw.icfpc.utils;

import cw.icfpc.model.Facet;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;

public class MathUtils
{

    public static FractionPoint reflect(FractionPoint p, Facet facet)
    {
        return new FractionPoint(Fraction.ZERO, Fraction.ZERO);
    }

}
