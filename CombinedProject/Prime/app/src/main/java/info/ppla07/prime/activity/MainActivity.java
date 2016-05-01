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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.SQLiteHandler;
import info.ppla07.prime.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;
	private ImageView btnBluetooth;
	private Button btnContact;
	private ImageView btnSetting;

	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.welcome);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnBluetooth = (ImageView) findViewById(R.id.btnBluetooth);
		btnContact = (Button) findViewById(R.id.btnContact);
		btnSetting = (ImageView) findViewById(R.id.btnSetting);

		startService(new Intent(this, BluetoothService.class));

//		// SqLite database handler
//		db = new SQLiteHandler(getApplicationContext());
//
//		// session manager
//		session = new SessionManager(getApplicationContext());
//
//		if (!session.isLoggedIn()) {
//			logoutUser();
//		}

//		// Fetching user details from SQLite
//		HashMap<String, String> user = db.getUserDetails();

//		String name = user.get("name");

		String name = "PRIME";

		// Displaying the user details on the screen
		txtName.setText("Welcome, " + name);

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

        // Bluetooth button click event
		btnBluetooth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
				startActivity(intent);
			}
		});

		// Setting button click event
		btnSetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
			}
		});

		// Contact button click event
		btnContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
//				SharedPreferences.Editor editor = sp.edit();
//				editor.putString("EmergencyContactsNames","");
//				editor.putString("EmergencyContactsNumbers","");
//				editor.commit();
//				Log.d("Error1", sp.getString("EmergencyContactsNames", ""));
//				Log.d("Error1",sp.getString("EmergencyContactsNumbers",""));
				Intent intent = new Intent(MainActivity.this, ContactEmergency.class);
				startActivity(intent);

			}
		});
	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */

	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
