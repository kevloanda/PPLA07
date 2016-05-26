package info.ppla07.prime.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.util.Log;
import android.location.Criteria;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import info.ppla07.prime.R;

public class SmsService extends Activity {
    private MyLocationListener mylistener;
    private Criteria criteria;
    private float LOCATION_REFRESH_DISTANCE = 0;
    Button sendBtn;
    TextView sendSMS;
    double longitude;
    double latitude;
    Location myLoc;
    private LocationManager locationManager;
    private String provider;
    GPSTracker gps;

    private class MyLocationListener extends Activity implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    public String getLoc (double x, double y) {
        String loc = x+","+y;
        return loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        gps = new GPSTracker(SmsService.this);

        // check if GPS enabled
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //sendSMS.setText(getLoc(latitude,longitude));
        } else{
            gps.showSettingsAlert();
        }

        sendSMSMessage();

//        sendBtn = (Button) findViewById(R.id.btnSendSMS);
//
//        sendSMS = (TextView) findViewById(R.id.textSMS);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mylistener = new MyLocationListener();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
//                LOCATION_REFRESH_DISTANCE, mylistener);
//        //locationManager.requestSingleUpdate(provider, mylistener,Looper.getMainLooper());
//        criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setCostAllowed(false);
//        provider = locationManager.getBestProvider(criteria, false);
//        myLoc = locationManager.getLastKnownLocation(provider);

//        sendSMS.setText(getLoc(myLoc));
//        sendSMSMessage();
//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                sendSMSMessage();
//            }
//        });
    }

    public void sendDelayedSMSMessage() {
        SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        String [] phoneNo = sp.getString("EmergencyContactsNumbers","").split(";");
        String message = sp.getString("EmergencyMessage","")+" Saya berada di http://maps.google.com/?q="+getLoc(latitude,longitude);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0;i<phoneNo.length;i++) {
                smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
            }
        }

        catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void sendSMSMessage() {
        new Task().execute();
    }

    private class Task extends AsyncTask<Void,Void,Void> {
        ProgressBar progress = (ProgressBar)findViewById(R.id.pbHeaderProgress);
        @Override
        protected Void doInBackground(Void... v) {
            SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);

            Log.i("Send SMS", "");
            String [] phoneNo = sp.getString("EmergencyContactsNumbers","").split(";");
            String message = sp.getString("EmergencyMessage","")+" Saya berada di http://maps.google.com/?q="+getLoc(latitude,longitude);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0;i<phoneNo.length;i++) {
                    smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
                }
                Thread.sleep(1000);
            }

            catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            progress.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }
    }
}
