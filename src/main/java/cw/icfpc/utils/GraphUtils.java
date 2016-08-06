package cw.icfpc.utils;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.openscience.cdk.graph.MinimumCycleBasis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphUtils
{

    public static AtomicPolygon merge(AtomicPolygon p1, AtomicPolygon p2)
    {
        Edge edge = p1.getAdjacentEdge(p2);
        if (edge == null)
            return null; // polygons are not adjacent

        Set<Edge> edges = new HashSet<>();
        edges.addAll(p1.getEdges());
        edges.addAll(p2.getEdges());
        edges.remove(edge);

        List<AtomicPolygon> merged = minimumCycles(edges);
        if (merged.size() != 1)
            throw new RuntimeException("Polygons cannot be merged properly");

        // TODO remove vertices if 2 edges lay on the same line after merge

        return merged.get(0);
    }

    public static List<AtomicPolygon> minimumCycles(Collection<Edge> edges)
    {
        Map<FractionPoint, Integer> verticesMap = new HashMap<>();
        List<FractionPoint> verticesList = new ArrayList<>();
        List<List<Integer>> adjacentList = new ArrayList<>();

        edges.forEach(edge -> {
            int aIndex = getVertexIndex(edge.getA(), verticesMap, verticesList, adjacentList);
            int bIndex = getVertexIndex(edge.getB(), verticesMap, verticesList, adjacentList);

            adjacentList.get(aIndex).add(bIndex);
            adjacentList.get(bIndex).add(aIndex);
        });

        int[][] graph = new int[adjacentList.size()][];
        for (int i = 0; i < adjacentList.size(); i++)
            graph[i] = adjacentList.get(i).stream().mapToInt(n -> n).toArray();

        MinimumCycleBasis minimumCycleBasis = new MinimumCycleBasis(graph);

        int[][] paths = minimumCycleBasis.paths();

        List<AtomicPolygon> polygons = new ArrayList<>();
        for (int i = 0; i < paths.length; i++)
        {
            List<FractionPoint> polyVertices = IntStream.of(paths[i])
                    .boxed()
                    .map(verticesList::get)
                    .collect(Collectors.toList());
            // workaround
            if (polyVertices.get(0) == polyVertices.get(polyVertices.size()-1)) {
                polyVertices.remove(polyVertices.size()-1);
            }
            AtomicPolygon poly = new AtomicPolygon(polyVertices);
            polygons.add(poly);
        }

        return polygons;
    }

    private static int getVertexIndex(FractionPoint p, Map<FractionPoint, Integer> vertices,
                                      List<FractionPoint> verticesList,
                                      List<List<Integer>> adjacentList)
    {
        Integer vertexNumber = vertices.get(p);
        if (vertexNumber == null)
        {
            verticesList.add(p);
            adjacentList.add(new ArrayList<>());
            vertexNumber = adjacentList.size() - 1;
            vertices.put(p, vertexNumber);
        }
        return vertexNumber;
    }

    public static boolean ifPositivePolygonInSilhouette(List<FractionPoint> vertices) {
        double sumAngle = 0;

        int pointsNumber = vertices.size();

        if(pointsNumber < 3)
            throw new RuntimeException("incorrect initial polygon in silhouette - less then 3 points");

        for(int i = 0; i < pointsNumber - 2; i++) {
            sumAngle += MathUtils.angleBetween(vertices.get(i), vertices.get(i+1), vertices.get(i+2));
        }

        sumAngle = MathUtils.angleBetween(vertices.get(pointsNumber-2),
                vertices.get(pointsNumber-1),
                vertices.get(0));

        sumAngle = MathUtils.angleBetween(vertices.get(pointsNumber-1),
                vertices.get(0),
                vertices.get(1));

        return (sumAngle > 0);
    }
}
