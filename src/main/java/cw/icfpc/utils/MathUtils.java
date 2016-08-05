package cw.icfpc.utils;

import cw.icfpc.model.Facet;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;

public class MathUtils
{
    public static FractionPoint reflect(FractionPoint p, Facet facet)
    {
        double nx = facet.getA().getY().doubleValue() - facet.getB().getY().doubleValue();
        double ny = facet.getB().getX().doubleValue() - facet.getA().getX().doubleValue();
        double len = Math.sqrt(nx*nx + ny*ny);
        nx /= len;
        ny /= len;

        double dot2 = 2 * (nx * p.getX().doubleValue() + ny * p.getY().doubleValue());

        double rx = p.getX().doubleValue() - dot2 * nx;
        double ry = p.getY().doubleValue() - dot2 * ny;

        return new FractionPoint(Fraction.getFraction(rx), Fraction.getFraction(ry));
    }

}
