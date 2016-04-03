package com.example.user.myapplicationa07;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        try {
            Class.forName("com.mysql.jdbc.driver");
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user?" + "user=christian&password=uwaaaaa");
            Statement statement = connect.createStatement();
            String query = "Insert into user values()";
            int result = statement.executeUpdate(query);
            if(result == 0) {
                setContentView(R.layout.error_page);
            }
        }
        catch(Exception e) {

        }
    }
}
