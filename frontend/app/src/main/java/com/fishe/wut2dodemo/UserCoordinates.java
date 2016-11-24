package com.fishe.wut2dodemo;

public class UserCoordinates {
    private Double mLatitude;
    private Double mLongitude;
    private static UserCoordinates ourInstance = null;

    private UserCoordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public static UserCoordinates getInstance(double latitude, double longitude) {
        if (ourInstance == null) {
            ourInstance = new UserCoordinates(latitude, longitude);
        }
        return ourInstance;
    }

    public void updateCoordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
}
