package alpha_algorithm;

import java.util.ArrayList;
import java.util.HashSet;

public class FullSearchCliqueTaskSolver implements CliqueTaskSolver {
    private ArrayList<HashSet<Integer>> graph;
    private ArrayList<Double> weights;
    private ArrayList<Integer> bestClique;
    private Double bestProfit;

    private boolean checkClique(ArrayList<Integer> clique) {
        for (int i = 0; i < clique.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                if (graph.get(clique.get(i)).contains(clique.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private double evaluateClique(ArrayList<Integer> clique) {
        double sum = 0;
        for (Integer vertexId : clique) {
            sum += weights.get(vertexId);
        }
        return sum;
    }

    private boolean getBit(int mask, int i) {
        return ((mask >> i) & 1) == 1;
    }

    @Override
    public ArrayList<Integer> cliqueSolve(ArrayList<HashSet<Integer>> graph, ArrayList<Double> weights) {
        this.graph = graph;
        this.weights = weights;
        this.bestClique = new ArrayList<Integer>();
        this.bestProfit = 0.0;
        for (int mask = 0; mask < (1L << graph.size()); ++mask) {
            ArrayList<Integer> currentClique = new ArrayList<Integer>();
            for (int i = 0; i < graph.size(); ++i) {
                if (getBit(mask, i)) {
                    currentClique.add(i);
                }
            }
            if (checkClique(currentClique)) {
                double currentProfit = evaluateClique(currentClique);
                if (currentProfit > bestProfit) {
                    bestProfit = currentProfit;
                    bestClique = currentClique;
                }
            }
        }
        return bestClique;
    }
}
