package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import cw.icfpc.utils.GraphUtils;
import cw.icfpc.utils.MathUtils;
import cw.icfpc.utils.PolyFormat;

import java.io.*;
import java.util.*;

public class ProblemReader {

    private String defaultProblem = "1\n"+
            "4\n"+
            "0,0\n"+
            "1,0\n"+
            "1/2,1/2\n"+
            "0,1/2\n"+
            "5\n"+
            "0,0 1,0\n"+
            "1,0 1/2,1/2\n"+
            "1/2,1/2 0,1/2\n"+
            "0,1/2 0,0\n"+
            "0,0 1/2,1/2\n";

    private int destId = 0;

    public State readDefaultProblem() throws IOException {
        return parseProblem(new BufferedReader(new StringReader(defaultProblem)));
    }
    
    public State readProblemFromFile(String aFile) throws IOException {
        try (BufferedReader input = new BufferedReader(new FileReader(aFile))) {
            return parseProblem(input);
        }
    }

    public State parseProblem(BufferedReader input) throws IOException {
        int polygonsNumber = readPolygonsNumber(input);
        List<List<FractionPoint>> polygons = readPolygonVertices(polygonsNumber, input);

        Map<FractionPoint, FractionPoint> allPoints = new HashMap<>();
        polygons.forEach(poly -> poly.forEach(p -> allPoints.put(p, p)));
        
        List<Edge> edges = readEdges(input);
        for (Edge e: edges) {
            if (allPoints.containsKey(e.getA())) {
                e.getA().destId = allPoints.get(e.getA()).destId;
            } else {
                e.getA().destId = destId++;
            }
            if (allPoints.containsKey(e.getB())) {
                e.getB().destId = allPoints.get(e.getB()).destId;
            } else {
                e.getB().destId = destId++;
            }
        }

        List<AtomicPolygon> atomicPolygons = atomizePolygons2(polygons, edges);
        return State.createNew(atomicPolygons);
    }

    public static List<AtomicPolygon> atomizePolygons2(List<List<FractionPoint>> polygons, List<Edge> edges) {
        List<AtomicPolygon> allPolygons = GraphUtils.minimumCycles(MathUtils.splitByIntersections(edges));

        polygons.forEach(polygon -> {
            if(!GraphUtils.ifPositivePolygonInSilhouette(polygon)) {
                AtomicPolygon hole = new AtomicPolygon(polygon);
                allPolygons.removeIf(p -> p.overlaps(hole));
            }
        });
        return allPolygons;
    }

    private List<Edge> readEdges(BufferedReader input) throws IOException {
        List<Edge> edges = new ArrayList<>();
        String edgeNumberAsString = input.readLine();
        int edgeNumber = Integer.parseInt(edgeNumberAsString);
        for(int j = 0; j < edgeNumber; j++)
        {
            String edgeCoordinatesAsString = input.readLine();
            Edge edge = PolyFormat.getEdge(edgeCoordinatesAsString);
            edges.add(edge);
        }
        return edges;
    }

    private List<List<FractionPoint>> readPolygonVertices(int polygonsNumber, BufferedReader input) throws IOException {
        List<List<FractionPoint>> polygons = new ArrayList<>(); 
        for(int i = 0; i < polygonsNumber; i++)
        {
            String verticesNumberAsString = input.readLine();
            int problemPolygonVertices = Integer.parseInt(verticesNumberAsString);
            List<FractionPoint> vertices = new ArrayList<FractionPoint>();
            for(int j = 0; j < problemPolygonVertices; j++)
            {
                String vertexCoordinatesAsString = input.readLine();
                FractionPoint vertex = PolyFormat.getFractionPoint(vertexCoordinatesAsString);
                vertex.destId = destId;
                destId++;
                vertices.add(vertex);
            }
            polygons.add(vertices);
        }
        return polygons;
    }

    private int readPolygonsNumber(BufferedReader input) throws IOException {
        int polygonsNumber;
        String polygonsNumberAsString = input.readLine();
        polygonsNumber = Integer.parseInt(polygonsNumberAsString);
        return polygonsNumber;
    }

}
