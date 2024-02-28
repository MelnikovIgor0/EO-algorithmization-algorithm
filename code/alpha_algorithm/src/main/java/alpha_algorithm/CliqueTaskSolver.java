package alpha_algorithm;

import java.util.ArrayList;
import java.util.HashSet;

public interface CliqueTaskSolver {
    ArrayList<Integer> cliqueSolve(ArrayList<HashSet<Integer>> graph, ArrayList<Double> weights);
}
