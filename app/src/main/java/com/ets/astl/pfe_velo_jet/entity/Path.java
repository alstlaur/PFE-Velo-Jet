package com.ets.astl.pfe_velo_jet.entity;

import java.util.Date;

public class Path {

    private String name;
    private float distance;
    private Date date;

    public Path() {

    }

    public Path(String name, float distance, Date date) {
        this.name = name;
        this.distance = distance;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
