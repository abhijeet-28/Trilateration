package com.example.abhi.wireless;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


class POINT {
    int x, y;

    POINT(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class canvas extends AppCompatActivity {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    GPSTracker gps;
    double centerlat, centerlong;

    TextView texting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
         texting=(TextView)findViewById(R.id.tv1);

        Button b1=(Button)findViewById(R.id.b1);
        b1.performClick();


    }

    public void update(View view)
    {
        gps = new GPSTracker(canvas.this);


        if(gps.canGetLocation()) {

             centerlat = gps.getLatitude();
             centerlong = gps.getLongitude();


            texting.setText("Your Location is - \nLat: " + centerlat + "\nLong: " + centerlong);
        } else {

            gps.showSettingsAlert();
        }
        Intent newintent=new Intent(canvas.this,plotter.class);
        startActivity(newintent);
    }

    public void plot(View view)
    {
        gps = new GPSTracker(canvas.this);


        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            texting.setText("Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            POINT loc=XY(centerlat,centerlong,latitude,longitude,(10*1.0)/(width*1.0));

            texting.setText(Integer.toString(loc.x)+" "+Integer.toString(loc.y));

        } else {

            gps.showSettingsAlert();
        }

    }

    public static POINT XY(double centerLatitude, double centerLongitude, double Latitude, double Longitude, double MetersPerPixel) {
        double rto = 1/MetersPerPixel;
        double dLAT = ((centerLatitude - Latitude)/ 0.00001) * rto;
        double dLNG = -1 * ((centerLongitude - Longitude) / 0.00001) * rto;
        int y = (int)Math.round(dLAT);
        int x = (int)Math.round(dLNG);
        POINT crd = new POINT(x, y );
        return crd;
    }


}

 class MyLocationListener implements LocationListener {

    public  String current="NA";
    public  String getCurrent()
    {
        return current;
    }

    @Override

    public void onLocationChanged(Location loc) {



             current=   "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude();



    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
