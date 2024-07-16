package alpha_algorithm;

import java.util.ArrayList;
import java.util.HashSet;

public class Greedy2CliqueTaskSolver implements CliqueTaskSolver {
    @Override
    public ArrayList<Integer> cliqueSolve(ArrayList<HashSet<Integer>> graph, ArrayList<Double> weights) {
        double max_profit = 0;
        HashSet<Integer> max_clique = new HashSet<Integer>();
        for (int root = 0; root < weights.size(); ++root) {
            double current_profit = weights.get(root);
            HashSet<Integer> current_clique = new HashSet<Integer>();
            current_clique.add(root);
            while (true) {
                int best_estimator = -1;
                for (int i = 0; i < weights.size(); ++i) {
                    if (current_clique.contains(i)) {
                        continue;
                    }
                    boolean ok = true;
                    for (int j : current_clique) {
                        if (graph.get(j).contains(i)) {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        continue;
                    }
                    if (best_estimator == -1 || weights.get(i) > weights.get(best_estimator)) {
                        best_estimator = i;
                    }
                }
                if (best_estimator == -1) {
                    break;
                }
                current_clique.add(best_estimator);
                current_profit += weights.get(best_estimator);
            }
            if (current_profit > max_profit) {
                max_profit = current_profit;
                max_clique = (HashSet<Integer>)(current_clique.clone());
            }
        }
        return new ArrayList<Integer>(max_clique);
    }
}
