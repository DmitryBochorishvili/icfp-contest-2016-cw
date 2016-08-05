package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import org.apache.commons.lang3.math.Fraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApp
{

    public static void main(String[] args)
    {
        StateVisualizer vis = new StateVisualizer();
        State s = createExampleState();
        try {
            vis.visualizeStateToFile(s, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Alive!");
    }

    private static State createExampleState() {
        List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>();
        List<FractionPoint> vertices = new ArrayList<FractionPoint>();
        vertices.add(new FractionPoint(Fraction.ZERO, Fraction.ZERO));
        vertices.add(new FractionPoint(Fraction.ONE, Fraction.ZERO));
        vertices.add(new FractionPoint(Fraction.ONE_HALF, Fraction.ONE_HALF));
        vertices.add(new FractionPoint(Fraction.ZERO, Fraction.ONE_HALF));

        polygons.add(new AtomicPolygon(vertices));

        return new State(polygons);
    }
}
