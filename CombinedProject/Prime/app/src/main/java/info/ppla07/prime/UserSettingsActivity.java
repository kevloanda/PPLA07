package info.ppla07.prime;
        import android.os.Bundle;
        import android.preference.PreferenceActivity;

public class UserSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

    }
}
