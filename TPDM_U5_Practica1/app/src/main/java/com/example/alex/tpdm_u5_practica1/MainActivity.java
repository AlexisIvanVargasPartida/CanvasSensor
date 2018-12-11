package com.example.alex.tpdm_u5_practica1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private CanvasView canvas;
    private int circleRadius=30;
    private float circleX;
    private float circleY;
    private Timer timer;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    Sensor sensor;
    SensorEventListener sensorEventListener;
    private float sensorX;
    private float sensorY;
    private float sensorZ;
    private  long lastSensorUpdateTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(sensor==null)
            finish();

        sensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[0]<sensor.getMaximumRange()){
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }else{
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        start();




        Display display=getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);

        int screenWidth=size.x;
        int screenHeight=size.y;

        circleX=screenWidth/2 - circleRadius;
        circleY=screenHeight/2 -circleRadius;

        canvas=new CanvasView(MainActivity.this);
        setContentView(canvas);

        handler=new Handler(){
            public  void handleMessage(Message message){
                canvas.invalidate();
            }
        };

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (sensorX<0){
                    circleX+=5;
                }else {
                    circleX-=5;
                }
              if(sensorY>0) {
                  circleY+=5;
              }else{
                  circleY-=5;
              }

              handler.sendEmptyMessage(0);

            }
        },0,100);

    }

    public void start(){
        sensorManager.registerListener(sensorEventListener,sensor,2000*1000);
    }
    public void stop(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        start();
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor= event.sensor;

        if(mySensor.getType()==Sensor.TYPE_ACCELEROMETER){
            sensorX=event.values[0];
            sensorY=event.values[1];
            sensorZ=event.values[2];


            long currentTime=System.currentTimeMillis();

            if((currentTime-lastSensorUpdateTime)>100){
              lastSensorUpdateTime=currentTime;
                sensorX=event.values[0];
                sensorY=event.values[1];
                sensorZ=event.values[2];
            }


        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class CanvasView extends View{
        private Paint pen;

        public CanvasView(Context context){
           super(context);
           setFocusable(true);

           pen=new Paint();

        }
        public void onDraw(Canvas screen){
            pen.setStyle(Paint.Style.FILL);
            pen.setAntiAlias(true);
            pen.setTextSize(40f);
            pen.setColor(Color.GREEN);

            screen.drawCircle(circleX,circleY,circleRadius,pen);


        }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.brujula) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
