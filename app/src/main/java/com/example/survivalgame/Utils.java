package com.example.survivalgame;

public class Utils {
    /**
     * getDistanceBetweenPoints returns the distance between 2D points point1 and point2
     * @param point1X X coordinate of the first point
     * @param point1Y Y coordinate of the first point
     * @param point2X X coordinate of the second point
     * @param point2Y Y coordinate of the second point
     * @return returns the absolute distance between the two given points
     */
    public static double getDistanceBetweenPoints(double point1X, double point1Y, double point2X, double point2Y) {
        return Math.sqrt(
                Math.pow(point1X - point2X, 2) +
                Math.pow(point1Y - point2Y, 2)
        );
    }

    public static int getThreshold(double value, double threshold) {
        return (int) Math.abs(value / threshold);
    }
}
