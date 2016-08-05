package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Facet;
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
            List<AtomicPolygon> polygons = readPolygonVertices(polygonsNumber, input);
            List<Facet> facets = readFacets(input);
            
            List<AtomicPolygon> atomicPolygons = atomizePolygons(polygons, facets);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    private List<AtomicPolygon> atomizePolygons(List<AtomicPolygon> polygons, List<Facet> facets) {
        polygons.forEach(polygon -> {
            // TODO: first find out, is polygon sorted clock-wise or counter-clock-wise

            // for each vertex in our polygon, try to create atomic polygon using available facets
            for(int i = 0; i < polygon.getVertices().size(); i++) {
                AtomicPolygon poly = findAtomicPolygonStartingWith(i, polygon, facets);
            }
        });
        return null;
    }

    private AtomicPolygon findAtomicPolygonStartingWith(int firstVertexIndex, AtomicPolygon polygon, List<Facet> facets) {
        int numberOfVertexesInPolygon = polygon.getVertices().size();
        // find first facet in list starting from point
        Facet firstFacet = null;
        int indexOfSecondPoint = -1;
        for(int secondVertex = firstVertexIndex; secondVertex < firstVertexIndex + numberOfVertexesInPolygon; secondVertex++) {
            int secondVertexNormalized = secondVertex >= numberOfVertexesInPolygon ? secondVertex - numberOfVertexesInPolygon : secondVertex;
            Facet f = null;
            // try to find (firstVertex,secondVertexNormalized) in facets list
            for(int i = 0; i < facets.size(); i++) {
                Facet facetToCheck = new Facet(polygon.getVertices().get(firstVertexIndex), polygon.getVertices().get(secondVertexNormalized));
                if(facets.get(i).equals(facetToCheck)) {
                    f = facetToCheck;
                    indexOfSecondPoint = secondVertexNormalized;
                    break;
                }
            }
            if(f != null) {
                firstFacet = f;
                break;
            }
        }
        if(firstFacet == null)
            throw new RuntimeException("something went wrong");

        List<FractionPoint> atomicPolygonVertices = new ArrayList<FractionPoint>();
        atomicPolygonVertices.add(firstFacet.getA());
        atomicPolygonVertices.add(firstFacet.getB());

        Facet previousFacet = firstFacet;
        while(true) {
            Facet f = getFacetWithMinimalAngle(previousFacet, polygon, facets, indexOfSecondPoint);
        }
    }

    private Facet getFacetWithMinimalAngle(Facet previousFacet, AtomicPolygon polygon, List<Facet> facets, int indexOfSecondPoint) {
        return null;
    }

    private List<Facet> readFacets(BufferedReader input) throws IOException {
        List<Facet> facets = new ArrayList<Facet>();
        String facetNumberAsString = input.readLine();
        int facetNumber = Integer.parseInt(facetNumberAsString);
        for(int j = 0; j < facetNumber; j++)
        {
            String facetCoordinatesAsString = input.readLine();
            Facet facet = PolyFormat.getFacet(facetCoordinatesAsString);
            facets.add(facet);
        }
        return facets;
    }

    private int readFacetNumber(BufferedReader input) throws IOException {
        int facetNumber;
        String polygonsNumberAsString = input.readLine();
        facetNumber = Integer.parseInt(polygonsNumberAsString);
        return facetNumber;
    }

    private List<AtomicPolygon> readPolygonVertices(int polygonsNumber, BufferedReader input) throws IOException {
        List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>(); // this is not really atomic polygon, since in input data polygons are not atomic. Just reuse structure for now.
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
            polygons.add(new AtomicPolygon(vertices));
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
