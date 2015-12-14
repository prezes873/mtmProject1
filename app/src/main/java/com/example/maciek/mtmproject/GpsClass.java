package com.example.maciek.mtmproject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;


/**
 * Created by maciek on 2015-12-13.
 */


public class GpsClass extends MainActivity {

    Location memberLoc, loc1;

    public GpsClass()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);


    }

    LocationManager locationManager;


    LocationListener ll = new LocationListener()
    {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }

        @Override
        public void onLocationChanged(Location location)
        {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            tvLocation.setText(String.valueOf(loc.getLatitude())+"\n"+String.valueOf(loc.getLongitude()));
            //tvLat.setText(String.valueOf(loc.getLatitude()));
            //tvLng.setText(String.valueOf(loc.getLongitude()));
            //tvSpeed.setText(String.valueOf(loc.getAltitude()));
            //tvDir.setText(String.valueOf(loc.getBearing()));

            loc1 = loc;
            if (memberLoc != null) {

                //tvDist.setText(String.valueOf(loc.distanceTo(memberLoc)));
                //tvDirTo.setText(String.valueOf(loc.bearingTo(memberLoc)));
            }
        }
    };



            public void savePoint()
            {
                memberLoc = loc1;
            }

    }
