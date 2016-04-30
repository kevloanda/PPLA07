package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.ppla07.prime.R;

public class Settings extends Activity {
    private ListView listView;
    List<String> strings;
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        listView = (ListView) findViewById(R.id.listView);

        strings = new ArrayList<String>();
        strings.add("Edit Profile");
        strings.add("Alarm");
        strings.add("Bracelet Settings");

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                strings);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        Intent activitySelecte = new Intent(Settings.this, Settings.class);
                        startActivity(activitySelecte);
                        break;
                    case 1:
                        Intent activityAlarm = new Intent(Settings.this, AlarmSettings.class);
                        startActivity(activityAlarm);
                        break;
                    case 2:
                        Intent activityBluetooth = new Intent(Settings.this, BluetoothActivity.class);
                        startActivity(activityBluetooth);
                        break;
                }
            }
        });
    }
}
