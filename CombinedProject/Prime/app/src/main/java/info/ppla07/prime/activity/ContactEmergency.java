package info.ppla07.prime.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.ppla07.prime.R;

public class ContactEmergency extends Activity {

    private ListView listView;
    List<String> strings;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contact_emergency);

        listView= (ListView) findViewById(R.id.listView);

        strings = new ArrayList<String>();
        strings.add("Select Contact");
        strings.add("Selected Contact");
        strings.add("Edit Message");

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                strings);

        listView.setAdapter(arrayAdapter);
    }
}
