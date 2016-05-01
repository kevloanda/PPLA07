package info.ppla07.prime.activity;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import info.ppla07.prime.R;


/**
 * Dalam class ini terdapat pengaturan untuk mendeteksi koneksi bluetooth antara bracelet dan app
 * @return
 */
public class BluetoothService extends Service {

    private MediaPlayer player;
    private BluetoothDevice device;
    private ThreadToConnectBT thread;
    private ThreadConnected myThreadConnected;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private int counter = 1;
    private boolean isPlayed = false;
    public void onCreate() {
        super.onCreate();
        //Inisialisasi data yang dibutuhkan
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        //Membuat media player untuk alarm
        player = MediaPlayer.create(this, R.raw.test);
        player.setLooping(true); // Set looping
        player.setVolume(100, 100);

        //Membuat intent untuk memeriksa koneksi bluetooth
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //The BroadcastReceiver that listens for bluetooth broadcasts
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Device is connected
                    if (isPlayed == true) {
                        player.pause();
                        isPlayed = false;
                    }
                    Toast.makeText(getBaseContext(), "Service to protect you has connected", Toast.LENGTH_LONG).show();
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Device has disconnected
                    counter++;
                    Toast.makeText(getBaseContext(), "Warning : CONNECTION LOST", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                    if(sharedpreferences.getString("AlarmSwitch", "").equals("on")) {
                        player.start();
                        isPlayed = true;
                    }
                    thread.cancel();
                    thread.interrupt();
                    myThreadConnected.interrupt();
                }
            }
        };
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter3);
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        device = intent.getParcelableExtra("btDevice");
        if (device != null) {
            thread = new ThreadToConnectBT(device);
            thread.start();
        }
        return 1;
    }

    private class ThreadToConnectBT extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadToConnectBT(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
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
                Toast.makeText(getBaseContext(), "Error :" + eMessage, Toast.LENGTH_LONG).show();
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            //connect successful
            if(success){
                startThreadConnected(bluetoothSocket);
            }else{
                //fail
                Toast.makeText(getBaseContext(), "Your bracelet connection failed", Toast.LENGTH_LONG).show();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
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
        }
        @Override
        public void run() {
            while (true) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectedInputStream));
                    final String message = reader.readLine();
                    if (message.equals("BAHAYA")) {
                        //Lempar data ke service di sini
                        Log.d("debug", message);
                        if (counter != 1) {
                            Intent activitySms = new Intent(BluetoothService.this, SmsService.class);
                            activitySms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(activitySms);
                        }
                        else {
                            counter++;
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Prime Service : Destroyed", Toast.LENGTH_LONG).show();
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

