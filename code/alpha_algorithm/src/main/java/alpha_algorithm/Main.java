package alpha_algorithm;

import graph.Graph;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.*;


public class Main {
    public static void main(String[] args) throws IOException {

        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file path");
        output.setRequired(true);
        options.addOption(output);

        Option alphaParam = new Option("a", "alpha", true, "alpha coefficient");
        alphaParam.setRequired(false);
        options.addOption(alphaParam);

        Option betaParam = new Option("b", "beta", true, "beta coefficient");
        betaParam.setRequired(false);
        options.addOption(betaParam);

        Option gammaParam = new Option("y", "gamma", true, "gamma coefficient");
        gammaParam.setRequired(false);
        options.addOption(gammaParam);

        Option solverParam = new Option("s", "clique-solver-strategy", true, "strategy of solving maximal clique task");
        gammaParam.setRequired(false);
        options.addOption(solverParam);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        String alphaCoefficient = cmd.getOptionValue("alpha");
        String betaCoefficient = cmd.getOptionValue("beta");
        String gammaCoefficient = cmd.getOptionValue("gamma");
        String cliqueSolverStrategy = cmd.getOptionValue("clique-solver-strategy");

        Double alpha = 1.0;
        if (alphaCoefficient != null) {
            alpha = Double.parseDouble(alphaCoefficient);
        }
        Double beta = 1.0;
        if (betaCoefficient != null) {
            beta = Double.parseDouble(betaCoefficient);
        }
        Double gamma = 0.8;
        if (gammaCoefficient != null) {
            gamma = Double.parseDouble(gammaCoefficient);
        }

        Graph graph = Graph.ParseGraph(inputFilePath);

        CliqueTaskSolver solver = new Greedy1CliqueTaskSolver();
        switch (cliqueSolverStrategy) {
            case "full":
                solver = new FullSearchCliqueTaskSolver();
                break;
            case "greedy1":
                solver = new Greedy1CliqueTaskSolver();
                break;
            case "greedy2":
                solver = new Greedy2CliqueTaskSolver();
                break;
            case "auto":
                if (graph.getVertices().size() < 30) {
                    solver = new FullSearchCliqueTaskSolver();
                } else if (graph.getVertices().size() < 200) {
                    solver = new Greedy2CliqueTaskSolver();
                } else {
                    solver = new Greedy1CliqueTaskSolver();
                }
                break;
            default:
                System.exit(1);
        }

        AlphaAlgorithm algorithm = new AlphaAlgorithm();
        ArrayList<Fragment> fragments = algorithm.algorithmize(graph,
                alpha,
                beta,
                gamma,
                solver
        );
        ResultExporter.exportToJson(fragments, outputFilePath);
    }
}