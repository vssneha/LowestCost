package com.jfpdazey.pathoflowestcost;

public class PathStateCollector
{
    private PathState bestPath;
    private PathStateComparator comparator;

    public PathStateCollector()
    {
        this.comparator = new PathStateComparator();
    }

    public PathState getBestPath() {
        return this.bestPath;
    }

    public void addPath(PathState newPath) {
        if (this.comparator.compare(newPath, this.bestPath) < 0)
            this.bestPath = newPath;
    }
}