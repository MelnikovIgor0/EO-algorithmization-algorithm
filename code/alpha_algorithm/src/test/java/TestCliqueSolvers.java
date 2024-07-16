import alpha_algorithm.FullSearchCliqueTaskSolver;
import alpha_algorithm.Greedy1CliqueTaskSolver;
import alpha_algorithm.Greedy2CliqueTaskSolver;

import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCliqueSolvers {
    private static class Graph {
        public ArrayList<HashSet<Integer>> graph;
        public ArrayList<Double> weight;

        public Graph(ArrayList<HashSet<Integer>> graph, ArrayList<Double> weight) {
            this.graph = graph;
            this.weight = weight;
        }
    }

    private Random random = new Random();

    private int randomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        return random.nextInt(max - min) + min;
    }

    private double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    Graph generateGraph(int limitNumberVertices) {
        ArrayList<HashSet<Integer>> result = new ArrayList<HashSet<Integer>>();
        int numberVertices = randomInt(2, limitNumberVertices);
        for (int i = 0; i < numberVertices; ++i) {
            result.add(new HashSet<Integer>());
        }
        int numberEdges = randomInt(0, (int)((numberVertices * (numberVertices - 1) / 2) * 0.8));
        for (int i = 0; i < numberEdges; ++i) {
            int from = randomInt(0, numberVertices);
            int to = randomInt(0, numberVertices);
            while (from == to || result.get(from).contains(to)) {
                from = randomInt(0, numberVertices);
                to = randomInt(0, numberVertices);
            }
            result.get(from).add(to);
            result.get(to).add(from);
        }
        ArrayList<Double> weights = new ArrayList<Double>();
        for (int i = 0; i < numberVertices; ++i) {
            weights.add(randomDouble(0, 100));
        }
        return new Graph(result, weights);
    }

    @Test
    void testStressFullSearchAlgorithm() {
        FullSearchCliqueTaskSolver solver = new FullSearchCliqueTaskSolver();
        for (int testId = 0; testId < 1000; ++testId) {
            Graph g = generateGraph(22);
            ArrayList<Integer> result = solver.cliqueSolve(g.graph, g.weight);
            for (int i = 0; i < result.size(); ++i) {
                for (int j = 0; j < result.size(); ++j) {
                    if (i != j && g.graph.get(result.get(i)).contains(result.get(j))) {
                        fail("Full search clique solver is incorrect");
                    }
                }
            }
        }
    }

    @Test
    void testStressGreedy1SearchAlgorithm() {
        Greedy1CliqueTaskSolver solver = new Greedy1CliqueTaskSolver();
        for (int testId = 0; testId < 1000; ++testId) {
            Graph g = generateGraph(1000);
            ArrayList<Integer> result = solver.cliqueSolve(g.graph, g.weight);
            for (int i = 0; i < result.size(); ++i) {
                for (int j = 0; j < result.size(); ++j) {
                    if (i != j && g.graph.get(result.get(i)).contains(result.get(j))) {
                        fail("GREEDY1 search clique solver is incorrect");
                    }
                }
            }
        }
    }

    @Test
    void testStressGreedy2SearchAlgorithm() {
        Greedy2CliqueTaskSolver solver = new Greedy2CliqueTaskSolver();
        for (int testId = 0; testId < 1000; ++testId) {
            Graph g = generateGraph(500);
            ArrayList<Integer> result = solver.cliqueSolve(g.graph, g.weight);
            for (int i = 0; i < result.size(); ++i) {
                for (int j = 0; j < result.size(); ++j) {
                    if (i != j && g.graph.get(result.get(i)).contains(result.get(j))) {
                        fail("GREEDY2 search clique solver is incorrect");
                    }
                }
            }
        }
    }
}
