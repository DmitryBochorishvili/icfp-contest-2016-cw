package cw.icfpc.utils;

import cw.icfpc.model.Facet;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

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
    
    public static boolean isClockwise(List<FractionPoint> loop) 
    {
        // http://stackoverflow.com/a/1165943/207791
        double area = 0;
        for (int i=0; i<loop.size(); i++) {
            FractionPoint p1 = loop.get(i);
            FractionPoint p0 = i > 0 ? loop.get(i-1) : loop.get(loop.size()-1);
            area += (p1.getX().doubleValue() - p0.getX().doubleValue()) * (p1.getY().doubleValue() + p0.getY().doubleValue());
        }
        return area > 0;
    }

}
