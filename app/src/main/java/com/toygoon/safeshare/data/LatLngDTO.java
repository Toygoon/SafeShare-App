package com.toygoon.safeshare.data;

public class LatLngDTO {
    public Double lat;
    public Double lng;

    public LatLngDTO(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String toString() {
        return this.lat + ", " + this.lng;
    }
}
