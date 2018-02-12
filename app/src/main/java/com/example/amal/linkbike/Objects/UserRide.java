package com.example.amal.linkbike.Objects;

import java.io.Serializable;

/**
 * Created by jaani on 11/26/2017.
 */

public class UserRide implements Serializable {

    String Id;
    String StartDate;
    String EndDate;
    String StartTime;
    String EndTime;
    long StartTimeMiliSecond;
    String StationDescripton;
    String StationAddress;
    Boolean Status;
    Double Fare;
    String Duration;

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public long getStartTimeMiliSecond() {
        return StartTimeMiliSecond;
    }

    public void setStartTimeMiliSecond(long startTimeMiliSecond) {
        StartTimeMiliSecond = startTimeMiliSecond;
    }

    public String getStationDescripton() {
        return StationDescripton;
    }

    public void setStationDescripton(String stationDescripton) {
        StationDescripton = stationDescripton;
    }

    public String getStationAddress() {
        return StationAddress;
    }

    public void setStationAddress(String stationAddress) {
        StationAddress = stationAddress;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public Double getFare() {
        return Fare;
    }

    public void setFare(Double fare) {
        Fare = fare;
    }
}
