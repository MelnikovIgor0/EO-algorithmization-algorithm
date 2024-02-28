package graph;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Integer> objects;
    private HashSet<Integer> prohibitedToTransform;

    public Graph() {
    }

    public Graph(
        ArrayList<Vertex> vertices,
        ArrayList<Integer> objects,
        HashSet<Integer> prohibitedToTransform
    ) {
        this.vertices = vertices;
        this.objects = objects;
        this.prohibitedToTransform = prohibitedToTransform;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Integer> getObjects() {
        return objects;
    }

    public HashSet<Integer> getProhibitedToTransform() {
        return prohibitedToTransform;
    };

    public static Graph ParseGraph(String filePath) throws IOException {
        StringBuilder xmlInput = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            xmlInput.append(line);
        }

        XmlMapper xmlMapper = new XmlMapper();
        Graph graph = xmlMapper.readValue(xmlInput.toString(), Graph.class);

        for (Vertex v : graph.getVertices()) {
            v.normalize();
        }
        if (graph.prohibitedToTransform == null) {
            graph.prohibitedToTransform = new HashSet<Integer>();
        }

        return graph;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Graph:\n\tVertices: " + vertices.size() + "\n");
        Integer counter = 0;
        for (Vertex v : vertices) {
            result.append("\t\tID: " + counter + "\n");
            result.append("\t\tObjects to read: ");
            for (Integer o : v.getObjectsToRead()) {
                result.append(o + " ");
            }
            result.append('\n');
            result.append("\t\tObjects to write: ");
            for (Integer o : v.getObjectsToWrite()) {
                result.append(o + " ");
            }
            result.append('\n');
            result.append("\t\tChildren: ");
            for (Integer o : v.getChildIds()) {
                result.append(o + " ");
            }
            result.append('\n');
            result.append("\t\tEO execution time: " + v.getExecutionTime().get(Color.EO) + "\n");
            result.append("\t\tRUST execution time: " + v.getExecutionTime().get(Color.RUST) + "\n");
            result.append("\t\tColor: " + v.getColor() + "\n");
            result.append("\n");
            ++counter;
        }
        result.append("\tObjects:" + objects.size() + "\n");
        counter = 0;
        for (Integer weight : objects) {
            result.append("\t\tID: " + counter + "\n");
            result.append("\t\tWeight: " + weight + "\n");
            result.append("\n");
            ++counter;
        }
        result.append("\tProhibited to transform: ");
        for (Integer vertexId : prohibitedToTransform) {
            result.append(vertexId + " ");
        }
        return result.toString();
    }
}
