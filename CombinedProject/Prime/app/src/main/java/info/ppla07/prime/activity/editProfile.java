package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.ppla07.prime.R;
import info.ppla07.prime.app.AppConfig;
import info.ppla07.prime.app.AppController;
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
                final String name = ((EditText) findViewById(R.id.editText)).getText().toString();
                final String email = ((EditText) findViewById(R.id.editText3)).getText().toString();
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
                    String tag_string_req = "req_update";
                    SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                    db.editProfile(name, email, sharedpreferences.getString("UserId", ""));
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_UPDATE, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d("Update", "Update Response: " + response.toString());

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");
                                if (!error) {
                                    Toast.makeText(getApplicationContext(), "Profile Changed!", Toast.LENGTH_LONG).show();
                                    Intent activitySelectedContact = new Intent(editProfile.this, Settings.class);
                                    startActivity(activitySelectedContact);
                                    finish();
                                } else {

                                    // Error occurred in update profile. Get the error
                                    // message
                                    String errorMsg = jObj.getString("error_msg");
                                    Toast.makeText(getApplicationContext(),
                                            errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Update", "Update Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting params to update url
                            SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("name", name);
                            params.put("email", email);
                            params.put("id", sharedpreferences.getString("UserId",""));

                            return params;
                        }

                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
