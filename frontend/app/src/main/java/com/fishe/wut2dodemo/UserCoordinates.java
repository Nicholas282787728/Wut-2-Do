package com.fishe.wut2dodemo;

/**
 * UserCoordinates stores the latitude and longitude of the user. Singleton pattern applied.
 */
public class UserCoordinates {
    private Double mLatitude;
    private Double mLongitude;
    private static UserCoordinates theUserCoordinates = null;

    private UserCoordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public static UserCoordinates getInstance(double latitude, double longitude) {
        if (theUserCoordinates == null) {
            theUserCoordinates = new UserCoordinates(latitude, longitude);
        }
        return theUserCoordinates;
    }

    public void updateCoordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
}
