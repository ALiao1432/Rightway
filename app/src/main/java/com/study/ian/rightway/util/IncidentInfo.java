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

    public String getDir() {
        return dir;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
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
