package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.ppla07.prime.R;

public class ContactEmergency extends Activity {

    private ListView listView;
    List<String> strings;
    ArrayAdapter<String> arrayAdapter;
    private final int PICK_CONTACT = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch( position )
                {
                    case 0: Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				        startActivityForResult(intent, PICK_CONTACT);
                        break;
                    case 1:  Intent activitySelectedContact = new Intent(ContactEmergency.this, SelectedContact.class);
                        startActivity(activitySelectedContact);
                        break;
                    case 2:  Intent activityEditMessage = new Intent(ContactEmergency.this, SelectedContact.class);
                        startActivity(activityEditMessage);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                        if(sharedpreferences.getString("EmergencyContacts", "").equals("")) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("EmergencyContacts", name);
                            editor.commit();
                        }
                        else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("EmergencyContact", sharedpreferences.getString("EmergencyContacts", "") + name);
                            editor.commit();
                        }
                    }
                }
                break;
        }
    }
}
