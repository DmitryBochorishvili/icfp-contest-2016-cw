package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import cw.icfpc.model.State;
import cw.icfpc.utils.GraphUtils;
import cw.icfpc.utils.MathUtils;
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
        else {
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

        }


        return parseProblem(contents.toString());
    }

    public State parseProblem(String s) {
        try {
            BufferedReader input =  new BufferedReader(new StringReader(s));
            int polygonsNumber = readPolygonsNumber(input);
            List<List<FractionPoint>> polygons = readPolygonVertices(polygonsNumber, input);
            List<Edge> edges = readEdges(input);
            
            List<AtomicPolygon> atomicPolygons = atomizePolygons2(polygons, edges);
            return new State(atomicPolygons);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private class EdgeWithVisitMark extends Edge {

        private boolean visited;

        public EdgeWithVisitMark(FractionPoint a, FractionPoint b) {
            super(a, b);
            visited = false;
        }

        public void visit() {
            visited = true;
        }

        public boolean isVisited() {
            return visited;
        }
    }

    public static List<AtomicPolygon> atomizePolygons2(List<List<FractionPoint>> polygons, List<Edge> edges) {
        List<AtomicPolygon> allPolygons = GraphUtils.minimumCycles((List<Edge>)MathUtils.splitByIntersections(edges));

        polygons.forEach(polygon -> {
            if(!GraphUtils.ifPositivePolygonInSilhouette(polygon)) {
                AtomicPolygon hole = new AtomicPolygon(polygon);
                allPolygons.removeIf(p -> p.overlaps(hole));
            }
        });
        return allPolygons;
    }

//    private List<AtomicPolygon> atomizePolygons(List<List<FractionPoint>> polygons, List<Edge> edges) {
//        List<AtomicPolygon> polygonsFound = new ArrayList<>();
//
//        List<EdgeWithVisitMark> edgesWithVisitMark = new ArrayList<>();
//        edges.forEach(origEdge -> {
//            edgesWithVisitMark.add(new EdgeWithVisitMark(origEdge.getA(), origEdge.getB()));
//            edgesWithVisitMark.add(new EdgeWithVisitMark(origEdge.getB(), origEdge.getA()));
//        });
//
//        polygons.forEach(polygon -> {
//            // TODO: first find out, is polygon sorted clock-wise or counter-clock-wise
//
//            // for each vertex in our polygon, try to create atomic polygon using available edges
//            polygon.forEach(startingPoint -> {
//                //AtomicPolygon poly = findAtomicPolygonStartingWith(i, polygon, edges);
//
//                List<FractionPoint> vertices = new ArrayList<>();
//                vertices.add(startingPoint);
//                EdgeWithVisitMark currentEdge = visitNextEdge(null, startingPoint, edgesWithVisitMark);
//                while(currentEdge != null) {
//                    FractionPoint nextPoint = currentEdge.getB();
//                    if(vertices.contains(nextPoint)) {// found result
//                        polygonsFound.add(new AtomicPolygon(vertices));
//                        break;
//                    }
//                    vertices.add(nextPoint);
//                    EdgeWithVisitMark nextEdge = visitNextEdge(currentEdge, nextPoint, edgesWithVisitMark);
//                    currentEdge = nextEdge;
//                }
//
//            });
//        });
//        return polygonsFound;
//    }
//
//
//    private EdgeWithVisitMark visitNextEdge(EdgeWithVisitMark previousEdge, FractionPoint point, List<EdgeWithVisitMark> edgesWithVisitMark) {
//        double maxAngle = -Math.PI * 2;
//        int maxAngleEdgeIndex = -1;
//
//        List<EdgeWithVisitMark> edgesToConsider = new ArrayList<>();
//        for(int e = 0; e < edgesWithVisitMark.size(); e++) {
//            EdgeWithVisitMark edge = edgesWithVisitMark.get(e);
//            if(edge.isVisited())
//                continue;
//            if(previousEdge == null) { // this is going to be first edge of figure. Then return first good
//                if(edge.getA().equals(point)) {//found needed edge
//                    edge.visit();
//                    return edge;
//                }
//            }
//            else {
//                if(edge.getA().equals(point)) {
//                    edgesToConsider.add(edge);
//                }
//            }
//        }
//        if(edgesToConsider.size() == 0) // walked through all edges but all of them connected to this point are visited
//            return null;
//
//        //TODO: find edge with minimal angle
//        for(int e = 0; e < edgesToConsider.size(); e++) {
//            double curAngle =  MathUtils.angleBetween(previousEdge.getA(), edgesToConsider.get(e).getA(), edgesToConsider.get(e).getB());
//            if(curAngle >= 0 && curAngle > maxAngle && curAngle < Math.PI - 1e-6) {
//                maxAngle = curAngle;
//                maxAngleEdgeIndex = e;
//            }
//        }
////        minAngleEdgeIndex = edgesToConsider.size() - 1; // TEMPORARY
//        if(maxAngleEdgeIndex != -1) {
//            edgesToConsider.get(maxAngleEdgeIndex).visit();
//            return edgesToConsider.get(maxAngleEdgeIndex);
//        }
//        return null;
//    }



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
