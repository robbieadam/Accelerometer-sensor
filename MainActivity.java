package android.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    TextView txt_currentAccel, txt_prevAccel, txt_acceleration;
    ProgressBar prog_shakeMeter;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    private double acclerationCurrentValue;
    private double accelerationPreviousValue;

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    private Viewport viewport;


    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
            new DataPoint(0, 0),
            new DataPoint(0, 0),
            new DataPoint(0, 0),
            new DataPoint(0, 0)
    });

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acclerationCurrentValue = Math.sqrt( (x * x + y * y + z * z));

            double changeInAccelleration = Math.abs (acclerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = acclerationCurrentValue;

            // update text views
            txt_currentAccel.setText("Current =" + (int) acclerationCurrentValue);
            txt_prevAccel.setText(("Prev = "  + (int) accelerationPreviousValue));
            txt_acceleration.setText("Acceleration change = " + (int) changeInAccelleration);

            prog_shakeMeter.setProgress((int) changeInAccelleration);

            if(changeInAccelleration > 14) {
                txt_acceleration.setBackgroundColor(Color.RED);
            }
            else if (changeInAccelleration > 5) {
                txt_acceleration.setBackgroundColor(Color.parseColor("#FCAD03"));
            }
            else if (changeInAccelleration > 2 ) {
                txt_acceleration.setBackgroundColor(Color.YELLOW);
            }
            else {
                txt_acceleration.setBackgroundColor(getResources().getColor((com.google.android.material.R.color.design_default_color_background)));
            }

            pointsPlotted++;
            if (pointsPlotted>1000){
                pointsPlotted = 1;
                series.resetData( new DataPoint[] { new DataPoint(0, 0)});
            }
            series.appendData( new DataPoint(pointsPlotted, changeInAccelleration), true, pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_acceleration = findViewById(R.id.txt_accel);
        txt_currentAccel = findViewById(R.id.txt_currentAccel);
        txt_prevAccel = findViewById(R.id.txt_prevAccel);

        prog_shakeMeter = findViewById(R.id.prog_shakeMeter);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}