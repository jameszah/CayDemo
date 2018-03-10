package com.james.caydemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;


public class MainActivity extends AppCompatActivity {

    String cayenneemail = "MrPeanut@gmail.com";
    String cayennepassword = "peanutpassword";

    // just placeholders to show the format - this will not work for you

    public String deviceID = "af749630-e884-11e7-848a-61efd1c01e7d";
    public String sensorID = "dbc76190-e889-11e7-8934-ff70c6ef636b";

    int i = 0;

    Cayenne myCayenne;

    Cayenne.CayDataPoint myCayDataPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // write the placeholder names into the boxes
        TextView tvEmail = (TextView) findViewById(R.id.textEmail);
        TextView tvPassword = (TextView) findViewById(R.id.textPassword);
        tvEmail.setText(cayenneemail);
        tvPassword.setText(cayennepassword);

        // creating the cayenne object, so we can write errors into Errorstatusbox, even though username/pass not yet entered

        TextView ErrorStatusBox = (TextView) findViewById(R.id.BigBox);
        myCayenne = new Cayenne(cayenneemail, cayennepassword, -7, ErrorStatusBox);  // -7 for Mountain, -5 for Eastern, etc

    }

    public void buttonGetTime(View v){
        myCayenne.getTime();
    }

    public void buttonGetAuth(View v){

        TextView ErrorStatusBox = (TextView) findViewById(R.id.BigBox);

        TextView tvEmail = (TextView) findViewById(R.id.textEmail);
        TextView tvPassword = (TextView) findViewById(R.id.textPassword);

        cayenneemail = String.valueOf(tvEmail.getText());
        cayennepassword = String.valueOf(tvPassword.getText());
        myCayenne = new Cayenne(cayenneemail, cayennepassword, -7, ErrorStatusBox);  // -7 for Mountain, -5 for Eastern, etc

        myCayenne.getCayenneAuth();
    }

    public void buttonGetThings(View v){
        myCayenne.getThings();
    }

    public void buttonGetData(View v) {

        if (myCayenne.SensorArrayList.size() > i) {
            String sensor = myCayenne.SensorArrayList.get(i);
            TextView tv = (TextView) findViewById(R.id.textSensor);
            tv.setText(sensor);

            i = i + 1;  // cycle through all the sensors

            if (myCayenne.NameIDhashmap.containsValue(sensor)) {
                String sens = myCayenne.IDNamehashmap.get(sensor);
                deviceID = sens.substring(0, 36);
                sensorID = sens.substring(36, 72);
            }
        } else {
            i = 0;
        }

        TextView tv = (TextView) findViewById(R.id.textDevID);
        tv.setText(deviceID);
        tv = (TextView) findViewById(R.id.textSenID);
        tv.setText(sensorID);

        // simple data access -- just stuff the value into textDP box when it arrives

        myCayDataPoint = myCayenne.new CayDataPoint(deviceID, sensorID, (TextView) findViewById(R.id.textDP));
        myCayDataPoint.update();


        // fancy data access - set up a data handler to manipulate, react, and display incoming data when it arrives

        myCayDataPoint.myResponse.addListener(new Cayenne.CayDataPointRecd() {
            public void CayDataPointRecdHandler_Callback() {
                myCayenne.AddtoBigBox("data handler");

                TextView tv = findViewById(R.id.textDPv);
                tv.setText(String.valueOf(myCayDataPoint.v));

                if (myCayDataPoint.v > 20) {
                    tv.setBackgroundColor(Color.RED);
                } else {
                    tv.setBackgroundColor(Color.WHITE);
                }

                tv = findViewById(R.id.textDPts);
                tv.setText(myCayDataPoint.ts);
                tv = findViewById(R.id.textDPunit);
                tv.setText(myCayDataPoint.unit);
                tv = findViewById(R.id.textDPdevice_type);
                tv.setText(myCayDataPoint.device_type);
            }
        });

        myCayDataPoint.update();

        // draw a graph, in the box "graph" whenever it arrives

        GraphView gv = findViewById(R.id.graph);
        gv.removeAllSeries();

        // 0 for left axix and 1 for right
        // make lower and upper the same if you don't want to specify the bounds

        myCayDataPoint.updategraph(gv, 0, Color.BLUE, 0.0, 0.0);
    }

}
