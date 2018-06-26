package com.study.ian.rightway.util;

public class IncidentInfo {

    private String location;
    private String dir;
    private String time;
    private String description;

    public IncidentInfo(String location, String dir, String time, String description) {
        this.location = location;
        this.dir = dir;
        this.time = time;
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiscription() {
        return description;
    }

    public void setDiscription(String discription) {
        this.description = discription;
    }

    @Override
    public String toString() {
        return "IncidentInfo ---" +
                "\nloc : " + location +
                "\ndir : " + dir +
                "\ntime : " + time +
                "\ndescription : " + description;
    }
}
