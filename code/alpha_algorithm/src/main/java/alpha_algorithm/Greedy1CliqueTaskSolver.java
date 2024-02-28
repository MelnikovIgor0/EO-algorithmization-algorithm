package alpha_algorithm;

import java.util.ArrayList;
import java.util.HashSet;

public class Greedy1CliqueTaskSolver implements CliqueTaskSolver {
    private ArrayList<HashSet<Integer>> graph;
    private ArrayList<Double> weights;
    private ArrayList<Integer> bestClique;
    private Double bestProfit;

    private void randomRelax() {
        ArrayList<Integer> randomOrder = new ArrayList<Integer>();
        for (int i = 0; i < graph.size(); ++i) {
            randomOrder.add(i);
        }
        ArrayList<Integer> currentClique = new ArrayList<Integer>();
        HashSet<Integer> prohibitedVertices = new HashSet<Integer>();
        double currentProfit = 0;
        for (Integer vertexId : randomOrder) {
            if (prohibitedVertices.contains(vertexId)) {
                continue;
            }
            currentClique.add(vertexId);
            currentProfit += weights.get(vertexId);
            prohibitedVertices.addAll(graph.get(vertexId));
        }
        if (currentProfit > bestProfit) {
            bestProfit = currentProfit;
            bestClique = (ArrayList<Integer>) currentClique.clone();
        }
    }

    @Override
    public ArrayList<Integer> cliqueSolve(ArrayList<HashSet<Integer>> graph, ArrayList<Double> weights) {
        this.graph = graph;
        this.weights = weights;
        bestClique = new ArrayList<Integer>();
        bestProfit = 0.0;
        for (int i = 0; i < graph.size() * 2; ++i) {
            randomRelax();
        }
        return bestClique;
    }
}
