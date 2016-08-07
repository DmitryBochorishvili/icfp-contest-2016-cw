package cw.icfpc.utils;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.BigFractionFormat;
import org.apache.commons.math3.fraction.FractionFormat;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PolyFormat
{

    public static String format(BigFraction f)
    {
        if (f.getDenominator().equals(BigInteger.ZERO))
            return "0";
        else if (f.getDenominator().equals(BigInteger.ONE))
            return f.getNumerator().toString();
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

        BigFractionFormat format = new BigFractionFormat();
        return FractionPoint.valueOf(format.parse(fractions[0]), format.parse(fractions[1]));
    }

    public static List<FractionPoint> getFractionPointList(String str)
    {
        return Stream.of(str.split(" "))
                .map(PolyFormat::getFractionPoint)
                .collect(Collectors.toList());
    }

    public static AtomicPolygon getAtomic(String str)
    {
        return new AtomicPolygon(getFractionPointList(str));
    }

    public static Edge getEdge(String str)
    {
        String[] points = str.split(" ");

        if (points.length != 2)
            throw new RuntimeException("Invalid edge to parse: " + str);

        return new Edge(getFractionPoint(points[0]), getFractionPoint(points[1]));
    }
}
