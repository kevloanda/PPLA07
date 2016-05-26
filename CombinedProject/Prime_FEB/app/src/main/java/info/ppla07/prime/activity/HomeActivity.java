package info.ppla07.prime.activity;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import info.ppla07.prime.R;
import info.ppla07.prime.helper.SQLiteHandler;
import info.ppla07.prime.helper.SessionManager;

public class HomeActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private TextView txtName;
    private TextView txtEmail;
    private ImageView btnBluetooth;
    private Button btnContact;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        txtName = (TextView) findViewById(R.id.welcome);
        btnBluetooth = (ImageView) findViewById(R.id.btnBluetooth);
        btnContact = (Button) findViewById(R.id.btnContact);

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
//
//		// Fetching user details from SQLite
//		HashMap<String, String> user = db.getUserDetails();
//
//		String name = user.get("name");
//
//        String userId = user.get("uid");
//
//
//		SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = sp.edit();
//		editor.putString("UserId",userId);
//		editor.commit();

        String name = "PRIME";

        // Displaying the user details on the screen
        txtName.setText("Welcome, " + name);

        // Bluetooth button click event
        btnBluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });

        // Contact button click event
        btnContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				SharedPreferences sp = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
//				SharedPreferences.Editor editor = sp.edit();
//				editor.putString("DeviceLost","false");
//				editor.putString("EmergencyContactsNames","");
//				editor.putString("EmergencyContactsNumbers","");
//				editor.commit();
//				Log.d("Error1", sp.getString("EmergencyContactsNames", ""));
//				Log.d("Error1",sp.getString("EmergencyContactsNumbers",""));
                Intent intent = new Intent(HomeActivity.this, ContactEmergency.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
