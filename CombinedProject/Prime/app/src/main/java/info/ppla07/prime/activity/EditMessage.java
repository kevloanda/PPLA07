package info.ppla07.prime.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import info.ppla07.prime.R;

import info.ppla07.prime.R;

public class EditMessage extends Activity {

    private Button btnMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        btnMessage = (Button) findViewById(R.id.btnMessage);

        // Contact button click event
        btnMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }
}
