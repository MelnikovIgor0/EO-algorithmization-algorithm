import alpha_algorithm.AlphaAlgorithm;
import graph.Color;
import graph.Graph;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class TestAlphaAlgorithm {
    private AlphaAlgorithm algorithm;

    @BeforeEach
    void setAlphaAlgorithm() {
        algorithm = new AlphaAlgorithm();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testBuildReversedGraph(String testInputPath) {
        try {
            Graph g = Graph.ParseGraph(testInputPath);
            Graph gReversed = algorithm.buildReversedGraph(g);
            assertEquals(g.getVertices().size(), gReversed.getVertices().size());
            assertEquals(g.getObjectWeight(), gReversed.getObjectWeight());
            assertEquals(g.getProhibitedToTransform(), gReversed.getProhibitedToTransform());
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                for (int neighbour : g.getVertices().get(vertexId).getChildIds()) {
                    assertTrue(gReversed.getVertices().get(neighbour).getChildIds().contains(vertexId));
                }
            }
            for (int vertexId = 0; vertexId < gReversed.getVertices().size(); ++vertexId) {
                for (int neighbour : gReversed.getVertices().get(vertexId).getChildIds()) {
                    assertTrue(g.getVertices().get(neighbour).getChildIds().contains(vertexId));
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    private boolean checkPathInGraphExists(Graph g, ArrayList<Boolean> used, int currentVertex, int finishVertex) {
        if (currentVertex == finishVertex) {
            return true;
        }
        used.set(currentVertex, true);
        for (int childId : g.getVertices().get(currentVertex).getChildIds()) {
            if (!used.get(childId)) {
                if (checkPathInGraphExists(g, used, childId, finishVertex)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPathInGraphExists(Graph g, int startVertexId, int finishVertexId) {
        ArrayList<Boolean> used = new ArrayList<Boolean>();
        for (int i = 0; i < g.getVertices().size(); ++i) {
            used.add(false);
        }
        return checkPathInGraphExists(g, used, startVertexId, finishVertexId);
    }

    private HashSet<Integer> getFragment(Graph g, Graph gCopy, int startVertex, int finishVertex) {
        gCopy.getVertices().get(finishVertex).getChildIds().clear();
        HashSet<Integer> currentFragment = new HashSet<Integer>();
        for (int possibleFragmentMember = 0;
             possibleFragmentMember < gCopy.getVertices().size();
             ++possibleFragmentMember) {
            if (checkPathInGraphExists(gCopy, startVertex, possibleFragmentMember) &&
                    g.getVertices().get(possibleFragmentMember).getChildIds().size() > 0) {
                currentFragment.add(possibleFragmentMember);
            }
        }
        return currentFragment;
    }

    private HashSet<Integer> getObjectsUsedInDescendants(Graph g, int vertexId) {
        HashSet<Integer> objectsInDescendants = new HashSet<Integer>();
        for (int otherVertexId = 0; otherVertexId < g.getVertices().size(); ++otherVertexId) {
            if (vertexId != otherVertexId &&
                    checkPathInGraphExists(g, vertexId, otherVertexId)) {
                for (Integer objectId : g.getVertices().get(otherVertexId).getObjectsToRead()) {
                    objectsInDescendants.add(objectId);
                }
                for (Integer objectId : g.getVertices().get(otherVertexId).getObjectsToWrite()) {
                    objectsInDescendants.add(objectId);
                }
            }
        }
        return objectsInDescendants;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testGetVertexDescendants(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                ArrayList<Integer> descendants = algorithm.getVertexDescendants(vertexId);
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size(); ++finishVertexId) {
                    assertEquals(descendants.contains(finishVertexId),
                            checkPathInGraphExists(g, vertexId, finishVertexId));
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testSetObjectsUsedInDescendants(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            algorithm.objectsUsedInDescendants = new ArrayList<HashSet<Integer>>();
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                algorithm.objectsUsedInDescendants.add(new HashSet<Integer>());
                algorithm.used2 = new ArrayList<Boolean>();
                for (int j = 0; j < algorithm.graph.getVertices().size(); ++j) {
                    algorithm.used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(vertexId, vertexId);
            }
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                assertEquals(getObjectsUsedInDescendants(g, vertexId),
                        algorithm.objectsUsedInDescendants.get(vertexId));
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @Timeout(Long.MAX_VALUE)
    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testSetFragment(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int startVertex = 0; startVertex < g.getVertices().size(); ++startVertex) {
                for (int finishVertex = 0; finishVertex < g.getVertices().size(); ++finishVertex) {
                    if (checkPathInGraphExists(g, startVertex, finishVertex)) {


                        algorithm.used2 = new ArrayList<Boolean>();
                        for (int i = 0; i < algorithm.graph.getVertices().size(); ++i) {
                            algorithm.used2.add(false);
                        }
                        algorithm.currentFragment = new HashSet<Integer>();
                        algorithm.setFragment(startVertex, finishVertex);

                        assertEquals(algorithm.currentFragment, getFragment(g, Graph.ParseGraph(inputFilePath), startVertex, finishVertex));
                    }
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testCheckVertexInLoop(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {

                algorithm.used2 = new ArrayList<Boolean>();
                for (int i = 0; i < algorithm.graph.getVertices().size(); ++i) {
                    algorithm.used2.add(false);
                }

                boolean hasLoop = false;
                for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                    if (checkPathInGraphExists(g, childId, vertexId)) {
                        hasLoop = true;
                        break;
                    }
                }

                assertEquals(hasLoop, algorithm.checkVertexInLoop(vertexId, vertexId));
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testSetVerticesInLoop(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            HashSet<Integer> verticesInLoops = new HashSet<Integer>();
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                    if (checkPathInGraphExists(g, childId, vertexId)) {
                        verticesInLoops.add(vertexId);
                        break;
                    }
                }
            }
            algorithm.setVerticesInLoops();
            assertEquals(verticesInLoops, algorithm.verticesInLoops);
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testSetFragmentRequirements(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            ArrayList<HashSet<Integer>> currentRequirements = new ArrayList<HashSet<Integer>>();
            Graph gReversed = algorithm.buildReversedGraph(g);
            algorithm.graphReversed = gReversed;
            algorithm.graph = g;
            algorithm.setFragmentRequirements();

            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                if (gReversed.getVertices().get(vertexId).getChildIds().size() > 1 &&
                        g.getVertices().get(vertexId).getChildIds().size() > 0) {
                    currentRequirements.add(new HashSet<Integer>());
                    currentRequirements.get(currentRequirements.size() - 1).add(vertexId);
                    for (Integer parentId : gReversed.getVertices().get(vertexId).getChildIds()) {
                        currentRequirements.get(currentRequirements.size() - 1).add(parentId);
                    }
                }
                if (g.getVertices().get(vertexId).getChildIds().size() > 1) {
                    int nonTerminals = 0;
                    for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                        if (g.getVertices().get(childId).getChildIds().size() > 0) {
                            ++nonTerminals;
                        }
                    }
                    if (nonTerminals > 1) {
                        currentRequirements.add(new HashSet<Integer>());
                        currentRequirements.get(currentRequirements.size() - 1).add(vertexId);
                        for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                            currentRequirements.get(currentRequirements.size() - 1).add(childId);
                        }
                    }
                }
            }

            assertEquals(currentRequirements.size(), algorithm.fragmentRequirements.size());
            for (HashSet<Integer> requirement : algorithm.fragmentRequirements) {
                assertTrue(currentRequirements.contains(requirement));
            }
            for (HashSet<Integer> requirement : currentRequirements) {
                assertTrue(algorithm.fragmentRequirements.contains(requirement));
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @Timeout(Long.MAX_VALUE)
    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testGetFragmentExportObjectsWeight(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;

            algorithm.objectsUsedInDescendants = new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                algorithm.objectsUsedInDescendants.add(new HashSet<Integer>());
                algorithm.used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    algorithm.used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i, i);
            }

            for (int startVertexId = 0; startVertexId < g.getVertices().size(); ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size(); ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath),
                                startVertexId,
                                finishVertexId);
                        HashSet<Integer> currentObjectsUsedInDescendants = new HashSet<Integer>();
                        for (Integer vertexId : currentFragment) {
                            for (Integer objectId : g.getVertices().get(vertexId).getObjectsToWrite()) {
                                if (getObjectsUsedInDescendants(g, vertexId).contains(objectId)) {
                                    currentObjectsUsedInDescendants.add(objectId);
                                }
                            }
                        }
                        int totalWeight = 0;
                        for (Integer objectId : currentObjectsUsedInDescendants) {
                            totalWeight += g.getObjectWeight().get(objectId);
                        }
                        algorithm.currentFragment = currentFragment;
                        assertEquals(totalWeight, algorithm.getFragmentExportObjectsWeight());
                    }
                }
            }
        } catch (IOException exc){
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml"
    })
    void testGetFragmentTotalExecutionTime(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int startVertexId = 0; startVertexId < g.getVertices().size(); ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size(); ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath), startVertexId, finishVertexId);
                        algorithm.currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath), startVertexId, finishVertexId);
                        int totalExecutionTimeEO = 0, totalExecutionTimeRust = 0;
                        for (Integer vertexId : currentFragment) {
                            totalExecutionTimeEO += g.getVertices().get(vertexId).getExecutionTime().get(Color.EO);
                            totalExecutionTimeRust += g.getVertices().get(vertexId).getExecutionTime().get(Color.RUST);
                        }
                        assertEquals(totalExecutionTimeEO, algorithm.getFragmentTotalExecutionTime(Color.EO));
                        assertEquals(totalExecutionTimeRust, algorithm.getFragmentTotalExecutionTime(Color.RUST));
                    }
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }
}
