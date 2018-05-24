package com.example.dto;

import java.math.BigDecimal;

/**
 * En data transfer object klass som hämtar icke-hanterade klasser från
 * routes-tabellen.
 *
 * @author Dmitri
 *
 */
public class RouteDTO {

    private int routeId;
    private int venueId;
    private int endpointId;
    private String color;
    private String colorHex;
    private String distanceInMeter;
    private double xFaktor;

    /**
     * Getters and Setters
     *
     * @return
     */
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public int getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(int endpointId) {
        this.endpointId = endpointId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getDistanceInMeter() {
        return distanceInMeter;
    }

    public void setDistanceInMeter(String distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
    }

    public double getxFaktor() {
        return xFaktor;
    }

    public void setxFaktor(BigDecimal xFaktor) {
        this.xFaktor = xFaktor.doubleValue();
    }

    @Override
    public String toString() {
        return "RouteDTO [routeId=" + routeId + ", venueId=" + venueId + ", endpointId=" + endpointId + ", color="
                + color + ", colorHex=" + colorHex + ", distanceInMeter=" + distanceInMeter + ", xFaktor=" + xFaktor
                + "]";
    }
}
