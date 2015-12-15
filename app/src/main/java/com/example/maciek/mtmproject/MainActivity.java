package com.example.maciek.mtmproject;

/**
 * Created by maciek on 2015-12-13.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    TextView tv, tvLocation;
    MojeView mv;
    SurfaceView sv;
    List<Object> objectList;
    Location loc1 = null;
    LocationManager locationManager;
    String objectName;
    Button btnAddPoint;
    boolean gpsConnected;
    DataBase dataBase;

    final float cameraB[] = new float[]{0, 0, -1};
    float namiarM[] = new float[]{1, 0, 0};
    float namiarB[] = new float[]{0, 1, 0};
    float cameraM[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tv = (TextView) findViewById(R.id.tvKat);
        mv = (MojeView) findViewById(R.id.view);
        objectList = new ArrayList<>();
        btnAddPoint = (Button) findViewById(R.id.btnAddPoint);
        dataBase = new DataBase(this);
        objectList = dataBase.getObjectListFromDB();

        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoint();
            }
        });


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

        float lonx = (float) (18.0 / 180 * Math.PI);
        float latx = (float) (55.0 / 180 * Math.PI);
        float lonu = (float) (18.0 / 180 * Math.PI);
        float latu = (float) (55.0 / 180 * Math.PI);
        float[] enu = latlonToENU(latx, lonx, 100, latu, lonu, 0);
        Log.i("krbrlog", "x:" + enu[0]);
        Log.i("krbrlog", "y:" + enu[1]);
        Log.i("krbrlog", "z:" + enu[2]);
        namiarM = enu;
    }

    LocationListener ll = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {

            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            tvLocation.setText(String.valueOf(loc.getLatitude())+"\n"+String.valueOf(loc.getLongitude()));

            loc1 = loc;
        }
    };

    public void savePoint()
    {
        if (loc1 != null) {
            objectList.add(new Object(loc1.getLongitude(), loc1.getLatitude(), objectName));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);
    }


    static final float Radius = 6378137;
    //lat i lon w radianach !!!!
    static float[] latLonToECEF(float lat,float lon,float h){
        float[] ECEF = new float[3];
        ECEF[0]=(float)((h+Radius)*Math.cos(lat)*Math.cos(lon));
        ECEF[1]=(float)((h+Radius)*Math.cos(lat)*Math.sin(lon));
        ECEF[2]=(float)((h+Radius)*Math.sin(lat));
        return ECEF;
    }

    //lat i lon w radianach
    static float[] latlonToENU(float lat,float lon,float h,
                               float ulat,float ulon,float uh){
        float[] ecefX=latLonToECEF(lat,lon,h);
        float[] ecefU=latLonToECEF(ulat,ulon,uh);
        float[] d = new float[3];
        d[0]=ecefX[0]-ecefU[0];
        d[1]=ecefX[1]-ecefU[1];
        d[2]=ecefX[2]-ecefU[2];
        float[] enu = new float[3];

        enu[0]= (float)(-Math.sin(ulon)*d[0]+Math.cos(ulon)*d[1]);
        enu[1]= (float)(-Math.cos(ulon)*Math.sin(ulat)*d[0]-Math.sin(ulon)*Math.sin(ulat)*d[1]+Math.cos(ulat)*d[2]);
        enu[2]= (float)(Math.cos(ulon)*Math.cos(ulat)*d[0]+Math.sin(ulon)*Math.cos(ulat)*d[1]+Math.sin(ulat)*d[2]);
        return enu;
    }


    @Override
    protected void onResume() {
        super.onResume();


        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(this,acc,SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this,mag,SensorManager.SENSOR_DELAY_GAME);

    }

    float[] magVal=null;
    float[]accVal=null;

    float[] rotFromBtoM=new float[9];
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case (Sensor.TYPE_ACCELEROMETER):
                accVal = sensorEvent.values.clone();
                break;
            case (Sensor.TYPE_MAGNETIC_FIELD):
                magVal = sensorEvent.values.clone();
                break;
        }


        if(magVal!=null&&accVal!=null){
            boolean success =
                    SensorManager.getRotationMatrix
                            (rotFromBtoM,null,accVal,magVal);

            //rotFrmBtoM*cameraB

            cameraM[0]=cameraB[0]*rotFromBtoM[0]+
                    cameraB[1]*rotFromBtoM[1]+
                    cameraB[2]*rotFromBtoM[2];
            cameraM[1]=cameraB[0]*rotFromBtoM[0+3]+
                    cameraB[1]*rotFromBtoM[1+3]+
                    cameraB[2]*rotFromBtoM[2+3];
            cameraM[2]=cameraB[0]*rotFromBtoM[0+6]+
                    cameraB[1]*rotFromBtoM[1+6]+
                    cameraB[2]*rotFromBtoM[2+6];
            //Log.i("kbbrlog", "getAngle: " + getAngle(northM, cameraM));
            tv.setText(""+(double)(Math.round(getAngle(namiarM, cameraM)* 1000))/1000);

            namiarB[0]=namiarM[0]*rotFromBtoM[0]+
                    namiarM[1]*rotFromBtoM[3]+
                    namiarM[2]*rotFromBtoM[6];
            namiarB[1]=namiarM[0]*rotFromBtoM[1]+
                    namiarM[1]*rotFromBtoM[4]+
                    namiarM[2]*rotFromBtoM[7];
            namiarB[2]=namiarM[0]*rotFromBtoM[2]+
                    namiarM[1]*rotFromBtoM[5]+
                    namiarM[2]*rotFromBtoM[8];


            mv.x=namiarB[0]/namiarB[2];
            mv.y=namiarB[1]/namiarB[2];
            mv.invalidate();
        }
    }


    double getAngle(float[]a,float b[]){
        double temp = (a[0]*b[0]+a[1]*b[1]+a[2]*b[2])/
                Math.sqrt(a[0]*a[0]+a[1]*a[1]+a[2]*a[2])/
                Math.sqrt(b[0]*b[0]+b[1]*b[1]+b[2]*b[2]);
        return Math.acos(temp);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void dodajNamiar(View v){
        namiarM=cameraM.clone();
    }
}
