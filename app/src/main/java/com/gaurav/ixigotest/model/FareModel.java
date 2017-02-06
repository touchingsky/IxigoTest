package com.gaurav.ixigotest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author gaurav
 * @version 1.0
 * @since 4/2/17
 */
public class FareModel implements Serializable {

    @SerializedName("providerId")
    private int providerId;

    @SerializedName("fare")
    private double fare;

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}
