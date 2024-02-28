package graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Vertex {
    private ArrayList<Integer> objectsToRead;
    private ArrayList<Integer> objectsToWrite;
    private HashMap<Color, Double> executionTime;
    private ArrayList<Integer> childIds;
    private Color color;

    public Vertex() {
    }

    public Vertex(
        ArrayList<Integer> objectsToRead,
        ArrayList<Integer> objectsToWrite,
        HashMap<Color, Double> executionTime,
        ArrayList<Integer> childIds,
        Color color
    ) {
        this.objectsToRead = objectsToRead;
        this.objectsToWrite = objectsToWrite;
        this.executionTime = executionTime;
        this.childIds = childIds;
        this.color = color;
    }

    public void normalize() {
        if (objectsToRead == null) {
            objectsToRead = new ArrayList<Integer>();
        }
        if (objectsToWrite == null) {
            objectsToWrite = new ArrayList<Integer>();
        }
        if (childIds == null) {
            childIds = new ArrayList<Integer>();
        }
        if (color == null) {
            color = Color.NOT_SPECIFIED;
        }
    }

    public ArrayList<Integer> getObjectsToRead() {
        return objectsToRead;
    }

    public ArrayList<Integer> getObjectsToWrite() {
        return objectsToWrite;
    }

    public HashMap<Color, Double> getExecutionTime() {
        return executionTime;
    }

    public ArrayList<Integer> getChildIds() {
        return childIds;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        color = newColor;
    }
}
