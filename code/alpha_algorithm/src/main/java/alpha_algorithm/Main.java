package alpha_algorithm;

import graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputPath = reader.readLine();
        String outputPath = reader.readLine();
        Graph graph = Graph.ParseGraph(inputPath);
        AlphaAlgorithm algorithm = new AlphaAlgorithm();
        ArrayList<Fragment> fragments = algorithm.algorithmize(graph,
                1.0,
                1.0,
                0.9,
                new FullSearchCliqueTaskSolver());
        ResultExporter.exportToJson(fragments, outputPath);
    }
}