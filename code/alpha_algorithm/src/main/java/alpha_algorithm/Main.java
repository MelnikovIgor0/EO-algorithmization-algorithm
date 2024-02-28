package alpha_algorithm;

import graph.Graph;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        Graph graph = Graph.ParseGraph("..\\..\\test_examples\\test_simple.xml");
        AlphaAlgorithm algorithm = new AlphaAlgorithm();
        ArrayList<Fragment> fragments = algorithm.algorithmize(graph,
                1.0,
                1.0,
                0.9,
                new FullSearchCliqueTaskSolver());
        ResultExporter.exportToJson(fragments, "..\\..\\test_examples\\result_simple.json");
    }
}