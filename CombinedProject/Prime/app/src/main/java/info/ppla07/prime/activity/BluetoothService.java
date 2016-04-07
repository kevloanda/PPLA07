package info.ppla07.prime.activity;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import info.ppla07.prime.R;

public class BluetoothService extends Service {

    private static final String TAG = null;
    MediaPlayer player;

    public void onCreate() {
        Toast.makeText(this, "BLUTUT KONTOL", Toast.LENGTH_LONG).show();
        super.onCreate();
        //Membuat media player untuk alarm
        player = MediaPlayer.create(this, R.raw.test);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

        //Membuat intent untuk menjalankan bluetooth service
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //The BroadcastReceiver that listens for bluetooth broadcasts
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Device is now connected
                    player.pause();
//                    player.reset();
//                    player.release();
                    Toast.makeText(getBaseContext(), "KONEK", Toast.LENGTH_LONG).show();

                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Device has disconnected
//                    player.reset();
                    player.start();
                    Toast.makeText(getBaseContext(), "DC", Toast.LENGTH_LONG).show();
                    //Ini hrs dimatiin thread, sama bluetooth socketnya dinullin

                }
            }
        };
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter3);
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
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

