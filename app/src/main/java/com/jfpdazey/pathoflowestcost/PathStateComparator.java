package com.jfpdazey.pathoflowestcost;

import java.util.Comparator;

public class PathStateComparator
        implements Comparator<PathState>
{
    private static int SORT_LEFT_FIRST = -1;
    private static int SORT_RIGHT_FIRST = 1;
    private static int SORT_EQUAL = 0;

    public int compare(PathState leftPath, PathState rightPath)
    {
        int comparedLength = compareLengths(leftPath, rightPath);
        return comparedLength != 0 ? comparedLength : compareCosts(leftPath, rightPath);
    }

    private int compareLengths(PathState leftPath, PathState rightPath) {
        int leftLength = getLengthFromPath(leftPath);
        int rightLength = getLengthFromPath(rightPath);

        return leftLength == rightLength ? SORT_EQUAL : leftLength > rightLength ? SORT_LEFT_FIRST : SORT_RIGHT_FIRST;
    }

    private int compareCosts(PathState leftPath, PathState rightPath) {
        int leftCost = getCostFromPath(leftPath);
        int rightCost = getCostFromPath(rightPath);

        return leftCost == rightCost ? SORT_EQUAL : leftCost < rightCost ? SORT_LEFT_FIRST : SORT_RIGHT_FIRST;
    }

    private int getLengthFromPath(PathState path) {
        if (path != null) {
            return path.getPathLength();
        }
        return -2147483648;
    }

    private int getCostFromPath(PathState path)
    {
        if (path != null) {
            return path.getTotalCost();
        }
        return 2147483647;
    }
}