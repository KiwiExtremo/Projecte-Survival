package com.example.survivalgame;

/**
 * The Utils class has methods that make multiple calculations that help with the game logic.
 */
public class Utils {
    /**
     * getDistanceBetweenPoints() calculates the distance between the points point1 and point2.
     *
     * @param point1X X coordinate of the first point.
     * @param point1Y Y coordinate of the first point.
     * @param point2X X coordinate of the second point.
     * @param point2Y Y coordinate of the second point.
     * @return returns the absolute distance between the two given points.
     */
    public static double getDistanceBetweenPoints(double point1X, double point1Y, double point2X, double point2Y) {
        return Math.sqrt(
                Math.pow(point1X - point2X, 2) +
                Math.pow(point1Y - point2Y, 2)
        );
    }

    /**
     * isInsideThreshold() calculates if a given value is inside or outside of a given threshold,
     * ignoring if the value is positive or negative.
     *
     * @param value Value to compare against the threshold.
     * @param threshold Threshold with which to check.
     * @return true if the value is bigger than the threshold, false otherwise.
     */
    public static boolean isInsideThreshold(double value, double threshold) {
        return ((int) Math.abs(value / threshold)) > 0;
    }
}
