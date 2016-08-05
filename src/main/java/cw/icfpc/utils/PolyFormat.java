package cw.icfpc.utils;

import cw.icfpc.model.Facet;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.Fraction;

import java.util.Collection;
import java.util.stream.Collectors;

public class PolyFormat
{

    public static String format(Fraction f)
    {
        if (f.getDenominator() == 0)
            return "0";
        else if (f.getDenominator() == 1)
            return Integer.toString(f.getNumerator());
        else
            return f.getNumerator() + "/" + f.getDenominator();
    }

    public static String format(FractionPoint point)
    {
        return new StringBuilder()
                .append(format(point.getX()))
                .append(',')
                .append(format(point.getY()))
                .toString();
    }

    public static String format(Collection<FractionPoint> points)
    {
        return String.join(" ", points.stream().map(p->format(p)).collect(Collectors.toList()));
    }

    public static FractionPoint getFractionPoint(String str)
    {
        String[] fractions = str.split(",");
        if (fractions.length != 2)
            throw new RuntimeException("Invalid fraction point to parse: " + str);

        return new FractionPoint(Fraction.getFraction(fractions[0]), Fraction.getFraction(fractions[1]));
    }

    public static Facet getFacet(String str)
    {
        String[] points = str.split(" ");

        if (points.length != 2)
            throw new RuntimeException("Invalid facet to parse: " + str);

        return new Facet(getFractionPoint(points[0]), getFractionPoint(points[1]));
    }
}
