package cw.icfpc.utils;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.Edge;
import cw.icfpc.model.FractionPoint;
import org.openscience.cdk.graph.MinimumCycleBasis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphUtils
{
    public static List<AtomicPolygon> minimumCycles(List<Edge> edges)
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
            List<FractionPoint> polyVertices = IntStream.of(paths[i]).boxed().map(index -> verticesList.get(index)).collect(Collectors.toList());
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
}
