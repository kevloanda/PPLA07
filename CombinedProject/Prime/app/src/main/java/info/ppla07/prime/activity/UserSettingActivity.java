package info.ppla07.prime.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import info.ppla07.prime.R;

public class UserSettingActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

    }
}