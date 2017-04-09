package com.jfpdazey.pathoflowestcost;

import java.util.Iterator;
import java.util.List;

public class RowVisitor
{
    private int row;
    private Grid grid;
    private PathStateCollector pathCollector;

    public RowVisitor(int startRow, Grid grid, PathStateCollector collector)
    {
        if (grid == null)
            throw new IllegalArgumentException("A visitor requires a grid");
        if (collector == null)
            throw new IllegalArgumentException("A visitor requires a collector");
        if ((startRow <= 0) || (startRow > grid.getRowCount())) {
            throw new IllegalArgumentException("Cannot visit a row outside of grid boundaries");
        }

        this.row = startRow;
        this.grid = grid;
        this.pathCollector = collector;
    }

    public PathState getBestPathForRow() {
        PathState initialPath = new PathState(this.grid.getColumnCount());

        visitPathsForRow(this.row, initialPath);

        return this.pathCollector.getBestPath();
    }

    private void visitPathsForRow(int row, PathState path) {
        if (canVisitRowOnPath(row, path)) {
            visitRowOnPath(row, path);
        }

        List adjacentRows = this.grid.getRowsAdjacentTo(row);
        boolean currentPathAdded = false;

        for (Iterator localIterator = adjacentRows.iterator(); localIterator.hasNext(); ) { int adjacentRow = ((Integer)localIterator.next()).intValue();
            if (canVisitRowOnPath(adjacentRow, path)) {
                PathState pathCopy = new PathState(path);
                visitPathsForRow(adjacentRow, pathCopy);
            } else if (!currentPathAdded) {
                this.pathCollector.addPath(path);
                currentPathAdded = true;
            } }
    }

    private boolean canVisitRowOnPath(int row, PathState path)
    {
        return (!path.isComplete()) && (!nextVisitOnPathWouldExceedMaximumCost(row, path));
    }

    private void visitRowOnPath(int row, PathState path) {
        int columnToVisit = path.getPathLength() + 1;
        path.addRowWithCost(row, this.grid.getValueForRowAndColumn(row, columnToVisit));
    }

    private boolean nextVisitOnPathWouldExceedMaximumCost(int row, PathState path) {
        int nextColumn = path.getPathLength() + 1;
        return path.getTotalCost() + this.grid.getValueForRowAndColumn(row, nextColumn) > PathState.MAXIMUM_COST;
    }
}