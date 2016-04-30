package info.ppla07.prime.activity;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import info.ppla07.prime.R;

public class BluetoothService extends Service {

    private static final String TAG = null;
    MediaPlayer player;
    ThreadConnected myThreadConnected;
    UUID myUUID;
    ThreadToConnectBT myThread;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";

    public void onCreate() {

        //Toast.makeText(this, "BLUTUT KONTOL", Toast.LENGTH_LONG).show();
        super.onCreate();

        //Membuat media player untuk alarm
        player = MediaPlayer.create(this, R.raw.test);
        player.setLooping(true); // Set looping
        player.setVolume(100, 100);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

//        //Membuat intent untuk menjalankan bluetooth service
//        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//
//        //The BroadcastReceiver that listens for bluetooth broadcasts
//
//        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                    //Device is now connected
//
//                    player.pause();
//                    Toast.makeText(getBaseContext(), "KONEK", Toast.LENGTH_LONG).show();
//
//                }
//                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                    //Device has disconnected
//
//                    player.start();
//                    Toast.makeText(getBaseContext(), "DC", Toast.LENGTH_LONG).show();
//                    //Ini hrs dimatiin thread, sama bluetooth socketnya dinullin
//
//                }
//            }
//        };
//        this.registerReceiver(mReceiver, filter1);
//        this.registerReceiver(mReceiver, filter3);
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
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

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful

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

        }

        @Override
        public void run() {

            boolean firstTimeKonek = false;
            boolean firstTimeDC = true;
            boolean test = true;

            while (true) {

                try {

//                    Log.d("debug", firstTimeKonek+" "+firstTimeDC);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connectedInputStream));
                    final String message = reader.readLine();
                    Log.d("debug2", message+"");

                    if(firstTimeKonek){
//                        player.pause();
//                        firstTimeKonek = false;
//                        firstTimeDC = true;
                    }

                } catch (IOException e) {

                    if(firstTimeDC){
//                        player.start();
//                        firstTimeDC = false;
//                        firstTimeKonek = true;
                    }
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

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        BluetoothDevice device = intent.getParcelableExtra("device");

        myThread = new ThreadToConnectBT(device);
        myThread.start();

        return 1;
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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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

