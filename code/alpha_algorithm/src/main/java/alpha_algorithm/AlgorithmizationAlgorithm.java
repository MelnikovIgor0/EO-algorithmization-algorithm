package alpha_algorithm;

import graph.Graph;

import java.util.ArrayList;

public interface AlgorithmizationAlgorithm {
    ArrayList<Fragment> algorithmize(Graph graph,
                                     Double alpha_coeff,
                                     Double beta_coeff,
                                     Double gamma_coeff,
                                     CliqueTaskSolver solver);
}
