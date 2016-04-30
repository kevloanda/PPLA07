package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.widget.Toast;

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
        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_emergency);

        listView= (ListView) findViewById(R.id.listView);

        strings = new ArrayList<String>();
        strings.add("Add Contact");
        strings.add("Selected Contact");
        strings.add("Edit Message");
        strings.add("Test SMS");

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
                    case 2:  Intent activityEditMessage = new Intent(ContactEmergency.this, EditMessage.class);
                        startActivity(activityEditMessage);
                        break;
                    case 3:  Intent activitySms = new Intent(ContactEmergency.this, SmsService.class);
                        startActivity(activitySms);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        ContentResolver cr = getContentResolver();
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        boolean save = true;
                        boolean saved = false;
                        while (phones.moveToNext() && !saved) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String[] savedNumbers = sharedpreferences.getString("EmergencyContactsNumbers", "").split(";");

                            for(int i = 0; i < savedNumbers.length; i++) {
                                if(savedNumbers[i].equals(number)) {
                                    save = false;
                                }
                            }

                            switch (type) {
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    if(save) {
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("EmergencyContactsNumbers", sharedpreferences.getString("EmergencyContactsNumbers", "") + number + ";");
                                        editor.commit();
                                        saved = true;
                                    }
                                    break;
                            }
                            Log.d("Error",number);
                        }
                        phones.close();
                        if(save) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("EmergencyContactsNames", sharedpreferences.getString("EmergencyContactsNames", "") + name + ";");
                            editor.commit();
                            CharSequence text = "Added " + name + " to Emergency Contact";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                            toast.show();
                        }
                        else {
                            Context context = getApplicationContext();
                            CharSequence text = "You have selected this contact. Please choose another contact";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                }
                break;
        }
    }
}
