package com.example.abhi.wireless;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        lv = (ListView) findViewById(R.id.views);








    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    public void open(View view)
    {
        Intent int1=new Intent(MainActivity.this,canvas.class);
        startActivity(int1);
    }

    public void refresh(View view)
    {
        HashMap<String,Integer> accesspoints=new HashMap<>();
        List<String> listofAP = new ArrayList<String>();
        HashMap<String,Integer> frequencies=new HashMap<>();
        for(int i=0;i<10;i++)
        {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.startScan();

            List<ScanResult> wifiList = wifiManager.getScanResults();

            for (ScanResult scanResult : wifiList) {

                String name = scanResult.SSID.toString();
                if (name.equals("AndroidAP") || name.equals("abhi") || name.equals("wifi4")) {
                    int rssi = scanResult.level;
                    int freq = scanResult.frequency;

                    if (accesspoints.containsKey(name)) {
                        int current=accesspoints.get(name);
                        accesspoints.put(name,current+rssi);
                        frequencies.put(name,freq);


                    } else {
                        accesspoints.put(name, rssi);
                        frequencies.put(name, freq);
                    }
                }
            }
        }
        for(String key:accesspoints.keySet())
        {

            int rss=(int)((accesspoints.get(key)*1.0)/10.0);
            int freq=frequencies.get(key);
            double dist=calculateDistance(rss,freq);
            String newstr="";
            newstr+="SSID: "+key+"\n";
            newstr+="RSSI: "+Integer.toString(rss)+"\n";
            newstr+="Distance: "+Double.toString(dist);
            //String newstr=scanResult.SSID.toString()+"==>"+Integer.toString(rssi)+"==>"+Double.toString(dist);
            //Log.d("bingo level",scanResult.SSID.toString()+"==>"+Integer.toString(rssi)+"==>"+Double.toString(dist));
            listofAP.add(newstr);


        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listofAP );

        lv.setAdapter(arrayAdapter);
    }

}


