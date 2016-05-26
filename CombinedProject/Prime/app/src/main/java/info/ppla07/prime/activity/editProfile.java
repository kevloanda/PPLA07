package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.SQLiteHandler;

public class editProfile extends Activity {

    private Button btnContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btnContact = (Button) findViewById(R.id.button3);

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        ((EditText) findViewById(R.id.editText)).setText(user.get("name"));
        ((EditText) findViewById(R.id.editText3)).setText(user.get("email"));

        btnContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                String name = ((EditText) findViewById(R.id.editText)).getText().toString();
                String email = ((EditText) findViewById(R.id.editText3)).getText().toString();
                boolean isEmailValid = true;
                boolean isEmailUnique = !db.isEmailExist(email);
                HashMap<String, String> user = db.getUserDetails();
                if (email.equals(user.get("email"))) {
                    isEmailUnique = true;
                }
                if (!email.contains("@")){
                    isEmailValid = false;
                }
                else {
                    int index = email.indexOf('@');
                    if(index == 0) {
                        isEmailValid = false;
                    }
                    else if(index == email.length()-1) {
                        isEmailValid = false;
                    }
                }

                if (!email.contains(".")){
                    isEmailValid = false;
                }
                else {
                    int index = email.indexOf('@');
                    if(index == 0) {
                        isEmailValid = false;
                    }
                    else if(index == email.length()-1) {
                        isEmailValid = false;
                    }
                }

                int index = email.indexOf('@');
                if(index == -1) {
                    isEmailValid = false;
                }
                else {
                    if(email.substring(index,email.length()).length() == 0) {
                        isEmailValid = false;
                    }
                }

                if (isEmailValid && isEmailUnique) {
                    SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                    db.editProfile(name, email, sharedpreferences.getString("UserId", ""));
                    db.close();
                    Context context = getApplicationContext();
                    CharSequence text = "Profile Changed!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    Intent activitySelectedContact = new Intent(editProfile.this, Settings.class);
                    startActivity(activitySelectedContact);
                } else if (!isEmailValid) {
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid Email";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (!isEmailUnique) {
                    Context context = getApplicationContext();
                    CharSequence text = "Email has been used";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}
