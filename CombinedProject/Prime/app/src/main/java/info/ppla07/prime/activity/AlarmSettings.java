package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import info.ppla07.prime.R;

public class AlarmSettings extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);
        Switch alarmSwitch = (Switch)findViewById(R.id.switch1);

        SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
        if(sharedpreferences.getString("AlarmSwitch","").equals("on")) {
            alarmSwitch.setChecked(true);
        }
        else {
            alarmSwitch.setChecked(false);
        }

//        Listener untuk switch
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedpreferences = getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                if (isChecked) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("AlarmSwitch", "on");
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("AlarmSwitch", "off");
                    editor.commit();
                }
            }
        });
    }
}
