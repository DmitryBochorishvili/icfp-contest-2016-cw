package cw.icfpc;

import cw.icfpc.model.State;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StateVisualizer {
    private final int boardXScale = 500;
    private final int boardYScale = 500;
    private final int boardXSize = boardXScale + 20;
    private final int boardYSize = boardYScale + 20;
    private final int axesStartXPosition = 10;
    private final int axesStartYPosition = boardYSize - 10;
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
        g2d.setColor(Color.WHITE);
        s.getFacets().forEach( facet -> {
            g2d.drawLine(axesStartXPosition + facet.getA().getX().getNumerator() * boardXScale / facet.getA().getX().getDenominator(),
                        axesStartYPosition - facet.getA().getY().getNumerator() * boardYScale / facet.getA().getY().getDenominator(),
                        axesStartXPosition + facet.getB().getX().getNumerator() * boardXScale / facet.getB().getX().getDenominator(),
                        axesStartYPosition - facet.getB().getY().getNumerator() * boardYScale / facet.getB().getY().getDenominator()
            );
        });
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        float dash1[] = {10.0f};

        Stroke prev = g2d.getStroke();
        g2d.setStroke(        new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f));
        g2d.drawLine(axesStartXPosition, axesStartYPosition, boardXSize - axesStartXPosition, axesStartYPosition);
        g2d.drawLine(axesStartXPosition, axesStartYPosition, axesStartXPosition, boardYSize - axesStartYPosition);
        g2d.setStroke(prev);
    }
}
