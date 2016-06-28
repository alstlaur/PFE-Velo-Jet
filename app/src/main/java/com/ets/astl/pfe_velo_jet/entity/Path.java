package com.ets.astl.pfe_velo_jet.entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

public class Path {

    private String name;
    private float distance;
    private Date date;
    private float speed;

    private ArrayList<LatLng> points;

    public Path() {
        this.name = "Nouveau parcour";
        this.distance = 0.0f;
        this.speed = 0.0f;
        this.date = new Date();
        this.points = new ArrayList<>();
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }
}
