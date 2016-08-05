package cw.icfpc;

import cw.icfpc.model.State;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StateVisualizer {
    private final int boardXSize = 540;
    private final int boardYSize = 540;
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
            g2d.drawLine(axesStartXPosition + facet.getA().getX().intValue(),
                        axesStartYPosition - facet.getA().getY().intValue(),
                        axesStartXPosition + facet.getB().getX().intValue(),
                        axesStartYPosition - facet.getB().getY().intValue()
            );
        });
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.drawLine(axesStartXPosition, axesStartYPosition, boardXSize - axesStartXPosition, axesStartYPosition);
        g2d.drawLine(axesStartXPosition, axesStartYPosition, axesStartXPosition, boardYSize - axesStartYPosition);
    }
}
