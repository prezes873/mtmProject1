package com.example.maciek.mtmproject;

/**
 * Created by maciek on 2015-12-13.
 */
public class Object {

    String nazwa;
    double lat, lgn, h;
    int id;


    public  Object(int id, double lgn, double lat, double h, String nazwa)
    {
        this.h = h;
        this.id = id;
        this.lat = lat;
        this.lgn = lgn;
        this.nazwa = nazwa;
    }
}
