package com.deshsomachar.livewell;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity implements SensorEventListener {
    static int onstart_sensor_count=0;
    static boolean started=false;
    static int steps=0;
    static int bmp=0;
    Button resetButton;
    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        count = (TextView) findViewById(R.id.count);
        resetButton=(Button)findViewById(R.id.resetButton);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        GetText();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count.setText("0");
                started=false;
                onstart_sensor_count=0;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!started){
            onstart_sensor_count=(int) event.values[0];
            started=true;
        }
        if (activityRunning) {
            count.setText(String.valueOf((int)event.values[0]-onstart_sensor_count));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
//    new AsyncTask<Void, Void, Void>() {
//
//
//        public CallAPI(){
//            //set context variables if required
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String urlString = params[0]; // URL to call
//            String data = params[1]; //data to post
//            OutputStream out = null;
//
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();
//
//                urlConnection.connect();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//            return null;
//        }
//    }.execute(new Void[0]);



    public  void  GetText()  throws UnsupportedEncodingException
    {
        // Get user defined values
        int steps = this.steps;
        int bpm   = this.bmp;

        // Create data variable for sent values to server

        String data = URLEncoder.encode("steps", "UTF-8")
                + "=" + URLEncoder.encode(""+steps, "UTF-8");

        data += "&" + URLEncoder.encode("bmp", "UTF-8") + "="
                + URLEncoder.encode(""+bmp, "UTF-8");


        String text = "";
        BufferedReader reader=null;

        // Send data
        try
        {

            // Defined URL  where to send data
            //URL url = new URL();

            // Send POST data request

           // / conn = url.openConnection();
            HttpsURLConnection con = (HttpsURLConnection) new URL("https://deshsomachar.com/api/logData.php").openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.10240 ");
            con.setRequestProperty("Content-Language", "en-US");
           // con.setRequestProperty("Cookie", "__test=5c8d17f12df4776e838d730a8aceec20; expires=Fri, 01-Jan-38 5:55:55 GMT; path=/");
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        }
        catch(Exception ex)
        {

        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {}
        }

        // Show response on activity
        //content.setText( text  );

    }

}

