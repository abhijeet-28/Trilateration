package com.example.abhi.wireless;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.*;

import static java.lang.Math.pow;

public class plotter extends AppCompatActivity {

    float drawx=0;
    float drawy=0;
    double scalefactor=400.0;
    double radius1=0;
    double radius2=0;
    double radius3=0;
    custommap mapper;
    long mInterpolateTime;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotter);
        mapper=new custommap(this);
        setContentView(mapper);
        int counter=0;
        while(counter<10)
        {
            drawx+=10;
            drawy+=10;
            counter++;
            setContentView(mapper);

        }
       refresh();
        Toast.makeText(getApplicationContext(),Double.toString(radius1),Toast.LENGTH_SHORT).show();

    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return pow(10.0, exp);
    }



    private class point
    {
        float x,y;
        point() {
            x=0;
            y=0;
        }
        point(float a,float b )
        {
            this.x=a;
            this.y=b;
        }
    };

    float norm (point p) // get the norm of a vector
    {
        return (float) pow(pow(p.x,2)+pow(p.y,2),.5);
    }

    point trilateration(point point1, point point2, point point3, double r1, double r2, double r3) {
        point resultPose=new point();
        //unit vector in a direction from point1 to point 2
        double p2p1Distance = pow(pow(point2.x-point1.x,2) + pow(point2.y-   point1.y,2),0.5);
        point ex=new point();
        ex.x= (float) ((point2.x-point1.x)/p2p1Distance);
        ex.y= (float) ((point2.y-point1.y)/p2p1Distance);

        point aux =new point();
        aux.x=point3.x-point1.x;
        aux.y=point3.y-point1.y;
        //signed magnitude of the x component
        double i = ex.x * aux.x + ex.y * aux.y;
        //the unit vector in the y direction.
        point aux2 = new point();
        aux2.x= (float) (point3.x-point1.x-i*ex.x);
        aux2.y= (float) (point3.y-point1.y-i*ex.y);
        point ey = new point();
        ey.x= aux2.x / norm (aux2);
        ey.y=aux2.y / norm (aux2);
        //the signed magnitude of the y component
        double j = ey.x * aux.x + ey.y * aux.y;
        //coordinates
        double x = (pow(r1,2) - pow(r2,2) + pow(p2p1Distance,2))/ (2 * p2p1Distance);
        double y = (pow(r1,2) - pow(r3,2) + pow(i,2) + pow(j,2))/(2*j) - i*x/j;
        //result coordinates
        double finalX = point1.x+ x*ex.x + y*ey.x;
        double finalY = point1.y+ x*ex.y + y*ey.y;
        resultPose.x = (float) finalX;
        resultPose.y = (float) finalY;
        return resultPose;
    }









    public void refresh()
    {
        HashMap<String,Integer> accesspoints=new HashMap<>();

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
        int average_rssi_1=(int)((accesspoints.get("wifi4")*1.0)/10.0);
        int freq_1=frequencies.get("wifi4");
        radius1=calculateDistance(average_rssi_1,freq_1)*200.0;


        int average_rssi_2=(int)((accesspoints.get("abhi")*1.0)/10.0);
        int freq_2=frequencies.get("abhi");
        radius2=calculateDistance(average_rssi_2,freq_2)*200.0;

        int average_rssi_3=(int)((accesspoints.get("AndroidAP")*1.0)/10.0);
        int freq_3=frequencies.get("AndroidAP");
        radius3=calculateDistance(average_rssi_3,freq_3)*200.0;



    }

    private class custommap extends View {
        int framesPerSecond = 60;
        long animationDuration = 1000000;
        long startTime;
        public custommap(Context context){
            super(context);
            this.startTime = System.currentTimeMillis();
            this.postInvalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float scalex=(float)0.5;
            float scaley=(float)0.5;
//            canvas.scale(scalex,scaley);
            canvas.translate(getWidth()/2,getHeight()/2);


            // custom drawing code here
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            // make the entire canvas white
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);

            // draw blue circle with anti aliasing turned off


            // draw green circle with anti aliasing turned on


            // draw red rectangle with anti aliasing turned off


            // draw the rotated text


            paint.setColor(Color.BLACK);

            int scale_x = 20;
            int scale_y = 20; //pixels per grid square
            int rows=getHeight()/scale_y;
            int columns=getWidth()/scale_x;
            for( int x = -columns; x < columns; x++)
//            for( int y = 0; y < rows; y++)
                canvas.drawLine(x*scale_x, -getHeight()/2, x*scale_x, getHeight()/2, paint);

            for( int y = -rows; y < rows; y++)
//            for( int y = 0; y < rows; y++)
                canvas.drawLine(-getWidth()/2, y*scale_y, getWidth()/2, y*scale_y, paint);



            paint.setAntiAlias(false);

            paint.setColor(Color.GREEN);
            canvas.drawCircle(drawx, drawy, 15, paint);

            paint.setAntiAlias(false);

            paint.setColor(Color.RED);
            canvas.drawCircle(0, 0, 15, paint);
            paint.setTextSize(30);
            paint.setColor(Color.BLACK);
            canvas.drawText("AP1", -10, 40, paint);

            int pos2x=200;
            int pos2y=0;
            paint.setColor(Color.RED);
            canvas.drawCircle(pos2x, pos2y, 15, paint);
            paint.setTextSize(30);
            paint.setColor(Color.BLACK);
            canvas.drawText("AP2", pos2x, 40, paint);



            int pos3x=200;
            int pos3y=-200;
            paint.setColor(Color.RED);
            canvas.drawCircle(pos3x, pos3y, 15, paint);
            paint.setTextSize(30);
            paint.setColor(Color.BLACK);
            canvas.drawText("AP3", pos3x, pos3y+40, paint);





            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLUE);
            float rad=(float)Math.sqrt(drawx*drawx+drawy*drawy);
            canvas.drawCircle(0,0,(float)radius1,paint);
            float rad2=(float)Math.sqrt((drawx-pos2x)*(drawx-pos2x)+drawy*drawy);
            canvas.drawCircle(pos2x,pos2y,(float)radius2,paint);
            float rad3=(float)Math.sqrt((pos3x-drawx)*(pos3x-drawx)+(pos3y-drawy)*(pos3y-drawy));
            canvas.drawCircle(pos3x,pos3y,(float)radius3,paint);

            long elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime < animationDuration)
                this.postInvalidateDelayed( 1000 / framesPerSecond);
            Random random=new Random();
            refresh();
            point ap1=new point(0.0f,0.0f);
            point ap2=new point(1.0f,0.0f);
            point ap3=new point(1.0f,1.0f);
            point locate=trilateration(ap1,ap2,ap3,radius1/200.0,radius2/200.0,radius3/200.0);
            drawx= (float) (locate.x*200.0);
            drawy= (float) (-locate.y*200.0);
//            int directionx=random.nextInt(3)-1;
//            int directiony=random.nextInt(3)-1;
//            drawx+=5*directionx;
//            drawy+=5*directiony;







        }



    }
}
