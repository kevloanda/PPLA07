package info.ppla07.prime.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.Command;
import info.ppla07.prime.helper.SQLiteHandler;
import info.ppla07.prime.helper.SessionManager;


public class BluetoothActivity extends Activity {
    //Tampilan nama dan email
    private TextView txtName;
    private TextView txtEmail;

    //Backend session
    private SQLiteHandler db;
    private SessionManager session;

    //Inisialisasi variabel dalam activity
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothObject> objectList;
    ArrayAdapter<String> btArrayAdapter;
    ArrayAdapter<String> btArrayAdapter2;
    ListView listViewDevicesFound;
    ListView listViewDevicesConnected;
    TextView textStatus;
    BluetoothService bService;
    Button btnBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                updateReceiver, new IntentFilter("update")
        );
        objectList = new ArrayList<BluetoothObject>();

        btnBluetooth = (Button)findViewById(R.id.btnBluetooth);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btnBluetooth.getText().equals("Scan for devices")) {
                    if (bluetoothAdapter.isEnabled()) {
                        setup();
                        Log.d("debug", "habis setup");
                    }
                    else {
                        checkEnabledBT();
                        Toast.makeText(getApplicationContext(), "WARNING : Bluetooth not Enabled.", Toast.LENGTH_LONG).show();
                    }
                } else if (btnBluetooth.getText().equals("Turn on Bluetooth")) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    btnBluetooth.setText("Scan for devices");
                }

            }
        });
        //        // SqLite database handler
//        db = new SQLiteHandler(getApplicationContext());
//
//        // session manager
//        session = new SessionManager(getApplicationContext());
//
//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }
        //Tampilan device yang connected
        listViewDevicesConnected = (ListView)findViewById(R.id.connectedList);
        btArrayAdapter2 = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_1);
        listViewDevicesConnected.setAdapter(btArrayAdapter2);
        refreshAdapter2();

        listViewDevicesConnected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String deviceName = (String) parent.getItemAtPosition(position);
                Intent disconnectIntent = new Intent (BluetoothActivity.this, BluetoothService.class);
                Command disconnect = new Command ("disconnect");
                disconnectIntent.putExtra("command", disconnect);
                Command deviceNameContent = new Command (deviceName);
                disconnectIntent.putExtra("deviceName", deviceNameContent);
                startService(disconnectIntent);

                //ga boleh ada fungsional di activity (soalnya suka ngelibatin variabel yg suka ilang datanya
            }
        });

        //Tampilan device yang ditemukan
        listViewDevicesFound = (ListView)findViewById(R.id.foundList);
        btArrayAdapter = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_1);
        listViewDevicesFound.setAdapter(btArrayAdapter);
        listViewDevicesFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String deviceName = (String) parent.getItemAtPosition(position);
                for (int i = 0; i < objectList.size(); i++) {
                    BluetoothDevice device = objectList.get(i).getInfo();
                    if (bluetoothAdapter.isEnabled()) {
                        if (deviceName.equals(device.getName())) {
                            Toast.makeText(BluetoothActivity.this,
                                    "Now connecting...",
                                    Toast.LENGTH_LONG).show();
                            //Dari sini yang jalanin service semua
                            Intent connectIntent = new Intent(BluetoothActivity.this, BluetoothService.class);
                            Command connect = new Command ("connect");
                            connectIntent.putExtra("command", connect);
                            connectIntent.putExtra("btDevice", device);
                            startService(connectIntent);
                            break;
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "WARNING : Bluetooth not Enabled.", Toast.LENGTH_LONG).show();
                        checkEnabledBT();
                    }

                }
            }
        });



        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        textStatus = (TextView) findViewById(R.id.status);
        textStatus.setText("Discoverable device :");
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "FEATURE_BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        //Cek hardware bluetooth bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d("debug", "masuk update receiver");
                refreshAdapter2();
        }
    };

    private void refreshAdapter2() {
        //Update session ttg device connected
        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);

        btArrayAdapter2.clear();
        String name = sharedpreferences.getString("ConnectedDevice", "");
        if (!(name.equals("noDevice"))) {
            Log.d("refreshadapter2", name);
            btArrayAdapter2.add(name);
        }
    }

    private class BluetoothObject {
        private BluetoothDevice infoDevice;
        private String macAddress = "";
        public BluetoothObject (BluetoothDevice device){
            if (device == null) {
                infoDevice = null;
                macAddress = "";
            }
            else {
                infoDevice = device;
                macAddress = device.getAddress();
            }
        }
        private BluetoothDevice getInfo () {
            return infoDevice;
        }
        private String getString() {
            return macAddress;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Turn ON BlueTooth if it is OFF
        checkEnabledBT();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void checkEnabledBT() {
        if (!bluetoothAdapter.isEnabled()) {
            btnBluetooth.setText("Turn on Bluetooth");
        }
        else {
            btnBluetooth.setText("Scan for devices");
        }
    }
    private void setup() {
        btArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
        textStatus.setText("Still scanning...");
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                textStatus.setText("Showing found devices...");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothObject btObject = new BluetoothObject(device);
                Log.d("string", device.getName() + "aa");
                if ((device.getName() != null) && (device.getName()).equals("PPL-A07")) {
                    if (objectList.isEmpty()) {
                        btArrayAdapter.add(btObject.getInfo().getName());
                        btArrayAdapter.notifyDataSetChanged();
                        objectList.add(btObject);
                    } else {
                        for (int j = 0; j < objectList.size(); j++) {
                            BluetoothObject a = objectList.get(j);
                            if (!(a.getString().equals(device.getAddress()))) {
                                btArrayAdapter.add(btObject.getInfo().getName());
                                btArrayAdapter.notifyDataSetChanged();
                                objectList.add(btObject);
                                break;
                            }
                        }
                    }
                }
            }
        }};

    @Override
    protected void onDestroy() {
        bluetoothAdapter.cancelDiscovery();
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
        Log.d("debug", "activity bluetooth desroyed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(BluetoothActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
