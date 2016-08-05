package cw.icfpc.utils;

import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<FractionPoint> getFractionPointList(String str)
    {
        return Stream.of(str.split(" "))
                .map(PolyFormat::getFractionPoint)
                .collect(Collectors.toList());
    }

    public static Edge getEdge(String str)
    {
        String[] points = str.split(" ");

        if (points.length != 2)
            throw new RuntimeException("Invalid edge to parse: " + str);

        return new Edge(getFractionPoint(points[0]), getFractionPoint(points[1]));
    }
}
