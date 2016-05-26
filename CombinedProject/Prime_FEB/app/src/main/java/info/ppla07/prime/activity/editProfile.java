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

import info.ppla07.prime.R;
import info.ppla07.prime.helper.SQLiteHandler;

public class editProfile extends Activity {

    private Button btnContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btnContact = (Button) findViewById(R.id.button3);

        btnContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.editText)).getText().toString();
		        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                db.editProfile(name,sharedpreferences.getString("UserId",""));
                Context context = getApplicationContext();
                CharSequence text = "Profile Changed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent activitySelectedContact = new Intent(editProfile.this, Settings.class);
                startActivity(activitySelectedContact);
            }
        });
    }
}
