package com.example.user.bluetoothapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class BluetoothService extends Service {

    private static final String TAG = null;
    MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();

        //Membuat media player untuk alarm
        player = MediaPlayer.create(this, R.raw.test);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
//        //Turn ON BlueTooth if it is OFF
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, 1);
//        }
//
//        setup();

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

