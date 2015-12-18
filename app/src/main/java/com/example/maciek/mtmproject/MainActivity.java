package com.example.maciek.mtmproject;

/**
 * Created by maciek on 2015-12-13.
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements SensorEventListener {
    TextView  tvLocation;
    MojeView mv;
    List<Object> objectList;
    List<float[]> namiarMList;
    Location loc1 = null;
    LocationManager locationManager;
    Button btnAddPoint, btnShowPoint;
    FragmentManager fm = getSupportFragmentManager();
    boolean Flag;
    final float cameraB[] = new float[]{0, 0, 1};
    float namiarM[] = new float[]{1, 0, 0};
    float namiarB[] = new float[]{0, 1, 0};
    float cameraM[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        mv = (MojeView) findViewById(R.id.view);
        objectList = new ArrayList<>();
        btnAddPoint = (Button) findViewById(R.id.btnAddPoint);
        btnShowPoint = (Button) findViewById(R.id.btnShowPoint);
        objectList = DataBase.getInstance(this).getObjectListFromDB();
        final MainActivity temp = this;
        Flag = true;
        namiarMList = new ArrayList<float[]>();

        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loc1 != null) {
                    ObjectNameDialog nameDialog = new ObjectNameDialog();
                    nameDialog.show(fm, "test");
                    nameDialog.addContext(temp);
                }
            }
        });

        btnShowPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectListDialog listDialog = new ObjectListDialog();
                listDialog.show(fm, "List Dialog");
                listDialog.addContext(temp);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

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

            tvLocation.setText(String.valueOf(loc.getLatitude()) + "\n" + String.valueOf(loc.getLongitude()));

                loc1 = loc;


        }
    };

    public void savePoint(String name) {
        DataBase.getInstance(this).addObjectToTable(loc1.getLongitude(), loc1.getLatitude(), loc1.getAltitude() , name);
        objectList = DataBase.getInstance(this).getObjectListFromDB();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);
    }


    static final float Radius = 6378137;

    //lat i lon w radianach !!!!
    static float[] latLonToECEF(float lat, float lon, float h) {
        float[] ECEF = new float[3];
        ECEF[0] = (float) ((h + Radius) * Math.cos(lat) * Math.cos(lon));
        ECEF[1] = (float) ((h + Radius) * Math.cos(lat) * Math.sin(lon));
        ECEF[2] = (float) ((h + Radius) * Math.sin(lat));
        return ECEF;
    }

    //lat i lon w radianach
    static float[] latlonToENU(float lat, float lon, float h,
                               float ulat, float ulon, float uh) {
        float[] ecefX = latLonToECEF(lat, lon, h);
        float[] ecefU = latLonToECEF(ulat, ulon, uh);
        float[] d = new float[3];
        d[0] = ecefX[0] - ecefU[0];
        d[1] = ecefX[1] - ecefU[1];
        d[2] = ecefX[2] - ecefU[2];
        float[] enu = new float[3];

        enu[0] = (float) (-Math.sin(ulon) * d[0] + Math.cos(ulon) * d[1]);
        enu[1] = (float) (-Math.cos(ulon) * Math.sin(ulat) * d[0] - Math.sin(ulon) * Math.sin(ulat) * d[1] + Math.cos(ulat) * d[2]);
        enu[2] = (float) (Math.cos(ulon) * Math.cos(ulat) * d[0] + Math.sin(ulon) * Math.cos(ulat) * d[1] + Math.sin(ulat) * d[2]);
        return enu;
    }


    @Override
    protected void onResume() {
        super.onResume();


        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, mag, SensorManager.SENSOR_DELAY_GAME);

    }

    float[] magVal = null;
    float[] accVal = null;

    float[] rotFromBtoM = new float[9];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case (Sensor.TYPE_ACCELEROMETER):
                accVal = sensorEvent.values.clone();
                break;
            case (Sensor.TYPE_MAGNETIC_FIELD):
                magVal = sensorEvent.values.clone();
                break;
        }


        if (magVal != null && accVal != null) {
            boolean success =
                    SensorManager.getRotationMatrix
                            (rotFromBtoM, null, accVal, magVal);

            //rotFrmBtoM*cameraB

            if (objectList.size() > 0 && loc1 != null) {
                namiarMList.clear();

                for (int i = 0 ; i < objectList.size(); i ++) {

                    float lonu = (float) ( loc1.getLongitude()  / 180 * Math.PI);
                    float latu = (float) ( loc1.getLatitude() / 180 * Math.PI);
                    float lonx = (float) ( objectList.get(i).lgn / 180 * Math.PI);
                    float latx = (float) ( objectList.get(i).lat/ 180 * Math.PI);
                    float[] enu = latlonToENU(latx, lonx, 50 , latu, lonu, 50 );

                    namiarM = enu;
                    namiarMList.add(namiarM);
                }
            }

                cameraM[0] = cameraB[0] * rotFromBtoM[0] +
                        cameraB[1] * rotFromBtoM[1] +
                        cameraB[2] * rotFromBtoM[2];
                cameraM[1] = cameraB[0] * rotFromBtoM[0 + 3] +
                        cameraB[1] * rotFromBtoM[1 + 3] +
                        cameraB[2] * rotFromBtoM[2 + 3];
                cameraM[2] = cameraB[0] * rotFromBtoM[0 + 6] +
                        cameraB[1] * rotFromBtoM[1 + 6] +
                        cameraB[2] * rotFromBtoM[2 + 6];

            List<float[]> listPoint;
            listPoint = new ArrayList<float[]>();
            listPoint.clear();
            for (int i = 0; i < namiarMList.size(); i++) {

                namiarM = namiarMList.get(i);
                namiarB[0] = namiarM[0] * rotFromBtoM[0] +
                        namiarM[1] * rotFromBtoM[3] +
                        namiarM[2] * rotFromBtoM[6];
                namiarB[1] = namiarM[0] * rotFromBtoM[1] +
                        namiarM[1] * rotFromBtoM[4] +
                        namiarM[2] * rotFromBtoM[7];
                namiarB[2] = namiarM[0] * rotFromBtoM[2] +
                        namiarM[1] * rotFromBtoM[5] +
                        namiarM[2] * rotFromBtoM[8];
                float[] temp = new float[3];

                temp[0] = namiarB[0] / namiarB[2];
                temp[1] = namiarB[1] / namiarB[2];
                temp[2] = namiarB[2];
                listPoint.add(temp);
            }

            //    mv.x = namiarB[0] / namiarB[2];
            //    mv.y = namiarB[1] / namiarB[2];
                mv.setDane(listPoint);
                mv.setOjectList(objectList);
                mv.invalidate();

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
