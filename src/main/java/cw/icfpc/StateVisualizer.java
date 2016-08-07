package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.State;
import org.apache.commons.math3.fraction.BigFraction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;

public class StateVisualizer {
    private final int boardXScale = 500;
    private final int boardYScale = 500;
    private static final int MARGIN = 80;

    private List<Scene> scenes = new LinkedList<>();
    private Scene currentScene;
    private int row = 1;
    private int rows = 1;

    private class Scene {
        private int offsetX = 0;
        private int offsetY = 0;
        private State state;
        private String title;
        private List<AtomicPolygon> facets = new LinkedList<>();
        private Color overrideColor;
    }

    public StateVisualizer(State s) {
        currentScene = new Scene();
        scenes.add(currentScene);
        currentScene.state = s;
    }
    
    public StateVisualizer addScene(State s, boolean belowPrevious) {
        return addScene(s, belowPrevious, "");
    }
    
    public StateVisualizer addScene(State s, boolean belowPrevious, String title) {
        if (currentScene.state == null) {
            currentScene.state = s;
            if (!title.isEmpty()) {
                currentScene.title = title;
            }
            return this;
        }

        if (!title.isEmpty()) {
            currentScene.title = title;
        }
        
        Scene prevScene = currentScene;
        currentScene = new Scene();
        scenes.add(currentScene);
        currentScene.state = s;
        if (belowPrevious) {
            currentScene.offsetX = prevScene.offsetX;
            currentScene.offsetY = prevScene.offsetY + boardYScale + 2* MARGIN;
            row++;
            rows = Math.max(row, rows);
        } else {
            row = 1;
            currentScene.offsetX = prevScene.offsetX + boardXScale + 2* MARGIN;
            currentScene.offsetY = 0;
        }
        
        return this;
    }

    public StateVisualizer addFacets(List<AtomicPolygon> facets, java.awt.Color overrideColor) {
        currentScene.facets.addAll(facets);
        currentScene.overrideColor = overrideColor;
        return this;
    }

    public void drawToFile(String fileName) throws IOException {
        String output = fileName == null ? "pic.png" : fileName;
        
        System.out.println(String.format("Drawing %d scenes to file %s", scenes.size(), output));

        BufferedImage img = new BufferedImage(
                currentScene.offsetX + boardXScale + 2*MARGIN,
                (boardYScale + 2*MARGIN) * rows, 
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        
        g2d.setBackground(Color.WHITE);
        
        for (Scene scene: scenes) {
            currentScene = scene;
            drawAxes(g2d);
            drawState(g2d, currentScene.state);
        }

        File f = new File(output);
        ImageIO.write(img, "PNG", f);
    }
    
    public static StateVisualizer builder(State s) {
        return new StateVisualizer(s);
    }

    public static StateVisualizer builder() {
        return new StateVisualizer(null);
    }

    private void drawState(Graphics2D g2d, State s) {
        g2d.setStroke(new BasicStroke(2));
        drawVertices(g2d, s);
        drawEdges(g2d, s);
        drawTitle(g2d, s);
    }

    private void drawTitle(Graphics2D g2d, State s)
    {
        g2d.setColor(Color.WHITE);
        String title = currentScene.title;
        if (title == null || title.isEmpty()) {
            title = "Iteration: " + s.getIteration() + ", Polygons: " + s.getAtomicPolygons().size();
        }
        g2d.drawString(title,
                currentScene.offsetX + MARGIN, currentScene.offsetY + boardYScale + MARGIN * 3 / 2);
    }

    private void drawVertices(Graphics2D g2d, State s) {
        g2d.setColor(Color.RED);
        s.getAtomicPolygons().forEach( polygon -> {
            polygon.getVertices().forEach( vertex -> {
                BigFraction x = vertex.getX();
                BigFraction y = vertex.getY();
                g2d.drawArc(getDisplayPositionX(x) - 3, getDisplayPositionY(y) - 3, 6, 6, 0, 360);
            });
        });
    }

    private int getDisplayPositionX(BigFraction x) {
        return currentScene.offsetX + MARGIN + (int) Math.round(x.doubleValue() * boardXScale);
    }

    private int getDisplayPositionY(BigFraction y) {
        return currentScene.offsetY + boardYScale + MARGIN - (int) Math.round(y.doubleValue() * boardYScale);
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
        g2d.setStroke(new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash1, 0.0f));
        g2d.drawLine(currentScene.offsetX + MARGIN, 
                currentScene.offsetY + boardYScale + MARGIN, 
                currentScene.offsetX + boardXScale + MARGIN - 1, 
                currentScene.offsetY + boardYScale + MARGIN);
        g2d.drawLine(currentScene.offsetX + MARGIN, 
                currentScene.offsetY + MARGIN - 3, 
                currentScene.offsetX + MARGIN, 
                currentScene.offsetY + boardYScale + MARGIN - 1);

        g2d.setColor(Color.YELLOW);
        float dash2[] = {3.0f};
        g2d.setStroke(new BasicStroke(1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f, dash2, 0.0f));
        
        //draw minor lines
        for(int x = MARGIN + currentScene.offsetX + boardXScale / 10; 
            x < currentScene.offsetX + boardXScale + MARGIN; 
            x += boardXScale / 10) {
            g2d.drawLine(x, currentScene.offsetY + MARGIN, x, currentScene.offsetY + boardYScale - 1 + MARGIN);
        }

        for(int y = currentScene.offsetY + boardYScale + MARGIN - boardYScale / 10;
            y > currentScene.offsetY + MARGIN;
            y -= boardYScale / 10) 
        {
            g2d.drawLine(currentScene.offsetX + MARGIN, 
                    y, 
                    currentScene.offsetX + MARGIN + boardXScale - 1, 
                    y);
        }

        g2d.setStroke(prev);
    }
    
    public static void main(String[] argv) throws IOException {
        String directory = "downloadedProblems";
        Files.newDirectoryStream(Paths.get(directory))
                .forEach(subdir -> {
                    if (!Files.isDirectory(subdir)) {
                        return;
                    }
                    System.out.println("Drawing directory: " + subdir);
                    final StateVisualizer vis = StateVisualizer.builder();
                    
                    try {
                        Files.newDirectoryStream(subdir).forEach(problemPath -> {
                            ProblemReader r = new ProblemReader();
                            State s = null;
                            try {
                                s = r.readProblemFromFile(problemPath.toAbsolutePath().toString());
                            } catch (IOException | IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                            vis.addScene(s, false, "Problem: " + problemPath.getFileName());
                        });

                        vis.drawToFile(subdir + "/problems.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
