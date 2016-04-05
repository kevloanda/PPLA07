package com.example.user.myapplicationa07;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view){
        try {
            String username = ((EditText)findViewById(R.id.username)).getText().toString();
            String email = ((EditText)findViewById(R.id.email)).getText().toString();
            String nomorhp = ((EditText)findViewById(R.id.phone)).getText().toString();
            String password = ((EditText)findViewById(R.id.password)).getText().toString();
            String twitter = ((EditText)findViewById(R.id.twitter)).getText().toString();

//            To be changed later
            String foto = "link_foto";

            AsyncHTTPRequest async = new AsyncHTTPRequest();

//            Change the url in execute according to the register.php location
            async.execute("http://192.168.173.1/register.php?username=" + username + "&email=" + email + "&password=" + password + "&nomorhp=" + nomorhp + "&foto=" + foto + "&twitter=" + twitter);

        }
        catch(Exception e) {
            Log.e("MYAPP", "exception", e);;
        }
    }
}
