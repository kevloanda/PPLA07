package info.ppla07.prime.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;
import info.ppla07.prime.R;

public class SmsService extends Activity {
    private MyLocationListener mylistener;
    private Criteria criteria;
    private float LOCATION_REFRESH_DISTANCE = 0;
    Button sendBtn;
    EditText txtphoneNo;
    String txtMessage;
    TextView sendSMS;
    double longitude;
    double latitude;
    Location myLoc;
    private LocationManager locationManager;
    private String provider;


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
    public String getLoc (Location location) {
        String loc = "";
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            loc = latitude + "," + longitude;
        } else {
            loc = "Error";
        }
        return loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sendBtn = (Button) findViewById(R.id.btnSendSMS);

        sendSMS = (TextView) findViewById(R.id.textSMS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mylistener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                LOCATION_REFRESH_DISTANCE, mylistener);
        //locationManager.requestSingleUpdate(provider, mylistener,Looper.getMainLooper());
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, false);
        myLoc = locationManager.getLastKnownLocation(provider);

        sendSMS.setText(getLoc(myLoc));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

    protected void sendSMSMessage() {
        SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);

        Log.i("Send SMS", "");
        String [] phoneNo = sp.getString("EmergencyContactsNumbers","").split(";");
        String message = sp.getString("EmergencyMessage","")+" Saya berada di http://maps.google.com/?q="+getLoc(myLoc);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0;i<phoneNo.length;i++) {
                smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
            }
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
