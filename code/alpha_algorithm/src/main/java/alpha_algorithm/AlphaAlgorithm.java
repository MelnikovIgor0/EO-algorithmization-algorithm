package alpha_algorithm;

import graph.Color;
import graph.Graph;
import graph.Vertex;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

import java.lang.Math;

public class AlphaAlgorithm implements AlgorithmizationAlgorithm {
    public static class SubFragment {
        public Double profit;
        public HashSet<Integer> vertices;

        public SubFragment(Double profit,
                           HashSet<Integer> vertices) {
            this.profit = profit;
            this.vertices = (HashSet<Integer>)vertices.clone();
        }

        @Override
        public int hashCode() {
            return profit.hashCode() * 37 + vertices.hashCode() * 31;
        }
    }
    public Graph graph;
    public Graph graphReversed;
    public Double alpha_coeff;
    public Double beta_coeff;
    public Double gamma_coeff;

    public AlphaAlgorithm() {
    }

    public Graph buildReversedGraph(Graph graph) {
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
        return new Graph(reversedVertices,
                graph.getObjectWeight(),
                graph.getProhibitedToTransform());
    }

    public void findDescendants(Integer vertexId,
                                ArrayList<Integer> currentDescendants,
                                ArrayList<Boolean> used2) {
        used2.set(vertexId, true);
        currentDescendants.add(vertexId);
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used2.get(childId)) {
                findDescendants(childId, currentDescendants, used2);
            }
        }
    }

    public ArrayList<Integer> getVertexDescendants(Integer vertexId) {
        ArrayList<Integer> currentDescendants = new ArrayList<Integer>();
        ArrayList<Boolean> used2 = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used2.add(false);
        }
        findDescendants(vertexId, currentDescendants, used2);
        return currentDescendants;
    }

    public void setObjectsUsedInDescendants(Integer vertexId,
                                            Integer rootId,
                                            ArrayList<Boolean> used2,
                                            ArrayList<HashSet<Integer>> objectsUsedInDescendants) {
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
                setObjectsUsedInDescendants(childId,
                        rootId,
                        used2,
                        objectsUsedInDescendants);
            }
        }
    }

    public void setFragment(Integer vertexId,
                            Integer finishVertex,
                            HashSet<Integer> currentFragment,
                            ArrayList<Boolean> used2) {
        used2.set(vertexId, true);
        if (graph.getVertices().get(vertexId).getChildIds().size() != 0) {
            currentFragment.add(vertexId);
        }
        if (vertexId.equals(finishVertex)) {
            return;
        }
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used2.get(childId)) {
                setFragment(childId,
                        finishVertex,
                        currentFragment,
                        used2);
            }
        }
    }

    public Boolean checkSubFragment(Integer startVertexId,
                                 Integer finishVertexId,
                                 HashSet<SubFragment> subFragments,
                                 ArrayList<Integer> numberPrevObjectUsage,
                                 ArrayList<HashSet<Integer>> objectsUsedInDescendants,
                                 ArrayList<HashSet<Integer>> fragmentRequirements,
                                 HashSet<Integer> verticesInLoops) {
        ArrayList <Boolean> used2 = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used2.add(false);
        }
        HashSet<Integer> currentFragment = new HashSet<Integer>();
        setFragment(startVertexId, finishVertexId, currentFragment, used2);
        if (currentFragment.contains(0)) {
            return false;
        }
        for (HashSet<Integer> requirement : fragmentRequirements) {
            int kol = 0;
            for (Integer vertexId : requirement) {
                if (currentFragment.contains(vertexId)) {
                    ++kol;
                }
            }
            if (kol != 0 && kol != requirement.size()) {
                return false;
            }
        }
        for (Integer vertexId : currentFragment) {
            if (graph.getProhibitedToTransform().contains(vertexId)) {
                return false;
            }
        }
        int numberVerticesTurningOutFragment = 0;
        int numberVerticesInputtingOutFragment = 0;
        for (Integer vertexId : currentFragment) {
            boolean turningOut = false;
            boolean inputtingOut = false;
            for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
                if (!currentFragment.contains(childId) &&
                        graph.getVertices().get(childId).getChildIds().size() != 0) {
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
                        (numberVerticesTurningOutFragment < 2 &&
                                graph.getVertices().get(finishVertexId).getChildIds().size() != 0) ||
                                (numberVerticesTurningOutFragment == 0 &&
                                        graph.getVertices().get(finishVertexId).getChildIds().size() == 0)
                ) &&
                (
                        (numberVerticesInputtingOutFragment < 2 &&
                                !startVertexId.equals(0)) ||
                        (numberVerticesInputtingOutFragment == 0)
                )
        ) {
            boolean isLoopPart = false;
            for (Integer vertexId : currentFragment) {
                if (verticesInLoops.contains(vertexId)) {
                    isLoopPart = true;
                    break;
                }
            }
            Double profit = getFragmentProfit(isLoopPart,
                    currentFragment,
                    numberPrevObjectUsage,
                    objectsUsedInDescendants);
            if (profit > 0) {
                subFragments.add(new SubFragment(profit, currentFragment));
                return true;
            }
        }
        return false;
    }

    public Boolean checkVertexInLoop(Integer vertexId,
                                     Integer startVertexId,
                                     ArrayList<Boolean> used2) {
        used2.set(vertexId, true);
        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (childId.equals(startVertexId)) {
                return true;
            }
            if (!used2.get(childId) && checkVertexInLoop(childId, startVertexId, used2)) {
                return true;
            }
        }
        return false;
    }

    public HashSet<Integer> setVerticesInLoops() {
        HashSet<Integer> verticesInLoops = new HashSet<Integer>();
        for (int vertexId = 0; vertexId < graph.getVertices().size(); ++vertexId) {
            ArrayList<Boolean> used2 = new ArrayList<Boolean>();
            for (int i = 0; i < graph.getVertices().size(); ++i) {
                used2.add(false);
            }
            if (checkVertexInLoop(vertexId, vertexId, used2)) {
                verticesInLoops.add(vertexId);
            }
        }
        return verticesInLoops;
    }

    public ArrayList<HashSet<Integer>> setFragmentRequirements() {
        ArrayList<HashSet<Integer>> fragmentRequirements = new ArrayList<HashSet<Integer>>();
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
        return fragmentRequirements;
    }

    public Integer getFragmentImportObjectsWeight(HashSet<Integer> currentFragment,
                                                  ArrayList<Integer> numberPrevObjectUsage) {
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

    public Integer getFragmentExportObjectsWeight(HashSet<Integer> currentFragment,
                                                  ArrayList<HashSet<Integer>> objectsUsedInDescendants) {
        HashSet<Integer> fragmentObjects = new HashSet<Integer>();
        for (Integer vertexId : currentFragment) {
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

    public Double getFragmentTotalExecutionTime(Color color,
                                                HashSet<Integer> currentFragment) {
        Double total = 0.0;
        for (Integer vertexId : currentFragment) {
            total += graph.getVertices().get(vertexId).getExecutionTime().get(color);
        }
        return total;
    }

    public Double getFragmentProfit(Boolean isLoopPart,
                                    HashSet<Integer> currentFragment,
                                    ArrayList<Integer> numberPrevObjectUsage,
                                    ArrayList<HashSet<Integer>> objectsUsedInDescendants) {
        Integer transportingWeight = (getFragmentImportObjectsWeight(currentFragment,
                numberPrevObjectUsage) + getFragmentExportObjectsWeight(currentFragment,
                objectsUsedInDescendants));
        if (isLoopPart) {
            return getFragmentTotalExecutionTime(Color.EO, currentFragment) -
                    getFragmentTotalExecutionTime(Color.RUST, currentFragment) -
                    gamma_coeff * (alpha_coeff + beta_coeff * transportingWeight);
        }
        return getFragmentTotalExecutionTime(Color.EO, currentFragment) -
                getFragmentTotalExecutionTime(Color.RUST, currentFragment) -
                (alpha_coeff + beta_coeff * transportingWeight);
    }

    public void findSubFragments(Integer vertexId,
                                 ArrayList<Boolean> used,
                                 HashSet<SubFragment> subFragments,
                                 ArrayList<Integer> numberPrevObjectUsage,
                                 ArrayList<HashSet<Integer>> objectsUsedInDescendants,
                                 ArrayList<HashSet<Integer>> fragmentRequirements,
                                 HashSet<Integer> verticesInLoops) {
        used.set(vertexId, true);
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) + 1);
        }

        for (Integer descendantId : getVertexDescendants(vertexId)) {
            checkSubFragment(vertexId,
                    descendantId,
                    subFragments,
                    numberPrevObjectUsage,
                    objectsUsedInDescendants,
                    fragmentRequirements,
                    verticesInLoops);
        }

        for (Integer childId : graph.getVertices().get(vertexId).getChildIds()) {
            if (!used.get(childId)) {
                findSubFragments(childId,
                        used,
                        subFragments,
                        numberPrevObjectUsage,
                        objectsUsedInDescendants,
                        fragmentRequirements,
                        verticesInLoops);
            }
        }

        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToRead()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
        for (Integer objectId : graph.getVertices().get(vertexId).getObjectsToWrite()) {
            numberPrevObjectUsage.set(objectId, numberPrevObjectUsage.get(objectId) - 1);
        }
    }

    public ArrayList<HashSet<Integer>> buildCliqueGraph(ArrayList<SubFragment> fragments) {
        ArrayList<HashSet<Integer>> cliqueGraph = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < fragments.size(); ++i) {
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
        return cliqueGraph;
    }

    public ArrayList<Fragment> convertToFragments(ArrayList<Integer> cliqueVertices,
                                                  ArrayList<SubFragment> subFragments) {
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
    public ArrayList<Fragment> algorithmize(Graph graph,
                                            Double alpha_coeff,
                                            Double beta_coeff,
                                            Double gamma_coeff,
                                            CliqueTaskSolver solver) {
        this.graph = graph;
        this.graphReversed = buildReversedGraph(graph);
        this.alpha_coeff = alpha_coeff;
        this.beta_coeff = beta_coeff;
        this.gamma_coeff = gamma_coeff;

        ArrayList <Integer> numberPrevObjectUsage = new ArrayList<Integer>();
        for (int i = 0; i < graph.getObjectWeight().size(); ++i) {
            numberPrevObjectUsage.add(0);
        }
        ArrayList<HashSet<Integer>> objectsUsedInDescendants = new ArrayList<HashSet<Integer>>();
        for (Integer i = 0; i < graph.getVertices().size(); ++i) {
            objectsUsedInDescendants.add(new HashSet<Integer>());
            ArrayList<Boolean> used2 = new ArrayList<Boolean>();
            for (int j = 0; j < graph.getVertices().size(); ++j) {
                used2.add(false);
            }
            setObjectsUsedInDescendants(i, i, used2, objectsUsedInDescendants);
        }
        ArrayList<HashSet<Integer>> fragmentRequirements = setFragmentRequirements();
        HashSet<Integer> verticesInLoops = setVerticesInLoops();

        ArrayList<Boolean> used = new ArrayList<Boolean>();
        for (int i = 0; i < graph.getVertices().size(); ++i) {
            used.add(false);
        }

        HashSet<SubFragment> subFragments = new HashSet<SubFragment>();
        findSubFragments(0,
                used,
                subFragments,
                numberPrevObjectUsage,
                objectsUsedInDescendants,
                fragmentRequirements,
                verticesInLoops);

        ArrayList<SubFragment> fragments = new ArrayList<SubFragment>(subFragments);
        ArrayList<HashSet<Integer>> cliqueGraph = buildCliqueGraph(fragments);
        ArrayList<Double> cliqueWeights = new ArrayList<Double>();
        for (SubFragment fragment : fragments) {
            cliqueWeights.add(fragment.profit);
        }
        ArrayList<Integer> result = solver.cliqueSolve(cliqueGraph, cliqueWeights);
        return convertToFragments(result, fragments);
    }
}
