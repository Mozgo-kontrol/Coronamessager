package com.alifmrdb.coronamessangeruni.services;

public class CoordinateWrapper {
    private double longitude;
    private double latitude;

    public CoordinateWrapper(double lat, double lon) {
        longitude = lon;
        latitude = lat;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }
}
