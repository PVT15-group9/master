package com.example.dto;

/**
 * En data transfer object klass som hämtar icke-hanterade klasser från
 * threshold-tabellen.
 *
 * @author Dmitri
 *
 */
public class ThresholdDTO {

    private int thresholdId;
    private int routeId;
    private String thresholdType;
    private int thresholdAmount;

    /**
     * Getters and Setters
     *
     * @return
     */
    public int getThresholdId() {
        return thresholdId;
    }

    public void setThresholdId(int thresholdId) {
        this.thresholdId = thresholdId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(String thresholdType) {
        this.thresholdType = thresholdType;
    }

    public int getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(int thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    @Override
    public String toString() {
        return "ThresholdDTO [thresholdId=" + thresholdId + ", routeId=" + routeId + ", thresholdType=" + thresholdType
                + ", thresholdAmount=" + thresholdAmount + "]";
    }
}
