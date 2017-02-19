package com.yufanlin.hopin;

/**
 * Created by Yufan on 2/19/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editEmail;
    private EditText editName;
    private EditText editPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;
    private FirebaseAuth fireBaseAuth;

    @Override
    //Executed when app is opened up for the first time in a cycle
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     //load saved values
        setContentView(R.layout.activity_main); //show view specified by activity_main

        fireBaseAuth = FirebaseAuth.getInstance();  //get instance of user

        progressDialog = new ProgressDialog(this);  //create a processDialog object

        buttonRegister = (Button) findViewById(R.id.buttonRegister);    //create button register

        editEmail = (EditText) findViewById(R.id.editEmail);        //take in user email input
        editPassword = (EditText) findViewById(R.id.editPassword);  //take in user password input

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);  //take in user sign in option

        buttonRegister.setOnClickListener(this);    //record button press
        textViewSignin.setOnClickListener(this);    //record press on sign in
    }

    //attempts to register a user based off of their username and password
    private void registerUser() {
        String email = editEmail.getText().toString().trim();   //get email from user
        String password = editPassword.getText().toString().trim(); //get password from user

        if (TextUtils.isEmpty(email)) { //checks if the input email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {  //checks if the input password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User");   //adds message to loading screen
        progressDialog.show();  //shows loading screen

        fireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //registration success and log in success
                            Toast.makeText(MainActivity.this, "You have successfully registered", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Registration Not Successful | Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {   //if button is clicked
            registerUser();             //call register user function
        }
        if (view == textViewSignin) {
            //open login activity here
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
