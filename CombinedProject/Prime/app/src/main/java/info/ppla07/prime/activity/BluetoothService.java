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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.Command;


/**
 * Dalam class ini terdapat pengaturan untuk mendeteksi koneksi bluetooth antara bracelet dan app
 * @return
 */
public class BluetoothService extends Service {

    protected BluetoothDevice connectedDevice;
    private MediaPlayer player;
    private BluetoothDevice device;
    private IBinder mBinder = new MyBinder();
    private ThreadToConnectBT thread;
    private ThreadConnected myThreadConnected;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private int counter = 1;
    private boolean isPlayed = false;
    private boolean isConnected = false;
    //private SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
    //private SharedPreferences.Editor editor = sharedpreferences.edit();


    public class MyBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        //Inisialisasi data yang dibutuhkan
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        setup();
    }

//    public String getNotification() {
//        if (myThreadConnected.isAlive()) {
//            return "connected";
//        }
//        return null;
//    }
//    public void setConnectedFlag (boolean flag) {
//        isConnected = flag;
//    }

    private void setup() {
        //Membuat media player untuk alarm
        Log.d("order", "setup service");
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
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.cancel();
                        Log.d("player", "mediaplayer mati");
                    }

                    //Update session ttg device connected
                    SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("ConnectedDevice", device.getName());
                    editor.putString("DeviceConnected","true");
                    editor.commit();
                    connectedDevice = device;
                    //Log.d("sessiondeviceconnected", sharedpreferences.getString("ConnectedDevice", ""));

                    //Trigger update ke activity bluetooth
                    Log.d("status", "connected");
                    Intent updateBroadcast = new Intent("update");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateBroadcast);
                    isConnected = true;
                    Toast.makeText(getBaseContext(), "Service to protect you has connected", Toast.LENGTH_LONG).show();
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    //Device has disconnected
                    counter = 1;
                    final SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                    if (isConnected) {
                        Toast.makeText(getBaseContext(), "Warning : CONNECTION LOST", Toast.LENGTH_LONG).show();
                        if(sharedpreferences.getString("AlarmSwitch", "").equals("on")) {
                            if(sharedpreferences.getString("VibrationSwitch","").equals("on")) {
                                // Get instance of Vibrator from current Context
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                // Start without a delay
                                // Vibrate for 100 milliseconds
                                // Sleep for 1000 milliseconds
                                long[] pattern = {0, 1000, 1000};

                                // The '0' here means to repeat indefinitely
                                // '0' is actually the index at which the pattern keeps repeating from (the start)
                                // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                                v.vibrate(pattern, 0);
                            }
                            player.start();
                            isPlayed = true;
                            Log.d("player", "mediaplayer bunyi");
                        }
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("DeviceLost","true");
//                        editor.commit();
//                        Handler mHandler = new Handler();
//                        mHandler.postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                if(sharedpreferences.getString("DeviceLost", "").equals("true")) {
//                                    SmsService sms = new SmsService();
//                                    sms.sendDelayedSMSMessage();
//                                }
//                            }
//
//                        }, 60000L);
                        isConnected = false;
                    }
                    //Update session ttg device connected
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("DeviceConnected","false");
                    editor.putString("ConnectedDevice", "noDevice");
                    editor.commit();
                    Log.d("debug", "service broadcast receiver");
                    //Log.d("sessiondeviceconnected", sharedpreferences.getString("ConnectedDevice", ""));

                    //Trigger update ke activity bluetooth
                    Log.d("status", "disconnected");
                    Intent updateBroadcast = new Intent("update");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateBroadcast);
                    connectedDevice = null;

                    if (thread != null) {
                        disconnect();
                    }
                }
            }
        };
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter3);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Debug-an
        if (thread == null) {
            Log.d("thread", "thread null");
        }
        else if (thread != null) {
            Log.d("thread", "thread not null");
        }
        if (myThreadConnected == null) {
            Log.d("mythreadconnected", "mythreadconnected null");
        }
        else if (myThreadConnected != null) {
            Log.d("mythreadconnected", "mythreadconnected not null");
        }

        if (intent != null) {
            Command command = intent.getParcelableExtra("command");
            if (command != null) {
                Log.d("command", command.getContent());
                //Diskonek
                //int theorder = order;
                String thecommand = command.getContent();
                if (thecommand.equals("disconnect")) {
                    Command content= intent.getParcelableExtra("deviceName");
                    String deviceName = content.getContent();
                    Log.d("order", "disconnect attempt");
//                    Log.d("connecteddevice", connectedDevice.getName());
                    if (connectedDevice.getName().equals(deviceName)) {
                        disconnect();
                        isConnected = false;
                        //Update session ttg device connected
                        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("DeviceConnected", "false");
                        editor.commit();
                        Toast.makeText(getBaseContext(), "Disconnect successful", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //fail
                        Toast.makeText(getBaseContext(), "Warning : FAILED TO DISCONNECT\nPlease restart app.", Toast.LENGTH_LONG).show();
                    }
                }
                //Konek
                else if (thecommand.equals("connect")) {
                    if (thread == null) {
                        device = intent.getParcelableExtra("btDevice");
                        thread = new ThreadToConnectBT(device);
                        thread.start();
                    }
                    else {
                        //fail
                        Toast.makeText(getBaseContext(), "Warning : YOU HAVE ALREADY CONNECTED\nPlease disconnect first.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        return 1;
    }
    public void disconnect() {
        thread.cancel();
        //thread.interrupt();
        myThreadConnected.cancel();
        //myThreadConnected.interrupt();
        thread = null;
        myThreadConnected = null;
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
                //Toast.makeText(getBaseContext(), "Error :" + eMessage, Toast.LENGTH_LONG).show();
                //hrsny pake update broadcast
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
                //Toast.makeText(getBaseContext(), "Your bracelet connection failed", Toast.LENGTH_LONG).show();
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
                        Log.d("pesanbahaya", message);
                        Log.d("counter", Integer.toString(counter));
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
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //setup();
        Log.d("debug", "ontaskremoved");
        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("DeviceConnected","false");
        editor.putString("ConnectedDevice", "noDevice");
        editor.commit();
    }
}

