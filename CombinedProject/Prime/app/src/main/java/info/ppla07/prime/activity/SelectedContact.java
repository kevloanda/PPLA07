package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.SelectedContactsAdapter;

public class SelectedContact extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contact);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        if(!(sharedpreferences.getString("EmergencyContactsNames", "").equals(""))) {
            String[] names = sharedpreferences.getString("EmergencyContactsNames", "").split(";");
            String[] numbers = sharedpreferences.getString("EmergencyContactsNumbers", "").split(";");
            ArrayList<String> selectedContact = new ArrayList<String>();
            for(int i = 0; i < names.length; i++) {
                selectedContact.add(names[i] + "-" + numbers[i]);
            }
            SelectedContactsAdapter adapter = new SelectedContactsAdapter(selectedContact, this);
            ListView listView = (ListView) findViewById(R.id.listView2);
            listView.setAdapter(adapter);
        }
        else {
            ArrayList<String> selectedContact = new ArrayList<String>();
            selectedContact.add("No Contacts Chosen");
            ListView listView = (ListView) findViewById(R.id.listView2);
            SelectedContactsAdapter adapter = new SelectedContactsAdapter(selectedContact, this);
            listView.setAdapter(adapter);
        }
    }
}
