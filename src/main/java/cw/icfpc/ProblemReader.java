package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import cw.icfpc.utils.PolyFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    public ProblemReader()
    {
    }

    public State readProblemFromFile(String aFile) throws FileNotFoundException {
        StringBuilder contents = new StringBuilder();
        if(aFile == null){
            contents.append(defaultProblem);
        }
        try {
            BufferedReader input =  new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }


        return parseProblem(contents.toString());
    }

    private State parseProblem(String s) {
        try {
            BufferedReader input =  new BufferedReader(new StringReader(s));
            int polygonsNumber = readPolygonsNumber(input);
            List<List<FractionPoint>> polygons = readPolygonVertices(polygonsNumber, input);
            List<Edge> edges = readEdges(input);
            
            List<AtomicPolygon> atomicPolygons = atomizePolygons(polygons, edges);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    private List<AtomicPolygon> atomizePolygons(List<List<FractionPoint>> polygons, List<Edge> edges) {
        polygons.forEach(polygon -> {
            // TODO: first find out, is polygon sorted clock-wise or counter-clock-wise

            // for each vertex in our polygon, try to create atomic polygon using available edges
            for(int i = 0; i < polygon.size(); i++) {
                AtomicPolygon poly = findAtomicPolygonStartingWith(i, polygon, edges);
            }
        });
        return null;
    }

    private AtomicPolygon findAtomicPolygonStartingWith(int firstVertexIndex, List<FractionPoint> vertices, List<Edge> edges) {
//        Collection<FractionPoint> vertices = polygon.getVertices();
        int numberOfVertexesInPolygon = vertices.size();
        // find first edge in list starting from point
        Edge firstEdge = null;
        int indexOfSecondPoint = -1;
        for(int secondVertex = firstVertexIndex; secondVertex < firstVertexIndex + numberOfVertexesInPolygon; secondVertex++) {
            int secondVertexNormalized = secondVertex >= numberOfVertexesInPolygon ? secondVertex - numberOfVertexesInPolygon : secondVertex;
            Edge f = null;
            // try to find (firstVertex,secondVertexNormalized) in edges list
            for(int i = 0; i < edges.size(); i++) {
                Edge edgeToCheck = new Edge(vertices.get(firstVertexIndex), vertices.get(secondVertexNormalized));
                if(edges.get(i).equals(edgeToCheck)) {
                    f = edgeToCheck;
                    indexOfSecondPoint = secondVertexNormalized;
                    break;
                }
            }
            if(f != null) {
                firstEdge = f;
                break;
            }
        }
        if(firstEdge == null)
            throw new RuntimeException("something went wrong");

        List<FractionPoint> atomicPolygonVertices = new ArrayList<FractionPoint>();
        atomicPolygonVertices.add(firstEdge.getA());
        atomicPolygonVertices.add(firstEdge.getB());

        Edge previousEdge = firstEdge;
        while(true) {
            Edge f = getEdgeWithMinimalAngle(previousEdge, vertices, edges, indexOfSecondPoint);
        }
    }

    private Edge getEdgeWithMinimalAngle(Edge previousEdge, List<FractionPoint> polygon, List<Edge> edges, int indexOfSecondPoint) {
        return null;
    }

    private List<Edge> readEdges(BufferedReader input) throws IOException {
        List<Edge> edges = new ArrayList<Edge>();
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

    private int readEdgeNumber(BufferedReader input) throws IOException {
        int edgeNumber;
        String polygonsNumberAsString = input.readLine();
        edgeNumber = Integer.parseInt(polygonsNumberAsString);
        return edgeNumber;
    }

    private List<List<FractionPoint>> readPolygonVertices(int polygonsNumber, BufferedReader input) throws IOException {
        List<List<FractionPoint>> polygons = new ArrayList<>(); // this is not really atomic polygon, since in input data polygons are not atomic. Just reuse structure for now.
        for(int i = 0; i < polygonsNumber; i++)
        {
            String verticesNumberAsString = input.readLine();
            int problemPolygonVertices = Integer.parseInt(verticesNumberAsString);
            List<FractionPoint> vertices = new ArrayList<FractionPoint>();
            for(int j = 0; j < problemPolygonVertices; j++)
            {
                String vertexCoordinatesAsString = input.readLine();
                FractionPoint vertex = PolyFormat.getFractionPoint(vertexCoordinatesAsString);
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
