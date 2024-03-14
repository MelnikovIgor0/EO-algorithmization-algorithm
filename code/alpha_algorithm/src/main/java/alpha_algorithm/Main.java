package alpha_algorithm;

import graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        Graph graph = Graph.ParseGraph(inputFilePath);
        AlphaAlgorithm algorithm = new AlphaAlgorithm();
        ArrayList<Fragment> fragments = algorithm.algorithmize(graph,
                1.0,
                1.0,
                0.9,
                new FullSearchCliqueTaskSolver());
        ResultExporter.exportToJson(fragments, outputFilePath);
    }
}