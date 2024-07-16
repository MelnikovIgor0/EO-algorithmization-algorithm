import alpha_algorithm.*;
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
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
                    assertTrue(gReversed.getVertices().get(neighbour)
                            .getChildIds().contains(vertexId));
                }
            }
            for (int vertexId = 0; vertexId < gReversed.getVertices().size(); ++vertexId) {
                for (int neighbour : gReversed.getVertices().get(vertexId).getChildIds()) {
                    assertTrue(g.getVertices().get(neighbour)
                            .getChildIds().contains(vertexId));
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    private boolean checkPathInGraphExists(Graph g,
                                           ArrayList<Boolean> used,
                                           int currentVertex,
                                           int finishVertex) {
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

    private boolean checkPathInGraphExists(Graph g,
                                           int startVertexId,
                                           int finishVertexId) {
        ArrayList<Boolean> used = new ArrayList<Boolean>();
        for (int i = 0; i < g.getVertices().size(); ++i) {
            used.add(false);
        }
        return checkPathInGraphExists(g, used, startVertexId, finishVertexId);
    }

    private HashSet<Integer> getFragment(Graph g,
                                         Graph gCopy,
                                         int startVertex,
                                         int finishVertex) {
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

    private HashSet<Integer> getObjectsUsedInDescendants(Graph g,
                                                         int vertexId) {
        HashSet<Integer> objectsInDescendants = new HashSet<Integer>();
        for (int otherVertexId = 0; otherVertexId < g.getVertices().size(); ++otherVertexId) {
            if (vertexId != otherVertexId &&
                    checkPathInGraphExists(g, vertexId, otherVertexId)) {
                for (Integer objectId : g.getVertices()
                        .get(otherVertexId).getObjectsToRead()) {
                    objectsInDescendants.add(objectId);
                }
                for (Integer objectId : g.getVertices()
                        .get(otherVertexId).getObjectsToWrite()) {
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testGetVertexDescendants(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {

                ArrayList<Integer> descendants = algorithm.getVertexDescendants(vertexId);
                for (int finishVertexId = 0; finishVertexId <
                        g.getVertices().size(); ++finishVertexId) {
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testSetObjectsUsedInDescendants(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < algorithm.graph.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(vertexId,
                        vertexId,
                        used2,
                        objectsUsedInDescendants);
            }
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                assertEquals(getObjectsUsedInDescendants(g, vertexId),
                        objectsUsedInDescendants.get(vertexId));
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testSetFragment(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int startVertex = 0; startVertex < g.getVertices().size();
                 ++startVertex) {
                for (int finishVertex = 0; finishVertex < g.getVertices().size();
                     ++finishVertex) {
                    if (checkPathInGraphExists(g, startVertex, finishVertex)) {
                        ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                        for (int i = 0; i < algorithm.graph.getVertices().size(); ++i) {
                            used2.add(false);
                        }
                        HashSet<Integer> currentFragment = new HashSet<Integer>();
                        algorithm.setFragment(startVertex,
                                finishVertex,
                                currentFragment,
                                used2
                        );

                        assertEquals(currentFragment,
                                getFragment(g,
                                        Graph.ParseGraph(inputFilePath),
                                        startVertex,
                                        finishVertex));
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testCheckVertexInLoop(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {

                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int i = 0; i < algorithm.graph.getVertices().size(); ++i) {
                    used2.add(false);
                }

                boolean hasLoop = false;
                for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                    if (checkPathInGraphExists(g, childId, vertexId)) {
                        hasLoop = true;
                        break;
                    }
                }

                assertEquals(hasLoop,
                        algorithm.checkVertexInLoop(vertexId,
                                vertexId,
                                used2
                        )
                );
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
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
            HashSet<Integer> verticesInLoopsByAlgorithm = algorithm.setVerticesInLoops();
            assertEquals(verticesInLoops, verticesInLoopsByAlgorithm);
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testSetFragmentRequirements(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            ArrayList<HashSet<Integer>> currentRequirements =
                    new ArrayList<HashSet<Integer>>();
            Graph gReversed = algorithm.buildReversedGraph(g);
            algorithm.graphReversed = gReversed;
            algorithm.graph = g;
            ArrayList<HashSet<Integer>> requirementsByAlgorithm =
                    algorithm.setFragmentRequirements();

            for (int vertexId = 0; vertexId < g.getVertices().size(); ++vertexId) {
                if (gReversed.getVertices().get(vertexId).getChildIds().size() > 1 &&
                        g.getVertices().get(vertexId).getChildIds().size() > 0) {
                    currentRequirements.add(new HashSet<Integer>());
                    currentRequirements.get(currentRequirements.size() - 1).add(vertexId);
                    for (Integer parentId : gReversed.getVertices()
                            .get(vertexId).getChildIds()) {
                        currentRequirements.get(currentRequirements.size() - 1)
                                .add(parentId);
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
                        currentRequirements.get(currentRequirements.size() - 1)
                                .add(vertexId);
                        for (Integer childId : g.getVertices().get(vertexId)
                                .getChildIds()) {
                            currentRequirements.get(currentRequirements.size() - 1)
                                    .add(childId);
                        }
                    }
                }
            }

            assertEquals(currentRequirements.size(),
                    requirementsByAlgorithm.size());
            for (HashSet<Integer> requirement : requirementsByAlgorithm) {
                assertTrue(currentRequirements.contains(requirement));
            }
            for (HashSet<Integer> requirement : currentRequirements) {
                assertTrue(requirementsByAlgorithm.contains(requirement));
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testGetFragmentExportObjectsWeight(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;

            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i,
                        i,
                        used2,
                        objectsUsedInDescendants
                );
            }

            for (int startVertexId = 0; startVertexId < g.getVertices().size();
                 ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size();
                     ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath),
                                startVertexId,
                                finishVertexId);
                        HashSet<Integer> currentObjectsUsedInDescendants =
                                new HashSet<Integer>();
                        for (Integer vertexId : currentFragment) {
                            for (Integer objectId : g.getVertices().
                                    get(vertexId).getObjectsToWrite()) {
                                if (getObjectsUsedInDescendants(g, vertexId)
                                        .contains(objectId)) {
                                    currentObjectsUsedInDescendants.add(objectId);
                                }
                            }
                        }
                        int totalWeight = 0;
                        for (Integer objectId : currentObjectsUsedInDescendants) {
                            totalWeight += g.getObjectWeight().get(objectId);
                        }
                        assertEquals(totalWeight,
                                algorithm.getFragmentExportObjectsWeight(currentFragment,
                                        objectsUsedInDescendants));
                    }
                }
            }
        } catch (IOException exc){
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    private Boolean fillNumberPrevObjectUsage(Integer vertexId,
                                   Graph g,
                                   ArrayList<Boolean> used,
                                   ArrayList<Integer> numberPrevObjectUsage) {
        used.set(vertexId, true);
        for (Integer objectId : g.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }
        for (Integer objectId : g.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }

        for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
            if (!used.get(childId)) {
                if (fillNumberPrevObjectUsage(childId, g, used, numberPrevObjectUsage)) {
                    return true;
                }
            }
        }

        for (Integer objectId : g.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
        for (Integer objectId : g.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
        return false;
    }

    private ArrayList<Integer> getNumberPrevObjectUsage(Graph g) {
        ArrayList<Integer> answer = new ArrayList<Integer>();
        ArrayList<Boolean> used = new ArrayList<Boolean>();
        for (int i = 0; i < g.getVertices().size(); ++i) {
            answer.add(0);
            used.add(false);
        }
        fillNumberPrevObjectUsage(0, g, used, answer);
        return answer;
    }

    @Timeout(Long.MAX_VALUE)
    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testGetFragmentImportObjectsWeight(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;

            for (int startVertexId = 0; startVertexId < g.getVertices().size();
                 ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size();
                     ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath),
                                startVertexId,
                                finishVertexId);

                        int totalWeight = 0;

                        assertEquals(totalWeight,
                                algorithm.getFragmentImportObjectsWeight(currentFragment,
                                        getNumberPrevObjectUsage(g)));
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testGetFragmentTotalExecutionTime(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            for (int startVertexId = 0; startVertexId < g.getVertices().size();
                 ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size();
                     ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath),
                                startVertexId,
                                finishVertexId);
                        int totalExecutionTimeEO = 0, totalExecutionTimeRust = 0;
                        for (Integer vertexId : currentFragment) {
                            totalExecutionTimeEO +=
                                    g.getVertices().get(vertexId).getExecutionTime()
                                            .get(Color.EO);
                            totalExecutionTimeRust +=
                                    g.getVertices().get(vertexId).getExecutionTime()
                                            .get(Color.RUST);
                        }
                        assertEquals(totalExecutionTimeEO,
                                algorithm.getFragmentTotalExecutionTime(Color.EO,
                                        currentFragment)
                        );
                        assertEquals(totalExecutionTimeRust,
                                algorithm.getFragmentTotalExecutionTime(Color.RUST,
                                        currentFragment)
                        );
                    }
                }
            }
        } catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    private Boolean checkIfGivenSubFragmentCorrect(Graph g,
                                                   HashSet<Integer> currentFragment,
                                                   Integer startVertexId,
                                                   Integer finishVertexId) {
        if (currentFragment.contains(0)) {
            return false;
        }
        for (Integer vertexId : g.getProhibitedToTransform()) {
            if (currentFragment.contains(vertexId)) {
                return false;
            }
        }
        for (Integer vertexId : currentFragment) {
            if (g.getVertices().get(vertexId).getChildIds().size() == 0) {
                return false;
            }
        }
        for (HashSet<Integer> requirement : algorithm.setFragmentRequirements()) {
            int amount = 0;
            for (Integer vertexId : requirement) {
                if (currentFragment.contains(vertexId)) {
                    ++amount;
                }
            }
            if (amount > 0 && amount < currentFragment.size()) {
                return false;
            }
        }

        Graph gr = algorithm.buildReversedGraph(g);

        int numberVerticesTurningOutFragment = 0;
        int numberVerticesInputtingOutFragment = 0;
        for (Integer vertexId : currentFragment) {
            if (gr.getVertices().get(vertexId).getChildIds().size() > 1) {
                for (Integer parentId : gr.getVertices().get(vertexId).getChildIds()) {
                    if (!currentFragment.contains(parentId)) {
                        return false;
                    }
                }
            }
            boolean turningOut = false;
            boolean inputtingOut = false;
            for (Integer childId : g.getVertices().get(vertexId).getChildIds()) {
                if (!currentFragment.contains(childId) &&
                        g.getVertices().get(childId).getChildIds().size() != 0) {
                    turningOut = true;
                    break;
                }
            }
            for (Integer childId : gr.getVertices().get(vertexId).getChildIds()) {
                if (!currentFragment.contains(childId)) {
                    inputtingOut = true;
                    break;
                }
            }
            if (turningOut) {
                ++numberVerticesTurningOutFragment;
            }
            if (inputtingOut) {
                ++numberVerticesInputtingOutFragment;
            }
        }
        if (
                (
                        (numberVerticesTurningOutFragment < 2 &&
                                g.getVertices().get(finishVertexId)
                                        .getChildIds().size() != 0) ||
                                (numberVerticesTurningOutFragment == 0 &&
                                        g.getVertices().get(finishVertexId)
                                                .getChildIds().size() == 0)
                ) &&
                        (
                                (numberVerticesInputtingOutFragment < 2 &&
                                        !startVertexId.equals(0)) ||
                                        (numberVerticesInputtingOutFragment == 0)
                        )
        ) {
            boolean isLoopPart = false;
            for (Integer vertexId : currentFragment) {
                if (algorithm.setVerticesInLoops().contains(vertexId)) {
                    isLoopPart = true;
                    break;
                }
            }
            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i,
                        i,
                        used2,
                        objectsUsedInDescendants
                );
            }
            Double profit = algorithm.getFragmentProfit(isLoopPart,
                    currentFragment,
                    getNumberPrevObjectUsage(g),
                    objectsUsedInDescendants);
            return (profit > 0);
        }
        return false;
    }

    @Timeout(Long.MAX_VALUE)
    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testCheckingIfSubFragmentCorrect(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            algorithm.graphReversed = algorithm.buildReversedGraph(g);
            algorithm.alpha_coeff = 1.0;
            algorithm.beta_coeff = 1.0;
            algorithm.gamma_coeff = 0.8;

            ArrayList <Integer> numberPrevObjectUsage = new ArrayList<Integer>();
            for (int i = 0; i < g.getObjectWeight().size(); ++i) {
                numberPrevObjectUsage.add(0);
            }
            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i,
                        i,
                        used2,
                        objectsUsedInDescendants
                );
            }
            ArrayList<HashSet<Integer>> fragmentRequirements =
                    algorithm.setFragmentRequirements();
            HashSet<Integer> verticesInLoops = algorithm.setVerticesInLoops();

            ArrayList<Boolean> used = new ArrayList<Boolean>();
            for (int i = 0; i < g.getVertices().size(); ++i) {
                used.add(false);
            }

            HashSet<AlphaAlgorithm.SubFragment> subFragments =
                    new HashSet<AlphaAlgorithm.SubFragment>();
            algorithm.findSubFragments(0,
                    used,
                    subFragments,
                    numberPrevObjectUsage,
                    objectsUsedInDescendants,
                    fragmentRequirements,
                    verticesInLoops);

            for (int startVertexId = 0; startVertexId < g.getVertices().size();
                 ++startVertexId) {
                for (int finishVertexId = 0; finishVertexId < g.getVertices().size();
                     ++finishVertexId) {
                    if (checkPathInGraphExists(g, startVertexId, finishVertexId)) {
                        HashSet<Integer> currentFragment = getFragment(g,
                                Graph.ParseGraph(inputFilePath),
                                startVertexId,
                                finishVertexId);
                        boolean ok = true;
                        if (checkIfGivenSubFragmentCorrect(g,
                                        currentFragment,
                                        startVertexId,
                                        finishVertexId)
                                ) {
                            ok = true;
                            for (AlphaAlgorithm.SubFragment x : subFragments) {
                                if (x.vertices.equals(currentFragment)) {
                                    ok = true;
                                    break;
                                }
                            }
                        } else {
                            ok = false;
                            for (AlphaAlgorithm.SubFragment x : subFragments) {
                                if (x.vertices.equals(currentFragment)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                        assertEquals(ok,
                                checkIfGivenSubFragmentCorrect(g,
                                        currentFragment,
                                        startVertexId,
                                        finishVertexId
                                )
                        );
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
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testBuildCliqueGraph(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            algorithm.graphReversed = algorithm.buildReversedGraph(g);
            algorithm.alpha_coeff = 1.0;
            algorithm.beta_coeff = 1.0;
            algorithm.gamma_coeff = 0.8;

            ArrayList <Integer> numberPrevObjectUsage = new ArrayList<Integer>();
            for (int i = 0; i < g.getObjectWeight().size(); ++i) {
                numberPrevObjectUsage.add(0);
            }
            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i,
                        i,
                        used2,
                        objectsUsedInDescendants
                );
            }
            ArrayList<HashSet<Integer>> fragmentRequirements =
                    algorithm.setFragmentRequirements();
            HashSet<Integer> verticesInLoops = algorithm.setVerticesInLoops();

            ArrayList<Boolean> used = new ArrayList<Boolean>();
            for (int i = 0; i < g.getVertices().size(); ++i) {
                used.add(false);
            }

            HashSet<AlphaAlgorithm.SubFragment> subFragments =
                    new HashSet<AlphaAlgorithm.SubFragment>();
            algorithm.findSubFragments(0,
                    used,
                    subFragments,
                    numberPrevObjectUsage,
                    objectsUsedInDescendants,
                    fragmentRequirements,
                    verticesInLoops);
            ArrayList<AlphaAlgorithm.SubFragment> fragments =
                    new ArrayList<AlphaAlgorithm.SubFragment>(subFragments);
            ArrayList<HashSet<Integer>> algorithmClique
                    = algorithm.buildCliqueGraph(fragments);

            for (int i = 0; i < subFragments.size(); ++i) {
                for (int j = 0; j < subFragments.size(); ++j) {
                    if (i == j) {
                        continue;
                    }
                    boolean ok = false;
                    for (int k : fragments.get(i).vertices) {
                        if (fragments.get(j).vertices.contains(k)) {
                            ok = true;
                            break;
                        }
                    }
                    assertEquals(algorithmClique.get(i).contains(j), ok);
                }
            }
        }
        catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testConvertCliqueToGraph(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            algorithm.graph = g;
            algorithm.graphReversed = algorithm.buildReversedGraph(g);
            algorithm.alpha_coeff = 1.0;
            algorithm.beta_coeff = 1.0;
            algorithm.gamma_coeff = 0.8;

            ArrayList <Integer> numberPrevObjectUsage = new ArrayList<Integer>();
            for (int i = 0; i < g.getObjectWeight().size(); ++i) {
                numberPrevObjectUsage.add(0);
            }
            ArrayList<HashSet<Integer>> objectsUsedInDescendants =
                    new ArrayList<HashSet<Integer>>();
            for (Integer i = 0; i < g.getVertices().size(); ++i) {
                objectsUsedInDescendants.add(new HashSet<Integer>());
                ArrayList<Boolean> used2 = new ArrayList<Boolean>();
                for (int j = 0; j < g.getVertices().size(); ++j) {
                    used2.add(false);
                }
                algorithm.setObjectsUsedInDescendants(i,
                        i,
                        used2,
                        objectsUsedInDescendants
                );
            }
            ArrayList<HashSet<Integer>> fragmentRequirements =
                    algorithm.setFragmentRequirements();
            HashSet<Integer> verticesInLoops =
                    algorithm.setVerticesInLoops();

            ArrayList<Boolean> used = new ArrayList<Boolean>();
            for (int i = 0; i < g.getVertices().size(); ++i) {
                used.add(false);
            }

            HashSet<AlphaAlgorithm.SubFragment> subFragments =
                    new HashSet<AlphaAlgorithm.SubFragment>();
            algorithm.findSubFragments(0,
                    used,
                    subFragments,
                    numberPrevObjectUsage,
                    objectsUsedInDescendants,
                    fragmentRequirements,
                    verticesInLoops);
            ArrayList<AlphaAlgorithm.SubFragment> fragments =
                    new ArrayList<AlphaAlgorithm.SubFragment>(subFragments);
            ArrayList<HashSet<Integer>> cliqueGraph =
                    algorithm.buildCliqueGraph(fragments);
            ArrayList<Double> cliqueWeights = new ArrayList<Double>();
            for (AlphaAlgorithm.SubFragment fragment : fragments) {
                cliqueWeights.add(fragment.profit);
            }
            CliqueTaskSolver solver = new FullSearchCliqueTaskSolver();
            ArrayList<Integer> result = solver.cliqueSolve(cliqueGraph,
                    cliqueWeights
            );
            ArrayList<Fragment> result_fragments =
                    algorithm.convertToFragments(result, fragments);

            ArrayList<Fragment> fragments_val = new ArrayList<>();
            for (Integer cliqueVertex : result) {
                fragments_val.add(new Fragment(new ArrayList<Integer>()));
                for (Integer vertexId : fragments.get(cliqueVertex).vertices) {
                    fragments_val.get(fragments_val.size() - 1)
                            .getVertices().add(vertexId);
                }
            }
            assertEquals(result_fragments.hashCode(), fragments_val.hashCode());
        }
        catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test_simple.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test1_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test2_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test4_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test5_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v2.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v3.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v4.xml",
            "..\\..\\code\\alpha_algorithm\\src\\test\\java\\resources\\test6_v5.xml"
    })
    void testAlgorithmize(String inputFilePath) {
        try {
            Graph g = Graph.ParseGraph(inputFilePath);
            ArrayList<Fragment> result = algorithm.algorithmize(g,
                    1.0,
                    1.0,
                    0.8,
                    new FullSearchCliqueTaskSolver()
            );
            for (int i = 0; i < result.size(); ++i) {
                for (int vertexId : result.get(i).getVertices()) {
                    for (int j = 0; j < result.size(); ++j) {
                        if (i != j && result.get(j).getVertices().contains(vertexId)) {
                            fail("Intersections in fragments found");
                        }
                    }
                }
            }
            for (Fragment f : result) {
                if (f.getVertices().contains(0)) {
                    fail("Found fragment, which contains root of CFG");
                }
            }
            for (Fragment f : result) {
                for (int vertexId : f.getVertices()) {
                    if (g.getVertices().get(vertexId).getChildIds().isEmpty()) {
                        fail("Found fragment, which contains sink");
                    }
                }
            }
            for (Fragment f : result) {
                for (int vertexId : f.getVertices()) {
                    if (g.getProhibitedToTransform().contains(vertexId)) {
                        fail("Found fragment, which contains prohibited to" +
                                "transformation vertex");
                    }
                }
            }
        }
        catch (IOException exc) {
            fail("IO exception\nException:\n" + exc.getMessage());
        }
    }
}
