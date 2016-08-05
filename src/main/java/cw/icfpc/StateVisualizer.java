package cw.icfpc;

import cw.icfpc.model.State;
import org.apache.commons.lang3.math.Fraction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StateVisualizer {
    private final int boardXScale = 500;
    private final int boardYScale = 500;
    private final int boardXSize = boardXScale + 60;
    private final int boardYSize = boardYScale + 60;
    private final int axesStartXPosition = 30;
    private final int axesStartYPosition = boardYSize - 30;
    public StateVisualizer(){}

    public void visualizeStateToFile(State s, String fileName) throws IOException {
        String output = fileName == null ? "pic.png" : fileName;

        BufferedImage img = new BufferedImage(boardXSize, boardYSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setBackground(Color.WHITE);
        drawAxes(g2d);

        drawState(g2d, s);

        File f = new File(output);
        ImageIO.write(img, "PNG", f);
    }

    private void drawState(Graphics2D g2d, State s) {
        g2d.setStroke(new BasicStroke(2));
        drawVertices(g2d, s);
        drawEdges(g2d, s);
    }

    private void drawVertices(Graphics2D g2d, State s) {
        g2d.setColor(Color.RED);
        s.getAtomicPolygons().forEach( polygon -> {
            polygon.getVertices().forEach( vertex -> {
                Fraction x = vertex.getX();
                Fraction y = vertex.getY();
                g2d.drawArc(getDisplayPositionX(x) - 3, getDisplayPositionY(y) - 3, 6, 6, 0, 360);
            });
        });
    }

    private int getDisplayPositionX(Fraction x) {
        return axesStartXPosition + x.getNumerator() * boardXScale / x.getDenominator();
    }

    private int getDisplayPositionY(Fraction y) {
        return axesStartYPosition - y.getNumerator() * boardYScale / y.getDenominator();
    }

    private void drawEdges(Graphics2D g2d, State s) {
        g2d.setColor(Color.WHITE);
        s.getEdges().forEach( edge -> {
            g2d.drawLine(getDisplayPositionX(edge.getA().getX()),
                    getDisplayPositionY(edge.getA().getY()),
                    getDisplayPositionX(edge.getB().getX()),
                    getDisplayPositionY(edge.getB().getY())
            );
        });
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        Stroke prev = g2d.getStroke();
        float dash1[] = {10.0f};
        g2d.setStroke(        new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f));
        g2d.drawLine(0, axesStartYPosition, boardXSize - 1, axesStartYPosition);
        g2d.drawLine(axesStartXPosition, 0, axesStartXPosition, boardYSize - 1);

        g2d.setColor(Color.YELLOW);
        float dash2[] = {3.0f};
        g2d.setStroke(        new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash2, 0.0f));
        //draw minor lines
        for(int x = axesStartXPosition + boardXScale / 10; x < boardXSize; x += boardXScale / 10) {
            g2d.drawLine(x, 0, x, boardYSize - 1);
        }
        for(int y = axesStartYPosition - boardYScale / 10; y > 0; y -= boardYScale / 10) {
            g2d.drawLine(0, y, boardXSize - 1, y);
        }

        g2d.setStroke(prev);
    }
}
