package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApp
{

    public static void main(String[] args)
    {
        StateVisualizer vis = new StateVisualizer();
        //State s = createExampleState();
        ProblemReader r = new ProblemReader();
        try {
            State s = r.readProblemFromFile(null);
            vis.visualizeStateToFile(s, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Alive!");
    }

    private static State createExampleState() {
        List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>();
        List<FractionPoint> vertices = new ArrayList<FractionPoint>();
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE, BigFraction.ZERO));
        vertices.add(new FractionPoint(BigFraction.ONE_HALF, BigFraction.ONE_HALF));
        vertices.add(new FractionPoint(BigFraction.ZERO, BigFraction.ONE_HALF));

        polygons.add(new AtomicPolygon(vertices));

        return new State(polygons);
    }
}
