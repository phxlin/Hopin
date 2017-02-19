package com.yufanlin.hopin;

/**
 * Created by Yufan on 2/19/17.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplitActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth fireBaseAuth;
    private TextView textLogout;
    private TextView textViewUseremail;
    private Button buttonDriver;
    private Button buttonPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        fireBaseAuth = FirebaseAuth.getInstance();  //get instance of user

        if(fireBaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }


        FirebaseUser user = fireBaseAuth.getCurrentUser();

        textViewUseremail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUseremail.setText(user.getEmail());

        textLogout = (TextView) findViewById(R.id.textLogout);
        textLogout.setOnClickListener(this);
    }

    private void DriverMode () {
        finish();
        startActivity(new Intent(this, WelcomeActivity.class));
    }
    private void PassengerMode () {
        finish();
        startActivity(new Intent(this, WelcomeActivity.class));
    }
    private void LogOut () {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }



    @Override
    public void onClick(View view) {
        if (view == buttonDriver) {   //if button is clicked
            DriverMode();             //call register user function
        }
        if (view == buttonPassenger) {
            //open login activity here
            PassengerMode();
        }
        if (view == textLogout) {
            LogOut();
        }
    }

}
