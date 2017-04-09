package com.jfpdazey.pathoflowestcost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathState
{
    public static int MAXIMUM_COST = 50;

    private List<Integer> rowsTraversed = new ArrayList();
    private int totalCost = 0;
    private int expectedLength = 0;

    PathState(int expectedLength) {
        this.expectedLength = expectedLength;
    }

    PathState(PathState anotherPathState) {
        this.totalCost = anotherPathState.totalCost;
        this.expectedLength = anotherPathState.expectedLength;
        for (Iterator localIterator = anotherPathState.rowsTraversed.iterator(); localIterator.hasNext(); ) { int rowTraversed = ((Integer)localIterator.next()).intValue();
            this.rowsTraversed.add(Integer.valueOf(rowTraversed)); }
    }

    public List<Integer> getRowsTraversed()
    {
        return this.rowsTraversed;
    }

    public int getTotalCost() {
        return this.totalCost;
    }

    public int getPathLength() {
        return this.rowsTraversed.size();
    }

    public void addRowWithCost(int row, int cost) {
        this.rowsTraversed.add(Integer.valueOf(row));
        this.totalCost += cost;
    }

    public boolean isComplete() {
        return this.rowsTraversed.size() == this.expectedLength;
    }

    public boolean isSuccessful() {
        return (isComplete()) && (!isOverCost());
    }

    public boolean isOverCost() {
        return this.totalCost > MAXIMUM_COST;
    }
}