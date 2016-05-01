package info.ppla07.prime.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
    ListView listViewDevicesFound;
    TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

//        // SqLite database handler
//        db = new SQLiteHandler(getApplicationContext());
//
//        // session manager
//        session = new SessionManager(getApplicationContext());
//
//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }
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
                    if (deviceName.equals(device.getName())) {
                        Toast.makeText(BluetoothActivity.this,
                                "Name: " + device.getName() + "\n"
                                        + "Address: " + device.getAddress() + "\n"
                                        + "BondState: " + device.getBondState() + "\n"
                                        + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                        + "Class: " + device.getClass(),
                                Toast.LENGTH_LONG).show();
                        //Dari sini yang jalanin service semua
                        Intent connectionIntent = new Intent(BluetoothActivity.this, BluetoothService.class);
                        connectionIntent.putExtra("btDevice", device);
                        startService(connectionIntent);
                        break;
                    }
                }
            }
        });

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        textStatus = (TextView) findViewById(R.id.status);
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

    private class BluetoothObject {
        private BluetoothDevice infoDevice;
        private String macAddress = "";
        public BluetoothObject (BluetoothDevice device){
            infoDevice = device;
            macAddress = device.getAddress();
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
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        setup();
    }

    private void setup() {
        objectList = new ArrayList<BluetoothObject>();
        btArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            textStatus.setText("Still scanning...");
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                textStatus.setText("Showing found devices...");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothObject btObject = new BluetoothObject(device);
                if ((device.getName()).equals("PPL-A07")) {
                    if (objectList.isEmpty()) {
                        btArrayAdapter.add(btObject.getInfo().getName());
                        btArrayAdapter.notifyDataSetChanged();
                        objectList.add(btObject);
                    }
                    else {
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
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                setup();
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
