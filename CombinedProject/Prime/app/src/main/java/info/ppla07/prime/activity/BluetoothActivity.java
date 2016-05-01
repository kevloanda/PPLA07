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
    UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothObject> objectList;
    ArrayAdapter<String> btArrayAdapter;
    ListView listViewDevicesFound;
    TextView textStatus;
    ThreadToConnectBT myThread;
    ThreadConnected myThreadConnected;
    boolean bluetooth_service_started = false;

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
                        myThread = new ThreadToConnectBT(device);
                        Toast.makeText(getApplicationContext(),
                                "berhasil buat thread",
                                Toast.LENGTH_LONG).show();
                        myThread.start();
                        break;
                    }
                }
            }
        });

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        textStatus = (TextView) findViewById(R.id.status);
        textStatus.setText("Welcome to Bluetooth App");
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "FEATURE_BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

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
        textStatus.setText("Showing found devices...");
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("debug", "device found");
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
                            Log.d("debug2", "device diproses");

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
        Toast.makeText(getApplicationContext(), "Activity Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);

        if(myThread!=null){
            myThread.cancel();
        }

        //Harusnya stop servicenya gak disini
        stopService(new Intent(getBaseContext(), BluetoothService.class));
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

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(BluetoothActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /*
        ThreadConnectBTdevice:
        Background Thread to handle BlueTooth connecting
        */
    private class ThreadToConnectBT extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadToConnectBT(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);
                    }
                });
                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        public ThreadConnected(BluetoothSocket socket) {
            Looper.prepare();
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutputStream = out;
            startService(new Intent(getBaseContext(), BluetoothService.class));
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    textStatus.setText("Bluetooth service has run");
                }
            });
        }
        @Override
        public void run() {
            while (true) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectedInputStream));
                    final String message = reader.readLine();
                    Log.d("debug", message);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText("Message :" + message);
                        }
                    });
                    if (message.equals("BAHAYA")) {
                        //sendSMS();
                        Intent activitySms = new Intent(BluetoothActivity.this, SmsService.class);
                        startActivity(activitySms);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
//                    Intent intent = new Intent(this, BluetoothActivity.class);
//                    startActivity(intent);
                }
            }
        }
        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
