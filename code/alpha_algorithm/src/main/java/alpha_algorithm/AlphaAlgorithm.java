package alpha_algorithm;

import graph.Color;
import graph.Graph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;

import java.lang.Math;

public class AlphaAlgorithm implements AlgorithmizationAlgorithm {
    private static class SubFragment {
        public Double profit;
        public HashSet<Integer> vertices;

        public SubFragment(Double profit, HashSet<Integer> vertices) {
            this.profit = profit;
            this.vertices = (HashSet<Integer>)vertices.clone();
        }

        @Override
        public int hashCode() {
            return profit.hashCode() * 37 + vertices.hashCode() * 31;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final SubFragment other = (SubFragment)obj;
            if (Math.abs(profit - other.profit) > 1e-7) {
                return false;
            }
            if (vertices.size() != other.vertices.size()) {
                return false;
            }
            for (Integer vertex : vertices) {
                if (!other.vertices.contains(vertex)) {
                    return false;
                }
            }
            return true;
        }
    }
    private Graph graph;
    private Graph graphReversed;
    private ArrayList<Boolean> used;
    private ArrayList<Boolean> used2;
    private Double alpha_coeff;
    private Double beta_coeff;
    private Double gamma_coeff;
    private HashSet<SubFragment> subFragments;
    private ArrayList<Integer> numberPrevObjectUsage;
    private ArrayList<Integer> currentDescendants;
    private HashSet<Integer> currentFragment;
    private ArrayList<HashSet<Integer>> objectsUsedInDescendants;
    private ArrayList<HashSet<Integer>> cliqueGraph;
    private ArrayList<HashSet<Integer>> fragmentRequirements;
    private HashSet<Integer> verticesInLoops;

    public AlphaAlgorithm() {
    }

    private Graph buildReversedGraph() {
        ArrayList<ArrayList<Integer>> matchesReversed = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            matchesReversed.add(new ArrayList<>());
        }
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            for (Integer j : graph.getVertices().get(i).getChildIds()) {
                matchesReversed.get(j).add(i);
            }
        }
        ArrayList<Vertex> reversedVertices = new ArrayList<Vertex>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            reversedVertices.add(new Vertex(
                    graph.getVertices().get(i).getObjectsToRead(),
                    graph.getVertices().get(i).getObjectsToWrite(),
                    graph.getVertices().get(i).getExecutionTime(),
                    matchesReversed.get(i),
                    graph.getVertices().get(i).getColor()
            ));
        }
        return new Graph(reversedVertices, graph.getObjectWeight(), graph.getProhibitedToTransform());
    }

    private void findDescendants(Integer vertexId) {
        used2.set(vertexId, true);
        currentDescendants.add(vertexId);
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used2.get(childId)) {
                findDescendants(childId);
            }
        }
    }

    private ArrayList<Integer> getVertexDescendants(Integer vertexId) {
        currentDescendants = new ArrayList<Integer>();
        used2 = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used2.add(false);
        }
        findDescendants(vertexId);
        return currentDescendants;
    }

    private void setObjectsUsedInDescendants(Integer vertexId, Integer rootId) {
        used2.set(vertexId, true);
        if (!vertexId.equals(rootId)) {
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
                objectsUsedInDescendants.get(rootId).add(objectId);
            }
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
                objectsUsedInDescendants.get(rootId).add(objectId);
            }
        }
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used2.get(childId)) {
                setObjectsUsedInDescendants(childId, rootId);
            }
        }
    }

    private void setFragment(Integer vertexId, Integer finishVertex) {
        used2.set(vertexId, true);
        if (graph.getVertices().get(vertexId).getChildIds().size() != 0) currentFragment.add(vertexId);
        if (vertexId.equals(finishVertex)) {
            return;
        }
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used2.get(childId)) {
                setFragment(childId, finishVertex);
            }
        }
    }

    private void checkSubFragment(Integer startVertexId, Integer finishVertexId) {
        used2 = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used2.add(false);
        }
        currentFragment = new HashSet<Integer>();
        setFragment(startVertexId, finishVertexId);
        if (currentFragment.contains(0)) {
            return;
        }
        for (Integer vertexId : currentFragment) {
            if (graph.getVertices().get(vertexId).getChildIds().size() == 0) {
                return;
            }
        }
        for (HashSet<Integer> requirement : fragmentRequirements) {
            int kol = 0;
            for (Integer vertexId : requirement) {
                if (currentFragment.contains(vertexId)) {
                    ++kol;
                }
            }
            if (kol != 0 && kol != requirement.size()) {
                return;
            }
        }
        for (Integer vertexId : currentFragment) {
            if (graph.getProhibitedToTransform().contains(vertexId)) {
                return;
            }
        }
        int numberVerticesTurningOutFragment = 0;
        int numberVerticesInputtingOutFragment = 0;
        for (Integer vertexId : currentFragment) {
            if (graphReversed.getVertices().get(vertexId).getChildIds().size() > 1) {
                for (Integer parentId : graphReversed.getVertices().get(vertexId).getChildIds()) {
                    if (!currentFragment.contains(parentId)) {
                        return;
                    }
                }
            }
            boolean turningOut = false;
            boolean inputtingOut = false;
            for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
                if (!currentFragment.contains(childId) && graph.getVertices().get(childId).getChildIds().size() != 0) {
                    turningOut = true;
                    break;
                }
            }
            for (Integer childId : graphReversed.getVertices().get(vertexId).getChildIds()) {
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
                        (numberVerticesTurningOutFragment < 2 && graph.getVertices().get(finishVertexId).getChildIds().size() != 0) ||
                                (numberVerticesTurningOutFragment == 0 && graph.getVertices().get(finishVertexId).getChildIds().size() == 0)
                ) &&
                (
                        (numberVerticesInputtingOutFragment < 2 && !startVertexId.equals(0)) ||
                        (numberVerticesInputtingOutFragment == 0 && startVertexId.equals(0))
                )
        ) {
            boolean isLoopPart = false;
            for (Integer vertexId : currentFragment) {
                if (verticesInLoops.contains(vertexId)) {
                    isLoopPart = true;
                    break;
                }
            }
            Double profit = getFragmentProfit(finishVertexId, isLoopPart);
            if (profit > 0) {
                subFragments.add(new SubFragment(profit, currentFragment));
            }
        }
    }

    private Boolean checkVertexInLoop(Integer vertexId, Integer startVertexId) {
        used2.set(vertexId, true);
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (childId.equals(startVertexId)) {
                return true;
            }
            if (!used2.get(childId) && checkVertexInLoop(childId, startVertexId)) {
                return true;
            }
        }
        return false;
    }

    private void setVerticesInLoops() {
        verticesInLoops = new HashSet<Integer>();
        for (int vertexId = 0; vertexId < graph.getVertices().size(); ++vertexId) {
            used2 = new ArrayList<Boolean>();
            for (int i = 0; i < graph.getVertices().size(); ++i) {
                used2.add(false);
            }
            if (checkVertexInLoop(vertexId, vertexId)) {
                verticesInLoops.add(vertexId);
            }
        }
    }

    private void setFragmentRequirements() {
        fragmentRequirements = new ArrayList<HashSet<Integer>>();
        for (int vertexId = 0; vertexId < graph.getVertices().size(); ++vertexId) {
            if (graphReversed.getVertices().get(vertexId).getChildIds().size() > 1 &&
                    graph.getVertices().get(vertexId).getChildIds().size() > 0) {
                fragmentRequirements.add(new HashSet<Integer>());
                fragmentRequirements.get(fragmentRequirements.size() - 1).add(vertexId);
                for (Integer parentId : graphReversed.getVertices().get(vertexId).getChildIds()) {
                    fragmentRequirements.get(fragmentRequirements.size() - 1).add(parentId);
                }
            }
            if (graph.getVertices().get(vertexId).getChildIds().size() > 1) {
                int numberNotLeafs = 0;
                for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
                    if (graph.getVertices().get(childId).getChildIds().size() > 0) {
                        ++numberNotLeafs;
                    }
                }
                if (numberNotLeafs > 1) {
                    fragmentRequirements.add(new HashSet<Integer>());
                    fragmentRequirements.get(fragmentRequirements.size() - 1).add(vertexId);
                    for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
                        fragmentRequirements.get(fragmentRequirements.size() - 1).add(childId);
                    }
                }
            }
        }
    }

    private Integer getFragmentImportObjectsWeight() {
        HashSet<Integer> fragmentObjects = new HashSet<Integer>();
        for (Integer vertexId : currentFragment) {
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
                if (numberPrevObjectUsage.get(objectId) > 0) {
                    fragmentObjects.add(objectId);
                }
            }
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
                if (numberPrevObjectUsage.get(objectId) > 0) {
                    fragmentObjects.add(objectId);
                }
            }
        }
        Integer total = 0;
        for (Integer objectId : fragmentObjects) {
            total += graph.getObjectWeight().get(objectId);
        }
        return total;
    }

    private Integer getFragmentExportObjectsWeight(Integer finishVertex) {
        HashSet<Integer> fragmentObjects = new HashSet<Integer>();
        for (Integer vertexId : currentFragment) {
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
                if (objectsUsedInDescendants.get(vertexId).contains(objectId)) {
                    fragmentObjects.add(objectId);
                }
            }
            for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
                if (objectsUsedInDescendants.get(vertexId).contains(objectId)) {
                    fragmentObjects.add(objectId);
                }
            }
        }
        Integer totalWeight = 0;
        for (Integer objectId : fragmentObjects) {
            totalWeight += graph.getObjectWeight().get(objectId);
        }
        return totalWeight;
    }

    private Double getFragmentTotalExecutionTime(Color color) {
        Double total = 0.0;
        for (Integer vertexId : currentFragment) {
            total += graph.getVertices().get(vertexId).getExecutionTime().get(color);
        }
        return total;
    }

    private Double getFragmentProfit(Integer finishVertexId, Boolean isLoopPart) {
        Integer transportingWeight = (getFragmentImportObjectsWeight() + getFragmentExportObjectsWeight(finishVertexId));
        if (isLoopPart) {
            return getFragmentTotalExecutionTime(Color.EO) -
                    getFragmentTotalExecutionTime(Color.RUST) -
                    gamma_coeff * (alpha_coeff + beta_coeff * transportingWeight);
        }
        return getFragmentTotalExecutionTime(Color.EO) -
                getFragmentTotalExecutionTime(Color.RUST) -
                (alpha_coeff + beta_coeff * transportingWeight);
    }

    private void findSubFragments(Integer vertexId) {
        used.set(vertexId, true);
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }

        for (Integer descendantId : getVertexDescendants(vertexId)) {
            checkSubFragment(vertexId, descendantId);
        }

        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used.get(childId)) {
                findSubFragments(childId);
            }
        }

        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
    }

    private void buildCliqueGraph(ArrayList<SubFragment> fragments) {
        cliqueGraph = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < subFragments.size(); ++i) {
            cliqueGraph.add(new HashSet<Integer>());
        }
        for (int i = 0; i < fragments.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                boolean intersection = false;
                for (Integer vertex : fragments.get(i).vertices) {
                    if (fragments.get(j).vertices.contains(vertex)) {
                        intersection = true;
                        break;
                    }
                }
                if (intersection) {
                    cliqueGraph.get(i).add(j);
                    cliqueGraph.get(j).add(i);
                }
            }
        }
    }

    private ArrayList<Fragment> convertToFragments(ArrayList<Integer> cliqueVertices, ArrayList<SubFragment> subFragments) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (Integer cliqueVertex : cliqueVertices) {
            fragments.add(new Fragment(new ArrayList<Integer>()));
            for (Integer vertexId : subFragments.get(cliqueVertex).vertices) {
                fragments.get(fragments.size() - 1).getVertices().add(vertexId);
            }
        }
        return fragments;
    }

    @Override
    public ArrayList<Fragment> algorithmize(Graph graph, Double alpha_coeff, Double beta_coeff, Double gamma_coeff, CliqueTaskSolver solver) {
        this.graph = graph;
        this.graphReversed = buildReversedGraph();
        this.alpha_coeff = alpha_coeff;
        this.beta_coeff = beta_coeff;
        this.gamma_coeff = gamma_coeff;
        subFragments = new HashSet<SubFragment>();
        used = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used.add(false);
        }
        numberPrevObjectUsage = new ArrayList<Integer>();
        for (int i = 0; i < graph.getObjectWeight().size(); ++i) {
            numberPrevObjectUsage.add(0);
        }
        objectsUsedInDescendants = new ArrayList<HashSet<Integer>>();
        for (Integer i = 0; i < graph.getVertices().size(); ++i) {
            objectsUsedInDescendants.add(new HashSet<Integer>());
            used2 = new ArrayList<Boolean>();
            for (int j = 0; j < graph.getVertices().size(); ++j) {
                used2.add(false);
            }
            setObjectsUsedInDescendants(i, i);
        }
        setFragmentRequirements();
        setVerticesInLoops();
        findSubFragments(0);
        ArrayList<SubFragment> fragments = new ArrayList<SubFragment>(subFragments);
        buildCliqueGraph(fragments);
        ArrayList<Double> cliqueWeights = new ArrayList<Double>();
        for (SubFragment fragment : fragments) {
            cliqueWeights.add(fragment.profit);
        }
        ArrayList<Integer> result = solver.cliqueSolve(cliqueGraph, cliqueWeights);
        return convertToFragments(result, fragments);
    }
}
