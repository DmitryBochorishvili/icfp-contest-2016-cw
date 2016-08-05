package cw.icfpc;

import cw.icfpc.model.AtomicPolygon;
import cw.icfpc.model.State;

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
//        int polygonsNumber = 0;
//        try {
//            BufferedReader input =  new BufferedReader(new StringReader(s));
//            String polygonsNumberAsString = input.readLine();
//            polygonsNumber = Integer.parseInt(polygonsNumberAsString);
//            List<AtomicPolygon> polygons = new ArrayList<AtomicPolygon>();
//            for(int i = 0; i < polygonsNumber; i++)
//            {
//                String verticesNumberAsString = input.readLine();
//                int polygonVertices = Integer.parseInt(verticesNumberAsString);
//                for(int j = 0; j < polygonVertices; j++)
//                {
//                    String vertexCoordinatesAsString = input.readLine();
//
//                }
//            }
//
//        }
//        catch (IOException ex){
//            ex.printStackTrace();
//        }
//
        return null;
    }

}
