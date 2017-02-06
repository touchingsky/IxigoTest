package com.gaurav.ixigotest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaurav
 * @version 1.0
 * @since 4/2/17
 */
public class FlightModel implements Serializable {

    @SerializedName("originCode")
    private String originCode;

    @SerializedName("destinationCode")
    private String destinationCode;

    @SerializedName("departureTime")
    private long departureTime;

    @SerializedName("arrivalTime")
    private long arrivalTime;

    @SerializedName("airlineCode")
    private String airlineCode;

    @SerializedName("class")
    private String classType;

    @SerializedName("fares")
    private List<FareModel> fares;

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public List<FareModel> getFares() {
        return fares;
    }

    public void setFares(List<FareModel> fares) {
        this.fares = fares;
    }
}
